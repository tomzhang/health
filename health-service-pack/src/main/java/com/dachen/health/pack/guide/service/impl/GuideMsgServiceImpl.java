package com.dachen.health.pack.guide.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.KeyBuilder;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.health.commons.constants.OrderEnum.OrderType;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.msg.entity.param.BusinessParam;
import com.dachen.health.msg.entity.vo.EventResult;
import com.dachen.health.msg.entity.vo.MsgGroupList;
import com.dachen.health.msg.entity.vo.MsgGroupVO;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.util.IMUtils;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.health.pack.conference.service.ICallRecordService;
import com.dachen.health.pack.guide.dao.IGuideDAO;
import com.dachen.health.pack.guide.entity.OrderCache;
import com.dachen.health.pack.guide.entity.ServiceStateEnum;
import com.dachen.health.pack.guide.service.IGuideMsgService;
import com.dachen.health.pack.guide.service.IGuideService;
import com.dachen.health.pack.guide.util.GuideMsgHelper;
import com.dachen.health.pack.guide.util.GuideUtils;
import com.dachen.im.server.data.request.EventListRequest;
import com.dachen.im.server.data.request.GroupListRequestMessage;
import com.dachen.im.server.data.response.GroupDetailVO;
import com.dachen.im.server.data.response.GroupListResult;
import com.dachen.im.server.data.response.GroupListVO;
import com.dachen.im.server.data.response.GroupUserInfo;
import com.dachen.im.server.data.response.MsgGroupDetail;
import com.dachen.im.server.enums.GroupTypeEnum;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;

@Service
public class GuideMsgServiceImpl implements IGuideMsgService {
	private final static Logger logger = LoggerFactory.getLogger(GuideMsgServiceImpl.class);
	
	@Autowired
	private IBusinessServiceMsg businessServiceMsg;
	
	@Autowired
	private IGuideDAO iGuideDAO;

	@Autowired
	private IGuideService guideService;
	
	@Autowired
	private ICallRecordService callRecordService;

	@Autowired
	JedisTemplate jedisTemplate;
	
	@Autowired
	UserManager userManager;
	
	/**
	 * 已接单池
	 */
	public final static String PATIENT_MESSAGE_TO_GUIDE_POOL_SERVICING = KeyBuilder.Z_PATIENT_MESSAGE_TO_GUIDE_POOL+":guide_";
	
	@Override
	public Map<String, Object> groupList(BusinessParam buisnessParam) {
		/**
		 * 业务轮询接口 1、获取会话列表
		 */
		if (StringUtils.isEmpty(buisnessParam.getUserId())) {
			buisnessParam.setUserId(String.valueOf(ReqUtil.instance.getUser()
					.getUserId()));
		}
		GroupListRequestMessage groupListParam = new GroupListRequestMessage();
		groupListParam.setCnt(buisnessParam.getCnt());
		groupListParam.setTs(buisnessParam.getTs());
		groupListParam.setUserId(buisnessParam.getUserId());
		// 获取会话列表
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(buisnessParam.getNewData()==1)
		{
			resultMap.put("msgGroupVO", groupList_new(groupListParam));
		}
		else
		{
			resultMap.put("msgGroupVO", groupList_old(groupListParam));
		}
//		BusinessVO businessVO = new BusinessVO();
//		businessVO.setMsgGroupVO(msgGroupVO);
		if (buisnessParam.getTs()>0 && buisnessParam.getNeedEvent() == 1) {
			EventListRequest requestMsg = new EventListRequest();
			requestMsg.setUserId(buisnessParam.getUserId());
			requestMsg.setTs(buisnessParam.getTs());
			EventResult eventResult = GuideMsgHelper.getEventList(requestMsg);
//			businessVO.setEvent(eventResult);
			resultMap.put("event", eventResult);
		}
//		CallRecordParam param = new CallRecordParam();
//		ConfCall cc = new ConfCall();
//		cc.setCreater(buisnessParam.getUserId());
//		param.setConfCall(cc);
//		Map<String, List<CallBusinessVO>> map = new HashMap<String, List<CallBusinessVO>>();
//		map.put("service", callRecordService.getOrderInServiceByGuide(param));
////		businessVO.setBusiness(map);
//		resultMap.put("business", map);
		return resultMap;
	}

	/**
	 * 获取会话列表（服务中） 1、对返回的会话列表进行分组，将原有的会话按照会话类型分为不同的组
	 */
	private MsgGroupVO groupList_old(GroupListRequestMessage msgGroupParam) {
		JSON map = MsgHelper.groupList(msgGroupParam);
		GroupListResult msgGroupList = JSON.toJavaObject(map,
				GroupListResult.class);

		String userId = msgGroupParam.getUserId();
		MsgGroupVO msgGroupVO = new MsgGroupVO();
		if (msgGroupList.getList() != null) {
			MsgGroupList guidelist = new MsgGroupList();
			MsgGroupList notificationGroup = new MsgGroupList();
			String groupId = null;
			long currentTime = System.currentTimeMillis();
			long maxTime = guideService.getMaxServiceTime();
			String creator = null;
			for (MsgGroupDetail msgGroupDetail : msgGroupList.getList()) {
				groupId = msgGroupDetail.getGid();

				if (IMUtils.isSysGroup(msgGroupDetail)) {
					notificationGroup.setUr(notificationGroup.getUr()
							+ msgGroupDetail.getUnread());
					notificationGroup.getList().add(msgGroupDetail);
					continue;
				}

				// 查询会话对应的订单服务状态
				OrderCache oderCache = iGuideDAO.getOrderCacheByGroup(groupId);
				if (oderCache == null || oderCache.getState() == null
						|| !oderCache.getState().equals(ServiceStateEnum.SERVING)) {
					continue;
				}
				if (msgGroupDetail.getUserIds() != null) {
					if (!msgGroupDetail.getUserIds().contains(userId)) {
						continue;
					}
				}
				boolean timeout = false;
				Long startTime = oderCache.getStartTime();
				if (startTime != null && currentTime - startTime > maxTime) {
					timeout = true;
				}
				List userIds = msgGroupDetail.getUserIds();
				msgGroupDetail.setUserList(IMUtils.getGroupUserList(userIds));

				// 服务超时
				Map<String, Object> business = new HashMap<String, Object>();
				business.put("timeout", timeout ? 1 : 0);
				business.put("orderType", OrderType.order.getIndex());
				msgGroupDetail.setBussiness(business);
				msgGroupDetail.setPackType(String
						.valueOf(PackEnum.PackType.phone.getIndex() + 1));
				// 消息类会话列表
				String gpic = msgGroupDetail.getGpic();
				/**
				 * 客服组是由患者创建，对于导医来说获取的就是创建者的名称和图片
				 */
				creator = msgGroupDetail.getCreator();
				if (creator != null && creator.trim().length() > 0) {
					int createId = Integer.valueOf(creator);
					UserSession user = ReqUtil.instance.getUser(createId);
					if (user != null) {
						msgGroupDetail.setGname(user.getName());
						msgGroupDetail.setGpic(user.getHeadImgPath());
						msgGroupDetail.setTelephone(user.getTelephone());
					}
				} else if (!StringUtils.isEmpty(gpic)) {
					gpic = PropertiesUtil.addUrlPrefix(gpic);
					msgGroupDetail.setGpic(gpic);
				}
				guidelist.setUr(guidelist.getUr() + msgGroupDetail.getUnread());
				guidelist.getList().add(msgGroupDetail);
			}
			msgGroupVO.setGuidelist(guidelist);
			msgGroupVO.setNotificationGroup(notificationGroup);
		}
		msgGroupVO.setTms(msgGroupList.getTms());
		msgGroupVO.setMore(msgGroupList.isMore());
		msgGroupVO.setTs(msgGroupList.getTs());
		return msgGroupVO;
	}
	
	private GroupListVO groupList_new(GroupListRequestMessage request) 
	{
		if(StringUtils.isEmpty(request.getUserId()))
		{
			request.setUserId(String.valueOf(ReqUtil.instance.getUserId()));
		}
		JSON map=MsgHelper.groupList_new(request); 
		GroupListVO msgGroupList = JSON.toJavaObject(map,GroupListVO.class);
		if(msgGroupList.getList()!=null )
		{
			String groupId = null;
			int totalUnReadCount = 0;
//			long currentTime = System.currentTimeMillis();
//			long maxTime = guideService.getMaxServiceTime();
			List<GroupDetailVO> groupList = new ArrayList<GroupDetailVO>(); 
			for (GroupDetailVO msgGroupDetail : msgGroupList.getList()) {
				groupId = msgGroupDetail.getGroupId();
				
				String patientUserId = GuideUtils.getUserIdByGuideDocGroupId(groupId);
				
				Double score = jedisTemplate.zscore(PATIENT_MESSAGE_TO_GUIDE_POOL_SERVICING+ReqUtil.instance.getUserId(),patientUserId);
				
				if(Objects.isNull(score)){
					continue;
				}
				
				if (GroupTypeEnum.NOTIFICATION.getValue() == msgGroupDetail.getType()) 
				{
//					groupList.add(msgGroupDetail);
//					totalUnReadCount = totalUnReadCount+msgGroupDetail.getUnreadCount();
					continue;
				}
				/**
				 * 有些会话可能是导医之前接的单，这里只返回会话对应当前订单的接单者为当前导医的数据
				 */
				boolean contains = false;
				if(msgGroupDetail.getGroupUsers()!=null)
				{
					for(GroupUserInfo groupUser:msgGroupDetail.getGroupUsers())
					{
						/**
						 * 客服组是由患者创建，对于导医来说获取的就是患者的名称和头像
						 */
						if(groupUser.getUserType()==UserEnum.UserType.patient.getIndex())
						{
							msgGroupDetail.setName(groupUser.getName());
							msgGroupDetail.setGpic(groupUser.getPic());
							User u = userManager.getUser(Integer.valueOf(groupUser.getId()));
							msgGroupDetail.setTelephone(u.getTelephone());
						}
						if(groupUser.getId().equals(request.getUserId()))
						{
							contains = true;
							continue;
						}
					}
				}
				if(!contains)
				{
					continue;
				}
				// 查询会话对应的订单服务状态
//				OrderCache orderCache = iGuideDAO.getOrderCacheByGroup(groupId);
//				if(orderCache ==null || orderCache.getState()==null || !orderCache.getState().equals(ServiceStateEnum.SERVING))
//				{
//					continue;
//				}
//				Long startTime = orderCache.getStartTime();
//				boolean timeout = false;
//				if(startTime!=null && currentTime-startTime > maxTime)
//				{
//					timeout = true;
//				}
//				msgGroupDetail.setBizStatus(timeout?"16":"15");//16:服务超时
//				//服务超时
//				Map<String,Object>business=new HashMap<String,Object>();
////				business.put("timeout", timeout?1:0);
//				business.put("orderType", OrderType.order.getIndex());
//				business.put("packType",String.valueOf(PackEnum.PackType.phone.getIndex()+1));
//				business.put("serviceBeginTime", startTime);
//				business.put("timeLong", maxTime/60000);
//				business.put("orderId", orderCache.getId());
//				msgGroupDetail.setParam(business);
				//消息类会话列表
				groupList.add(msgGroupDetail);
				totalUnReadCount = totalUnReadCount+msgGroupDetail.getUnreadCount();
			}
			msgGroupList.setUr(totalUnReadCount);
			msgGroupList.setCount(groupList==null?0:groupList.size());
			msgGroupList.setList(groupList);
		}
		return msgGroupList;
	}
}
