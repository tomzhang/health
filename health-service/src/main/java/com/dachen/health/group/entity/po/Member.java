package com.dachen.health.group.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import com.alibaba.fastjson.annotation.JSONField;

@Entity(value = "dachen_room_member",noClassnameStored=true)
@Indexes({ @Index("roomId"), @Index("userId"), @Index("roomId,userId"),
		@Index("userId,role") })
public class Member {
	@Id
	@JSONField(serialize = false)
	private ObjectId id;

	// 房间Id
	@JSONField(serialize = false)
	private ObjectId roomId;

	// 成员Id
	private Integer userId;

	// 成员昵称
	private String nickname;

	// 成员角色：1=创建者、2=管理员、3=成员
	private Integer role;

	// 大于当前时间时禁止发言
	private Long talkTime;

	// 最后一次互动时间
	private Long active;

	// 加入时间
	private Long createTime;

	// 修改时间
	private Long modifyTime;
	
	// 是否接收消息 0=否、1=是
	private Integer isReceive=1 ;

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

//	public Integer getSub() {
//		return sub;
//	}
//
//	public void setSub(Integer sub) {
//		this.sub = sub;
//	}

	public Long getTalkTime() {
		return talkTime;
	}

	public void setTalkTime(Long talkTime) {
		this.talkTime = talkTime;
	}

	public Long getActive() {
		return active;
	}

	public void setActive(Long active) {
		this.active = active;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Long modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Integer getIsReceive() {
		return isReceive;
	}

	public void setIsReceive(Integer isReceive) {
		this.isReceive = isReceive;
	}

}
