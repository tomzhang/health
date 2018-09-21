package com.dachen.health.group.entity.param;


public class RoomParam {
	
	private String jid;
	// 房间名称
	private String name;
	// 房间描述
	private String desc;
	// 最大成员数
	private Integer maxUserSize;
	// 创建者Id
	private Integer userId;
	
	/*
	 * 群组类型
	 * roomType =1 表示群组为患者
	 * roomType =2 表示群组为医助
	 * roomType =3 表示群组为医生
	 */
	private Integer roomType;
	
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
	public Integer getMaxUserSize() {
		return maxUserSize;
	}
	public void setMaxUserSize(Integer maxUserSize) {
		this.maxUserSize = maxUserSize;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getRoomType() {
		return roomType;
	}
	public void setRoomType(Integer roomType) {
		this.roomType = roomType;
	}
}
