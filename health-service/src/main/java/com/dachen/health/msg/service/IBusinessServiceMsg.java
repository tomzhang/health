package com.dachen.health.msg.service;

import java.util.List;
import java.util.Map;
import java.util.Set;


import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.exception.HttpApiException;

public interface IBusinessServiceMsg {

	void userChangeNotify(UserChangeTypeEnum userChangeType, Integer userId, Integer toUserId) throws HttpApiException;

	void userChangeNotify(UserChangeTypeEnum userChangeType, EventEnum event, Integer userId, Integer toUserId) throws HttpApiException;

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
	void addGroupNotify(Integer userId, String groupId);

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
	void deleteGroupNotify(Integer userId, String groupId) throws HttpApiException;

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
	void changeDeptGroupNotify(String groupId);

	/**
	 * 集团值班时间改变通知
	 * 
	 * @param groupId
	 */
	void groupDutyTimeChangeNotify(String groupId);

	/**
	 * 
	 * @author 李淼淼
	 * @date 2015年9月9日
	 */
	void orderChangeNotify(Integer userId, String msgGroupId, Integer orderStatus);

	public void drugRemindNotify(String userId, Map<String, Object> map);
	/**
	 * 发送通知
	 * 
	 * @param toUserIds
	 * @param sysGroupEnum
	 * @param mpt
	 * @param param
	 */
	void sendTextMsg(String toUserIds, SysGroupEnum sysGroupEnum, List<ImgTextMsg> mpt, Map<String, Object> param) throws HttpApiException;

	public void sendNotifytoMyMsg(String toUserId, String gid, String content) throws HttpApiException;

	/**
	 * 发送指令
	 * 
	 * @param eventEnum
	 * @param from
	 * @param to
	 */
	void sendEventFriendChange(EventEnum eventEnum, String from, String to);

	void sendNotifyMsgToAll(String gid, String content) throws HttpApiException;

	void sendUserToUserMsg(String fromUserId, String gid, String content) throws HttpApiException;
	void sendPushUserToUserMsg(String fromUserId, String gid, String content, Integer pushUser) throws HttpApiException;
	void sendPushUsersToUserMsg(String fromUserId, String gid, String content, Set<String> pushUsers) throws HttpApiException;
	
	void sendUserToUserMsg(String fromUserId, String gid, String content, String isPush) throws HttpApiException;

	void sendTextMsgToGid(String fromUserId, String gid, List<ImgTextMsg> mpt, Map<String, Object> param) throws HttpApiException;
	void sendTextMsgToGid(String fromUserId, String gid, List<ImgTextMsg> mpt, Map<String, Object> param, boolean isPush) throws HttpApiException;
	void sendTextMsgToPushUser(String fromUserId, String gid, List<ImgTextMsg> mpt, Map<String, Object> param, Integer pushUser) throws HttpApiException;
	void sendTextMsgToPushUser(String fromUserId, String gid, List<ImgTextMsg> mpt, Map<String, Object> param, Boolean isPush, Integer pushUser) throws HttpApiException;
	void sendTextMsgToPushUsers(String fromUserId, String gid, List<ImgTextMsg> mpt, Map<String, Object> param, Set<String> pushUsers) throws HttpApiException;

	public void sendUserToUserLinkMsg(String fromUserId, String gid, String content, String uri) throws HttpApiException;
	
	public void sendUserToUserParam(String fromUserId, String gid, String content, Map<String,Object> param) throws HttpApiException;
	public void sendPushUserToUserParam(String fromUserId, String gid, String content, Map<String,Object> param, Integer pushUser) throws HttpApiException;
	public void sendUserToUserParam(String fromUserId, String gid, String content, Map<String,Object> param, Boolean isPush) throws HttpApiException;

	// 发送取消关注指令
	void sendEventUnFollow(List<Integer> userIds, String pid);

	// 发送取消关注指令
	void sendEventUnFollow(Integer userId, String pid);

	public void sendEventDoctorDisturb(EventEnum eventEnum, String from, String to);

	// 发送样式为7的标题内容
	public void sendTextMsgToGidOnToUserId(String toUserId, String gid, List<ImgTextMsg> mpt, Map<String, Object> param) throws HttpApiException;

	public void sendNotifyMsgToUser(String toUserId, String gid, String content) throws HttpApiException;

	public void sendEventForGuide(String guideOrderId, String userId, String type,String doctorName);
	
	/**
	 * 医生审核成功，发一条指令，改变医生端的平台认证入口；
	 * @param doctorId
	 * @param userId
	 * @param type
	 * @param doctorName
	 */
	public void sendEventForDoctor(Integer doctorId, Integer userId,int type,String doctorName,Integer userLevel,Long limitedPeriodTime);
	
	/**
	 * 医生审核成功，发一条指令，改变医生端的平台认证入口；
	 * @param doctorId
	 * @param userId
	 * @param type
	 * @param doctorName
	 */
	public void sendEventForDoctor(Integer doctorId, Integer userId,int type,String doctorName);
	
	
	/**
	 * 集团屏蔽状态变化
	 * @param groupId
	 * @param skipStatus
	 * @author tan.yf
	 * @date 2016年6月27日
	 */
	public void changeSkipGroupNotify(String groupId,String skipStatus);

	void refreshCircleTab(String userId);

	void refreshCircleTab(List<Integer> userId);

	void sendExpertiseChangeEventForDoctor(Integer userId, Integer userId1, Integer status, String name, Integer changeFlag);

	void userInfoChangeEvent(Integer userId, String name);
}
