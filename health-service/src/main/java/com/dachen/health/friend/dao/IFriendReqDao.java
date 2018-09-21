package com.dachen.health.friend.dao;

import java.util.List;

import org.bson.types.ObjectId;

import com.dachen.commons.page.PageVO;
import com.dachen.health.friend.entity.param.FriendReqQuery;
import com.dachen.health.friend.entity.po.FriendReq;

public interface IFriendReqDao {
	void save(FriendReq friendReq);

	void update(FriendReq friendReq);

	FriendReq getFriendReqById(String id);

	PageVO queryFriendReq(FriendReqQuery friendReqQuery);

	FriendReq getUnTreatedFriendReq(Integer userId, Integer toUserId);
	
	FriendReq getFriendReq(Integer userId, Integer toUserId, int status);

	FriendReq getWaitAcceptFriendReq(Integer fromUserId, Integer toUserId);

	void deleteFriendReqById(ObjectId id) ;
	
	FriendReq getFriendReq(Integer userId, Integer toUserId);
	
	//查询制定的好友关系
	public List<FriendReq> getFriendReqListByUserId(Integer userId,String column) ;
}
