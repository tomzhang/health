package com.dachen.health.pack.schedule.entity.vo;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.schedule.entity.po.Schedule.ScheduleType;

public class ScheduleParam extends PageVO {
	private Integer userId;
	private String searchDate;
	private Long startDate;
	private Long endDate;
	private Integer userType;
	private Integer week;
	
	private ScheduleType type;

	private List<String> relationIds;
	
	public Integer getWeek() {
		return week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Integer getUserId() {
		return userId;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getSearchDate() {
		return searchDate;
	}
	
	public void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}
	
	public Long getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}
	
	public Long getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public ScheduleType getType() {
		return type;
	}

	public void setType(ScheduleType type) {
		this.type = type;
	}

	public List<String> getRelationIds() {
		return relationIds;
	}

	public void setRelationIds(List<String> relationIds) {
		this.relationIds = relationIds;
	}
}
