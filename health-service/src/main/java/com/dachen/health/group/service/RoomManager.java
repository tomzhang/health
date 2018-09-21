package com.dachen.health.group.service;

import java.util.List;

import org.bson.types.ObjectId;

import com.dachen.health.commons.vo.User;
import com.dachen.health.group.entity.param.MemberParam;
import com.dachen.health.group.entity.param.RoomParam;
import com.dachen.health.group.entity.vo.MemberVO;
import com.dachen.health.group.entity.vo.RoomVO;

public interface RoomManager {
	public static final String BEAN_ID = "RoomManagerImpl2";

	RoomVO add(User user, RoomParam room, List<Integer> idList);

	void delete(User user, ObjectId roomId);

	void update(User user, ObjectId roomId, String roomName, String notice,
			String desc);

	RoomVO get(ObjectId roomId);

	@Deprecated
	List<RoomVO> selectList(int pageIndex, int pageSize);

	Object selectHistoryList(int userId, int type);
//
	Object selectHistoryList(int userId, int type, int pageIndex, int pageSize);

	void deleteMember(User user, ObjectId roomId, int userId);

	void updateMember(User user, ObjectId roomId, MemberParam member);

	void updateMember(User user, ObjectId roomId, List<Integer> idList);

	Object getMember(ObjectId roomId, int userId);

	List<MemberVO> getMemberList(ObjectId roomId);

	void join(int userId, ObjectId roomId, int type);

	void leave(int userId, ObjectId roomId);
	
	public void refuseMessage(int userId, ObjectId roomId, int isReceive) ;
	
	boolean setAdministator(ObjectId roomId,int userId,int currentUserId);
	
	boolean cancalAdministator(ObjectId roomId,int userId,int currentUserId);

}
