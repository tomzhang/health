package com.dachen.health.base.entity.vo;

import org.mongodb.morphia.annotations.Property;

public class HospitalDeptVO {
	@Property("_id")
	private String id;
	
	/**
	 * 上级科室
	 */
	private String parentId;
	
	private String name;
	
	/**
	 * 启用状态---EnableStatusEnum
	 */
	private Integer enableStatus;
	
	/**
	 * 数据状态---DataStatusEnum
	 */
	private Integer dataStatus;
	
	
	private Integer isLeaf;
	
	/**
	 * 权重
	 */
	private Integer weight;
	
	private Long lastUpdatorTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
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

	public Integer getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Long getLastUpdatorTime() {
		return lastUpdatorTime;
	}

	public void setLastUpdatorTime(Long lastUpdatorTime) {
		this.lastUpdatorTime = lastUpdatorTime;
	}
	
}
