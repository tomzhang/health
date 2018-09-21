package com.dachen.health.group.entity.vo;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

public class RoomVO {
   // 群组id
	@Id
	private ObjectId id;
	
	/*app端创建，用于发送消息的*/
	private String jid;
	
	/* 群组名称*/
	private String name;
	
	/* 群组描述*/
	private String desc;
	
	/* 当前成员数*/
	private Integer userSize;
	
	/* 最大成员数*/
	private Integer maxUserSize;

	/* 创建者Id*/
	private Integer userId;
	/* 创建者昵称 */
	private String nickname;
	/* 创建时间*/
	private Long createTime;
	
	/*
	 * 群组类型
	 * roomType =1 表示群组为患者
	 * roomType =2 表示群组为医助
	 * roomType =3 表示群组为医生
	 */
	private Integer roomType=0;
	
	// 修改人
	private Integer modifier;
	// 修改时间
	private Long modifyTime;

	/* 公告列表*/
	private List<NoticeVO> notices;

	/* 成员列表*/
	private List<MemberVO> members;
	
	//房间标签
	//private List<String> tags;

	//房间公告
	//private NoticeVO notice;
	// 自己
	//private MemberVO member;
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

//	public NoticeVO getNotice() {
//		return notice;
//	}
//
//	public void setNotice(NoticeVO notice) {
//		this.notice = notice;
//	}

	public List<NoticeVO> getNotices() {
		return notices;
	}

	public void setNotices(List<NoticeVO> notices) {
		this.notices = notices;
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

//	public MemberVO getMember() {
//		return member;
//	}
//
//	public void setMember(MemberVO member) {
//		this.member = member;
//	}

	public List<MemberVO> getMembers() {
		return members;
	}

	public void setMembers(List<MemberVO> members) {
		this.members = members;
	}

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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
