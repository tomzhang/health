package com.dachen.health.pack.pay.service.impl;


import com.dachen.careplan.api.client.CarePlanApiClientProxy;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.KeyBuilder;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.base.utils.JobTaskUtil;
import com.dachen.health.commons.constants.AccountEnum;
import com.dachen.health.commons.constants.IllHistoryEnum.IllHistoryRecordType;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderSessionCategory;
import com.dachen.health.commons.constants.OrderEnum.OrderStatus;
import com.dachen.health.commons.constants.OrderEnum.OrderType;
import com.dachen.health.commons.constants.PackConstants;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.utils.OrderNotify;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.friend.service.FriendsManager;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.account.entity.param.RechargeParam;
import com.dachen.health.pack.account.entity.vo.RechargeVO;
import com.dachen.health.pack.account.service.IRechargeService;
import com.dachen.health.pack.consult.Service.ElectronicIllCaseService;
import com.dachen.health.pack.guide.dao.IGuideDAO;
import com.dachen.health.pack.guide.entity.po.OrderRelationPO;
import com.dachen.health.pack.guide.service.IGuideService;
import com.dachen.health.pack.guide.util.GuideUtils;
import com.dachen.health.pack.illhistory.dao.IllHistoryRecordDao;
import com.dachen.health.pack.illhistory.entity.po.DrugInfo;
import com.dachen.health.pack.illhistory.entity.po.IllHistoryRecord;
import com.dachen.health.pack.illhistory.entity.po.RecordCare;
import com.dachen.health.pack.illhistory.entity.po.RecordOrder;
import com.dachen.health.pack.illhistory.service.IllHistoryInfoService;
import com.dachen.health.pack.incomeNew.service.IncomelogService;
import com.dachen.health.pack.messageGroup.Impl.MessageGroupServiceImpl;
import com.dachen.health.pack.messageGroup.MessageGroupEnum;
import com.dachen.health.pack.order.dao.IOrderSessionContainerDao;
import com.dachen.health.pack.order.dao.IPendingOrderStatusDao;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.OrderSessionContainer;
import com.dachen.health.pack.order.entity.po.PendingOrderStatus;
import com.dachen.health.pack.order.service.IOrderDoctorService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.pack.pay.entity.PaymentVO;
import com.dachen.health.pack.pay.service.IPayHandleBusiness;
import com.dachen.health.user.dao.ITagDao;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.RedisUtil;
import com.mobsms.sdk.MobSmsSdk;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayHandleBusinessImpl implements IPayHandleBusiness{
	
	@Autowired
	private IOrderService orderService;
	
	@Autowired
    private IRechargeService rechargeService;
	
    @Autowired
    private IOrderSessionService orderSessionService;
    
    @Autowired
    private IGuideService guideService;
    
    @Autowired
    private FriendsManager friendsManager;
    
    @Autowired
    private IBaseUserService baseUserService;
    
    @Autowired
    private IBusinessServiceMsg businessServiceMsg;
    
    @Autowired
    private IPatientService patientService;
    
    @Resource
    private MobSmsSdk mobSmsSdk;
    
    @Autowired
    private IBaseDataService baseDataService;
    
    @Autowired
    IGuideDAO iGuideDAO;
    
    @Autowired
    IOrderDoctorService orderDoctorService;
    
    @Resource
	private UserManager userManager;
    
    @Resource
    private IncomelogService incomelogService;
    
    @Autowired
    ElectronicIllCaseService illcaseService;

    @Resource
    protected CarePlanApiClientProxy carePlanApiClientProxy;
    
    @Autowired
    IPackService packService;
    
    @Autowired
    IOrderSessionContainerDao orderSessionContainerDao;
    
    @Autowired
    IllHistoryInfoService illHistoryInfoService ;

    @Autowired
    IllHistoryRecordDao illHistoryRecordDao;

	@Autowired
	private ITagDao tagDao;

	@Autowired
	private IPendingOrderStatusDao pendingOrderStatusDao;

	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;

    @Autowired
    private MessageGroupServiceImpl messageGroupService;

    Logger logger = LoggerFactory.getLogger(PayHandleBusinessImpl.class);

	public final static String MESSAGE_REPLY_ORDER_COUNT = KeyBuilder.MESSAGE_REPLY_COUNT+":order";
    
	public void handlePayBusinessLogic(PaymentVO paymentVo) throws ServiceException, HttpApiException {
		String tag = "handlePayBusinessLogic";
		logger.info("{}. paymentVo={}", tag, paymentVo);

		RechargeParam param = new RechargeParam();
		param.setPayNo(paymentVo.getPayNo());
		RechargeVO recharegVo = rechargeService.findRechargeByPayNo(param);
		logger.info("{}. recharegVo={}", tag, recharegVo);

		if (recharegVo == null) {
			throw new ServiceException(400, "未找到订单号：" + paymentVo.getPayNo() + "相关的记录！");
		}
		logger.info("{}. recharegVo.getRechargeStatus()={}", tag, recharegVo.getRechargeStatus());
		if (recharegVo.getRechargeStatus() == AccountEnum.RechargeStatus.成功.getIndex()
				|| recharegVo.getRechargeStatus() == AccountEnum.RechargeStatus.失败.getIndex()) {
			throw new ServiceException(200, "已处理过的订单..");
		}
		logger.info("{}. paymentVo.getTradeStatus()={}", tag, paymentVo.getTradeStatus());
		if (PackConstants.AIL_WAIT_BUYER_PAY.equals(paymentVo.getTradeStatus())) {
			// TODO 更新订单状态 支付宝预支付通知
			throw new ServiceException(200, "暂不处理.");
		}

		if (PackConstants.AIL_TRADE_SUCCESS.equals(paymentVo.getTradeStatus())) {
			logger.debug("{}. AIL_TRADE_SUCCESS", tag);
			Order order = null;
			if (AccountEnum.PayType.alipay.getIndex() == paymentVo.getPayType()
					|| AccountEnum.PayType.wechat.getIndex() == paymentVo.getPayType()
					|| AccountEnum.PayType.integral.getIndex() == paymentVo.getPayType()) {
				logger.debug("{}. alipay wechat integral", tag);

				// 处理订单状态
				recharegVo.setRechargeStatus(AccountEnum.RechargeStatus.成功.getIndex());
				recharegVo.setAlipayNo(paymentVo.getPayAlftNo());
				recharegVo.setPartner(paymentVo.getPartner());
				rechargeService.updateRecharge(recharegVo);
				logger.debug("{}. after updateRecharge={}", tag, recharegVo);

				order = orderService.getOne(recharegVo.getSourceId());
				logger.debug("{}. order={}", tag, order);

				if (order.getOrderType() == OrderType.feeBill.getIndex()) {
					order.setOrderStatus(OrderEnum.OrderStatus.已完成.getIndex());
				} else {
					order.setOrderStatus(OrderEnum.OrderStatus.已支付.getIndex());
				}
				
				order.setId(recharegVo.getSourceId());
				order.setPayTime(System.currentTimeMillis());
				order.setPayType(paymentVo.getPayType());
				order.setRemarks("");
				orderService.updateOrder(order);
				logger.info("{}. after updateOrder. order={}", tag, order);


				/**改版之后的订单会话处理 start  2016-12-07 16:20:51**/
				OrderSession os = orderSessionService.findOneByOrderId(order.getId());
				logger.info("{}. os={}", tag, os);
				OrderSessionContainer po ;
				if((order.getPackType() == PackType.message.getIndex() ||
						order.getPackType() == PackType.phone.getIndex() ||
						order.getPackType() == PackType.integral.getIndex() ||
						order.getPackType() == PackType.online.getIndex()) &&
						os == null){
					logger.info("{}. no careTemplate case", tag);

					Pack pack=new Pack();
					//门诊订单没有套餐
					if(order.getPackType()==PackType.online.getIndex()){
						pack.setPackType(PackType.online.getIndex());
					}else{
						pack = packService.getPack(order.getPackId());
					}
					po = orderService.processOrderSession(order, pack, order.getCareOrderId());
					logger.info("{}. po={}", tag, po);
					sendNewOrderCard(po,IllHistoryRecordType.order.getIndex());
					logger.info("{}. after sendNewOrderCard", tag);
				}else if(order.getPackType() == PackType.careTemplate.getIndex()){
					logger.info("{}. careTemplate case", tag);
					/****防止数据修改时将老订单未支付状态 插入了Osc数据***/
					po = orderSessionContainerDao.findByOrderId(order.getId());
					logger.info("{}. po={}", tag, po);
					if(po == null){
						po = new OrderSessionContainer();
						po.setDoctorId(order.getDoctorId());
						po.setUserId(order.getUserId());
						po.setPatientId(order.getPatientId());
						po.setOrderSessionId(os.getId());
						po.setOrderId(order.getId());
						po.setPackType(order.getPackType());
						po.setSessionType(OrderSessionCategory.care.getIndex());
						po.setStatus(OrderStatus.已支付.getIndex());
						po.setMsgGroupId(os.getMsgGroupId());
						po.setTotalReplyCount(0);
						po.setReplidCount(0);
						orderSessionContainerDao.insert(po);
						logger.info("{}. after insert po={}", tag, po);
					}else{
						orderSessionContainerDao.updateById(po.getId(),null,null,null,0,0,OrderStatus.已支付.getIndex());
					}
					logger.info("{}. before sendNewOrderCard", tag);
					sendNewOrderCard(po,IllHistoryRecordType.care.getIndex());
					logger.info("{}. after sendNewOrderCard", tag);
				}else{
					po = new OrderSessionContainer();
					po.setMsgGroupId(os.getMsgGroupId());
				}
				/** 修改病程 **/
				logger.info("{}. before updateRecordPayOrSessionGroup. orderId={}, msgGroupId={}", tag, order, po.getMsgGroupId());
				illHistoryRecordDao.updateRecordPayOrSessionGroup(order.getId(), true, po.getMsgGroupId());
				/**改版之后的订单会话处理 start  2016-12-07 16:20:51**/
				logger.info("{}. after updateRecordPayOrSessionGroup. orderId={}, msgGroupId={}", tag, order, po.getMsgGroupId());

				logger.info("{}. before addJesQueTask.", tag);
				orderService.addJesQueTask(order);
				logger.info("{}. after addJesQueTask.", tag);

				//生成对应待处理状态数据PendingOrderStatus
				logger.info("{}. before createPendingOrderStatus.", tag);
				createPendingOrderStatus(order);
				logger.info("{}. after createPendingOrderStatus.", tag);

				//会诊支付之后清理聊天统计重新计算待回复
				if(order.getPackType() == PackType.consultation.getIndex()) {
					jedisTemplate.hdel(MESSAGE_REPLY_ORDER_COUNT,po.getMsgGroupId());
				}

				//支付成功相关业务异步处理
				logger.info("{}. 支付成功相关业务异步处理paySuccessNotify...", tag);
				OrderNotify.paySuccessNotify(order.getId());
			}
		} else if (PackConstants.AIL_TRADE_FINISHED.equals(paymentVo.getTradeStatus())) {
			// 6个月后订单结束
			throw new ServiceException(200, "支付宝完成交易订单处理逻辑");
		} else {
			throw new ServiceException(100, "交易类型不匹配,处理支付宝通知失败...");
		}
	}


	//生成对应待处理状态数据PendingOrderStatus,,支付成功的订单
	public void createPendingOrderStatus(Order order){
		if(order.getOrderStatus()!=OrderStatus.已支付.getIndex()){
			return;
		}

		//电话咨询订单
		if(order.getPackType()==PackType.phone.getIndex()){
			PendingOrderStatus pendingOrderStatus=new PendingOrderStatus();
			pendingOrderStatus.setOrderId(order.getId());
			pendingOrderStatus.setOrderStatus(1);//订单一创建就是待处理状态
			pendingOrderStatus.setFlagTime(System.currentTimeMillis());//设置开始等待时间
			pendingOrderStatus.setOrderWaitType(2);//2.电话订单-医生未开始
			pendingOrderStatusDao.add(pendingOrderStatus);
		}

		//图文咨询订单
		if(order.getPackType()==PackType.message.getIndex()){
			PendingOrderStatus pendingOrderStatus=new PendingOrderStatus();
			pendingOrderStatus.setOrderId(order.getId());
			pendingOrderStatus.setOrderStatus(0);//订单不是待处理状态，患者发消息置为待处理状态
			pendingOrderStatus.setOrderWaitType(1);//1.图文订单-医生未回复
			pendingOrderStatusDao.add(pendingOrderStatus);
		}

		//健康关怀订单
		if(order.getPackType()==PackType.careTemplate.getIndex()){
			PendingOrderStatus pendingOrderStatus=new PendingOrderStatus();
			pendingOrderStatus.setOrderId(order.getId());
			pendingOrderStatus.setOrderStatus(0);//订单不是待处理状态，有最新待办事项则置为待处理状态
			pendingOrderStatus.setOrderWaitType(4);//4.健康关怀-患者未答题
			pendingOrderStatusDao.add(pendingOrderStatus);
		}
	}

	public void sendNewOrderCard(OrderSessionContainer osc,int type) throws HttpApiException {
		IllHistoryRecord record = illHistoryRecordDao.findByOrderIdAndType(osc.getOrderId(), type);
		OrderParam orderParam = new OrderParam();
		orderParam.setOrderId(osc.getOrderId());
		Order o = orderService.getOne(osc.getOrderId());
		orderParam.setExpectAppointmentIds(o.getExpectAppointmentIds());
		if(type == IllHistoryRecordType.order.getIndex()){
			RecordOrder rd = record.getRecordOrder();
			orderParam.setDiseaseDesc(rd.getDiseaseDesc());
			orderParam.setOrderStatus(osc.getStatus());
			orderParam.setPackType(osc.getPackType());
			orderParam.setPatientId(osc.getPatientId());
			orderParam.setGid(osc.getMsgGroupId());
			orderParam.setDiseaseID(rd.getDiseaseId());
			orderParam.setHopeHelp(rd.getHopeHelp());
			orderParam.setDiseaseDuration(rd.getDiseaseDuration());
			orderParam.setDrugGoodsIds(rd.getDrugGoodsIds());
			orderParam.setDrugCase(rd.getDrugCase());
			orderParam.setDrugPicUrls(rd.getDrugPicUrls());
			orderParam.setPicUrls(rd.getPics());
			orderParam.setDoctorId(osc.getDoctorId());
			orderParam.setUserId(osc.getUserId());
			orderParam.setTreatCase(rd.getTreatCase());
			if(StringUtils.isNotBlank(rd.getDrugInfos())){
				List<DrugInfo> drugInfoList = JSONMessage.parseArray(rd.getDrugInfos(), DrugInfo.class);
				orderParam.setDrugInfoList(drugInfoList);
			}
		}else if (type == IllHistoryRecordType.care.getIndex()){
			RecordCare rc = record.getRecordCare();
			orderParam.setDiseaseDesc(rc.getDiseaseDesc());
			orderParam.setOrderStatus(osc.getStatus());
			orderParam.setPackType(osc.getPackType());
			orderParam.setPatientId(osc.getPatientId());
			orderParam.setGid(osc.getMsgGroupId());
			orderParam.setDiseaseID(rc.getDiseaseId());
			orderParam.setHopeHelp(rc.getHopeHelp());
			orderParam.setDiseaseDuration(rc.getDiseaseDuration());
			orderParam.setDrugGoodsIds(rc.getDrugGoodsIds());
			orderParam.setDrugCase(rc.getDrugCase());
			orderParam.setDrugPicUrls(rc.getDrugPicUrls());
			orderParam.setPicUrls(rc.getPics());
			orderParam.setDoctorId(osc.getDoctorId());
			orderParam.setUserId(osc.getUserId());
			orderParam.setTreatCase(rc.getTreatCase());
			if(StringUtils.isNotBlank(rc.getDrugInfos())){
				List<DrugInfo> drugInfoList = JSONMessage.parseArray(rc.getDrugInfos(), DrugInfo.class);
				orderParam.setDrugInfoList(drugInfoList);
			}
		}
		orderService.sendNewOrderIllCard(orderParam);
	}


	public void handleBusinessWhenPaySuccess(Integer orderId) throws HttpApiException {
		String tag = "handleBusinessWhenPaySuccess";
		logger.info("{}. orderId={}", tag, orderId);

		Order order = orderService.getOne(orderId);
		logger.info("{}. order={}", tag, order);
		RechargeVO recharegVo = rechargeService.findOneByOrderId(orderId);
		logger.info("{}. recharegVo={}", tag, recharegVo);
		//1、电话订单生成对应的电子病历
		/*try {
			if (order != null && order.getPackType() != null
					&& order.getPackType().intValue() == PackEnum.PackType.phone.getIndex()) {
				// 获取电话订单对应的病情信息
				Integer diseaseId = order.getDiseaseId();
				Disease disease = diseaseService.findByPk(diseaseId);
				ConsultOrderPO po = iGuideDAO.getObjectByOrderId(orderId);
				if (po != null && po.getDiseaseInfo() != null) {
					disease.setDiseaseImgs(po.getDiseaseInfo().getDiseaseImgs());
				}
				String illCaseInfoId = illcaseService.createPhoneOrderIllCase(disease, order);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("orderId", order.getId());
				params.put("illCaseInfoId", illCaseInfoId);
				orderMapper.updateOrderIllCaseInfoId(params);
			}
			
		} catch (Exception e) {
			logger.error("支付回调：电话订单生成对应的电子病历失败", e);
		}*/

		//2、先判断是否是好友，不是好友则添加好友并发送消息。如果是好友则不添加也不发送消息
		try {
			Integer mainDoctorId = order.getDoctorId();
			DoctorPatient doctorPatient = tagDao.findByDoctorIdAndPatientId(mainDoctorId, order.getPatientId());
			if (doctorPatient == null) {
				//2016-12-12医患关系的变化
				baseUserService.addDoctorPatient(mainDoctorId, order.getPatientId(), order.getUserId());
				businessServiceMsg.sendEventFriendChange(EventEnum.ADD_FRIEND, mainDoctorId.toString(), order.getUserId().toString());
			}
		} catch (Exception e) {
			logger.error("支付回调：添加好友并发送消息异常:" + e.getMessage(), e);
		}

		//3、endPay
		try {
			guideService.endPay(order.getId());
		} catch (Exception e) {
			logger.error("支付回调：回写导医订单状态异常:" + e.getMessage(), e);
		}
		
		//4、发送订单支付成功提示，创建导医日程，更新OrderSession时间
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("order", order);
			paramMap.put("recharegVo", recharegVo);
			OrderSession orderSession = orderSessionService.findOneByOrderId(order.getId());
			if (orderSession != null) {
				//创建导医日程
				//scheduleService.createGuiderSchedule(order, orderSession.getAppointTime());
				//更新订单时间
				orderSession.setLastModifyTime(System.currentTimeMillis());
				orderSessionService.updateByPrimaryKeySelective(orderSession);
				orderService.sendOrderNoitfy(String.valueOf(order.getUserId()), String.valueOf(order.getDoctorId()),
						orderSession.getMsgGroupId(), OrderEnum.OrderNoitfyType.payorder, paramMap);
                messageGroupService.updateGroupBizState(orderSession.getMsgGroupId()
                        , MessageGroupEnum.NEW_ORDER.getIndex());
			}
		} catch (Exception e) {
			logger.error("支付回调：发送订单支付成功提示，创建导医日程，更新订单Session时间异常:" + e.getMessage(), e);
		}
		
		//5、判断订单类型如果是门诊订单，则向redis等待队列添加数据
		try {
			if (order.getOrderType() == OrderEnum.OrderType.outPatient.getIndex() && order.getDoctorId() != 0) {
				String wkey = RedisUtil.generateKey(RedisUtil.WAITING_QUEUE, String.valueOf(order.getDoctorId()));
				RedisUtil.rpush(wkey, recharegVo.getSourceId().toString());
			}
		} catch (Exception e) {
			logger.error("支付回调：向redis等待队列添加数据异常:" + e.getMessage(), e);
		}
		
		try {
			if (order.getOrderType() == OrderType.appointment.getIndex()) {
				// 更新serviceBegin 时间
				// orderSessionService.beginService(order.getId());
				/**
				 * 名医面对面版本修改为手动点击开始服务
				 */
			} else if (order.getOrderType() == OrderType.feeBill.getIndex()) {
				orderSessionService.finishServiceWhenPaySuccess(orderId);
			} else if (order.getOrderType() == OrderType.care.getIndex()) {
				carePlanApiClientProxy.saveDrugRecipe(order.getId(), order.getCareTemplateId());//关怀支付完成生成药方
			}
		} catch (Exception e) {
			logger.error("支付回调：业务处理:" + e.getMessage(), e);
		}
		
        if(order.getPackType() == PackType.phone.getIndex()) {
        	//当订单为电话订单的时候才发短信给患者
        	Patient patient = patientService.findByPk(order.getPatientId());
        	OrderRelationPO  po = iGuideDAO.getGuideIdByOrderId(order.getId());
        	User user = userManager.getUser(po.getDoctorId());
			final String smsContent = baseDataService.toContent("0901", patient.getUserName(), user.getName(),
					GuideUtils.convertDate2Str(po.getAppointStartTime(), po.getAppointEndTime()));
            //根据patient里的userId去查询他的关系用户 在判断用户属于哪个平台
            User p_user = userManager.getUser(patient.getUserId());
            String signature = null;
            if(mobSmsSdk.isBDJL(p_user)){
            	signature = BaseConstants.BD_SIGN;
            }else{
            	signature = BaseConstants.XG_SIGN;
            }
            mobSmsSdk.send(patient.getTelephone(), smsContent,signature);
            //最后将此订单加入到消息队列等待处理  （已支付电话咨询订单预约时间前10分钟）
            long delayTime = po.getAppointStartTime() - 10*60*1000 - System.currentTimeMillis();
			if (delayTime > 0) {
				JobTaskUtil.autoSendMsgToGuider(orderId, delayTime/1000);
			}
            //预约时间超过半小时导医还未开启三方通话
            long halfHourTime = po.getAppointStartTime() + 30*60*1000 - System.currentTimeMillis();
			if (halfHourTime > 0) {
				JobTaskUtil.autoSendMsgToGuiderOfHalfHour(orderId, halfHourTime/1000);
			}
        }else if (order.getPackType() == PackType.message.getIndex()){
        	orderSessionService.beginService(orderId);
        }
	}

}
