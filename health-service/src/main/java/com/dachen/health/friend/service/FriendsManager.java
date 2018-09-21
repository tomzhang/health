package com.dachen.health.friend.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.entity.param.FriendReqQuery;
import com.dachen.health.friend.entity.po.FriendReq;
import com.dachen.health.friend.entity.po.FriendSetting;
import com.dachen.sdk.exception.HttpApiException;

public interface FriendsManager {


	boolean addFriends(Integer userId, Integer toUserId);


	boolean deleteFriends(Integer userId, Integer toUserId) throws HttpApiException;

	List<Object> queryBlacklist(Integer userId);

	boolean setFriends(Integer userId, Integer toUserId,FriendSetting settings);

	Object getFriend(Integer userId, Integer toUserId);


	Object getSessionList(int userId,int queryType,long lastTime);


	Object getMsgsByUser(int userId, int toUserId,long  lastTime);
	
	/**
	 * 发送好友验证请求
	 * @param toUserId
	 * @param applyContent
	 */
	void sendApplyAddFriend(Integer userId,Integer toUserId,String applyContent) throws HttpApiException;
	/**
	 * 加好友:
	 * @param userId
	 * @param toUserId
	 */
	User applyAddFriend(Integer userId, Integer toUserId,boolean isNeedFriendReq) throws HttpApiException;
	/**
	 * 回复加好友请求：
	 * 对发送给我的好友请求进行处理，同意：则建立好友关系，改变请求状态，同时生成会话；如果拒绝，则改变好友请求状态。
	 * @param reqId
	 * @param result：1：同意；2：拒绝
	 */
	void replyAddFriend(String id,int result) throws HttpApiException;
	
	/**
	 * 获取好友请求
	 * 1、我发送的好友请求及其状态
	 * 2、发送给我的好友请求及其状态
	 * @return
	 */
	PageVO getFriendReq(FriendReqQuery friendReqQuery) throws HttpApiException;
	
	FriendReq getFriendReqById(String id);
	
	boolean addFriendReq(FriendReq friendReq);


	/**
	 * 添加手机联系人
	 */
	Map addPhoneFriend(Integer userId, String phone) throws HttpApiException;


	void sendInviteMsg(int userId, String phone) throws HttpApiException;
	void sendInviteMsgAsync(int userId, String phone);

	/**
	 * 添加同集团好友
	 * @param userId
	 * @param toUserId
	 */
	User addGroupFriend(Integer userId, Integer toUserId);
	
	/**
	 * 删除同集团好友
	 * @param userId
	 * @param toUserId
	 */
	void delGroupFriend(Integer userId, Integer toUserId);
	
	/**
	 * 是否为朋友，适用u_doctor_assistant/u_doctor_friend/u_doctor_patient/u_patient_friend
	 * @param userId
	 * @param toUserId
	 * @param clazz
	 * @return
	 */
	<T> boolean friends(Integer userId, Integer toUserId, Class<T> clazz);
	
	<T> List<T> getFriends(Integer userId, Class<T> clazz);
	
	// ^ add by gengchao 2016/8/5 增加相关接口提供给drugorg调用
	/**
	 * add by gengchao 2016/8/5  校验好友关系是否存在等待验证状态
	 * @param userId
	 * @param toUserId
	 * @return
	 */
	FriendReq getUnTreatedFriendReq(Integer userId, Integer toUserId);
	
	/**
	 * add by gengchao 2016/8/5 获取用户好友关系
	 * @param userId
	 * @param toUserId
	 * @return
	 */
	FriendReq getFriendReq(Integer userId, Integer toUserId);
	
	/**
	 * add by gengchao 2016/8/5 删除用户好友关系
	 * @param id
	 * @return
	 */
	boolean deleteFriendReqById(String id);
	
	/**
	 * add by gengchao 2016/8/5 添加好友关系
	 * @param friendReq
	 * @return
	 */
	boolean addFriendReqOfDrugOrg(FriendReq friendReq);
	
	/**
	 * add by gengchao 2016/8/5 更新好友关系 
	 * @param friendReq
	 * @return
	 */
	boolean updateFriendReq(FriendReq friendReq);
	// $ 

	public List<Integer> getFriendReqListByUserId(Integer userId);
	
	public List<Integer> getMyDocIds(Integer userId);

	Map getFriends(List<Integer> userIdList);
}
