package com.dachen.health.api.client.schedule.entity;

import java.io.Serializable;

public class CCareItemSchedule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String careItemId;
	private String careItemTitle;
	private Integer careItemType;
	private Long fullSendTime;
	
	private String carePlanId;
	private String carePlanName;
	
	private Integer orderId;
	private Integer userId;
	private Long createTime;
	public String getCareItemId() {
		return careItemId;
	}
	public void setCareItemId(String careItemId) {
		this.careItemId = careItemId;
	}
	public String getCareItemTitle() {
		return careItemTitle;
	}
	public void setCareItemTitle(String careItemTitle) {
		this.careItemTitle = careItemTitle;
	}
	public Integer getCareItemType() {
		return careItemType;
	}
	public void setCareItemType(Integer careItemType) {
		this.careItemType = careItemType;
	}
	public Long getFullSendTime() {
		return fullSendTime;
	}
	public void setFullSendTime(Long fullSendTime) {
		this.fullSendTime = fullSendTime;
	}
	public String getCarePlanId() {
		return carePlanId;
	}
	public void setCarePlanId(String carePlanId) {
		this.carePlanId = carePlanId;
	}
	public String getCarePlanName() {
		return carePlanName;
	}
	public void setCarePlanName(String carePlanName) {
		this.carePlanName = carePlanName;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
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

}
