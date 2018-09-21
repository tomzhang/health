package com.dachen.health.common.listener;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventType;
import com.dachen.commons.asyn.event.annotation.EcEventListener;
import com.dachen.commons.asyn.event.annotation.EcEventMapping;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.base.utils.JobTaskUtil;
import com.dachen.health.checkbill.dao.XGCheckItemReqDao;
import com.dachen.health.checkbill.entity.po.ImageData;
import com.dachen.health.checkbill.entity.po.XGCheckItemReq;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderNoitfyType;
import com.dachen.health.commons.constants.PackConstants;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.account.entity.vo.RechargeVO;
import com.dachen.health.pack.account.service.IRechargeService;
import com.dachen.health.pack.consult.dao.ElectronicIllCaseDao;
import com.dachen.health.pack.consult.entity.po.IllCaseInfo;
import com.dachen.health.pack.messageGroup.IMessageGroupService;
import com.dachen.health.pack.messageGroup.MessageGroupEnum;
import com.dachen.health.pack.order.dao.IPendingOrderStatusDao;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.PendingOrderStatus;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.pack.pay.service.IPayHandleBusiness;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.mobsms.sdk.MobSmsSdk;
import com.squareup.okhttp.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component("OrderBusinessListener")
@EcEventListener
public class OrderBusinessListener {
	
	private static final Logger log = LoggerFactory.getLogger(OrderBusinessListener.class);


	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	@Resource
	OrderMapper orderMapper;
	
	@Autowired
	private XGCheckItemReqDao xgCheckItemReqDao;

	public static String XG_SERVER = PropertiesUtil.getContextProperty("xg.server"); //玄关接口访问地址
	public static String XG_SERVER_ANALYSIS = PropertiesUtil.getContextProperty("xg.illhistory.analysis"); //玄关病历分析页面
	public static String ILLHISTORYINFO_INFO = PropertiesUtil.getContextProperty("xg.illhistory.info"); //病历分析说明页面
	public static String ILLHISTORYINFO_H5 = PropertiesUtil.getContextProperty("xg.illhistory.h5"); //病历H5页面
	
	@Autowired
	private IPendingOrderStatusDao pendingOrderStatusDao;
	
	@Resource
	private IPayHandleBusiness payHandleBusiness;
	
	@Resource
	private IOrderService orderService;
	
	@Resource
	private IOrderSessionService orderSessionService;
	
	@Resource
	private UserManager userManager;
	
	@Resource
	private IPatientService patientService;
	
	@Resource
	private ElectronicIllCaseDao illcaseDao;
	
	@Resource	
	private MobSmsSdk mobSmsSdk;
	
	@Resource
	private IBaseDataService baseDataService;

	@Autowired
    private IRechargeService rechargeService;

    @Autowired
    private IMessageGroupService messageGroupService;

	@EcEventMapping(type = {EventType.OrderPaySuccess})
    public void firePaySuccess(EcEvent event) throws Exception {
		Integer orderId = event.param("orderId");
		
		try {
			payHandleBusiness.handleBusinessWhenPaySuccess(orderId);
		} catch (Exception e) {
			log.error("支付成功处理相关业务异常。订单ID："+orderId, e);
			log.error(e.getMessage(), e);
		}
	}
	
	@EcEventMapping(type = {EventType.SendMessageAndNotify})
    public void fireSendMessageAndNotify(EcEvent event) throws Exception {
		String tag = "fireSendMessageAndNotify";
		Integer orderId = event.param("orderId");
		Boolean isFromWeChat = event.param("isFromWeChat");
		if (log.isInfoEnabled()) {
			log.info("{}. orderId={}, isFromWeChat={}", tag, orderId, isFromWeChat);
		}
		
		try {
			Order order = orderService.getOne(orderId);
			OrderSession os = orderSessionService.findOneByOrderId(orderId);
			
			if (log.isInfoEnabled()) {
				log.info("{}. order={}, os={}", tag, order, os);
			}
			
			Map<String,Object> mapString = new HashMap<>();
	        User user = userManager.getUser(order.getDoctorId());
	        Patient patient = patientService.findByPk(order.getPatientId());
	        IllCaseInfo info = null;
	        if(StringUtils.isNotBlank(order.getIllCaseInfoId())){
			      info = illcaseDao.getIllCase(order.getIllCaseInfoId());
			}
	        mapString.put("title", StringUtil.returnPackTitle(order.getPackType(),order.getPrice()));
	        mapString.put("patient", patient);
	        mapString.put("docName", user.getName());
	        if(info != null)
	        	mapString.put("diseaseDesc", info.getMainCase());
	        mapString.put("orderType", order.getOrderType());
	        mapString.put("price", order.getPrice());
	        mapString.put("order", order);
	        mapString.put("illCaseInfoId", order.getIllCaseInfoId());
	        
			if (isFromWeChat != null && isFromWeChat) {
				// "1042"：尊敬的用户您好，您已成功提交订单，请登录玄关健康APP（http://t.cn/RqiNrGi）进一步完成支付，祝您早日康复。
				User patientUser = userManager.getUser(order.getUserId());
				mobSmsSdk.send(patientUser.getTelephone(), baseDataService.toContent("1042", BaseConstants.XG_PLATFORM,
						shortUrlComponent.generateShortUrl(BaseConstants.XG_OPEN_PAT())));
			}
			
			if (log.isInfoEnabled()) {
				log.info("{}. order.getUserId()={}, order.getDoctorId()={}, os.getMsgGroupId()={}", tag, order.getUserId(), order.getDoctorId(), os.getMsgGroupId());
			}
			
			// 医患会话发送通知
			 if(info != null){
				 orderService.sendOrderNoitfy(String.valueOf(order.getUserId()), String.valueOf(order.getDoctorId()),
							os.getMsgGroupId(), OrderNoitfyType.neworder, mapString);
                 messageGroupService.updateGroupBizState(os.getMsgGroupId(), MessageGroupEnum.NEW_ORDER.getIndex());
			 }
			
			// 医助会话发送通知
			/*if(StringUtil.isNotEmpty(os.getAssistantDoctorGroupId()))
			{
			orderService.sendDoctorAssistantOrderNotify(String.valueOf(order.getAssistantId()),
					String.valueOf(order.getDoctorId()), os.getAssistantDoctorGroupId(), OrderNoitfyType.neworder,
					null);
			}
			// 患助会话发送通知
			if(StringUtil.isNotEmpty(os.getAssistantPatientGroupId()))
			{
				orderService.sendPatientAssistantOrderNotify(String.valueOf(order.getUserId()),
						String.valueOf(order.getAssistantId()), os.getAssistantPatientGroupId(), OrderNoitfyType.neworder,
						null);
			}*/

			/**免费图文咨询下订单就开始服务**/
			if( ( order.getPackType() == PackType.message.getIndex() ||
					 order.getPackType() == PackType.integral.getIndex()) 
					&& (order.getPrice() == null || order.getPrice() == 0)) {
				//先发送支付成功的卡片
				RechargeVO recharegVo = rechargeService.findOneByOrderId(orderId);
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("order", order);
				paramMap.put("recharegVo", recharegVo);
				OrderSession orderSession = orderSessionService.findOneByOrderId(orderId);
				orderService.sendOrderNoitfy(String.valueOf(order.getUserId()), String.valueOf(order.getDoctorId()), orderSession.getMsgGroupId(), OrderEnum.OrderNoitfyType.payorder, paramMap);
                messageGroupService.updateGroupBizState(orderSession.getMsgGroupId(), MessageGroupEnum.NEW_ORDER.getIndex());
				orderSessionService.beginService(orderId);
			}
			
//			if (PackType.careTemplate.getIndex() == order.getPackType()) {
//				// 订单开始后立刻发送需要发送的关怀项
//				this.orderService.beginService4FreePlanImmediately(order);
//			}
			
		} catch (Exception e) {
			log.error("创建订单完成发送消息和通知失败。订单ID：" + orderId, e);
		}
	}

	@EcEventMapping(type = {EventType.ARM_TO_MP3})
	public void arm2mp3(EcEvent event) {
		String key = event.param("key");
		
		if (StringUtil.isNotEmpty(key)) {
			QiniuUtil.avthumb(key);
		}
	}

	@EcEventMapping(type = {EventType.SEND_CHECKITEM_TO_XG})
	public void uploadCheckItem(EcEvent event) {
        Integer patientId = event.param("patientId");
        String patientName = event.param("patientName");
        Integer sex = event.param("sex");
        String hospitalId = event.param("hospitalId");
        String medicalHistoryUrl = event.param("medicalHistoryUrl");
        String checkItemId = event.param("checkItemId");
        String checkUpId = event.param("checkUpId");
        String phone = event.param("phone");
        String address = event.param("address");

		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("PatientID", patientId);
		paramMap.put("Sex", (sex != null && sex.intValue() == 1) ? "M" : "F");
		paramMap.put("Name", patientName);
		paramMap.put("HospitalID", hospitalId);
        paramMap.put("Phone", phone);
        paramMap.put("Address", address);
		paramMap.put("MedicalHistoryUrl", medicalHistoryUrl);

		List<String> images = event.param("images");

		List<ImageData> imageDatas = Lists.newArrayList();
		images.forEach(a->{
			ImageData imageData = new ImageData();
			imageData.setInspection(checkUpId);
			imageData.setImageUrl(a);
			imageDatas.add(imageData);
		});
		paramMap.put("Images", imageDatas);

		Gson gson = new Gson();
		String requestParam = gson.toJson(paramMap);

		OkHttpClient client = new OkHttpClient();

		String url = XG_SERVER + "/Service/api/AppSync/UploadAppData";
		RequestBody body = RequestBody.create(JSON, requestParam);
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();

		Map<String, Object> result = null;
		try {
			Response response = client.newCall(request).execute();
			result = JSONMessage.parseObject(response.body().string(), Map.class);

			XGCheckItemReq xgCheckItemReq = xgCheckItemReqDao.findByCheckItemId(checkItemId);
			if (Objects.isNull(xgCheckItemReq)) {
				xgCheckItemReq = new XGCheckItemReq();
				xgCheckItemReq.setPatientId(patientId);
				xgCheckItemReq.setSex(sex);
				xgCheckItemReq.setPatientName(patientName);
				xgCheckItemReq.setHospitalId(hospitalId);
				xgCheckItemReq.setImages(images);
				xgCheckItemReq.setMedicalHistoryUrl(medicalHistoryUrl);
				xgCheckItemReq.setCheckUpId(checkUpId);
				xgCheckItemReq.setPhone(phone);
				xgCheckItemReq.setAddress(address);
				Long now = System.currentTimeMillis();
				xgCheckItemReq.setCreateTime(now);
				xgCheckItemReq.setUpdateTime(now);
				if (StringUtils.equals((String)result.get("Code"), "1")) {
					xgCheckItemReq.setSuccess(true);
					xgCheckItemReq.setResponseMessage("Success");
				} else {
					xgCheckItemReq.setSuccess(false);
					xgCheckItemReq.setResponseMessage((String) result.get("Message"));
				}
				xgCheckItemReq.setCheckItemId(checkItemId);
				xgCheckItemReqDao.save(xgCheckItemReq);

				if (!StringUtils.equals((String)result.get("Code"), "1")){
                    JobTaskUtil.reSendImagesToXG(xgCheckItemReq.getId());
                }
			} else {
				if (StringUtils.equals((String)result.get("Code"), "1")) {
					xgCheckItemReq.setSuccess(true);
					xgCheckItemReq.setResponseMessage("Success");
				} else {
					xgCheckItemReq.setSuccess(false);
					xgCheckItemReq.setResponseMessage((String) result.get("Message"));
                    JobTaskUtil.reSendImagesToXG(xgCheckItemReq.getId());
				}
				xgCheckItemReqDao.update(xgCheckItemReq);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@EcEventMapping(type = {EventType.OrderSessionSendMsg})
    public void fireOrdersessionSendmsg(EcEvent event) throws Exception {
		Integer orderId = event.param("orderId");
		String messageBit = event.param("messageBit");
		
		try {
			Order order=orderService.getOne(orderId);
			PendingOrderStatus pos=pendingOrderStatusDao.queryByOrderId(orderId);
			if(order.getPackType()==PackType.message.getIndex()&&pos!=null&&!StringUtil.isEmpty(messageBit)){
				char last = messageBit.charAt(messageBit.length()-1);
				if(last=='0'){//图文订单患者发消息
					pos.setOrderStatus(1);//图文咨询订单设置为待处理
					pos.setFlagTime(System.currentTimeMillis());//设置开始等待时间
					pendingOrderStatusDao.updateById(pos);
				}
				if(messageBit.contains("0")&&last=='1'){//图文订单医生发消息
					pendingOrderStatusDao.deleteByOrderId(orderId);
				}
			}
		} catch (Exception e) {
			log.error("发送消息改变医生助手看到的订单状态异常。订单ID："+orderId, e);
		}
	}

	@Autowired
	protected ShortUrlComponent shortUrlComponent;
	
	@EcEventMapping(type = {EventType.OrderSessionSendFirstMsg})
    public void fireOrdersessionSendFirstmsg(EcEvent event) throws Exception {
		Integer orderId = event.param("orderId");
		String messageGroupId = event.param("messageGroupId");
		
		try {
			//第5次取订单，医生第一次回复，给患者发短信，可以异步
			Order o = orderMapper.getOne(orderId);
			Patient p = patientService.findByPk(o.getPatientId());
			User d = userManager.getUser(o.getDoctorId());
			//亲，XX医生已经回复您了，快去看看医生说了什么吧
			String url = shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", messageGroupId, UserType.patient.getIndex()));
			mobSmsSdk.send(p.getTelephone(), baseDataService.toContent("1049", d.getName(), url));
			
		} catch (Exception e) {
			log.error("发送消息改变医生助手看到的订单状态异常。订单ID："+orderId, e);
		}
	}
}
