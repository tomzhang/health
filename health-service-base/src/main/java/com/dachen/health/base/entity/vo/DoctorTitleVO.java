package com.dachen.health.base.entity.vo;

import org.mongodb.morphia.annotations.Property;

public class DoctorTitleVO {
	
	@Property("_id")
	private String id;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 启用状态---EnableStatusEnum
	 */
	private Integer enableStatus;
	
	/**
	 * 数据状态---DataStatusEnum
	 */
	private Integer dataStatus;
	
	/**
	 * 职称等级
	 */
	private Integer rank;
	
	private Long lastUpdatorTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getEnableStatus() {
		return enableStatus;
	}

	public void setEnableStatus(Integer enableStatus) {
		this.enableStatus = enableStatus;
	}

	public Integer getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(Integer dataStatus) {
		this.dataStatus = dataStatus;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Long getLastUpdatorTime() {
		return lastUpdatorTime;
	}

	public void setLastUpdatorTime(Long lastUpdatorTime) {
		this.lastUpdatorTime = lastUpdatorTime;
	}
}
