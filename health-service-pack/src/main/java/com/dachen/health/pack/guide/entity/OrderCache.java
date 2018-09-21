package com.dachen.health.pack.guide.entity;

public class OrderCache {
	private String id;
	
	private ServiceStateEnum state;
	
	private Long startTime;

	private Integer guideId;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ServiceStateEnum getState() {
		return state;
	}

	public void setState(ServiceStateEnum state) {
		this.state = state;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Integer getGuideId() {
		return guideId;
	}

	public void setGuideId(Integer guideId) {
		this.guideId = guideId;
	} 
}
