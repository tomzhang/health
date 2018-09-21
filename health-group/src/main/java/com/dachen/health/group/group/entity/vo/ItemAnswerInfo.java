package com.dachen.health.group.group.entity.vo;

public class ItemAnswerInfo {
	private String itemId;
	private Integer type;
	private Long planTime;
	private String planTimeStr;
	private Long answerTime;
	private String answerTimeStr;
	private Integer status;
	private String statusDesc;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Long getPlanTime() {
		return planTime;
	}

	public void setPlanTime(Long planTime) {
		this.planTime = planTime;
	}

	public Long getAnswerTime() {
		return answerTime;
	}

	public void setAnswerTime(Long answerTime) {
		this.answerTime = answerTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getPlanTimeStr() {
		return planTimeStr;
	}

	public void setPlanTimeStr(String planTimeStr) {
		this.planTimeStr = planTimeStr;
	}

	public String getAnswerTimeStr() {
		return answerTimeStr;
	}

	public void setAnswerTimeStr(String answerTimeStr) {
		this.answerTimeStr = answerTimeStr;
	}

}
