package com.dachen.health.api.client.schedule.entity;

import java.io.Serializable;

public class CCareItemScheduleUpdate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8884536677956210900L;
	
	private String careItemId;
	
	private Long fullSendTime;

	public String getCareItemId() {
		return careItemId;
	}

	public void setCareItemId(String careItemId) {
		this.careItemId = careItemId;
	}

	public Long getFullSendTime() {
		return fullSendTime;
	}

	public void setFullSendTime(Long fullSendTime) {
		this.fullSendTime = fullSendTime;
	}

}
