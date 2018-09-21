package com.dachen.health.pack.conference.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.constant.CallEnum;
import com.dachen.health.base.constant.DownTaskEnum;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.base.utils.JobTaskUtil;
import com.dachen.health.commons.constants.OrderEnum.OrderStatus;
import com.dachen.health.commons.constants.PackConstants;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.entity.DownTask;
import com.dachen.health.commons.service.DownTaskService;
import com.dachen.health.commons.vo.User;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.conference.dao.CallRecordRepository;
import com.dachen.health.pack.conference.entity.param.CallRecordParam;
import com.dachen.health.pack.conference.entity.param.ConfDetailParam;
import com.dachen.health.pack.conference.entity.po.CallRecord;
import com.dachen.health.pack.conference.entity.po.ConfCall;
import com.dachen.health.pack.conference.entity.vo.CallBusinessVO;
import com.dachen.health.pack.conference.entity.vo.CallListVO;
import com.dachen.health.pack.conference.entity.vo.CallRecordVO;
import com.dachen.health.pack.conference.entity.vo.ConfDetailVO;
import com.dachen.health.pack.conference.entity.vo.ConfListVO;
import com.dachen.health.pack.conference.entity.vo.ConferenceStatusVO;
import com.dachen.health.pack.conference.entity.vo.ParticipantsVO;
import com.dachen.health.pack.conference.service.ICallRecordService;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.ICureRecordService;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.pack.schedule.dao.IScheduleDao;
import com.dachen.health.pack.schedule.entity.po.Schedule;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.manager.IConferenceManager;
import com.dachen.util.MongodbUtil;
import com.dachen.util.RedisUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.mobsms.sdk.MobSmsSdk;
import com.mongodb.DBObject;

@Service
public class CallRecordServiceImpl implements ICallRecordService {
	Logger logger=LoggerFactory.getLogger(getClass());
	@Autowired
	private IOrderService orderService;
	@Autowired
	private IOrderSessionService orderSessionServrice;

	@Autowired
	private IPatientService patientService;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private IConferenceManager conferenceManager;
	@Autowired
	private CallRecordRepository callRecordRepository;
	
	@Resource
    private MobSmsSdk mobSmsSdk;
	@Autowired
	private IBusinessServiceMsg sendMsgService;
	
	@Autowired
	private ICureRecordService CureRecordService;
	
	@Autowired
	private DownTaskService downTaskService;
	
	@Autowired
	private IBaseDataService baseDataService;
	
	@Resource
	private IScheduleDao scheduleDao;

	@Override
	public Map<String, Object> createConference(Integer orderId) {
		// 根据订单查找对应的导医，医生， 患者
		// 然后根生成默认会议，并向数据库里插入一条电话纪录
		// 邀请导医(主持人)和患者加入会议
		if (orderId == null) {
			throw new ServiceException("订单ID不能为空");
		}
		Order order = orderService.getOne(orderId);// 此处只能查到当前患者和医生，没有导医
		if (order == null) {
			throw new ServiceException("找不到对应的订单");
		}
		if(OrderStatus.已支付.getIndex()!=order.getOrderStatus()){
			throw new ServiceException(30005, "非已支付订单不可作此操作！");
		}

		int patientId = order.getPatientId();
		Patient patient = patientService.findByPk(patientId);
		if (patient == null) {
			throw new ServiceException("没有找到对应的患者");
		}

		// 首先创建会议————>邀请导医自己
		Map<String, Object> map = conferenceManager.createConference(CallEnum.CONFERENCE_MAXMEMBER,
				CallEnum.CONFERENCE_DURATION, CallEnum.CONFERENCE_PLAYTOONE);
		String confId = map.get("confId").toString();
		if (confId.equals("0")) {
			throw new ServiceException("创建电话会议失败");
		}
		Map<String, String> smap = new HashMap<String,String>();
		smap.put("uId", ReqUtil.instance.getUserId()+"");
		smap.put("orderId", orderId+"");
		smap.put("status", 100+"");//默认100为创建会议中
		RedisUtil.hmset(confId, smap,CallEnum.CONFERENCE_DURATION*60*1000);
		
		
		
		// 更新订单开始时间
//		orderSessionServrice.beginService(orderId);
//		
//		int uid = ReqUtil.instance.getUserId();
//		// 向纪录表插入纪录
//		CallRecordParam param = new CallRecordParam();
//		param = new CallRecordParam();
//		param.setCallType(CallEnum.CallType.conference.getIndex());
//		param.setCreateTime(new Date().getTime());
//		param.setOrderId(orderId);
//		ConfCall cc = new ConfCall();
//		cc.setConfId(confId);
//		cc.setCreater(String.valueOf(uid));
//		cc.setDuration(CallEnum.CONFERENCE_DURATION);
//		cc.setMaxMember(CallEnum.CONFERENCE_MAXMEMBER);
//		cc.setMediaType(CallEnum.CONFERENCE_MEDIATYPE);
//		cc.setPlayTone(CallEnum.CONFERENCE_PLAYTOONE);
//		cc.setResult(CallEnum.ConferenceStatus.createSuccess.getIndex());
//		param.setConfCall(cc);
//		CallRecordVO crVO = callRecordRepository.saveCallRecord(param);
//
//		List<String> list = new ArrayList<String>();
//		User user = userRepository.getUser(uid);
//		String jsonGuide = "{'nickName':'" + uid + "','number':'" + user.getTelephone() + "','role':'2'}";
//		list.add(jsonGuide);
//
//		// String jsongPatient =
//		// "{'nickName':'"+patientId+"','number':'"+patient.getTelephone()+"','role':'1'}";
//		// list.add(jsongPatient);
//
//		Map<String, Object> temp = conferenceManager.inviteConference(confId, list);
//		// 向数据库会议明细表插入纪录，
//		ConfDetailParam detailParam = new ConfDetailParam();
//		detailParam.setCrId(crVO.getId());
//		detailParam.setMemberId(String.valueOf(uid));
//		detailParam.setTelephone(user.getTelephone());
//		detailParam.setIsNow(true);
//		detailParam.setJoinTime(new Date().getTime());
//		detailParam.setRole(CallEnum.CONFERENCE_ROLE_GUIDE);
//		detailParam.setStatus(CallEnum.CallStatus.inviting.getIndex());
//		callRecordRepository.saveConfDetail(detailParam);// 保存导医记录

//		map.put("invite", temp.get("invite"));
//		map.put("guide", user);
//		map.put("conference", crVO);
		map.put("confId", confId);
		map.put("msg", "会议创建中");
		return map;
	}

	@Override
	public Map<String, Object> inviteMember(String recordId, String userId) {
		// 根据电话纪录ID去查询会议ID
		if (StringUtil.isEmpty(recordId)) {
			throw new ServiceException("电话纪录ID不能为空");
		}
		CallRecordVO vo = callRecordRepository.getCallRecordById(recordId);
		if (vo == null) {
			throw new ServiceException("电话纪录ID有误");
		}
		if (StringUtil.isEmpty(userId)) {
			throw new ServiceException("待邀请人ID不能为空");
		}

		int uid = Integer.parseInt(userId);
		List<String> list = new ArrayList<String>();
		String json = "";
		User user = userRepository.getUser(uid);
		Patient patient = null;
		String telephone = "";
		Integer role = 1;
		Map<String, Object> map = new HashMap<String, Object>();
		if (user == null) {
			patient = patientService.findByPk(uid);
			if (patient == null) {
				throw new ServiceException("待邀请人ID有误");
			} else {
				telephone = patient.getTelephone();
				role = CallEnum.CONFERENCE_ROLE_PATIENT;
				json = "{'nickName':'" + uid + "','number':'" + telephone + "','role':'1'}";
			}
		} else {
			telephone = user.getTelephone();
			if (user.getUserType() == 3) {
				role = CallEnum.CONFERENCE_ROLE_DOCTOR;
				json = "{'nickName':'" + uid + "','number':'" + telephone + "','role':'1'}";
			} else if (user.getUserType() == 6) {
				role = CallEnum.CONFERENCE_ROLE_GUIDE;
				json = "{'nickName':'" + uid + "','number':'" + telephone + "','role':'2'}";
			} else {
				map.put("msg", "用户类型错误");
				return map;
			}

		}
		// json = "{'nickName':'"+uid+"','number':'"+telephone+"','role':'1'}";
		list.add(json);
		map = conferenceManager.inviteConference(vo.getConfCall().getConfId(), list);
		// 向数据库里插入纪录 没有callID
		ConfDetailParam detailParam = new ConfDetailParam();
		detailParam.setCrId(vo.getId());
		detailParam.setMemberId(String.valueOf(uid));
		detailParam.setTelephone(telephone);
		detailParam.setIsNow(false);
		callRecordRepository.updateConfDetailIsNow(detailParam);// 先更新之前的isNow
																// == False
		detailParam.setIsNow(true);
		detailParam.setJoinTime(new Date().getTime());
		detailParam.setRole(role);
		detailParam.setStatus(CallEnum.CallStatus.inviting.getIndex());
		callRecordRepository.saveConfDetail(detailParam);
		return map;
	}

	@Override
	public Map<String, Object> removeConference(String recordId, String userId) {
		if (StringUtil.isEmpty(recordId)) {
			throw new ServiceException("电话纪录ID不能为空");
		}
		CallRecordVO crVO = callRecordRepository.getCallRecordById(recordId);
		if (crVO == null) {
			throw new ServiceException("电话纪录ID有误");
		}
		ConfDetailParam detailParam = new ConfDetailParam();
		detailParam.setCrId(recordId);
		detailParam.setMemberId(userId);
		ConfDetailVO cdVO = callRecordRepository.getConfDetailNow(detailParam);
		if (cdVO == null) {
			throw new ServiceException("退出会议失败");
		}
		return conferenceManager.removeConference(crVO.getConfCall().getConfId(), cdVO.getCallId());
	}

	@Override
	public Map<String, Object> dismissConference(String recordId) {
		if (StringUtil.isEmpty(recordId)) {
			throw new ServiceException("电话纪录ID不能为空");
		}
		CallRecordVO crVO = callRecordRepository.getCallRecordById(recordId);
		if (crVO == null) {
			throw new ServiceException("电话纪录ID有误");
		}
		Map<String,Object> map = conferenceManager.dismissConference(crVO.getConfCall().getConfId());
		if(map.get("code").toString().equals("108101")){
			map.put("code", "0");
			map.put("dismiss", true);
			map.put("reason", "请求成功");
		}
		return map;
	}

	@Override
	public Map<String, Object> deafConference(String recordId, String userId) {
		if (StringUtil.isEmpty(recordId)) {
			throw new ServiceException("电话纪录ID不能为空");
		}
		CallRecordVO crVO = callRecordRepository.getCallRecordById(recordId);
		if (crVO == null) {
			throw new ServiceException("电话纪录ID有误");
		}
		ConfDetailParam detailParam = new ConfDetailParam();
		detailParam.setCrId(recordId);
		detailParam.setMemberId(userId);
		ConfDetailVO cdVO = callRecordRepository.getConfDetailNow(detailParam);
		if (cdVO == null) {
			throw new ServiceException("禁听失败");
		}
		return conferenceManager.deafConference(crVO.getConfCall().getConfId(), cdVO.getCallId());
	}

	@Override
	public Map<String, Object> unDeafConference(String recordId, String userId) {
		if (StringUtil.isEmpty(recordId)) {
			throw new ServiceException("电话纪录ID不能为空");
		}
		CallRecordVO crVO = callRecordRepository.getCallRecordById(recordId);
		if (crVO == null) {
			throw new ServiceException("电话纪录ID有误");
		}
		ConfDetailParam detailParam = new ConfDetailParam();
		detailParam.setCrId(recordId);
		detailParam.setMemberId(userId);
		ConfDetailVO cdVO = callRecordRepository.getConfDetailNow(detailParam);
		if (cdVO == null) {
			throw new ServiceException("禁音失败");
		}
		return conferenceManager.unDeafConference(crVO.getConfCall().getConfId(), cdVO.getCallId());
	}

	@Override
	public Map<String, Object> queryConference(String recordId) {
		if (StringUtil.isEmpty(recordId)) {
			throw new ServiceException("电话纪录ID不能为空");
		}
		CallRecordVO crVO = callRecordRepository.getCallRecordById(recordId);
		if (crVO == null) {
			throw new ServiceException("电话纪录ID有误");
		}
		return conferenceManager.queryConference(crVO.getConfCall().getConfId());
	}

	@Override
	public Map<String, Object> recordConference(String confId) {
		return conferenceManager.recordConference(confId);
	}

	@Override
	public Map<String, Object> stopRecordConference(String confId) {
		return conferenceManager.stopRecordConference(confId);
	}

	@Override
	public Map<String, Object> getStatus(String confId) {
		// 根据纪录Id去电话明细表去电话明细表里各个与会者最新状态,同时调用一次会议查询接口
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, String> map = null;
		if (StringUtil.isEmpty(confId) || (map = RedisUtil.hgetAll(confId)) == null) {
			throw new ServiceException("电话纪录ID不能为空");
		}
		if(map.get("status").equals("100") || map.get("status").equals("37") || map.get("status").equals("38")){
//			throw new ServiceException("会议创建中");
			resultMap.putAll(map);
			return resultMap;
		}
//		else if(map.get("status").equals("37")){
//			throw new ServiceException("余额不足创建三方通话失败" );
//		}else if(map.get("status").equals("38")){
//			throw new ServiceException("未知原因创建三方通话失败" );
//		}
		
//		CallRecordVO crVO = callRecordRepository.getCallRecordById(confId);
		CallRecordVO crVO = callRecordRepository.getCallRecordByConfId(confId);
		if (crVO == null) {
			throw new ServiceException("电话纪录ID有误");
		}
		Integer orderId = crVO.getOrderId();
		Order order = orderService.getOne(orderId);
		if (order == null) {
			throw new ServiceException("订单有误");
		}
		// conferenceManager.queryConference(crVO.getConfCall().getConfId());//调用一次会议查询接口
		Integer doctorId = order.getDoctorId();
		Integer patientId = order.getPatientId();
		Integer guideId = ReqUtil.instance.getUserId();

		List<ParticipantsVO> ppList = new ArrayList<ParticipantsVO>();
		ParticipantsVO temp = null;
		User doctor = userRepository.getUser(doctorId);
		User guide = userRepository.getUser(guideId);
		Patient patient = patientService.findByPk(patientId);
		if (doctor == null || guide == null || patient == null) {
			throw new ServiceException("doctor or guide or patient is null");
		}
		temp = new ParticipantsVO();
		temp.setId(String.valueOf(doctor.getUserId()));
		temp.setName(doctor.getName());
		temp.setHeadImg(doctor.getHeadPicFileName());
		temp.setStatus(0);
		temp.setType(3);
		ppList.add(temp);
		temp = new ParticipantsVO();
		temp.setId(String.valueOf(patient.getId()));
		temp.setName(patient.getUserName());
		temp.setHeadImg(patient.getTopPath());
		temp.setStatus(0);
		temp.setType(1);
		ppList.add(temp);
		temp = new ParticipantsVO();
		temp.setHeadImg(guide.getHeadPicFileName());
		temp.setId(String.valueOf(guide.getUserId()));
		temp.setName(guide.getName());
		temp.setStatus(0);
		temp.setType(2);
		ppList.add(temp);

		ConfDetailParam param = new ConfDetailParam();
		param.setCrId(crVO.getId());
		param.setIsNow(true);
		List<ConfDetailVO> list = callRecordRepository.getConfDetailsByParam(param);
		

		for (int i = 0; i < ppList.size(); i++) {
			ParticipantsVO pVO = ppList.get(i);
			for (ConfDetailVO vo : list) {
				if (vo.getMemberId().equals(pVO.getId())) {
					pVO.setStatus(vo.getStatus());
					break;
				}
			}
		}

		resultMap.put("list", ppList);
		resultMap.put("recordId", crVO.getId());
		return resultMap;
	}

	@Override
	public String getMessage(String confId) {
		CallRecordVO vo = callRecordRepository.getCallRecordByConfId(confId);
		String message = "";
		if (vo == null) {
			return message;
			// 2015-11-25 14:15 三方会议
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		message = sdf.format(new Date(vo.getCreateTime())) + " 三方会议";
		return message;
	}

	@Override
	public void endJoin(DBObject object) throws HttpApiException {
		// 加入成功可知callId，更新数据里isNow数据对应状态为接通中以及并更新callId，如果是导医开始录音，可防止由于某种意外在退出之前就还没有开始录音
		// <request>
		// <event>confJoin</event>
		// <appId>3f3f6b8f1f4143198ee65511dbb77daf</appId>
		// <confId>22222229</confId>
		// <number>13316966281</number>
		// <callId>EF190</callId>
		// <scene>2</scene>
		// <reason>1</reason>
		// <time>2015-10-21 09:29:51</time>
		// </request>
		ConfDetailParam param = new ConfDetailParam();
		String confId = MongodbUtil.getString(object, "confId");
		String telephone = MongodbUtil.getString(object, "number");
		String callId = MongodbUtil.getString(object, "callId");
		Integer status = MongodbUtil.getInteger(object, "scene");

		boolean refuse = false;
		if (status == 1) {
			status = CallEnum.CallStatus.success.getIndex();
		} else {
			refuse = true;
			status = CallEnum.CallStatus.refuse.getIndex();
			// if(status == 1 || status == 2){
			param.setUnJoinTime(new Date().getTime());
			// }
		}
		CallRecordVO crVO = callRecordRepository.getCallRecordByConfId(confId);
		if (crVO == null) {
			return;
		}
		param.setTelephone(telephone);
		param.setCrId(crVO.getId());
		param.setCallId(callId);
		param.setIsNow(true);
		param.setStatus(status);
		callRecordRepository.endConfDetailUpdate(param);
		
		ConfDetailVO joinVO = callRecordRepository.getConfDetailNow(param);
		if (joinVO == null) {
			return;
		}
		
		// 导医接通了才能拨打患者电话，
		if (joinVO.getRole() == CallEnum.CONFERENCE_ROLE_GUIDE && joinVO.getStatus() == CallEnum.CallStatus.success.getIndex()) {
			// 当前用户是管理员，开始录音
			// recordConference(crVO.getConfCall().getConfId());

			// 导医接通（可能会多次拨通），开始拨打患者
			// 先查看数据库里有没有isNow == true 状态的 "患者"，如果没有就拨打，
			// 如果有，查看此时患者状态是否为通话状态（包含拨号，禁听，静音等），如果不是通话中状态则拨打患者。
			// 如果不同步，前端通过轮询获取状态，来判断导医手动拨打()
			ConfDetailParam param2 = new ConfDetailParam();
			param2.setCrId(crVO.getId());
			param2.setRole(CallEnum.CONFERENCE_ROLE_PATIENT);
			param2.setIsNow(true);
			ConfDetailVO patientVO = callRecordRepository.getConfDtailNowByRole(param2);
			if (patientVO == null) {
				// 根据订单反查患者
				Order order = orderService.getOne(crVO.getOrderId());// 此处只能查到当前患者和医生，没有导医
				if (order == null) {
					throw new ServiceException("找不到对应的订单");
				}
				int patientId = order.getPatientId();
				Patient patient = patientService.findByPk(patientId);
				if (patient == null) {
					throw new ServiceException("没有找到对应的患者");
				}
				List<String> list = new ArrayList<String>();
				String jsongPatient = "{'nickName':'" + patientId + "','number':'" + patient.getTelephone()
						+ "','role':'1'}";
				list.add(jsongPatient);
				conferenceManager.inviteConference(confId, list);// 拨打患者
				ConfDetailParam detailParam = new ConfDetailParam();
				detailParam.setCrId(crVO.getId());
				detailParam.setMemberId(String.valueOf(patientId));
				detailParam.setTelephone(patient.getTelephone());
				detailParam.setJoinTime(new Date().getTime());
				detailParam.setRole(CallEnum.CONFERENCE_ROLE_PATIENT);
				detailParam.setStatus(CallEnum.CallStatus.inviting.getIndex());
				detailParam.setIsNow(false);
				callRecordRepository.updateConfDetailIsNow(detailParam);// 先更新之前的isNow
																		// ==
																		// False
				detailParam.setIsNow(true);
				callRecordRepository.saveConfDetail(detailParam);// 保存患者纪录
			} else {
				int patientStatus = patientVO.getStatus();
				if (patientStatus > 40) {// 非通话和邀请状态
					List<String> list = new ArrayList<String>();
					String jsongPatient = "{'nickName':'" + patientVO.getMemberId() + "','number':'"
							+ patientVO.getTelephone() + "','role':'1'}";
					list.add(jsongPatient);
					conferenceManager.inviteConference(confId, list);
					ConfDetailParam detailParam = new ConfDetailParam();
					detailParam.setCrId(crVO.getId());
					detailParam.setMemberId(String.valueOf(patientVO.getMemberId()));
					detailParam.setTelephone(patientVO.getTelephone());
					detailParam.setJoinTime(new Date().getTime());
					detailParam.setRole(CallEnum.CONFERENCE_ROLE_PATIENT);
					detailParam.setStatus(CallEnum.CallStatus.inviting.getIndex());
					detailParam.setIsNow(false);
					callRecordRepository.updateConfDetailIsNow(detailParam);// 先更新之前的isNow
																			// ==
																			// False
					detailParam.setIsNow(true);
					callRecordRepository.saveConfDetail(detailParam);// 保存患者纪录
				}
			}

		} else {
			removeGuide(param,crVO);
			
			if( refuse){
				ConfDetailParam cdp = new ConfDetailParam();
				cdp.setCrId(crVO.getId());
				cdp.setMemberId(joinVO.getMemberId());
				List<ConfDetailVO> list = callRecordRepository.getDetailList(cdp);
				
				if(list.size() == 1 ){
					
					//获取订单的预约时间
					Order order = orderService.getOne(crVO.getOrderId());
					
					User doctor = userRepository.getUser(order.getDoctorId());
					Patient patient = patientService.findByPk(order.getPatientId());
					Schedule schedule = scheduleDao.getByRelationId(crVO.getOrderId()+"");
					
					if(doctor == null || patient == null || schedule ==null){
						return;
					}
					SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH点mm分"); 
					String time = sdf.format(schedule.getScheduleTime());
					//医生电话未接通，给医生发短信
					if(joinVO.getRole() == CallEnum.CONFERENCE_ROLE_DOCTOR ){
//					  0026  {0}医生您好，{1}患者的电话咨询服务的预约时间为{2}，您的电话现在接不通，请您收到短信后尽快联系患者，谢谢！
						OrderSession orderSession = orderSessionServrice.findOneByOrderId(order.getId());
						if(orderSession == null){
							throw new ServiceException("订单会话不存在");
						}
						String[] params = new String[]{doctor.getName(),patient.getUserName(),time};
						final String content = baseDataService.toContent("0026", params) + shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", orderSession.getMsgGroupId(), UserType.doctor.getIndex()));
						mobSmsSdk.send(doctor.getTelephone(), content);
					//患者未接通，发短信给医生和患者
					}else if(joinVO.getRole() == CallEnum.CONFERENCE_ROLE_PATIENT){
//				     0025	{0}医生您好，{1}者的电话预约时间为{2}，因患者电话目前无法接通，稍后客服会再联系患者，给您带来不便非常抱歉。
						String[] params = new String[]{doctor.getName(),patient.getUserName(),time};
						final String content = baseDataService.toContent("0025", params);
						mobSmsSdk.send(doctor.getTelephone(), content);
						
//					0024	您好，{0}医生的电话预约时间为{1}，您的电话接不通，请您尽快拨打客服电话400-618-8886，联系客服10087帮您重新联系医生。
						params = new String[]{doctor.getName(),time};
						final String content1 = baseDataService.toContent("0024", params);
						
						//根据patient里的userId查询该患者是属于哪个平台
						User p_user = userRepository.getUser(patient.getUserId());
						String signature = null;
						if(mobSmsSdk.isBDJL(p_user)){
							signature = BaseConstants.BD_SIGN;
						}else{
							signature = BaseConstants.XG_SIGN;
						}
						mobSmsSdk.send(patient.getTelephone(), content1,signature);
					}
					
				}
				
			}
			
			
		}
		//开始录音
//		beginRecord(crVO);
	}

	@Autowired
	protected ShortUrlComponent shortUrlComponent;

	@Override
	public void endUnJoin(DBObject object) {
		// 退出回调,更新对应会议明细数据
		// <?xml version="1.0"?>
		// <request>
		// <event>confUnjoin</event>
		// <appId>3f3f6b8f1f4143198ee65511dbb77daf</appId>
		// <confId>22222229</confId>
		// <callId>EF18E</callId>
		// <scene>1</scene>
		// <time>2015-10-21 09:30:17</time>
		// </request>
		// 1，会议成员被移除；2、会议成员主动退出会议； 255，未知场景;
		ConfDetailParam param = new ConfDetailParam();
		String confId = MongodbUtil.getString(object, "confId");
		String callId = MongodbUtil.getString(object, "callId");
		Integer status = MongodbUtil.getInteger(object, "scene");

		if (status == 1) {
			status = CallEnum.CallStatus.overByHost.getIndex();
		} else if (status == 2) {
			status = CallEnum.CallStatus.overBySelf.getIndex();
		} else {
			status = CallEnum.CallStatus.overByOthor.getIndex();
		}
		CallRecordVO crVO = callRecordRepository.getCallRecordByConfId(confId);
		if (crVO == null) {
			return;
		}
		param.setCrId(crVO.getId());
		param.setCallId(callId);
		param.setIsNow(true);
		param.setStatus(status);
		param.setUnJoinTime(new Date().getTime());
		callRecordRepository.endUnJoinUpdate(param);
		// 如果是医生或患者挂断，且会议中没有导医，就解散会议（会存不同步引发会议结束问题会议超时解散）
		ConfDetailVO unJoinVO = callRecordRepository.getConfDetailNow(param);
		if (unJoinVO.getRole() != CallEnum.CONFERENCE_ROLE_GUIDE) {
			List<ConfDetailVO> list = callRecordRepository.getConfDetailsByParam(param);
			for (ConfDetailVO temp : list) {
				if (temp.getRole() == CallEnum.CONFERENCE_ROLE_GUIDE && temp.getStatus() > 40) {
					// 导医挂断状态，则解散会议,已解散的会议不会重复解散
					dismissConference(crVO.getId());
					break;
				}
			}
		}
	}

	@Override
	public void endMedia(DBObject object) throws HttpApiException {
		// <?xml version="1.0"?>
		// <request>
		// <event>userMediaCtrRet</event>
		// <appId>579ab45966b14c96a192bab9e14539d6</appId>
		// <confId>66666911</confId>
		// <callId>EF204</callId>
		// <result>0</result>
		// <notifyType>3</notifyType>
		// <time>2015-10-21 10:45:27</time>
		// </request>
		// result Int 必选 操作结果：0、成功；其它值为失败:1、操作执行失败；2 没有主持人；3、服务器繁忙；4、非法操作
		// notifyType Int 必选 1静音；2取消禁音；3禁听；4取消禁听;5成员由来宾变成主席；6 成员由主席变成来宾
		ConfDetailParam param = new ConfDetailParam();
		String confId = MongodbUtil.getString(object, "confId");
		String callId = MongodbUtil.getString(object, "callId");
		Integer status = MongodbUtil.getInteger(object, "result");
		Integer notify = MongodbUtil.getInteger(object, "notifyType");

		CallRecordVO crVO = callRecordRepository.getCallRecordByConfId(confId);
		if (crVO == null) {
			return;
		}
		// 只有媒体操作成功，才更新状态

		if (status == 0) {
			param.setCrId(crVO.getId());
			param.setCallId(callId);
			param.setIsNow(true);
			if (notify == 1) {
				status = CallEnum.MidiaStatus.muteSuccess.getIndex();
			} else if (notify == 2) {
				// status = CallEnum.MidiaStatus.unmuteSuccess.getIndex();
				status = CallEnum.CallStatus.success.getIndex();
			} else if (notify == 3) {
				status = CallEnum.MidiaStatus.deafSuccess.getIndex();
			} else if (notify == 4) {
				// status = CallEnum.MidiaStatus.undeafSuccess.getIndex();
				status = CallEnum.CallStatus.success.getIndex();
			}
			param.setStatus(status);
			callRecordRepository.endUnJoinUpdate(param);
			if (notify == 4) {
				// 取消禁听移除导医
				removeGuide(param,crVO);
			}

		}

	}

	@Override
	public void endStopRecord(DBObject object) {
		// 录音结束后是，保存录音对应url还是把录音down下来保存在某个文件服务器
		// <?xml version="1.0"?>
		// <request>
		// <event>confRecordCtrRet</event>
		// <appId>34ac18eca3254b50b0652546ad7874ce</appId>
		// <confId>22222223</confId>
		// <result>1</result>
		// <notifyType>2</notifyType>
		// <startTime>2015-10-21 11:54:39</startTime>
		// <endTime>2015-10-21 11:54:39</endTime>
		// <recordUrl>url</recordUrl>
		// </request>
		
		
		// notifyType Int   必选   1开始录音；2停止录音
		// result	  Int	必选	操作结果。　0成功 ，其它值为失败。

		Integer notifyType = MongodbUtil.getInteger(object, "notifyType");
		Integer result = MongodbUtil.getInteger(object, "result");
		int code = CallEnum.RecordStatus.recordFail.getIndex();
		String confId = MongodbUtil.getString(object, "confId");
		String recordId = MongodbUtil.getString(object, "recordId");
		
		CallRecordVO crVO = callRecordRepository.getCallRecordByConfId(confId);
		if (crVO == null) {
			return;
		}
		
		String url = null;
		if(notifyType == 1){
			if(result == 0){
				code = CallEnum.RecordStatus.recordIng.getIndex();
			}else{
				code =  CallEnum.RecordStatus.recordFail.getIndex();
			}
		}else if(notifyType == 2){
			if(result == 0){
				code = CallEnum.RecordStatus.recordSuccess.getIndex();
				url = MongodbUtil.getString(object, "recordUrl");
			}else{
				code =  CallEnum.RecordStatus.recordFail.getIndex();
			}
		}else{
			return ;
		}
		
		CallRecordParam param = new CallRecordParam();
		ConfCall cc = new ConfCall();
		cc.setConfId(confId);
		cc.setStatus(code);
		if(!StringUtil.isEmpty(url)){
			param.setRecordUrl(url);
			param.setRecordId(recordId);
		}
		param.setConfCall(cc);
		callRecordRepository.updateRecordStatusByConfId(param);//更新对应的状态
		
		//成功停止录音向任务表里添加纪录 
		if(notifyType == 2 && result == 0 && !StringUtil.isEmpty(url)){
			DownTask dt =  new DownTask();
			Long now = System.currentTimeMillis();
			dt.setCreateTime(now);
			dt.setLastUpdateTime(now);
			dt.setRecordId(recordId);//第三方录音id
			dt.setSourceUrl(url);
			dt.setStatus(DownTaskEnum.DownStatus.recordAdd.getIndex());
			dt.setBussessType(DownTaskEnum.TableToClass.confRecord.getBusessType());
			dt.setOrderId(crVO.getOrderId()+"");
			downTaskService.save(dt);
			JobTaskUtil.endStopRecord(recordId);
		}
	}

	@Override
	public void endQuerry(DBObject object) {
		// <members>
		// <member>
		// <callId>EF191</callId>
		// <nickName>tone20359</nickName>
		// <number>15118002141</number>
		// <role>1</role>
		// <flagMute>1</flagMute>
		// <flagDeaf>1</flagDeaf>
		// </member>
		// </members>
		// members XML节点 必选 Number父节点
		// callId String 可选 呼叫Id
		// nickname String 可选 成员昵称
		// number String 必选 会议成员号码。
		// role Int 必选 1: 普通与会者; 2: 主持人.
		// flagMute Int 必选 是否禁音：1、禁音中；2；未禁音
		// flagDeaf Int 必选 是否禁听：1、禁听中；2；未禁听
		// [{ "request" : " " , "event" : "confCreate" , "appId" :
		// "3f3f6b8f1f4143198ee65511dbb77daf" , "members" : [ { "callId" :
		// "cl3333" , "confId" : "cf2222"} , { "callId" : "cl5555" , "confId" :
		// "cf4444"}]}]
		if (object == null || object.get("members") == null) {
			return;
		}
		List<DBObject> memberList = (List<DBObject>) object.get("members");

		String confId = MongodbUtil.getString(object, "confId");
		CallRecordVO crVO = callRecordRepository.getCallRecordByConfId(confId);
		if (crVO == null) {
			return;
		}
		ConfDetailParam param = new ConfDetailParam();
		for (DBObject obj : memberList) {
			param = new ConfDetailParam();
			param.setCrId(crVO.getId());
			param.setCallId(MongodbUtil.getString(obj, "callId"));
			param.setTelephone(MongodbUtil.getString(obj, "number"));
			Integer muteStatus = MongodbUtil.getInteger(obj, "flagMute");
			Integer deafStatus = MongodbUtil.getInteger(obj, "flagDeaf");
			if (deafStatus == 1) {
				param.setStatus(CallEnum.MidiaStatus.deafSuccess.getIndex());
			} else {
				if (muteStatus == 1) {
					param.setStatus(CallEnum.MidiaStatus.muteSuccess.getIndex());
				} else {
					param.setStatus(CallEnum.CallStatus.success.getIndex());
				}
			}
			callRecordRepository.endConfDetailUpdate(param);// 查询会议更新状态
		}

	}
	
	@Override
	public void endConfCreate(DBObject object){
//		会议创建通知
//		<request>
//		  <event>confCreate</event>
//		  <appId>3f3f6b8f1f4143198ee65511dbb77daf</appId>
//		  <confId>22222223</confId>
//		  <result>0</result>
//		  <time>2015-10-21 09:23:05</time>
//		  <maxMember>3</maxMember>
//		  <mediaType>1</mediaType>
//		  <playTone>1</playTone>
//		  <duration>30</duration>
//		</request>
		Integer result = MongodbUtil.getInteger(object, "result");
		String confId = MongodbUtil.getString(object, "confId");
		Map<String, String> map =RedisUtil.hgetAll(confId);
		
		if(result == 0){
			// TODO Auto-generated 需要判断状态来拨打对导医，并保存相关纪录
			if(map == null){
				throw new ServiceException("三方通话ID异常");
			}
			map.put("status", "0");
			if(StringUtil.isEmpty(map.get("orderId"))){
				RedisUtil.hmset(confId, map,5*60*1000);
				return;
			}
//			map.put("status", "0");
			int orderId =Integer.parseInt(map.get("orderId")) ;
			int uId = Integer.parseInt(map.get("uId"));
//			// 更新订单开始时间
//			orderSessionServrice.beginService(orderId);
			// 向纪录表插入纪录
			CallRecord param = new CallRecord();
			param.setCallType(CallEnum.CallType.conference.getIndex());
			param.setCreateTime(new Date().getTime());
			param.setOrderId(orderId);
			ConfCall cc = new ConfCall();
			cc.setConfId(confId);
			cc.setCreater(String.valueOf(uId));
			cc.setDuration(CallEnum.CONFERENCE_DURATION);
			cc.setMaxMember(CallEnum.CONFERENCE_MAXMEMBER);
			cc.setMediaType(CallEnum.CONFERENCE_MEDIATYPE);
			cc.setPlayTone(CallEnum.CONFERENCE_PLAYTOONE);
			cc.setResult(CallEnum.ConferenceStatus.createSuccess.getIndex());
			param.setConfCall(cc);
			CallRecord crVO = callRecordRepository.saveCallRecord(param);

			List<String> list = new ArrayList<String>();
			User user = userRepository.getUser(uId);
			String jsonGuide = "{'nickName':'" + uId + "','number':'" + user.getTelephone() + "','role':'2'}";
			list.add(jsonGuide);
			Map<String, Object> temp = conferenceManager.inviteConference(confId, list);
			// 向数据库会议明细表插入纪录，
			ConfDetailParam detailParam = new ConfDetailParam();
			detailParam.setCrId(crVO.getId());
			detailParam.setMemberId(String.valueOf(uId));
			detailParam.setTelephone(user.getTelephone());
			detailParam.setIsNow(true);
			detailParam.setJoinTime(new Date().getTime());
			detailParam.setRole(CallEnum.CONFERENCE_ROLE_GUIDE);
			detailParam.setStatus(CallEnum.CallStatus.inviting.getIndex());
			callRecordRepository.saveConfDetail(detailParam);// 保存导医记录
		}else if(result == 37){
			map.put("status", "37");
			if(StringUtil.isEmpty(map.get("orderId"))){
				RedisUtil.hmset(confId, map,5*60*1000);
				return;
			}
		}else{
			map.put("status", "38");
			if(StringUtil.isEmpty(map.get("orderId"))){
				RedisUtil.hmset(confId, map,5*60*1000);
				return;
			}
		}
		RedisUtil.hmset(confId, map,CallEnum.CONFERENCE_DURATION*60*1000);

	}

	@Override
	public void endDismiss(DBObject object) throws HttpApiException {
		// 何存会议结束时间
		// <?xml version="1.0"?>
		// <request>
		// <event>confDismiss</event>
		// <appId>3f3f6b8f1f4143198ee65511dbb77daf</appId>
		// <confId>22222223</confId>
		// <scene>1</scene>
		// <time>2015-10-21 09:26:42</time>
		// </request>
		// scene Int 可选 通知场景：0：默认场景;1：接口调用；2：会议超时解散；3：余额不足；4：其它原因
		String confId = MongodbUtil.getString(object, "confId");
		Integer result = MongodbUtil.getInteger(object, "scene");
		CallRecordVO crVO = callRecordRepository.getCallRecordByConfId(confId);
		if (crVO == null) {
			return;
		}
		//会议成功结束，发短信
		CallRecordParam	param2 = new CallRecordParam();
		param2.setId(crVO.getId());
		List<ConfDetailVO> confDetailList = callRecordRepository.getConfDetailByCrId(param2).get(crVO.getId());// 获取所有会议会议纪录详情（避免在循环里对数据库操作）
		boolean callRecordStatus = status(confDetailList);
		 
		CallRecordParam param = new CallRecordParam();
		ConfCall cc = new ConfCall();
		cc.setConfId(confId);
		if (result == 0) {
			result = CallEnum.ConferenceStatus.overBydefault.getIndex();
		} else if (result == 1) {
			result = CallEnum.ConferenceStatus.overByInterface.getIndex();
		} else if (result == 2) {
			result = CallEnum.ConferenceStatus.overByTimeOut.getIndex();
		} else if (result == 3) {
			result = CallEnum.ConferenceStatus.overByNoMoney.getIndex();
		} else {
			result = CallEnum.ConferenceStatus.overByOther.getIndex();
		}
		cc.setResult(result);
		param.setConfCall(cc);
		param.setEndTime(new Date().getTime());
		param.setRecordStatus(callRecordStatus==true?1:2);
		callRecordRepository.updateCallRecordByConfId(param);
		
		
		 if(confDetailList != null && !confDetailList.isEmpty()){
			 String doctorNum = "";
			 String patientNum = "";
			 
			 User doctor = null;
			 Patient patient = null;
			 for(ConfDetailVO vo :confDetailList){
				 
				 if(doctor != null && patient != null){
					 break;
				 }
				 
				 if(vo.getRole() == CallEnum.CONFERENCE_ROLE_DOCTOR && StringUtil.isEmpty(doctorNum)){
					 doctorNum = vo.getTelephone();
					 doctor = userRepository.getUser(Integer.parseInt(vo.getMemberId()));
				 }else if(vo.getRole() == CallEnum.CONFERENCE_ROLE_PATIENT && StringUtil.isEmpty(patientNum) ){
					 patientNum = vo.getTelephone();
					 patient = patientService.findByPk(Integer.parseInt(vo.getMemberId()));
				 }
			 }
			
			if(callRecordStatus){
				 if(doctor == null || patient == null){
					 throw new ServiceException("患者或者医生没找到");
				 }
//				 医生：您与患者***的电话咨询订单已结束，请您尽快填写本次订单咨询记录，咨询记录结束后订单款项才会结算，感谢您本次服务。
//				 患者：您与医生***的电话咨询订单已结束，如果对本次订单有疑问，请拨打平台客服电话：400-0000-0000。医生之后会为您填写本次订单咨询记录，请稍后查看。
//
//				 医生推送：标题：电话咨询服务成功
//				 内容：您与患者***的电话咨询已结束，点击填写咨询记录。
				 //发短信
//				 String doctorContent = "您与患者"+ patient.getUserName() +"的电话咨询订单已结束，请您尽快填写本次订单咨询记录，咨询记录结束后订单款项才会结算，感谢您本次服务。";
//				 String patientContent = "您与医生"+ doctor.getUsername()+"的电话咨询订单已结束，如果对本次订单有疑问，请拨打平台客服电话：400-0000-0000。医生之后会为您填写本次订单咨询记录，请稍后查看。";
//				 
//				 mobSmsSdk.send(doctorNum, doctorContent);
//				 mobSmsSdk.send(patientNum, patientContent);
				 
				String[]params=new String[]{patient.getUserName()};
				final String content1 = baseDataService.toContent("0054", params);
				mobSmsSdk.send(doctorNum, content1);
				
				
				
				params=new String[]{doctor.getName()};
				final String content2 = baseDataService.toContent("0055", params);
				mobSmsSdk.send(patientNum, content2);
				 //发通知
//				   医生推送：您与患者***的电话咨询已结束，点击填写咨询记录。
				 List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
				 ImgTextMsg imgTextMsg = new ImgTextMsg();
				 imgTextMsg.setStyle(7);
				 imgTextMsg.setTitle(UserChangeTypeEnum.DOCOTR_OUT_CONFERENCE.getAlias());
				 imgTextMsg.setContent("您与患者"+ patient.getUserName() +"的电话咨询已结束。");
				 imgTextMsg.setTime(System.currentTimeMillis());
				 Map<String, Object> imParam = new HashMap<String, Object>();
				 imParam.put("bizType",13);
				 imParam.put("bizId",crVO.getOrderId());
	             imgTextMsg.setParam(imParam);
				 mpt.add(imgTextMsg);
				 Map<String, Object> msg = new HashMap<String, Object>();
				 msg.put("pushTip", "您与患者"+ patient.getUserName() +"的电话咨询已结束。");
				 sendMsgService.sendTextMsg(doctor.getUserId().toString(), SysGroupEnum.TODO_NOTIFY, mpt, msg);
			 }
		 }
	}

	@Override
	public Map<String, Object> getCallRecordByParam(CallRecordParam param) {
		// 返回以电话类型键，对应电话纪录列表为值的Map集合
		Map<String, Object> map = new HashMap<String, Object>();
		if (param.getOrderId() == null) {
			return map;
		}

		List<CallRecordVO> list = callRecordRepository.getCallRecordByOrderId(param);// 获取所有类型的通话纪录
		Map<String, List<ConfDetailVO>> cdMap = callRecordRepository.getConfDetailByCrId(new CallRecordParam());// 获取所有会议会议纪录详情

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, List<CallRecordVO>> mapList = new LinkedHashMap<String, List<CallRecordVO>>();
		for (CallRecordVO temp : list) {
			long start = temp.getCreateTime();
			Date tt = new Date(start);
			String outKey = sdf.format(tt);
			List<CallRecordVO> outList = mapList.get(outKey);
			if (outList == null) {
				outList = new ArrayList<CallRecordVO>();
			}

			int type = temp.getCallType();
			if (type == CallEnum.CallType.conference.getIndex()) {
				// 查对应的电话详情
				List<ConfDetailVO> cdList = cdMap.get(temp.getId());
				if (cdList != null && !cdList.isEmpty()) {
					// //分类统计（1，医生没有任何接通纪录，2患者没有任何接通纪录）
					// List<ConfDetailVO> lMap = new ArrayList<ConfDetailVO>();
					//
					// Map<Integer,List<ConfDetailVO>> llMap = new
					// LinkedHashMap<Integer,List<ConfDetailVO>>();
					// for(ConfDetailVO cdVO :cdList){
					// Integer key = cdVO.getRole();
					// List<ConfDetailVO> cdvoList = llMap.get(key);
					// if(cdvoList == null){
					// cdvoList = new ArrayList<ConfDetailVO>();
					// }
					// cdvoList.add(cdVO);
					// llMap.put(key, cdvoList);
					// }
					// Iterator<Entry<Integer,List<ConfDetailVO>>> iterator =
					// llMap.entrySet().iterator();
					// while(iterator.hasNext()){
					// Entry<Integer,List<ConfDetailVO>> entry =
					// iterator.next();
					// ConfListVO cf = new ConfListVO();
					// cf.setType(String.valueOf(entry.getKey()));
					// cf.setList(entry.getValue());
					// lMap.add(cf);
					// }
					temp.getConfCall().setList(cdList);
				}
			}
			outList.add(temp);
			mapList.put(outKey, outList);
		}
		List<CallListVO> clList = new ArrayList<CallListVO>();
		Iterator<Entry<String, List<CallRecordVO>>> iterator = mapList.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, List<CallRecordVO>> entry = iterator.next();
			CallListVO vo = new CallListVO();
			vo.setDate(entry.getKey());
			vo.setList(entry.getValue());
			clList.add(vo);
		}
		map.put("dataList", clList);
		return map;
	}

	@Override
	public CallRecordVO getLastConference(CallRecordParam param) {
		if (param.getOrderId() == null) {
			throw new ServiceException("订单ID不能为空");
		}
		// 查询默认会议时长内最新会议
		ConfCall cc = new ConfCall();
		cc.setCreater(String.valueOf(ReqUtil.instance.getUserId()));
		cc.setResult(CallEnum.ConferenceStatus.overBydefault.getIndex());
		param.setConfCall(cc);
		param.setDuration(Long.parseLong(CallEnum.CONFERENCE_DURATION + ""));
		return callRecordRepository.getConference(param);
	}

	@Override
	public Integer getStatusByOrderId(String orderId) {
		int result = 0;
		CallRecordParam param = new CallRecordParam();
		param.setOrderId(Integer.parseInt(orderId));
		CallRecordVO vo = callRecordRepository.getConference(param);
		if (StringUtil.isEmpty(vo.getId())) {
			result = 0;// 没有拨打纪录
		} else {
			result = 2;
			if(vo.getCallType().intValue() == CallEnum.CallType.conference.getIndex()){//只有拨打类型为4 的时候才会有cc.confCall
				ConfCall cc = vo.getConfCall();
				if (cc!=null&&cc.getResult() < CallEnum.ConferenceStatus.overBydefault.getIndex()) {
					result = 3;// 服务中
				}else{
					param = new CallRecordParam();
					param.setId(vo.getId());
					List<ConfDetailVO> list = callRecordRepository.getConfDetailByCrId(param).get(vo.getId());
					if (list != null && status(list)) {
						result=1;
					} 
				}
			} else if(vo.getCallType().intValue() == CallEnum.CallType.doctorToPatient.getIndex()){
				// 有拨打记录判断成功与失败
				if(!StringUtil.isEmpty(vo.getFrom()) && !StringUtil.isEmpty(vo.getTo()) && vo.getEndTime() > vo.getCreateTime()){
					result = 1;
				}
			}
		}
		return result;
	}

	/**
	 * 移除导医
	 * 录音
	 * 订单开始服务
	 * @param param
	 * @param vo
	 */
	private void removeGuide(ConfDetailParam param,CallRecordVO vo) throws HttpApiException {
		List<ConfDetailVO> cdList = callRecordRepository.getConfDetailsByParam(param);
		// System.err.println("remove===>size: "+cdList.size());
		if (cdList.size() < 3) {
			return;
		}
		int i = 0;
		String userId = null;

		for (ConfDetailVO cdVO : cdList) {
			if (cdVO.getStatus() == CallEnum.CallStatus.success.getIndex()) {
				i++;
			}
			if (cdVO.getRole() == CallEnum.CONFERENCE_ROLE_GUIDE) {
				userId = cdVO.getMemberId();
			}
		}
		if (i >= 3 && !StringUtil.isEmpty(userId)) {
			//开始录音
			beginRecord(vo);
			removeConference(param.getCrId(), userId);
			// 更新订单开始时间
			orderSessionServrice.beginService(vo.getOrderId());
			param.setMemberId(userId);
		}

	}

	@Override
	public List<ConfListVO> getRecordStatus(CallRecordParam param) {
		List<ConfListVO> statuslist = new ArrayList<ConfListVO>();
		if (param.getOrderId() == null) {
			return statuslist;
		}
		List<CallRecordVO> list = callRecordRepository.getCallRecordByOrderId(param);// 获取所有类型的通话纪录
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, List<CallRecordVO>> mapList = new LinkedHashMap<String, List<CallRecordVO>>();
		// 按日期分组
		for (CallRecordVO temp : list) {
			long start = temp.getCreateTime();
			String outKey = sdf.format(start);
			List<CallRecordVO> outList = mapList.get(outKey);
			if (outList == null) {
				outList = new ArrayList<CallRecordVO>();
			}
			outList.add(temp);
			mapList.put(outKey, outList);
		}
		// 把分组后的数据重新封装
//		Map<String, List<ConfDetailVO>> cdMap = callRecordRepository.getConfDetailByCrId(new CallRecordParam());// 获取所有会议会议纪录详情（避免在循环里对数据库操作）
		Iterator<Entry<String, List<CallRecordVO>>> iterator = mapList.entrySet().iterator();
		while (iterator.hasNext()) {// 迭代每天
			Entry<String, List<CallRecordVO>> entry = iterator.next();
			ConfListVO listVO = new ConfListVO();
			listVO.setTime(entry.getKey());
			List<ConferenceStatusVO> temp = new ArrayList<ConferenceStatusVO>();
			boolean next = false;
			for (CallRecordVO vo : entry.getValue()) {// 迭代每天的所有纪录
				// 每次会议
				boolean status = false;
				String confId = "0";
				// 成功状态 导医退出后，医生和患者有正常通话，也就是医生和患者退出(不包含拒接退出)时间在导医之后，否则就失败。
				if (vo.getCallType() == CallEnum.CallType.conference.getIndex()) {
					// 查对应的电话详情
					confId = vo.getConfCall().getConfId();
					CallRecordParam cdParam = new CallRecordParam();
					cdParam.setId(vo.getId());
					Map<String, List<ConfDetailVO>> cdMap = callRecordRepository.getConfDetailByCrId(cdParam);
					List<ConfDetailVO> cdList = cdMap.get(vo.getId());
					if (cdList != null ) {
						status = status(cdList);
					}
				}else if(vo.getCallType() == CallEnum.CallType.doctorToPatient.getIndex()){
					//通话中的不显示
					if((vo.getEndTime() == -1 || vo.getEndTime() == 0) && (vo.getCreateTime() == 0)){
						logger.info(vo.getCallid()+"还在通话中,不显示在前端"+vo.getId());
						next = true;
						continue;
					}
					if(!StringUtil.isEmpty(vo.getFrom()) && !StringUtil.isEmpty(vo.getTo()) && vo.getEndTime() > vo.getCreateTime()){
						status = true;
					}
					confId = vo.getCallid();
				}
				ConferenceStatusVO cs = new ConferenceStatusVO();
				cs.setConfId(confId);
				cs.setCreateTime(vo.getCreateTime());
				if (vo.getEndTime() == -1 || vo.getEndTime() == 0) {
					cs.setEndTime(System.currentTimeMillis());
				} else {
					cs.setEndTime(vo.getEndTime());
				}
				cs.setStatus(status);
				cs.setRecordId(vo.getRecordId());
				cs.setCallType(vo.getCallType()+"");
				temp.add(cs);
			}
			if(next){
				continue;
			}
			listVO.setList(temp);
			statuslist.add(listVO);
		}
		return statuslist;
	}

	@Override
	public List<CallBusinessVO> getOrderInServiceByGuide(CallRecordParam param) {
		if (StringUtil.isEmpty(param.getConfCall().getCreater())) {
			throw new ServiceException("导医ID有误");
		}
		List<CallBusinessVO> volist = new ArrayList<CallBusinessVO>();
		List<CallRecordVO> list = callRecordRepository.getConferenceInServiceByGuide(param);
		CallBusinessVO vo = null;
		if (!list.isEmpty()) {
			for (CallRecordVO temp : list) {
				vo = new CallBusinessVO();
				vo.setCrId(temp.getId());
				vo.setOrderId(temp.getOrderId());
				volist.add(vo);
			}
		}
		return volist;
	}
	
	@Override
	public void sendNoConsultNotice() throws HttpApiException {
		//每个小时触发一次，刚好在会议结束且成功状态后的48小时这个点上且还没有填写咨询纪录的刚需要发送通知
		//先查会议表（会议结束且），然后根据会议表查会议详情，
		logger.info("检查咨询纪录发送通开始-----");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		long query = calendar.getTime().getTime() -(CallEnum.CONFERENCE_NOTICE_INTERVAL+2)*60*60*1000;
		CallRecordParam param = new CallRecordParam();
		ConfCall cc = new ConfCall();
		cc.setResult(CallEnum.ConferenceStatus.overBydefault.getIndex());
		param.setConfCall(cc);
		param.setEndTime(query);
		List<CallRecordVO> list = callRecordRepository.getCallToSendNotice(param);
		Map<String ,List<ConfDetailVO>> map = callRecordRepository.getConfDetailByCrId(param);
		
		int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
		for(CallRecordVO vo :list){
			if(CureRecordService.findByOrderId(vo.getOrderId()).isEmpty()){//根据订单Id去查对应的状态如果没有填写则发送没
				long endTime = vo.getEndTime() + CallEnum.CONFERENCE_NOTICE_INTERVAL*60*60*1000;
				calendar.setTime(new Date(endTime));
				int endHour = calendar.get(Calendar.HOUR_OF_DAY);
				List<ConfDetailVO> temp = map.get(vo.getId());
				if(temp != null && !temp.isEmpty()){
					if(status(temp) && nowHour == endHour){//通话成功  且48小时 
						 User guide = userRepository.getUser(Integer.parseInt(vo.getConfCall().getCreater()));
						 User doctor = null;
						 Patient patient = null;
						 for(ConfDetailVO cdVO :temp){
							 if(doctor != null && patient != null){
								 break;
							 }
							 if(cdVO.getRole() == CallEnum.CONFERENCE_ROLE_DOCTOR && doctor == null){
								 doctor = userRepository.getUser(Integer.parseInt(cdVO.getMemberId()));
							 }else if(cdVO.getRole() == CallEnum.CONFERENCE_ROLE_PATIENT && patient == null ){
								 patient = patientService.findByPk(Integer.parseInt(cdVO.getMemberId()));
							 }
						 }
						 if(guide == null || doctor == null || patient == null){
							 continue;
						 }
						 // 后48小时医生没有填写咨询记录，给导医发推送，点击到该订单填写页。文案：***患者与***医生的电话咨询已结束48小时，医生未填写咨询记录，请您尽快处理。
						 List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
						 ImgTextMsg imgTextMsg = new ImgTextMsg();
						 imgTextMsg.setStyle(7);
						 imgTextMsg.setTime(System.currentTimeMillis());
						 imgTextMsg.setTitle(UserChangeTypeEnum.GUIDE_WRTIE_CONSULT.getAlias());
						 imgTextMsg.setContent(patient.getUserName() +"患者与"+ doctor.getUsername() + "医生的电话咨询已结束48小时，医生未填写咨询记录，请您尽快处理。");
						 Map<String, Object> imParam = new HashMap<String, Object>();
						 imParam.put("bizType",14);
						 imParam.put("bizId",vo.getOrderId());
			             imgTextMsg.setParam(imParam);
						 mpt.add(imgTextMsg);
						 Map<String, Object> msg = new HashMap<String, Object>();
						 msg.put("pushTip", "电话咨询订单48小时未填写咨询记录，请尽快联系医生填写。");
						 sendMsgService.sendTextMsg(guide.getUserId().toString(), SysGroupEnum.TODO_NOTIFY, mpt, msg);
						 logger.info("-----发送通知-----toUser:" + guide.getUserId() + "OrderId:" + vo.getOrderId());
					}
				}
			}
			
		}
		logger.info("检查咨询纪录发送通关闭-----");
	}
	
	
	private boolean status(List<ConfDetailVO> cdList){
		boolean status = false;
		long guideUnjoinTime = 0;
		long doctorUnjoinTime = 0;
		long patientUnjoinTime = 0;
		for (ConfDetailVO cdVO : cdList) {
			if(cdVO.getIsNow()){
				if (cdVO.getRole() == CallEnum.CONFERENCE_ROLE_GUIDE) {
					guideUnjoinTime = cdVO.getUnJoinTime();// 获取导医最后退出时间
				} else if (cdVO.getRole() == CallEnum.CONFERENCE_ROLE_DOCTOR) {
					doctorUnjoinTime = cdVO.getUnJoinTime();// 获取导医最后退出时间
					if(doctorUnjoinTime == 0 && cdVO.getStatus().intValue() == CallEnum.CallStatus.success.getIndex()){
						doctorUnjoinTime = System.currentTimeMillis();
					}
				} else if (cdVO.getRole() == CallEnum.CONFERENCE_ROLE_PATIENT) {
					patientUnjoinTime = cdVO.getUnJoinTime();// 获取患者最后退出时间
					if(patientUnjoinTime == 0 && cdVO.getStatus().intValue() == CallEnum.CallStatus.success.getIndex()){
						patientUnjoinTime = System.currentTimeMillis();
					}
				}
			}
		}
		
		if(guideUnjoinTime == 0){
			return false;
		}
		
		if (patientUnjoinTime > guideUnjoinTime && doctorUnjoinTime > guideUnjoinTime) {
			status = true;
		}
		return status;
	}
	
	public boolean beginRecord(CallRecordVO vo){
		boolean result = false;
//		至少有两个人才可以录音，然后录音开始后，只要有人在就不会停止录音，只要没人了就会自动停止录音
//		没主持人 操作录音的时候  会返回非法操作~！ 录音中主持人退出后，不影响正常录音，不需要重新设置主持人
		
		if(vo == null || vo.getConfCall() == null ){
			return result;
		}
		if(vo.getConfCall().getStatus() == null || (vo.getConfCall().getStatus() == CallEnum.RecordStatus.recordFail.getIndex())){//录音
			Map<String,Object> map = conferenceManager.recordConference(vo.getConfCall().getConfId());
			if(map.get("code").equals("0")){
				result = true;
			}
		}
		return result;
	}

	@Override
	public List<CallRecordVO> getRecordByOrderId(Integer orderId) {
		// TODO Auto-generated method stub
		return callRecordRepository.getVoiceByOrderId(orderId);
	}
}
