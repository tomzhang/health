package com.dachen.health.api.client.schedule.entity;

import java.io.Serializable;

public class CSchedule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	/**
	 * 日程主题
	 */
	private String title;
	
	/**
	 * 日程类型
	 * @see ScheduleType
	 */
	private Integer type;
	
	/**
	 * 关系Id，如订单Id
	 */
	private String relationId;
	
	/**
	 * 用户Id
	 */
	private Integer userId;
	
	/**
	 * 日程发生时间
	 */
	private Long scheduleTime;
	
	/**
	 * 创建时间
	 */
	private Long createTime;
	
	/**
	 * 发送提醒时间
	 */
	private Long sendTime;
	
	private String careItemId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getRelationId() {
		return relationId;
	}

	public void setRelationId(String relationId) {
		this.relationId = relationId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Long getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(Long scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getSendTime() {
		return sendTime;
	}

	public void setSendTime(Long sendTime) {
		this.sendTime = sendTime;
	}

	public String getCareItemId() {
		return careItemId;
	}

	public void setCareItemId(String careItemId) {
		this.careItemId = careItemId;
	}

}
