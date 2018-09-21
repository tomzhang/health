package com.dachen.health.commons.vo;
/**
 * 
 * @Description 游客vo
 * @title GuestUser
 * @author liminng
 * @data 2016年6月1日
 */
public class GuestUser {
	private String id;
	private String deviceID;
	private Long createTime;
	private Long activeTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public Long getActiveTime() {
		return activeTime;
	}
	public void setActiveTime(Long activeTime) {
		this.activeTime = activeTime;
	}
	
	
}
