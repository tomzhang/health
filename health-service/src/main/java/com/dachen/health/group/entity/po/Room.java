package com.dachen.health.group.entity.po;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import com.alibaba.fastjson.annotation.JSONField;

@Entity(value = "dachen_room",noClassnameStored = true)
@Indexes({ @Index("userId"), @Index("jid"), @Index("userId,jid") })
public class Room {
	// 房间编号
	@Id
	private ObjectId id;

	private String jid;
	// 房间名称
	private String name;
	// 房间描述
	private String desc;
	// 房间主题
//	private String subject;
	// 房间分类
//	private Integer category;
	// 房间标签
//	private List<String> tags;

	// 房间公告
	private Notice notice;

	// 当前成员数
	private Integer userSize;
	// 最大成员数
	private Integer maxUserSize;

	// 创建者Id
	private Integer userId;

	// 创建时间
	private Long createTime;
	// 修改人
	private Integer modifier;
	// 修改时间
	private Long modifyTime;

	/*
	 * 群组类型
	 * roomType =1 表示群组为患者
	 * roomType =2 表示群组为医助
	 * roomType =3 表示群组为医生
	 */
	private Integer roomType;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}


	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notice) {
		this.notice = notice;
	}

	public Integer getUserSize() {
		return userSize;
	}

	public void setUserSize(Integer userSize) {
		this.userSize = userSize;
	}

	public Integer getMaxUserSize() {
		return maxUserSize;
	}

	public void setMaxUserSize(Integer maxUserSize) {
		this.maxUserSize = maxUserSize;
	}

//	public Member getMember() {
//		return member;
//	}
//
//	public void setMember(Member member) {
//		this.member = member;
//	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getModifier() {
		return modifier;
	}

	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}

	public Long getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Long modifyTime) {
		this.modifyTime = modifyTime;
	}


	public Integer getRoomType() {
		return roomType;
	}

	public void setRoomType(Integer roomType) {
		this.roomType = roomType;
	}
}
