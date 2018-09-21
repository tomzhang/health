package com.dachen.health.msg.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.base.dao.IBaseUserDao;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.doctor.service.ICommonGroupDoctorService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.im.server.constant.SysConstant.SysUserEnum;
import com.dachen.im.server.data.EventVO;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.request.UpdateGroupRequestMessage;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.im.server.enums.MsgTypeEnum;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.pub.service.PubGroupService;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.StringUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mongodb.morphia.AdvancedDatastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service(BusinessMsgServiceImpl.BEAN_ID)
public class BusinessMsgServiceImpl implements IBusinessServiceMsg {

	public static final String BEAN_ID = "BusinessMsgServiceImpl";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	protected AdvancedDatastore dsForRW;
	@Autowired
	private ICommonGroupDoctorService commongdService;
	@Autowired
	private IBaseUserDao baseUserDao;
	@Autowired
	private IMsgService msgService;

	@Autowired
	private PubGroupService pubGroupService;

	public void userChangeNotify(UserChangeTypeEnum userChangeType, Integer userId, Integer toUserId) throws HttpApiException {
		this.userChangeNotify(userChangeType, null, userId, toUserId);
	}

	/**
	 * 好友变化的后的触发方法
	 * 
	 */
	public void userChangeNotify(UserChangeTypeEnum userChangeType, EventEnum event, Integer userId, Integer toUserId) throws HttpApiException {

		// 加为好友
		if (userChangeType == UserChangeTypeEnum.ADD_FRIEND) {
//			// 给对方发送通知
//			User user = dsForRW.createQuery(User.class).field("_id").equal(userId).get();
//			String toAddMsg = user.getName() + "加你为好友了！";
//			sendMsg(userChangeType, userId, toUserId.toString(), toAddMsg, getGroupId(userId, toUserId));
//
//			// 给自己发送通知
//			User toUser = dsForRW.createQuery(User.class).field("_id").equal(toUserId).get();
//			String fromAddMsg = "你加" + toUser.getName() + "为好友了！";
//			sendMsg(userChangeType, toUserId, userId.toString(), fromAddMsg, getGroupId(toUserId, userId));
			// 给双方发送指令
			sendEventFriendChange(EventEnum.ADD_FRIEND, userId.toString(), toUserId.toString());
		}
		// 删除destUserId好友,通知destUserId
		else if (userChangeType == UserChangeTypeEnum.DEL_FRIEND) {
			// 关闭会话
			UpdateGroupRequestMessage imMsg = new UpdateGroupRequestMessage();
			imMsg.setAct(9);
			imMsg.setFromUserId(userId.toString());
			imMsg.setToUserId(toUserId.toString());
			try {
				msgService.updateGroup(imMsg);
			} catch (ServiceException e) {
				if (e.getResultCode() == 10001) {
					// detailMessage = 参数不正确:组ID不正确
					// IM抛出来的异常，与成伟沟通，其建议先暂时吃掉处理
				} else {
					throw e;
				}
			}
			// 给双方发送指令
			sendEventFriendChange(EventEnum.DEL_FRIEND, userId.toString(), toUserId.toString());
		}
		// 给用户发送好友验证请求变化消息
		else if (userChangeType == UserChangeTypeEnum.FRIEND_REQ_CHANGE) {
			User fromUser = dsForRW.createQuery(User.class).field("_id").equal(userId).get();
			String fromAddMsg = fromUser.getName() + "申请加你为好友！";
			sendMsg(userChangeType, userId, toUserId.toString(), fromAddMsg, getGroupId(userId, toUserId));

			User toUser = dsForRW.createQuery(User.class).field("_id").equal(toUserId).get();
			String toAddMsg = "你申请加" + toUser.getName() + "为好友！";
			sendMsg(userChangeType, toUserId, userId.toString(), toAddMsg, getGroupId(toUserId, userId));

		}
		// 个人资料变化,通知自己的所有好友和给自己所属医生集团同事发更新指令
/*		else if (userChangeType == UserChangeTypeEnum.PROFILE_CHANGE) {
			// 向集团的个人资料监听频道发个事件
			// RedisUtil.publish("PROFILE_CHANGE", userId.toString());

			List<UserDetailVO> doctorFriend = relationDao.getRelations(RelationType.doctorFriend, userId);
			List<UserDetailVO> patientFriend = relationDao.getRelations(RelationType.patientFriend, userId);
			List<UserDetailVO> doctorPatient = relationDao.getRelations(RelationType.doctorPatient, userId);
			List<UserDetailVO> doctorAssistant = relationDao.getRelations(RelationType.doctorAssistant, userId);

			StringBuffer ids = new StringBuffer();
			for (UserDetailVO userDetailVO : doctorFriend) {
				ids.append(userDetailVO.getUserId()).append("|");
			}
			for (UserDetailVO userDetailVO : patientFriend) {
				ids.append(userDetailVO.getUserId()).append("|");
			}
			for (UserDetailVO userDetailVO : doctorPatient) {
				ids.append(userDetailVO.getUserId()).append("|");
			}
			for (UserDetailVO userDetailVO : doctorAssistant) {
				ids.append(userDetailVO.getUserId()).append("|");
			}
			List<String> list = getGroupDoctorList(userId.toString());
			// 集团中没人则只给自己发
			list.add(userId.toString());
			for (String id : list) {
				ids.append(id).append("|");
			}
			if (ids.length() > 1) {
				sendEventProfileChange(EventEnum.PROFILE_CHANGE, userId.toString(),
						ids.substring(0, ids.length() - 1).toString());
			} else {
				return;
			}
		}*/
		// 医生开始接单发送指令
		else if (userChangeType == UserChangeTypeEnum.DOCTOR_ONLINE) {
			sendEventProfileChange(EventEnum.DOCTOR_ONLINE, userId.toString(), userId.toString());
		}
		// 医生结束接单发送指令
		else if (userChangeType == UserChangeTypeEnum.DOCOTR_OFFLINE) {
			// 医生正常下线
			// DOCTOR_OFFLINE("8","医生下线"),
			// DOCTOR_OFFLINE_SYSTEM_FORCE("9","医生被系统强制下线"),
			if (event != null) {
				sendEventProfileChange(event, userId.toString(), userId.toString());
			}
		}
	}

	/**
	 * </p>
	 * 加入医生集团通知
	 * </p>
	 * 
	 * @param userId
	 *            加入人id
	 * @param groupId
	 *            集团id
	 * @author fanp
	 * @date 2015年9月1日
	 */
	public void addGroupNotify(Integer userId, String groupId) {
		try {
			pubGroupService.addSubUser(groupId, String.valueOf(userId), UserEnum.UserType.doctor.getIndex());
		} catch (Exception e) {
			
		}
		String doctorIds = getAllGroupDoctorIds(groupId);
		if (doctorIds.length() == 0)
			return;
		EventVO eventVO = new EventVO();
		eventVO.setUserId(doctorIds);
		eventVO.setEventType(EventEnum.GROUP_ADD_DOCTOR.getValue());
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		param.put("groupId", groupId);
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
	}

	

	/**
	 * </p>
	 * 离开医生集团通知
	 * </p>
	 * 
	 * @param userId
	 *            离职人id
	 * @param groupId
	 *            集团id
	 * @author pijingwei
	 * @date 2015年9月8日
	 */
	public void deleteGroupNotify(Integer userId, String groupId) throws HttpApiException {
		pubGroupService.delSubUser(groupId, String.valueOf(userId), UserEnum.UserType.doctor.getIndex());
		
		String doctorIds = getAllGroupDoctorIds(groupId);
		if (doctorIds.length() == 0)
			return;
		EventVO eventVO = new EventVO();
		eventVO.setUserId(doctorIds+"|"+userId);
		eventVO.setEventType(EventEnum.GROUP_DELETE_DOCTOR.getValue());
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		param.put("groupId", groupId);
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
	}

	/**
	 * </p>
	 * 医生集团医生科室变动通知
	 * </p>
	 * 
	 * @param groupId
	 *            集团id
	 * @author fanp
	 * @date 2015年9月8日
	 */
	public void changeDeptGroupNotify(String groupId) {
		String doctorIds = getAllGroupDoctorIds(groupId);
		if (doctorIds.length() == 0)
			return;
		EventVO eventVO = new EventVO();
		eventVO.setUserId(doctorIds);
		eventVO.setEventType(EventEnum.GROUP_CHANGEDEPT_DOCTOR.getValue());
		eventVO.setTs(System.currentTimeMillis());
		eventVO.setParam(new HashMap<String, Object>());
		msgService.sendEvent(eventVO);
	}
	
	public void groupDutyTimeChangeNotify(String groupId) {
		String doctorIds = getAllGroupDoctorIds(groupId);
		if (doctorIds.length() == 0)
			return;
		
		EventVO eventVO = new EventVO();
		eventVO.setUserId(doctorIds);
		eventVO.setEventType(EventEnum.GROUP_DUTY_TIME_CHANGE.getValue());
		eventVO.setTs(System.currentTimeMillis());
		eventVO.setParam(new HashMap<String, Object>());
		msgService.sendEvent(eventVO);
	}
	
	/**
	 * 获取groupId下所有的doctorId，使用“|”分隔
	 * @param groupId
	 * @return
	 */
	private String getAllGroupDoctorIds(String groupId) {
		// 查找医生集团用户
		List<Integer> doctorIds = baseUserDao.getDoctorIdByGroup(groupId);
		// 给集团所有人发送指令
		StringBuilder sb = new StringBuilder();
		if (doctorIds.size() > 0) {
			for (int i = 0, j = doctorIds.size(); i < j; i++) {
				sb.append(doctorIds.get(i));
				if (i < j - 1) {
					sb.append("|");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * </p>
	 * 离开医生集团通知
	 * </p>
	 * 
	 * @param userId
	 *            通知人
	 * @param msgGroupId
	 *            会话组id
	 * @author limiaomiao
	 * @date 2015年9月9日
	 */
	@Override
	public void orderChangeNotify(Integer userId, String msgGroupId, Integer orderStatus) {
		EventVO eventVO = new EventVO();
		eventVO.setUserId(userId + "");
		eventVO.setEventType(EventEnum.ORDER_CHANGE_STATUS.getValue());
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		param.put("msgGroupId", msgGroupId);
		param.put("orderStatus", orderStatus);
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
	}

	@Override
	public void drugRemindNotify(String userId, Map<String, Object> map) {
		EventVO eventVO = new EventVO();
		eventVO.setUserId(userId);
		eventVO.setEventType(EventEnum.DRUG_REMIND.getValue());
		eventVO.setTs(System.currentTimeMillis());
		eventVO.setParam(map);
		msgService.sendEvent(eventVO);
	}
	
	
	/**
	 * 老的发送通知方法，改为调用新的
	 */
	// 发送通知
	private void sendMsg(UserChangeTypeEnum userChangeType, Integer userId, String toUserIds, String content,
			SysGroupEnum sysGroupEnum) throws HttpApiException {
		List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
		ImgTextMsg imgTextMsg = new ImgTextMsg();
		imgTextMsg.setStyle(7);
		imgTextMsg.setTitle(userChangeType.getAlias());
		imgTextMsg.setContent(content);
		mpt.add(imgTextMsg);
		sendTextMsg(toUserIds, sysGroupEnum, mpt, null);
	}

	/**
	 * 发送im中提醒内容
	 */
	public void sendNotifyMsgToAll(String gid, String content) throws HttpApiException {
		MessageVO msg = new MessageVO();
		msg.setType(MsgTypeEnum.REMIND.getValue());
		msg.setFromUserId(SysUserEnum.SYS_001.getUserId());
		msg.setGid(gid);
		msg.setContent(content);
		msgService.baseSendMsg(msg);
	}

	public void sendNotifyMsgToUser(String toUserId, String gid, String content) throws HttpApiException {
		MessageVO msg = new MessageVO();
		msg.setType(MsgTypeEnum.REMIND.getValue());
		msg.setFromUserId(SysUserEnum.SYS_001.getUserId());
		msg.setGid(gid);
		msg.setContent(content);
		msg.setToUserId(toUserId);
		msgService.baseSendMsg(msg);
	}

	/**
	 * 发送im中提醒内容
	 */
	public void sendNotifytoMyMsg(String toUserId, String gid, String content) throws HttpApiException {
		MessageVO msg = new MessageVO();
		msg.setType(MsgTypeEnum.REMIND.getValue());
		msg.setFromUserId(SysUserEnum.SYS_001.getUserId());
		msg.setGid(gid);
		msg.setToUserId(toUserId);
		msg.setContent(content);
		msgService.baseSendMsg(msg);
	}

	/**
	 * 发送图文通知的步骤 1、确定要发送到的人，以|分割，可以一次发给多个人 toUserIds
	 * 2、确定要发送到的组，目前待办通知都是发送到SysGroupEnum.TODO_NOTIFY sysGroupEnum
	 * 3、组装图文消息，可以一次发送多个，每个图文消息是一个ImgTextMsg对象 mpt 4、要发送的额外信息，map类型，可以为null
	 * param
	 */
	// 发送通知
	public void sendTextMsg(String toUserIds, SysGroupEnum sysGroupEnum, List<ImgTextMsg> mpt,
			Map<String, Object> param) throws HttpApiException {
		if (toUserIds == null || toUserIds.trim().length() == 0 || toUserIds.equalsIgnoreCase("null")) {
			throw new ServiceException("发送系统通知的时候，toUserId不能为空。");
		}
		MessageVO msg = new MessageVO();
		msg.setType(14);
		msg.setFromUserId(SysUserEnum.SYS_001.getUserId());
		msg.setGid(sysGroupEnum.getValue());
		msg.setMpt(mpt);
		msg.setToUserId(toUserIds);
		msg.setParam(param);
		msgService.baseSendMsg(msg);
	}

	@Override
	public void sendTextMsgToGid(String fromUserId, String gid, List<ImgTextMsg> mpt, Map<String, Object> param) throws HttpApiException {
		this.doSendTextMsgToPushUsers(fromUserId, gid, mpt, param, null, null);
	}
	
	@Override
	public void sendTextMsgToGid(String fromUserId, String gid, List<ImgTextMsg> mpt, Map<String, Object> param, boolean isPush) throws HttpApiException {
		this.doSendTextMsgToPushUsers(fromUserId, gid, mpt, param, isPush, null);
	}
	
	@Override
	public void sendTextMsgToPushUser(String fromUserId, String gid, List<ImgTextMsg> mpt, Map<String, Object> param, Integer pushUser) throws HttpApiException {
		Set<String> pushUsers = new HashSet<String>(1);
		pushUsers.add(pushUser.toString());
		this.doSendTextMsgToPushUsers(fromUserId, gid, mpt, param, null, pushUsers);
	}
	@Override
	public void sendTextMsgToPushUser(String fromUserId, String gid, List<ImgTextMsg> mpt, Map<String, Object> param, Boolean isPush, Integer pushUser) throws HttpApiException {
		Set<String> pushUsers = null;
		if (null != pushUser) {
			pushUsers = new HashSet<String>(1);
			pushUsers.add(pushUser.toString());
		}
		
		this.doSendTextMsgToPushUsers(fromUserId, gid, mpt, param, isPush, pushUsers);
	}
	
	@Override
	public void sendTextMsgToPushUsers(String fromUserId, String gid, List<ImgTextMsg> mpt, Map<String, Object> param, Set<String> pushUsers) throws HttpApiException {
		this.doSendTextMsgToPushUsers(fromUserId, gid, mpt, param, null, pushUsers);
	}
	
	protected void doSendTextMsgToPushUsers(String fromUserId, String gid, List<ImgTextMsg> mpt, Map<String, Object> param, Boolean isPush, Set<String> pushUsers) throws HttpApiException {
		MessageVO msg = new MessageVO();
		msg.setType(14);
		msg.setFromUserId(fromUserId);
		msg.setGid(gid);
		msg.setMpt(mpt);
		msg.setParam(param);
		msg.setIsPush(null == isPush?null:String.valueOf(isPush));
		msg.setPushUsers(pushUsers);
		msgService.baseSendMsg(msg);
	}

	public void sendTextMsgToGidOnToUserId(String toUserId, String gid, List<ImgTextMsg> mpt,
			Map<String, Object> param) throws HttpApiException {
		MessageVO msg = new MessageVO();
		msg.setType(14);
		msg.setFromUserId(SysUserEnum.SYS_001.getUserId());
		msg.setGid(gid);
		msg.setToUserId(toUserId);
		msg.setMpt(mpt);
		msg.setParam(param);
		msgService.baseSendMsg(msg);
	}

	// 模拟用户发送消息
	@Override
	public void sendUserToUserMsg(String fromUserId, String gid, String content) throws HttpApiException {
		this.sendUserToUserMsg(fromUserId, gid, content, null);
	}
	
	@Override
	public void sendPushUserToUserMsg(String fromUserId, String gid, String content, Integer pushUser) throws HttpApiException {
		Set<String> pushUsers = new HashSet<String>(1);
		pushUsers.add(pushUser.toString());
		this.sendUserToUserMsg(fromUserId, gid, content, null, pushUsers);
	}
	
	@Override
	public void sendPushUsersToUserMsg(String fromUserId, String gid, String content, Set<String> pushUsers) throws HttpApiException {
		this.sendUserToUserMsg(fromUserId, gid, content, null, pushUsers);
	}
	
	public void sendUserToUserMsg(String fromUserId, String gid, String content, String isPush) throws HttpApiException {
		this.sendUserToUserMsg(fromUserId, gid, content, isPush, null);
	}
	
	private void sendUserToUserMsg(String fromUserId, String gid, String content, String isPush, Set<String> pushUsers) throws HttpApiException {
//		String tag = "sendUserToUserMsg";
//		if (logger.isInfoEnabled()) {
//			logger.debug("{}, pushUsers={}", tag, pushUsers);
//		}
		
		MessageVO msg = new MessageVO();
		msg.setType(MsgTypeEnum.TEXT.getValue());
		msg.setFromUserId(fromUserId);
		msg.setGid(gid);
		msg.setContent(content);
		msg.setIsPush(isPush);
		msg.setPushUsers(pushUsers);
		msgService.baseSendMsg(msg);
	}

	// 模拟用户发送文本链接消息
	public void sendUserToUserLinkMsg(String fromUserId, String gid, String content, String uri) throws HttpApiException {
		MessageVO msg = new MessageVO();
		msg.setType(MsgTypeEnum.TEXT_LINK.getValue());
		msg.setFromUserId(fromUserId);
		msg.setGid(gid);
		msg.setContent(content);
		msg.setUri(uri);
		msgService.baseSendMsg(msg);
	}
	
	// 模拟用户发送文本带参数
	public void sendUserToUserParam(String fromUserId, String gid, String content, Map<String,Object> param) throws HttpApiException {
		this.sendUserToUserParam(fromUserId, gid, content, param, null);
	}
	
	@Override
	public void sendPushUserToUserParam(String fromUserId, String gid, String content, Map<String,Object> param, Integer pushUser) throws HttpApiException {
		Set<String> pushUsers = new HashSet<String>(1);
		pushUsers.add(pushUser.toString());
		this.sendPushUsersToUserParam(fromUserId, gid, content, param, null, pushUsers);
	}
	
	@Override
	public void sendUserToUserParam(String fromUserId, String gid, String content, Map<String,Object> param, Boolean isPush) throws HttpApiException {
		this.sendPushUsersToUserParam(fromUserId, gid, content, param, isPush, null);
	}
	
	private void sendPushUsersToUserParam(String fromUserId, String gid, String content, Map<String,Object> param, 
			Boolean isPush, Set<String> pushUsers) throws HttpApiException {
		MessageVO msg = new MessageVO();
		msg.setType(MsgTypeEnum.TEXT_LINK.getValue());
		msg.setFromUserId(fromUserId);
		msg.setGid(gid);
		if (null != isPush) {
			msg.setIsPush(String.valueOf(isPush));
		}
		msg.setContent(content);
		msg.setParam(param);
		msg.setPushUsers(pushUsers);
		msgService.baseSendMsg(msg);
	}

	private SysGroupEnum getGroupId(Integer userId, Integer toUserId) {
		return SysGroupEnum.FRIEND_DOCTOR;
	}

	// 发送指令
	public void sendEventFriendChange(EventEnum eventEnum, String from, String to) {
		EventVO eventVO = new EventVO();
		eventVO.setEventType(eventEnum.getValue());
		eventVO.setUserId(from + "|" + to);
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("from", from);
		param.put("to", to);
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
	}


	// 发送取消关注指令
	@Override
	public void sendEventUnFollow(List<Integer> userIds, String pid) {
		if(SdkUtils.isEmpty(userIds)){
			return;
		}
		EventVO eventVO = new EventVO();
		eventVO.setEventType(EventEnum.GROUP_QUIT.getValue());
		eventVO.setUserId(StringUtils.join(userIds, "|"));
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>(1);
		param.put("pid", pid);
		eventVO.setParam(param);
		logger.info("发送取消关注指令参数 {}",ToStringBuilder.reflectionToString(eventVO));
		msgService.sendEvent(eventVO);
	}

	// 发送取消关注指令
	@Override
	public void sendEventUnFollow(Integer userId, String pid) {
		if(null ==userId){
			return;
		}
		EventVO eventVO = new EventVO();
		eventVO.setEventType(EventEnum.GROUP_QUIT.getValue());
		eventVO.setUserId(String.valueOf(userId));
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>(1);
		param.put("pid", pid);
		eventVO.setParam(param);
		logger.info("发送取消关注指令参数 {}",ToStringBuilder.reflectionToString(eventVO));
		msgService.sendEvent(eventVO);
	}
	// 发送指令
	public void sendEventProfileChange(EventEnum eventEnum, String userId, String toUserIds) {
		EventVO eventVO2 = new EventVO();
		eventVO2.setEventType(eventEnum.getValue());
		eventVO2.setUserId(toUserIds);
		eventVO2.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		eventVO2.setParam(param);
		msgService.sendEvent(eventVO2);
	}

	// 发送指令
	public void sendEventDoctorDisturb(EventEnum eventEnum, String from, String to) {
		EventVO eventVO = new EventVO();
		eventVO.setEventType(eventEnum.getValue());
		eventVO.setUserId(from + "|" + to);
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("from", from);
		param.put("to", to);
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
	}

   // 导医处理订单发送指令 指令值=23
   // type=1:联系不上医生；type=2：医生没时间；type=3：通知已有推荐医生
	public void sendEventForGuide(String guideOrderId, String userId,String type,String doctorName) {
		EventVO eventVO = new EventVO();
		eventVO.setEventType(EventEnum.GUIDE_ORDER_HANDLE.getValue());
		eventVO.setUserId(userId);
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("guideOrderId", guideOrderId);
		param.put("type", type);
		param.put("doctorName", doctorName);
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
	}
	//发送指定给医生   type UserEnum.UserStatus.normal( normal(1, "正常"),  fail(3, "审核未通过"),)
	@Override
	public void sendEventForDoctor(Integer doctorId, Integer userId,int status,String doctorName) {
		if(userId==0){
			throw new ServiceException("用户id不能为空！！！");
		}
		EventVO eventVO = new EventVO();
		eventVO.setEventType(EventEnum.DOCTOR_STATUS_CHANGE.getValue());
		eventVO.setUserId(userId.toString());
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("doctorId", doctorId);
		param.put("status", status);
		param.put("doctorName", doctorName);
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
	}
	
	@Override
	public void sendEventForDoctor(Integer doctorId, Integer userId,int status,String doctorName,Integer userLevel,Long limitedPeriodTime) {
		if(userId==0){
			throw new ServiceException("用户id不能为空！！！");
		}
		EventVO eventVO = new EventVO();
		eventVO.setEventType(EventEnum.DOCTOR_STATUS_CHANGE.getValue());
		eventVO.setUserId(userId.toString());
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("doctorId", doctorId);
		param.put("status", status);
		param.put("doctorName", doctorName);
		param.put("userLevel", userLevel);
		param.put("limitedPeriodTime", limitedPeriodTime);
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
	}
	
		
	public List getGroupDoctorList(String userId) {
		List<String> doctorList = new ArrayList<String>();
		User user = new User();
		user.setUserId(Integer.parseInt(userId));
		user = commongdService.getGroupListByUserId(user);
		if (user.getGroupList() != null && user.getGroupList().size() > 0) {
			String groupId = (String) user.getGroupList().get(0).get("id");
			doctorList = findGroupDoctorListByGroupId(groupId);
		}
		return doctorList;
	}

	public List findGroupDoctorListByGroupId(String groupId) {
		DBCursor gdrsor = dsForRW.getDB().getCollection("c_group_doctor").find(new BasicDBObject("groupId", groupId));

		List<String> idList = new ArrayList<String>();
		while (gdrsor.hasNext()) {
			DBObject gdj = gdrsor.next();
			idList.add(gdj.get("doctorId").toString());
		}
		return idList;
	}
	
	/**
	 * </p>
	 * 医生集团屏蔽状态变动通知
	 * </p>
	 * 
	 * @param groupId
	 *            集团id
	 * @author tanyf
	 * @date 2016年6月27日
	 */
	@Override
	public void changeSkipGroupNotify(String groupId,String skipStatus) {
		String doctorIds = getAllGroupDoctorIds(groupId);
		if (doctorIds.length() == 0)
			return;
		EventVO eventVO = new EventVO();
		eventVO.setUserId(doctorIds);
		eventVO.setEventType(EventEnum.GROUP_SKIP_CHANGE.getValue());
		eventVO.setTs(System.currentTimeMillis());
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("groupId", groupId);
		param.put("skip", skipStatus);
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
	}

	/**
	 * 加入 退出 解散 科室、联盟 需要重新拉取圈子tab数据  发送 EventEnum.GROUP_ADD_DOCTOR 指令
	 * @param userId
     */
	@Override
	public void refreshCircleTab(String userId){
		if(StringUtils.isEmpty(userId)){
			return;
		}
		EventVO eventVO=new EventVO();
		eventVO.setUserId(userId);
		eventVO.setEventType(EventEnum.GROUP_ADD_DOCTOR.getValue());
		eventVO.setTs(System.currentTimeMillis());
		Map<String,Object> map=new HashedMap();
		map.put("userId",userId);
		eventVO.setParam(map);
		msgService.sendEvent(eventVO);
	}

	@Override
	public void refreshCircleTab(List<Integer> userId){
		if(SdkUtils.isEmpty(userId)){
			return;
		}
		EventVO eventVO=new EventVO();
		eventVO.setUserId(StringUtils.join(userId,"|"));
		eventVO.setEventType(EventEnum.GROUP_ADD_DOCTOR.getValue());
		eventVO.setTs(System.currentTimeMillis());
		Map<String,Object> map=new HashedMap();
		map.put("userId",userId);
		eventVO.setParam(map);
		msgService.sendEvent(eventVO);
	}

	@Override
	public void sendExpertiseChangeEventForDoctor(Integer userId, Integer doctorId, Integer status, String doctorName, Integer  flag) {
		if(userId==0){
			throw new ServiceException("用户id不能为空！！！");
		}
		EventVO eventVO = new EventVO();
		eventVO.setEventType(EventEnum.DOCTOR_SKILL_CHANGE.getValue());
		eventVO.setUserId(userId.toString());
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("doctorId", doctorId);
		param.put("status", status);
		param.put("doctorName", doctorName);
		param.put("changeFlag", flag);
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
	}

	@Override
	public void userInfoChangeEvent(Integer userId, String name) {
		if (userId == null || userId == 0) {
			throw new ServiceException("用户id不能为空！！！");
		}
		EventVO eventVO = new EventVO();
		eventVO.setEventType(EventEnum.USER_INFO_CHANGE.getValue());
		eventVO.setUserId(userId.toString());
		eventVO.setTs(System.currentTimeMillis());
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		param.put("name", name);
		eventVO.setParam(param);
		msgService.sendEvent(eventVO);
	}
}
