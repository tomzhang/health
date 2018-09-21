package com.dachen.health.group.entity.vo;

import org.bson.types.ObjectId;

public class NoticeVO {
	/**
	 * ID
	 */
	private ObjectId id;
	
	/**
	 * 群组ID
	 */
	private ObjectId roomId;
	
	/**
	 * 公告文本
	 */
	private String text;
	
	/**
	 * 公告发布人id
	 */
	private String userId;
	

	/**
	 * 公告发布人名称
	 */
	private String nickname;

	/**
	 * 公告发布时间
	 */
	private String time;


	public ObjectId getId() {
		return id;
	}


	public void setId(ObjectId id) {
		this.id = id;
	}


	public ObjectId getRoomId() {
		return roomId;
	}


	public void setRoomId(ObjectId roomId) {
		this.roomId = roomId;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getNickname() {
		return nickname;
	}


	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}
}
