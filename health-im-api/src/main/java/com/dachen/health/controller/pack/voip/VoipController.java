package com.dachen.health.controller.pack.voip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dachen.health.pack.order.entity.po.PendingOrderStatus;
import com.dachen.health.pack.order.service.IPendingOrderStatusService;
import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.alipay.util.UtilData;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.logger.LoggerUtils;
import com.dachen.health.base.constant.CallEnum;
import com.dachen.health.base.constant.CallEnum.CallType;
import com.dachen.health.base.constant.DownTaskEnum;
import com.dachen.health.base.utils.JobTaskUtil;
import com.dachen.health.common.helper.BeanUtils;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderNoitfyType;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.entity.DownTask;
import com.dachen.health.commons.entity.TelephoneAccount;
import com.dachen.health.commons.service.DownTaskService;
import com.dachen.health.commons.service.TelephoneService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.conference.dao.CallRecordRepository;
import com.dachen.health.pack.conference.entity.po.AuthRequest;
import com.dachen.health.pack.conference.entity.po.CallRecord;
import com.dachen.health.pack.conference.service.ICallRecordService;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.model.CallResult;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.ICallResultService;
import com.dachen.health.pack.patient.service.IDiseaseService;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.user.entity.po.VOIP;
import com.dachen.util.MongodbUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.dachen.util.XmlToDbObject;
import com.mongodb.DBObject;
import com.ucpaas.restsdk.UcPaasRestSdk;

@RestController
@RequestMapping("/voip")
public class VoipController {

	@Resource
	UserManager userManager;

	@Resource
	UserRepository userRepository;

	@Resource
	ICallResultService callResultService;
	
	
	  @Autowired
	   private IOrderService orderService;
//	  @Autowired
//	  private  IBusinessServiceMsg businessServiceMsg;
	  
		@Resource
		private IOrderSessionService service;
		
		@Autowired
	    IPatientService patientService;
		

//	    @Autowired
//		private UserRepositoryImpl userManagerImpl;

	    @Autowired
		private IDiseaseService diseaseService;
	    
	    @Resource
	    IOrderSessionService orderSessionSevice;
	    
	    @Resource
	    TelephoneService telephoneService;
	    
	    @Autowired
	    private DownTaskService downService;
	    
	    @Autowired
		private ICallRecordService conferenceService;
	    
	    @Autowired
		private CallRecordRepository callRecordRepository;

		@Autowired
		IBusinessServiceMsg businessServiceMsg;

		@Autowired
		private IPendingOrderStatusService pendingOrderStatusService;
	    
	@Deprecated
	private JSONMessage call(Integer userId,Integer toUserId,Integer orderId) throws HttpApiException {
//		int userId = ReqUtil.instance.getUserId();
		User user = userManager.getUser(userId);
		User toUser = userManager.getUser(toUserId);
		if (user == null) {
			throw new ServiceException(10003, "找不到对应用户");
		} else {
			if (StringUtil.isEmpty(user.getTelephone())) {
				throw new ServiceException(10004, "你未设置电话，无法呼出");
			}
		}
		if (toUser == null) {
			throw new ServiceException(10003, "找不到对应用户");
		} else {
			if (StringUtil.isEmpty(toUser.getTelephone())) {
				throw new ServiceException(10004, "你未设置电话，无法呼出");
			}
		}
		VOIP voip = getVoipInfo(user);

		VOIP tovoip = getVoipInfo(toUser);
		
	 
//		VOIP tovoip = getVoipInfo(phone);

		tovoip.getDateCreated();

		//  check before call
		boolean canCall = false;
		 Map businessMap=new HashMap();
		//  check befor call
		if (!canCall) {
			JSONObject ret = UcPaasRestSdk.callback(voip.getVoipAccount(),
					toUser.getTelephone(), toUser.getTelephone(),
					user.getTelephone(),orderId.toString());
			if("000000".equals(((JSONObject)ret.get("resp")).get("respCode")))
			{
				String callId=(String)((JSONObject)((JSONObject)ret.get("resp")).get("callback")).get("callId");
				CallResult result=new CallResult();
				result.setCallid(callId);
				result.setUserData(orderId.toString());
				callResultService.save(result);
				
				CallRecord param = new CallRecord();
				param.setCallType(CallEnum.CallType.doctorToPatient.getIndex());
				param.setOrderId(orderId);
				param.setCallid(callId);
				callRecordRepository.saveCallRecord(param);
				
				service.beginService(orderId);
				
				OrderSession orderSession=orderSessionSevice.findOneByOrderId(orderId);
				orderService.sendOrderNoitfy(toUserId.toString(),userId.toString(), orderSession.getMsgGroupId(), OrderNoitfyType.beginCall, null);
				
				return JSONMessage.success("拨打成功", ret);
			}
			else
			{
				return JSONMessage.success("拨打失败", ret);
			}
			
		}

		return JSONMessage.failure("呼叫失败");
	}

	/**
	 * 
	 * @api {[get,post]} /voip/callByTel
	 * @apiVersion 1.0.0
	 * @apiName callByTel
	 * @apiGroup VOIP
	 * @apiDescription VOIP拨打
	 * @apiParam {String} access_token 凭证
	 * @apiParam {String} toTel 	电话号码（不能为空，拨给哪个）
	 * @apiParam {String} fromTel 	电话号码(可以为空。为空时取当前用户的电话)
	 * @return
	 * @apiAuthor 屈军利
	 * @date 2015年8月14日
	 */
	@RequestMapping("/callByTel")
	public JSONMessage callByTel(@RequestParam String toTel,String fromTel) {
		if(fromTel==null || fromTel.trim().length()==0)
		{
			User user = userManager.getUser(ReqUtil.instance.getUserId());
			fromTel = user.getTelephone();
		}
		TelephoneAccount toTelAcount=	telephoneService.findByTelePhone(toTel);
		TelephoneAccount fromTelAcount=	telephoneService.findByTelePhone(fromTel);
		if(toTelAcount==null||fromTelAcount==null){
			throw new ServiceException(40010, "第三方接口调用失败，请检查号码是否正确");
		}
		JSONObject ret = UcPaasRestSdk.callback(fromTelAcount.getClientNumber(),
				toTelAcount.getTelephone(), toTelAcount.getTelephone(),
				fromTelAcount.getTelephone(),"");
		if("000000".equals(((JSONObject)ret.get("resp")).get("respCode")))
		{
//			String callId=(String)((JSONObject)((JSONObject)ret.get("resp")).get("callback")).get("callId");
//			CallResult result=new CallResult();
//			result.setCallid(callId);
//			result.setUserData(orderId.toString());
			return JSONMessage.success("拨打成功", ret);
		}
		else
		{
			return JSONMessage.success("拨打失败", ret);
		}
	}
	/**
	 * 
	 * @api {[get,post]} /voip/callByOrder
	 * @apiVersion 1.0.0
	 * @apiName callByOrder
	 * @apiGroup VOIP
	 * @apiDescription 根据订单医生向患者拨打电话
	 * @apiParam {String} access_token 凭证
	 * @apiParam {Integer} orderId 订单id
	 * @return
	 * @apiAuthor 屈军利
	 * @date 2015年8月14日
	 */
	@RequestMapping("/callByOrder")
	public JSONMessage callByOrder(Integer orderId) throws HttpApiException {
		return callByOrderId(orderId,null);
	}
	
	/**
	 * 
	 * @api {[get,post]} /voip/callByOrderAndUserId
	 * @apiVersion 1.0.0
	 * @apiName callByOrderAndUserId
	 * @apiGroup VOIP
	 * @apiDescription 根据订单和主叫用户向患者拨打电话
	 * @apiParam {String} access_token 凭证
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {Integer} userId 主叫用户id
	 * @return
	 * @apiAuthor 屈军利
	 * @date 2015年8月14日
	 */
	@RequestMapping("/callByOrderAndUserId")
	public JSONMessage callByOrder(Integer orderId,Integer userId) throws HttpApiException {
		return callByOrderId(orderId,userId);
	}
	
	/**
	 * 
	 * @api {[get,post]} /voip/callByOrderToDoctor
	 * @apiVersion 1.0.0
	 * @apiName callByOrderToDoctor
	 * @apiGroup VOIP
	 * @apiDescription 根据订单和主叫用户向医生拨打电话
	 * @apiParam {String} access_token 凭证
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {Integer} userId 主叫用户id,不能为空
	 * @return
	 * @apiAuthor 屈军利
	 * @date 2015年8月14日
	 */
	@RequestMapping("/callByOrderToDoctor")
	public JSONMessage callByOrderToDoctor(Integer orderId,Integer userId) throws HttpApiException {
		return callByOrderIdToDoctor(orderId,userId);
	}
	

	
	
	/**
	 * 
	 * @api {[get,post]} /voip/callByOrderAndUserIdToUserId
	 * @apiVersion 1.0.0
	 * @apiName callByOrderAndUserIdToUserId
	 * @apiGroup VOIP
	 * @apiDescription 根据订单和主叫用户id和被叫用户id拨打电话
	 * @apiParam {String} access_token 凭证
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {Integer} fromUserId 主叫用户id,不能为空
	 * @apiParam {Integer} toUserId 被叫用户id,不能为空									 
	 * @return
	 * @apiAuthor 屈军利
	 */
	@RequestMapping("/callByOrderAndUserIdToUserId")
	public JSONMessage callByOrderAndUserIdToUserId(Integer orderId,Integer fromUserId,Integer toUserId) throws HttpApiException {
		return callByOrderIdUserIdToUserId(orderId,fromUserId,toUserId);
	}
	
	  /**
     * 根据主叫用户向被叫用户打电话
     * @param orderId
     * @param fromUserId
     * @return
     */
	private JSONMessage callByOrderIdUserIdToUserId(Integer orderId, Integer fromUserId, Integer toUserId) throws HttpApiException {
		
		if(orderId==null||fromUserId==null){
			throw new ServiceException(40006, "parameter orderId or fromUserId is null !");
		}
		Order order=orderService.getOne(orderId);
		if(order==null){
			throw new ServiceException(40005, "can't find order#"+orderId);
		}

		User fromUser=userManager.getUser(fromUserId);
		if(fromUser==null){
			throw new ServiceException(40007, "can't fount fromUser #"+order.getDoctorId());
		}
		if(StringUtil.isEmpty(fromUser.getTelephone())){
			throw new ServiceException(40008, "fromUser #"+fromUser.getName()+" 's phone not found");
		}
		
		User toUser=userManager.getUser(toUserId);
		if(toUser==null){
			throw new ServiceException(40007, "can't fount toUser #"+order.getDoctorId());
		}
		if(StringUtil.isEmpty(toUser.getTelephone())){
			throw new ServiceException(40008, "toUser  #"+toUser.getName()+" 's phone not found");
		}
		
		TelephoneAccount fromAccout=	telephoneService.findByTelePhone(fromUser.getTelephone());
		TelephoneAccount toAccout=	telephoneService.findByTelePhone(toUser.getTelephone());
		return callByOrderAndAccount(orderId, fromUser, toUser, fromAccout, toAccout);
	}
	
    /**
     * 根据主叫用户向医生打电话
     * @param orderId
     * @param fromUserId
     * @return
     */
	private JSONMessage callByOrderIdToDoctor(Integer orderId, Integer fromUserId) throws HttpApiException {
		
		if(orderId==null||fromUserId==null){
			throw new ServiceException(40006, "parameter orderId or fromUserId is null !");
		}
		Order order=orderService.getOne(orderId);
		if(order==null){
			throw new ServiceException(40005, "can't find order#"+orderId);
		}

		User fromUser=userManager.getUser(fromUserId);
		if(fromUser==null){
			throw new ServiceException(40007, "can't fount doctor user #"+order.getDoctorId());
		}
		if(StringUtil.isEmpty(fromUser.getTelephone())){
			throw new ServiceException(40008, "doctor user #"+fromUser.getName()+" 's phone not found");
		}
		User toUser=userManager.getUser(order.getDoctorId());

		TelephoneAccount fromAccout=	telephoneService.findByTelePhone(fromUser.getTelephone());
		TelephoneAccount toAccout=	telephoneService.findByTelePhone(toUser.getTelephone());
		return callByOrderAndAccount(orderId, fromUser, toUser, fromAccout, toAccout);
	}

	/**
	 * 根据主叫用户向患者打电话
	 * @param orderId
	 * @param fromUserId
	 * @return
	 */
	private JSONMessage callByOrderId(Integer orderId,Integer fromUserId) throws HttpApiException {
		if(orderId==null){
			throw new ServiceException(40006, "parameter  [orderId] is null !");
		}
		Order order=orderService.getOne(orderId);
		if(order==null){
			throw new ServiceException(40005, "can't find order#"+orderId);
		}
		if(fromUserId==null)
		{
			fromUserId= order.getDoctorId();
		}
		User fromUser=userManager.getUser(fromUserId);
		if(fromUser==null){
			throw new ServiceException(40007, "can't fount doctor user #"+order.getDoctorId());
		}
		if(StringUtil.isEmpty(fromUser.getTelephone())){
			throw new ServiceException(40008, "doctor user #"+order.getDoctorId()+" 's phone not found");
		}
		User toUser=userManager.getUser(order.getUserId());
		String patientTelephone=null;
		Patient p = patientService.findByPk(order.getPatientId());
		if(p != null){
			patientTelephone = p.getTelephone();
		}
		if(StringUtil.isEmpty(patientTelephone)){
			throw new ServiceException(40009, "patient #"+order.getPatientId()+" 's phone not found");
		}
		TelephoneAccount fromAccout=	telephoneService.findByTelePhone(fromUser.getTelephone());
		TelephoneAccount toAccout=	telephoneService.findByTelePhone(patientTelephone);
		return callByOrderAndAccount(orderId, fromUser, toUser, fromAccout, toAccout);
					
		}
	
	/**
	 * 
	 * @api {[get,post]} /voip/callByType
	 * @apiVersion 1.0.0
	 * @apiName callByType
	 * @apiGroup VOIP
	 * @apiDescription 单纯双向回拔电话无关订单（from 和 to 可以是用户Id，也可以是患者ID，根据type类型传参）
	 * @apiParam {String} access_token 凭证
	 * @apiParam {Integer} from  主叫方ID
	 * @apiParam {Integer} to 被叫方ID
	 * @apiParam {Integer} type 呼叫类型（type = 1助医（助手Id，医生Id）;type=2助患（助手ID，患者ID））
	 * @return
	 * @apiAuthor zhy
	 * @date 2017年1月7日
	 */
	@RequestMapping("/callByType")
	public JSONMessage callByType(Integer from,Integer to,Integer type){
		if(from == null || to == null || type == null){
			throw new ServiceException("参数不全");
		}
		TelephoneAccount fromAccout = null;
		TelephoneAccount toAccout = null;
		if(type == 1){//助医
			fromAccout = getUserAccount(from);
			toAccout = getUserAccount(to) ;
		}else if(type == 2){//助患 
			fromAccout = getUserAccount(from);
			toAccout=	getPatientAccount(to);
		}else {
			throw new ServiceException("暂不支持该类型");
		}
		return callByFromToAccount(fromAccout, toAccout);
	}
	private TelephoneAccount getUserAccount(Integer userId){
		User user=userManager.getUser(userId);
		if(user==null){
			throw new ServiceException(40007, "can't fount  user #"+userId);
		}
		if(StringUtil.isEmpty(user.getTelephone())){
			throw new ServiceException(40008, " user #"+userId+" 's phone not found");
		}
		return telephoneService.findByTelePhone(user.getTelephone());
	}
	private TelephoneAccount getPatientAccount(Integer patientId){
		Patient p = patientService.findByPk(patientId);
		if(p == null){
			throw new ServiceException(40007, "can't fount  patient #"+patientId);
		}
		if(StringUtil.isEmpty(p.getTelephone())){
			throw new ServiceException(40009, "patient #"+patientId+" 's phone not found");
		}
		return telephoneService.findByTelePhone(p.getTelephone());
	}
	private JSONMessage callByFromToAccount(TelephoneAccount fromAccout, TelephoneAccount toAccout){
		if(fromAccout==null||toAccout==null){
			throw new ServiceException(40010, "电话号码有误");
		}
	
			JSONObject ret = UcPaasRestSdk.callback(fromAccout.getClientNumber(),
					toAccout.getTelephone(), fromAccout.getTelephone(),
					toAccout.getTelephone(),"0");
			if("000000".equals(((JSONObject)ret.get("resp")).get("respCode")))
			{
				return JSONMessage.success("拨打成功", ret);
			}else{
				return JSONMessage.success("拨打失败", ret);
			}
	}
	private JSONMessage callByOrderAndAccount(Integer orderId, User fromUser, User toUser, TelephoneAccount fromAccout,
			TelephoneAccount toAccout) throws HttpApiException {
		if(fromAccout==null||toAccout==null){
			throw new ServiceException(40010, "电话号码有误");
		}
	
			JSONObject ret = UcPaasRestSdk.callback(fromAccout.getClientNumber(),
					toAccout.getTelephone(), toAccout.getTelephone(),
					fromUser.getTelephone(),orderId.toString());
			if("000000".equals(((JSONObject)ret.get("resp")).get("respCode")))
			{
				String callId=(String)((JSONObject)((JSONObject)ret.get("resp")).get("callback")).get("callId");
				CallResult result=new CallResult();
				result.setCallid(callId);
				result.setUserData(orderId.toString());
				callResultService.save(result);
				
				
				CallRecord param = new CallRecord();
				/**
				 * 默认为医生打电话给患者（CallType.doctorToPatient）：老代码逻辑（暂时不做细分处理，因为该字段没有用到）
				 * 添加CallType.assistantToPatient , CallType.assistantToDoctor
				 * 在电话接通之后的回掉中有用到
				 */
				Integer callType = CallEnum.CallType.doctorToPatient.getIndex();
				if(fromUser.getUserType() == UserType.assistant.getIndex() && 
						toUser.getUserType() == UserType.patient.getIndex())
					callType = CallEnum.CallType.assistantToPatient.getIndex();
				else if(fromUser.getUserType() == UserType.assistant.getIndex() && 
						toUser.getUserType() == UserType.doctor.getIndex())
					callType = CallEnum.CallType.assistantToDoctor.getIndex();
				param.setCallType(callType);
				param.setOrderId(orderId);
				param.setCallid(callId);
				param.setRecordStatus(1);//默认为1
				callRecordRepository.saveCallRecord(param);
				
//				service.beginService(orderId);
				OrderSession orderSession=orderSessionSevice.findOneByOrderId(orderId);
				orderService.sendOrderNoitfy(toUser.getUserId().toString(),fromUser.getUserId().toString(), orderSession.getMsgGroupId(), OrderNoitfyType.beginCall, null);
				return JSONMessage.success("拨打成功", ret);
			}
			else
			{
				return JSONMessage.success("拨打失败", ret);
			}
	}

	private VOIP getVoipInfo(User user) {
		VOIP voip = user.getVoip();
		if (voip == null) {
			JSONObject client = null;
			JSONObject ret2=UcPaasRestSdk.findClientByMobile(user.getTelephone());
			if ("000000".equals(((JSONObject)ret2.get("resp")).get("respCode"))) {
				client = (JSONObject)((JSONObject)ret2.get("resp")).get("client");
			}
			else
			{
				JSONObject ret = UcPaasRestSdk.createClient(null,user.getTelephone());
				if ("000000".equals(((JSONObject)ret.get("resp")).get("respCode"))) {
					client = (JSONObject)((JSONObject)ret.get("resp")).get("client");
				} 
			}
			if (client != null) {
				voip = toVoip(client);
				userRepository.updateVoip(user.getUserId(),
						BeanUtils.toMap(voip));
			}

		}
		return voip;
	}
	
	private VOIP getVoipInfo(String mobile) {
		   VOIP voip =null;
	 
			JSONObject client = null;
			JSONObject ret2=UcPaasRestSdk.findClientByMobile(mobile);
			if ("000000".equals(((JSONObject)ret2.get("resp")).get("respCode"))) {
				client = (JSONObject)((JSONObject)ret2.get("resp")).get("client");
			}
			else
			{
				JSONObject ret = UcPaasRestSdk.createClient(null,mobile);
				if ("000000".equals(((JSONObject)ret.get("resp")).get("respCode"))) {
					client = (JSONObject)((JSONObject)ret.get("resp")).get("client");
				} 
			}
			if (client != null) {
				voip = toVoip(client);
				 
			}

	 
		return voip;
	}

	private VOIP toVoip(JSONObject client) {
		VOIP tovoip;
		tovoip = new VOIP();
		tovoip.setDateCreated(client.getString("createDate"));
		tovoip.setVoipPwd(client.getString("clientPwd"));
		tovoip.setSubAccountSid(client.getString("clientNumber"));
		tovoip.setVoipAccount(client.getString("clientNumber"));
		return tovoip;
	}

	@RequestMapping("/confirmWithOutCharge")
	public JSONMessage confirmWithOutCharge() {
		return JSONMessage.success();
	}

	/**
	 * 
	 * @api {[get,post]} /voip/endCall
	 * @apiVersion 1.0.0
	 * @apiName endCall
	 * @apiGroup VOIP
	 * @apiDescription 
	 * @apiParam 							{String} 															access_token 											凭证
	 * @apiParam 							{HttpServletRequest}									result 														请求对象
	 * @apiSuccess {int} userId 用户id
	 * @apiAuthor 李淼淼
	 */
	@RequestMapping("/endCall")
	public void endCall(HttpServletRequest request,HttpServletResponse response) {
		try {
			InputStream inStream = request.getInputStream();
			ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
			outSteam.close();
			inStream.close();
			String resultxml = new String(outSteam.toByteArray(), "utf-8");
			CallResult callResult = null;
			try {
				InputStream is = new ByteArrayInputStream(resultxml.getBytes("UTF-8"));
				List<DBObject> list = XmlToDbObject.getDBObjects(is);
				if(list == null || list.size() == 0){
					return;
				}
				DBObject obj = list.get(0);
				if(obj.get("confId") != null){
					String event = MongodbUtil.getString(obj, "event");
					if(event.equals("confCreate")){
						conferenceService.endConfCreate(obj);
					}else if(event.equals("confJoin")){
						conferenceService.endJoin(obj);
					}else if(event.equals("confUnjoin")){
						conferenceService.endUnJoin(obj);
					}else if(event.equals("userMediaCtrRet")){
						conferenceService.endMedia(obj);
					}else if(event.equals("confRecordCtrRet")){
						conferenceService.endStopRecord(obj);
					}else if(event.equals("confDismiss")){
						conferenceService.endDismiss(obj);
					}else if(event.equals("confQueryRet")){
						conferenceService.endQuerry(obj);
					}
					return;
				}else if(obj.get("callid") != null){
					callResult = (CallResult)UtilData.getObjectFromXML(resultxml,CallResult.class,"request");
					if(callResult == null ){
						return ;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			if(callResult.getEvent().equals("callreq")){
				authCation(callResult ,response);//鉴权
				return;
			}
			
			//需要更新进去
//			List<CallResult> callResults=callResultService.findByCallid(callResult.getCallid());
//			CallResult  oldCallResult=callResults.get(0);
			CallRecord oldCallRecord = callRecordRepository.getCallVOByCallid(callResult.getCallid());
			if(oldCallRecord == null){
				return;
			}
			
//			int reason = Integer.parseInt(callResult.getReason());
//			boolean beginService = false;
//			String startTime = callResult.getStarttime();
//			String stopTime = callResult.getStoptime();
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			long start = sdf.parse(startTime).getTime();
//			long stop = sdf.parse(stopTime).getTime();
//			if(reason == 0){
//				beginService = true;
//			}else if(reason == 1){
//				if(stop > start){
//					beginService = true;
//				}
//			}
			
			Order order=orderService.getOne(Integer.valueOf(oldCallRecord.getOrderId()));
			if(order == null ){
				return;
			}
			OrderSession os = orderSessionSevice.findOneByOrderId(order.getId());
//			else{
//				if(beginService){
//					service.beginService(Integer.valueOf(oldCallResult.getUserData()));
//				}
//			}
		   
		 
//		    callResult.setId(oldCallResult.getId());
//		    callResult.setUserData(oldCallResult.getUserData());
//		    callResultService.update(callResult);
		    
		   
		   
		    if(callResult.getEvent().equals("callhangup")){
		    	//医生接通患者没有接通
		    	if(callResult.getReason().equals("0") ||callResult.getReason().equals("4") || callResult.getReason().equals("5") || callResult.getReason().equals("6")){
		    		Integer f = callResult.getLengthA();
		    		Integer t = callResult.getLength();
		    		if(f != null && f > 0 && (t==null || t <=0)){
						User d = userManager.getUser(order.getDoctorId());
						businessServiceMsg.sendNotifyMsgToAll(os.getMsgGroupId(),d.getName()+"医生已拨打您的电话，但无人接听，请及时通知医生您可以接听电话了");
		    		}
		    	}
		    	//电话结束发通知
		    	OrderSession orderSession=orderSessionSevice.findOneByOrderId(order.getId());
				
				String startTime = callResult.getStarttime();
				String stopTime = callResult.getStoptime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				long start = sdf.parse(startTime).getTime();
				long stop = sdf.parse(stopTime).getTime();
		    	
		    	CallRecord record = new CallRecord();
			    record.setCallid(callResult.getCallid());
			    if(oldCallRecord.getCallType() == null)
			    	record.setCallType(CallEnum.CallType.doctorToPatient.getIndex());
			    record.setEndTime(stop);
			    record.setCreateTime(start);
			    record.setFrom(callResult.getCaller());
			    record.setTo(callResult.getCalled());
			    record.setRecordUrl(callResult.getRecordurl());
			    record.setRecordId(callResult.getCallid());
			    record.setOrderId(order.getId());
			    callRecordRepository.updateCallRecordByCallid(record);
			    
			    String url = callResult.getRecordurl();
			    if( !StringUtil.isEmpty(url)){
			    	DownTask dt = new DownTask();
			    	dt.setCreateTime(System.currentTimeMillis());
			    	dt.setLastUpdateTime(System.currentTimeMillis());
			    	dt.setRecordId(callResult.getCallid());
			    	dt.setBussessType(DownTaskEnum.TableToClass.callRecord.getBusessType());
			    	dt.setSourceUrl(url);
			    	dt.setStatus(DownTaskEnum.DownStatus.recordAdd.getIndex());
			    	dt.setOrderId(order.getId()+"");
			    	downService.save(dt);
			    	JobTaskUtil.endStopRecord(callResult.getCallid());
			    }
		    	//发送消息通知
			    orderService.sendOrderNoitfy(order.getUserId().toString(),order.getDoctorId().toString(), orderSession.getMsgGroupId(), OrderNoitfyType.endCall, null);
		    }else if(callResult.getEvent().equals("callestablish")){
		    	/**
		    	 * 患者和医生通电话的 
		    	 * 只有两个人都结通才会进来
		    	 */
		    	if(oldCallRecord.getCallType() != null && 
		    			oldCallRecord.getCallType() == CallType.doctorToPatient.getIndex()){
		    		if (order.getOrderType() == null || 
			    			order.getOrderType().intValue() != OrderEnum.OrderType.consultation.getIndex()) {
			    		service.beginService(oldCallRecord.getOrderId());
						User d = userManager.getUser(order.getDoctorId());
						Patient p = patientService.findByPk(order.getPatientId());
						businessServiceMsg.sendNotifyMsgToUser(d.getUserId()+"",os.getMsgGroupId(),"您已与"+p.getUserName()+"患者电话沟通");
						businessServiceMsg.sendNotifyMsgToUser(order.getUserId()+"",os.getMsgGroupId(),d.getName()+"医生已与您电话沟通");
			    	}
		    	}

		    	//更新订单待处理状态，by qinyuan.chen
				PendingOrderStatus pos=pendingOrderStatusService.queryByOrderId(order.getId());
		    	pos.setFlagTime(System.currentTimeMillis());
				pos.setOrderWaitType(3);
				pendingOrderStatusService.updatePendingOrderStatus(pos);
		    }
			 
		}catch(Exception e) {
			    LoggerUtils.printExceptionLog(e.getMessage(), e.fillInStackTrace());
				 
		}

	}
	/**
	 * 简单对appid 以及 accountid校验
	 * @param callResult
	 * @param response
	 */
	private void authCation(CallResult callResult,HttpServletResponse response){
		if(UcPaasRestSdk.authVoip(callResult.getAccountid(), callResult.getAppid())){
			 PrintWriter pw = null;  
		        try {  
		            response.setContentType("text/xml;charset=utf-8");   
		            response.setCharacterEncoding("UTF-8");  
		            response.setHeader("Cache-Control", "no-cache");  
		            pw = response.getWriter();  
		            pw.print(getXmlRecord());  
		            pw.flush();  
		        }  
		        catch (Exception e) {  
		            e.printStackTrace();  
		        }  
		        finally {  
		            if (pw != null)  
		                pw.close();  
		        }  
		}
	}
	private String getXmlRecord(){
		StringBuffer sf = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sf.append("<response>\n")
		.append("<retcode>0</retcode>\n")
		.append("<record>1</record>\n")
		.append("</response>");
		return sf.toString();
	}
	
	/**
	 * 用来测试
	 * @param request
	 * @param response
	 */
	@RequestMapping("/getAuthcationInfo")
	public void  authcation(HttpServletRequest request,HttpServletResponse response){
		try {
			InputStream inStream = request.getInputStream();
			ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
			outSteam.close();
			inStream.close();
			String resultxml = new String(outSteam.toByteArray(), "utf-8");
			AuthRequest authRequest = (AuthRequest)UtilData.getObjectFromXML(resultxml,AuthRequest.class,"request");
			if(UcPaasRestSdk.authVoip(authRequest.getAccountid(), authRequest.getAppid())){
				 PrintWriter pw = null;  
			        try {  
			            response.setContentType("text/xml;charset=utf-8");   
			            response.setCharacterEncoding("UTF-8");  
			            response.setHeader("Cache-Control", "no-cache");  
			            pw = response.getWriter();  
			            pw.print(getXmlRecord());  
			            pw.flush();  
			        }  
			        catch (Exception e) {  
			            e.printStackTrace();  
			        }  
			        finally {  
			            if (pw != null)  
			                pw.close();  
			        }  
				
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
