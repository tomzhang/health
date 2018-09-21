package com.dachen.health.group.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import com.alibaba.fastjson.annotation.JSONField;

@Entity(value = "dachen_room_notice",noClassnameStored=true)
@Indexes({ @Index("roomId"), @Index("userId") })
public class Notice {
	@Id
	@JSONField(serialize = false)
	private ObjectId id;
	@JSONField(serialize = false)
	private ObjectId roomId;
	private String text;
	private String userId;
	private String nickname;
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

