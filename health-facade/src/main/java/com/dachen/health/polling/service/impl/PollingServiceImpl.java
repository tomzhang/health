package com.dachen.health.polling.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.constants.UserSession;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.health.pack.guide.dao.IGuideDAO;
import com.dachen.health.pack.guide.entity.OrderCache;
import com.dachen.health.pack.guide.entity.ServiceStateEnum;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO;
import com.dachen.health.pack.guide.service.IGuideService;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.mapper.OrderSessionMapper;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.OrderSessionExample;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.polling.service.IPollingService;
import com.dachen.im.server.constant.SysConstant;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.request.MsgListRequestMessage;
import com.dachen.im.server.data.response.MsgListResult;
import com.dachen.im.server.data.response.SendMsgResult;
import com.dachen.util.ReqUtil;

@Service(PollingServiceImpl.BEAN_ID)
public class PollingServiceImpl implements IPollingService{
	
	Logger logger=LoggerFactory.getLogger(getClass());

	public static final String BEAN_ID = "PollingServiceImpl";
	
	@Resource
	private OrderMapper orderMapper;
	
	@Resource
	private IMsgService msgService;
	@Resource
	private OrderSessionMapper orderSessionMapper;
	@Autowired
	private IOrderService orderService;
//	@Autowired
//	private ICheckInService checkInService;
	
	@Resource
	private UserRepository userRepository;
	
	@Resource
	private IOrderSessionService orderSessionService;
	
	@Autowired
	private IGuideDAO iGuideDAO;
	@Autowired
	private IGuideService guideService;
	
//	@Autowired
//	private IGroupDoctorDao groupDoctorDao;
	
//	@Autowired
//	private PubAccountService pubAccountService;
	
	@Autowired
    private IBusinessServiceMsg businessServiceMsg;
	/**
	 * 业务轮询接口
	 * 1、获取会话列表
	 */
//	public BusinessVO getBusiness(BusinessParam buisnessParam) {
		/*if(StringUtils.isEmpty(buisnessParam.getUserId()))
		{
			buisnessParam.setUserId(String.valueOf(ReqUtil.instance.getUser().getUserId()));
		}
		GroupListRequestMessage groupListParam = new GroupListRequestMessage();
		groupListParam.setCnt(buisnessParam.getCnt());
		groupListParam.setTs(buisnessParam.getTs());
		groupListParam.setUserId(buisnessParam.getUserId());
		//获取会话列表
		MsgGroupVO msgGroupVO=getGroupList(groupListParam);
		BusinessVO businessVO=new BusinessVO();
		businessVO.setMsgGroupVO(msgGroupVO);
		
		if(buisnessParam.getNeedEvent()==1)
		{
			EventListRequest requestMsg = new EventListRequest();
			requestMsg.setUserId(buisnessParam.getUserId());
			requestMsg.setTs(buisnessParam.getTs());
			EventResult eventResult= getEventList(requestMsg);
			businessVO.setEvent(eventResult);
			
		}*/
//		Map business = getBusinessMap(buisnessParam);
//		businessVO.setBusiness(business);
//		return null;
//	}
	
	/**
	 * 获取消息
	 * 
	 */
	public MsgListResult getMsg(MsgListRequestMessage msgGetParam) {
		if(StringUtils.isEmpty(msgGetParam.getUserId()))
		{
			msgGetParam.setUserId(String.valueOf(ReqUtil.instance.getUser().getUserId()));
		}
		JSON imGetMsg = MsgHelper.msgList(msgGetParam);
		String gid=msgGetParam.getGroupId();
		
		Map<String, Object> bussiness=new HashMap<String, Object>();
		if(gid.startsWith(SysConstant.GUIDE_DOCTOR_GROUP_PREFIX))
		{
			setGuideOrderRelevant(gid,bussiness);
		}
			
		MsgListResult result = JSON.toJavaObject(imGetMsg,MsgListResult.class);
		result.setBussiness(bussiness);
//		List<MessageVO> msgList =result.getMsgList();
//		if(msgList!=null && msgList.size()>0)
//		{
//			String userId = null;
//			for(MessageVO msg:msgList)
//			{
//				userId = msg.getFromUserId();
//				if(userId!=null && !SysConstant.isSysUser(userId)){
//					UserSession user = ReqUtil.instance.getUser(Integer.valueOf(userId));
//					msg.setUserName(user.getName());
//					msg.setUpic(user.getHeadImgPath());
//				}
//			}
//		}
		return result;
	}

	
	/**
	 * 获取会话列表
	 * 1、对返回的会话列表进行分组，将原有的会话按照会话类型分为不同的组
	 *    DOCTOR("3_3","医生_医生")-->doctorlisty
	 *    DOC_PATIENT("1_3","患者_医生")  如果当前用户是医生，则放入patientlist，如果当前用户是患者，则放入doctorlist
	 *    
	 */
	/*private MsgGroupVO getGroupList(GroupListRequestMessage msgGroupParam) {
		UserSession currentUser = null;
		if(StringUtils.isEmpty(msgGroupParam.getUserId()))
		{
			currentUser =ReqUtil.instance.getUser();
			msgGroupParam.setUserId(String.valueOf(currentUser.getUserId()));
		}
		else
		{
			currentUser = ReqUtil.instance.getUser(Integer.valueOf(msgGroupParam.getUserId()));
		}
		if(currentUser==null)
		{
			throw new ServiceException("找不到当前用户");
		}
		int userType = currentUser.getUserType();
		
		MsgGroupVO msgGroupVO=new MsgGroupVO();
		JSON map=MsgHelper.groupList(msgGroupParam); 
		GroupListResult msgGroupList = JSON.toJavaObject(map,GroupListResult.class);
		
		MsgGroupList patientlist=new MsgGroupList();
		MsgGroupList doctorlist=new MsgGroupList();
		MsgGroupList assistantlist=new MsgGroupList();
		MsgGroupList notificationGroup=new MsgGroupList();
		MsgGroupList customerGroup=new MsgGroupList();
		
		msgGroupVO.setMore(msgGroupList.isMore());
		msgGroupVO.setTs(msgGroupList.getTs());
		msgGroupVO.setTms(msgGroupList.getTms());
		msgGroupVO.setDoctorlist(doctorlist);
		msgGroupVO.setPatientlist(patientlist);
		msgGroupVO.setAssistantlist(assistantlist);
		msgGroupVO.setNotificationGroup(notificationGroup);
		msgGroupVO.setCustomerGroup(customerGroup);
		if(msgGroupList.getList()!=null )
		{
			String groupId = null;
			for (MsgGroupDetail msgGroupDetail : msgGroupList.getList()) {
				//消息类会话列表
				List userIds=msgGroupDetail.getUserIds();
				groupId = msgGroupDetail.getGid();
				msgGroupDetail.setUserList(IMUtils.getGroupUserList(userIds));
				msgGroupDetail.setUserIds(null);
				MessageVO lastMsg = msgGroupDetail.getLastMsg();
				if(SysConstant.isPubGroup(groupId)){
					// 公共号
					GroupInfo group = pubAccountService.getGroupInfo(groupId,msgGroupParam.getUserId(),true);
					if(group==null)
					{
						continue;
					}
					msgGroupDetail.setGname(group.getGname());
					msgGroupDetail.setGpic(group.getGpic());
					msgGroupDetail.setRtype(group.getRtype());
					msgGroupDetail.setType(group.getType());
					msgGroupDetail.setCanReply(group.isCanReply());
					msgGroupDetail.setUserList(group.getUserList());
					if(group.getType()==GroupTypeEnum.PUBLIC.getValue())
					{
						String fromUserId = lastMsg.getFromUserId();
						PubPO pub = null;
						if(SysConstant.isPubGroup(fromUserId))
						{
							//发送者为公共号
							pub = pubAccountService.getPub(fromUserId);
						}
						String pubName = (pub!=null?pub.getNickName():group.getGname());
						if(!StringUtils.isEmpty(pubName))
						{
							lastMsg.setContent(pubName+":"+lastMsg.getContent());
						}
					}
					
					customerGroup.setUr(customerGroup.getUr()+msgGroupDetail.getUnread());
					customerGroup.getList().add(msgGroupDetail);
					continue;
				}
				
				String gpic = PropertiesUtil.addUrlPrefix(msgGroupDetail.getGpic());
				msgGroupDetail.setGpic(gpic);
				if(msgGroupDetail.getRtype()==null)
				{
					continue;
				}
				if(msgGroupDetail.getUserList() !=null && msgGroupDetail.getUserList().size()==2)
				{
					//双人会话组，显示对方的名称和图片
					for(GroupUserInfo groupUserInfo:msgGroupDetail.getUserList())
					{
						if(msgGroupParam.getUserId().equals(groupUserInfo.getId()))
						{
							continue;
						}
						if(StringUtils.isEmpty(msgGroupDetail.getGname()) 
								|| msgGroupDetail.getType()==GroupTypeEnum.DOUBLE.getValue())
						{
							msgGroupDetail.setGname(groupUserInfo.getName());
						}
						if(StringUtils.isEmpty(msgGroupDetail.getGpic()))
						{
							msgGroupDetail.setGpic(groupUserInfo.getPic());
						}
						break;
					}
				}
				if(msgGroupDetail.getType()==GroupTypeEnum.MULTI.getValue())
				{
					if(lastMsg!=null && lastMsg.getType()!=MsgTypeEnum.REMIND.getValue())
					{
						//多人会话在会话列表中显示最后一条消息的发送者名称
						String uid = lastMsg.getFromUserId();
						if(!SysConstant.isSysUser(uid) && !msgGroupParam.getUserId().equals(uid))
						{
							UserSession subUser= ReqUtil.instance.getUser(Integer.valueOf(uid));
							if(subUser!=null)
							{
								lastMsg.setContent(subUser.getName()+":"+lastMsg.getContent());
							}
						}
					}
				}
					
				if(SysConstant.isSysGroup(msgGroupDetail))
				{
					notificationGroup.setUr(notificationGroup.getUr()+msgGroupDetail.getUnread());
					notificationGroup.getList().add(msgGroupDetail);
					continue;
				}
				
				if(msgGroupDetail.getRtype().equals(RelationTypeEnum.DOCTOR.getValue()))
				{
					doctorlist.setUr(doctorlist.getUr()+msgGroupDetail.getUnread());
					doctorlist.getList().add(msgGroupDetail);
				}
				else if(msgGroupDetail.getRtype().equals(RelationTypeEnum.PATIENT.getValue()))
				{
					patientlist.setUr(patientlist.getUr()+msgGroupDetail.getUnread());
					patientlist.getList().add(msgGroupDetail);
				}
				else if(msgGroupDetail.getRtype().equals(RelationTypeEnum.DOC_PATIENT.getValue())||msgGroupDetail.getRtype().equals(RelationTypeEnum.DOC_OUTPATIENT.getValue()))
				{
					String gid=msgGroupDetail.getGid();
					OrderSessionExample orderSessionExample=new OrderSessionExample();
					orderSessionExample.createCriteria().andMsgGroupIdEqualTo(gid);
					List<OrderSession> orderSessions=orderSessionMapper.selectByExample(orderSessionExample);
					if(orderSessions!=null&&!orderSessions.isEmpty()&&orderSessions.get(0).getOrderId()!=null){
						Order order=orderMapper.getOne(orderSessions.get(0).getOrderId());
						if(order!=null){
							msgGroupDetail.setOrderStatus(order.getOrderStatus());
							msgGroupDetail.setOrderId(orderSessions.get(0).getOrderId());
							
							if(PackType.message.getIndex()== order.getPackType()){
								msgGroupDetail.setPackType("2");
							}else if(PackType.phone.getIndex() == order.getPackType()){
								msgGroupDetail.setPackType("3");
							}
						}
					}
					if(userType==UserType.doctor.getIndex())
					{
						patientlist.setUr(patientlist.getUr()+msgGroupDetail.getUnread());
						patientlist.getList().add(msgGroupDetail);
					}
					else if(userType==UserType.patient.getIndex())
					{
						doctorlist.setUr(doctorlist.getUr()+msgGroupDetail.getUnread());
						doctorlist.getList().add(msgGroupDetail);
					}
					
					Map bussiness=new HashMap();
					getOrderRelevant(gid, bussiness);
					msgGroupDetail.setBussiness(bussiness);
					
				}
					
			}
		}
		
		if(patientlist.getList().size()==0)
		{
			msgGroupVO.setPatientlist(null);
		}
		if(doctorlist.getList().size()==0)
		{
			msgGroupVO.setDoctorlist(null);
		}
		if(assistantlist.getList().size()==0)
		{
			msgGroupVO.setAssistantlist(null);
		}
		if(notificationGroup.getList().size()==0)
		{
			msgGroupVO.setNotificationGroup(null);
		}
		if(customerGroup.getList().size()==0)
		{
			msgGroupVO.setCustomerGroup(null);
		}
		return msgGroupVO;
	}
	
	private EventResult getEventList(EventListRequest requestMsg)
	{
		try{
			if(requestMsg.getTs()==null)
			{
				requestMsg.setTs(0L);
			}
			JSON imGetMsg = MsgHelper.eventList(requestMsg);
			if(imGetMsg==null)
			{
				return null;
			}
			EventResult result = JSON.toJavaObject(imGetMsg,EventResult.class);
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}*/
	
	
	/**
	 * 发送消息接口
	 */
	public SendMsgResult sendMsg(MessageVO msg) throws HttpApiException {
		//checkBeforeSend(msg);
		SendMsgResult result=msgService.baseSendMsg(msg);
		return result;
	}
	
	//导医组
	private void setGuideOrderRelevant(String gid, Map<String, Object> bussiness)
	{
		OrderCache orderCache = iGuideDAO.getOrderCacheByGroup(gid);
		bussiness.put("orderId", orderCache.getId());
		UserSession currentUser = ReqUtil.instance.getUser();
		//判断当前用户是否是该订单对应的导医(如果不是，则返回状态：服务已经结束)
		boolean isPatient = false;
		if(currentUser!=null)
		{
			int currentUserId = currentUser.getUserId();
			ConsultOrderPO order =iGuideDAO.getConsultOrderPO(orderCache.getId()); 
			int userId = order.getUserId();//患者Id
			if(currentUserId != userId)
			{
				Integer guideId = order.getGuideId();//导医Id
				//当前用户不是患者
				//导医Id为空，表示还未接单;当前用户是其他导医（非接单的导医）；这两种情况都显示服务结束；
				if(guideId==null || currentUserId != guideId.intValue())
				{
					//服务结束(导医会话)
					bussiness.put("sessionStatus", 22);
					return;
				}
			}
			else
			{
				isPatient = true;
			}
		}
		ServiceStateEnum state = orderCache.getState();
		if(state==null)
		{
			state = ServiceStateEnum.NO_START;
		}
		if(state.getValue()==ServiceStateEnum.NO_START.getValue()) {
			//等待接单(导医会话)
			bussiness.put("sessionStatus", 21);
//			bussiness.put("leftTime", 15);//剩余15分钟
		} else if(state.getValue()==ServiceStateEnum.SERVING.getValue()) {
			long currentTime = System.currentTimeMillis();
			long maxTime = guideService.getMaxServiceTime();//毫秒
			Long startTime = orderCache.getStartTime();
			long serviceTime = currentTime-startTime; //服务时间 
			long leftTime = 0; 
			if(startTime!=null && serviceTime > maxTime){
				//服务中---服务超时
				bussiness.put("sessionStatus", 16);
				leftTime = (serviceTime - maxTime)/60000;
			}else{
				//服务中
				bussiness.put("sessionStatus", 15);
				leftTime = (maxTime - serviceTime)/60000;
			}
			bussiness.put("serviceBeginTime", startTime);
			bussiness.put("timeLong", maxTime);
			if(!isPatient)
			{
				bussiness.put("leftTime", leftTime);
			}
		} else {
			//服务结束(导医会话)
			bussiness.put("sessionStatus", 22);
		}
	}
	
	
	/**
	 * 订单超时时发送提醒
	 * @param gid
	 */
	 public void sendOvertimeMsg(String gid) throws HttpApiException {
		  if(gid.startsWith("guide"))
		  {
			  OrderCache orderCache = iGuideDAO.getOrderCacheByGroup(gid);
			  if(orderCache!=null)
			  {
				  ConsultOrderPO order = iGuideDAO.getConsultOrderPO(orderCache.getId());
				  if(order!=null&&(order.getIsSendOverTime()==null||!order.getIsSendOverTime()))
				  {
					  Map<String, Object> updateValue=new HashMap<String, Object>();
					  updateValue.put("isSendOverTime", true);
					  iGuideDAO.updateConsultOrder(order.getId(), updateValue);
					  businessServiceMsg.sendNotifyMsgToAll(gid, "订单已经超时！");
				  }
			  }
			
		  }
		  else
		  {
            OrderSessionExample example=new OrderSessionExample();
            example.createCriteria().andMsgGroupIdEqualTo(gid);
            List<OrderSession> orderSessions=  orderSessionMapper.selectByExample(example);
            if(orderSessions.size()>0&&orderSessions.get(0).getIsSendOverTime()==null)
            {
           	 orderSessions.get(0).setIsSendOverTime(true);
           	 orderSessionService.updateByPrimaryKeySelective(orderSessions.get(0));
           	 businessServiceMsg.sendNotifyMsgToAll(gid, "订单已经超时！");
            }
		  }
	    }
	  
	  
	  /**
	   * 定期给超期的会话发送提醒,处理会话超期了，但是一直没进入聊天界面，无法触发客户端调用发送超期提醒消息的问题
	   */
	  public void sendOvertimeMsg( ) throws HttpApiException {
		  //普通订单处理
		  List<OrderSession> orderSessions=orderService.getOverTimeOrderSession();
		  for (OrderSession orderSession : orderSessions) 
		  {
			Long serviceBeginTime=orderSession.getServiceBeginTime();
			Long nowTime=new Date().getTime();
		    Integer  timeLong=orderSession.getTimeLong();
			long duration=nowTime-serviceBeginTime;
			long leftTime=timeLong-duration/60000;
			if(leftTime==0){
					leftTime=leftTime+1;
			}
			//超期
			if(leftTime<0)
			{
				orderSession.setIsSendOverTime(true);
				try {
					 orderSessionService.updateByPrimaryKeySelective(orderSession);
					 businessServiceMsg.sendNotifyMsgToAll(orderSession.getMsgGroupId(), "订单已经超时！");
				} catch (Exception e) {
					logger.error( e.getMessage());
				}
			   
			}
		}
		  //导医订单处理
		  List<ConsultOrderPO> ConsultOrderPOList= iGuideDAO.getNotSendTimeOutOrderList();
		  for (ConsultOrderPO consultOrderPO : ConsultOrderPOList) 
		  {
			  Map<String, Object> updateValue=new HashMap<String, Object>();
			  updateValue.put("isSendOverTime", true);
			  iGuideDAO.updateConsultOrder(consultOrderPO.getId(), updateValue);
			  businessServiceMsg.sendNotifyMsgToAll(consultOrderPO.getGroupId(), "订单已经超时！");
		  }
	   }
		
}
