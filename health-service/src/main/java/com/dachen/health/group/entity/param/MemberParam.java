package com.dachen.health.group.entity.param;

import org.bson.types.ObjectId;

public class MemberParam {
	// 房间Id
	private ObjectId roomId;

	// 成员Id
	private Integer userId;

	// 成员昵称
	private String nickname;

	// 成员角色：1=创建者、2=管理员、3=成员
	private Integer role;

	// 大于当前时间时禁止发言
	private Long talkTime;
	
	// 是否接收消息 0=否、1=是
	private Integer isReceive=1 ;

	public ObjectId getRoomId() {
		return roomId;
	}

	public void setRoomId(ObjectId roomId) {
		this.roomId = roomId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public Long getTalkTime() {
		return talkTime;
	}

	public void setTalkTime(Long talkTime) {
		this.talkTime = talkTime;
	}

	public Integer getIsReceive() {
		return isReceive;
	}

	public void setIsReceive(Integer isReceive) {
		this.isReceive = isReceive;
	}
}
