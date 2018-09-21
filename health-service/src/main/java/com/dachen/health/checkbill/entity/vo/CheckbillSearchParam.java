package com.dachen.health.checkbill.entity.vo;

import java.util.List;

import com.dachen.commons.page.PageVO;

public class CheckbillSearchParam extends PageVO{

	private List<Integer> patientIds;
	
	private Integer userId;

	public List<Integer> getPatientIds() {
		return patientIds;
	}

	public void setPatientIds(List<Integer> patientIds) {
		this.patientIds = patientIds;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "CheckbillSearchParam [patientIds=" + patientIds + ", userId=" + userId + "]";
	}

}
