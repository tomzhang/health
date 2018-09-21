package com.dachen.health.group.service.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.entity.param.MemberParam;
import com.dachen.health.group.entity.param.RoomParam;
import com.dachen.health.group.entity.po.Member;
import com.dachen.health.group.entity.po.Room;
import com.dachen.health.group.entity.vo.MemberVO;
import com.dachen.health.group.entity.vo.RoomVO;
import com.dachen.health.group.service.RoomManager;
import com.dachen.util.BeanUtil;
import com.dachen.util.DateUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Service(RoomManager.BEAN_ID)
public class RoomManagerImplForIM implements RoomManager {

	@Autowired
	private UserRepository userRepository;


	@Override
	public RoomVO add(User user, RoomParam example, List<Integer> memberUserIdList) {
		Long currentTimeSeconds = DateUtil.currentTimeSeconds();
		
		Room entity = new Room();
		entity.setId(ObjectId.get());
		entity.setJid(example.getJid());
		entity.setName(StringUtil.isEmpty(example.getName())?"我的群组":example.getName());// 必须
		entity.setDesc(example.getDesc()==null?"":entity.getDesc());// 必须
		entity.setUserSize(0);
		entity.setMaxUserSize(example.getMaxUserSize()==null?50:example.getMaxUserSize());
		
		entity.setUserId(user.getUserId());
//		entity.setNickname(user.getName()==null?user.getName():user.getName());
		entity.setCreateTime(currentTimeSeconds);
		entity.setModifier(user.getUserId());
		entity.setModifyTime(currentTimeSeconds);

		// 保存房间配置

		// 创建者
		Member member = new Member();
		member.setActive(currentTimeSeconds);
		member.setCreateTime(member.getActive());
		member.setModifyTime(0L);
		member.setNickname(user.getName());
		member.setRole(1);
		member.setRoomId(entity.getId());
		member.setIsReceive(1);
		member.setTalkTime(0L);
		member.setUserId(user.getUserId());
		// 初试成员列表
		List<Member> memberList = Lists.newArrayList(member);
		// 初试成员列表不为空
		if (null != memberUserIdList && !memberUserIdList.isEmpty()) 
		{
			ObjectId roomId = entity.getId();
			for (int userId : memberUserIdList) 
			{
				User _user = userRepository.getUser(userId);
				// 成员
				Member _member = new Member();
				_member.setActive(currentTimeSeconds);
				_member.setCreateTime(currentTimeSeconds);
				_member.setModifyTime(0L);
				_member.setNickname(_user.getName());
				_member.setRole(3);
				_member.setRoomId(roomId);
				_member.setTalkTime(0L);
				_member.setUserId(_user.getUserId());

				memberList.add(_member);
			}
		}
		// 保存成员列表


		RoomVO roomVO = (RoomVO) BeanUtil.copy(entity, RoomVO.class);
		roomVO.setNickname(member.getNickname());
//		BeanUtils.copyProperties(entity, roomVO);
		return roomVO;
	}

	@Override
	public void delete(User user, ObjectId roomId) {
		// IMPORTANT 1-3、删房间推送-已改

	}

	@Override
	public void update(User user, ObjectId roomId, String roomName,
			String notice, String desc) {
		BasicDBObject q = new BasicDBObject("_id", roomId);
		BasicDBObject o = new BasicDBObject();
		BasicDBObject values = new BasicDBObject();
		if (!StringUtil.isEmpty(roomName)) {
			// o.put("$set", new BasicDBObject("name", roomName));
			values.put("name", roomName);

			// IMPORTANT 1-2、改房间名推送-已改
		}
		if (!StringUtil.isEmpty(desc)) {
			// o.put("$set", new BasicDBObject("desc", desc));
			values.put("desc", desc);
		}
		if (!StringUtil.isEmpty(notice)) {
			BasicDBObject dbObj = new BasicDBObject();
			dbObj.put("roomId", roomId);
			dbObj.put("text", notice);
			dbObj.put("userId", user.getUserId());
			dbObj.put("nickname", user.getName());
			dbObj.put("time", DateUtil.currentTimeSeconds());

			// 更新最新公告
			// o.put("$set", new BasicDBObject("notice", dbObj));
			values.put("notice", dbObj);

			// 新增历史公告记录

			// IMPORTANT 1-5、改公告推送-已改
		}
		o.put("$set", values);
	}

	@Override
	public RoomVO get(ObjectId roomId) {

		RoomVO roomVO = new RoomVO();
		if (null != roomVO) {
			// Member member;
			List<MemberVO> members = getMemberList(roomId);

			// room.setMember(member);
			roomVO.setMembers(members);
		}
		return roomVO;
	}
	




	@Override
	public void deleteMember(User user, ObjectId roomId, int userId) {
		User toUser = userRepository.getUser(userId);

		// IMPORTANT 1-4、删除成员推送-已改
	}

	@Override
	public void updateMember(User user, ObjectId roomId,
			List<Integer> userIdList) {
		for (int userId : userIdList) {
			MemberParam _member = new MemberParam();
			_member.setUserId(userId);
			_member.setRole(3);
			updateMember(user, roomId, _member);
		}
	}




	@Override
	public void join(int userId, ObjectId roomId, int type) {
		MemberParam member = new MemberParam();
		member.setUserId(userId);
		member.setRole(1 == type ? 1 : 3);
		updateMember(null, roomId, member);
	}



	@Override
	public void leave(int userId, ObjectId roomId) {

	}
	
	
	/**
	 * 用户设置是否接收群消息
	 * @param roomId
	 * @param userId
	 * @param isReceive（0表示屏蔽消息，1表示接收消息）
	 * @return
	 */
	@Override
	public void refuseMessage(int userId, ObjectId roomId, int isReceive) 
	{
		DBObject condition = new BasicDBObject("roomId", roomId).append("userId", userId);
		DBObject updateObj =new BasicDBObject("$set",new BasicDBObject("isReceive", isReceive));
	}

	@Override
	public List<RoomVO> selectList(int pageIndex, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object selectHistoryList(int userId, int type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object selectHistoryList(int userId, int type, int pageIndex,
			int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateMember(User user, ObjectId roomId, MemberParam member) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getMember(ObjectId roomId, int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MemberVO> getMemberList(ObjectId roomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setAdministator(ObjectId roomId, int userId,
			int currentUserId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cancalAdministator(ObjectId roomId, int userId,
			int currentUserId) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
