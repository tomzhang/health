package com.dachen.health.group.group.entity.vo;

public class OutpatientVO {

	private String name = "在线门诊";
	
	private String image;
	
	private Long price;
	
	private String onLineState;
	
	private Boolean isFree;
	
	private Long dutyDuration;
	
	private Long taskDuration = 0L;
	
	private String groupId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public String getOnLineState() {
		return onLineState;
	}

	public void setOnLineState(String onLineState) {
		this.onLineState = onLineState;
	}

	public Boolean getIsFree() {
		return isFree;
	}

	public void setIsFree(Boolean isFree) {
		this.isFree = isFree;
	}

	public Long getDutyDuration() {
		return dutyDuration;
	}

	public void setDutyDuration(Long dutyDuration) {
		this.dutyDuration = dutyDuration;
	}

	public Long getTaskDuration() {
		return taskDuration;
	}

	public void setTaskDuration(Long taskDuration) {
		this.taskDuration = taskDuration;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
