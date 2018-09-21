package com.dachen.health.pack.pack.entity.po;

import java.util.List;

public class PackParam2 {
	
	
	private Integer doctorId;
	
	
	private Long textMin;
	
	private Long textMax;
	
	private Long phoneMin;
	
	private Long phoneMax;
	
	private Long carePlanMin;
	
	private Long carePlanMax;

	private List<Integer> doctorIds;
	
	private Integer type;
	
	private String groupId;
	
	private List<String> skipGroupIds;
	
	public List<String> getSkipGroupIds() {
		return skipGroupIds;
	}

	public void setSkipGroupIds(List<String> skipGroupIds) {
		this.skipGroupIds = skipGroupIds;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<Integer> getDoctorIds() {
		return doctorIds;
	}

	public void setDoctorIds(List<Integer> doctorIds) {
		this.doctorIds = doctorIds;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getTextMin() {
		return textMin;
	}

	public void setTextMin(Long textMin) {
		this.textMin = textMin;
	}

	public Long getTextMax() {
		return textMax;
	}

	public void setTextMax(Long textMax) {
		this.textMax = textMax;
	}

	public Long getPhoneMin() {
		return phoneMin;
	}

	public void setPhoneMin(Long phoneMin) {
		this.phoneMin = phoneMin;
	}

	public Long getPhoneMax() {
		return phoneMax;
	}

	public void setPhoneMax(Long phoneMax) {
		this.phoneMax = phoneMax;
	}

	public Long getCarePlanMin() {
		return carePlanMin;
	}

	public void setCarePlanMin(Long carePlanMin) {
		this.carePlanMin = carePlanMin;
	}

	public Long getCarePlanMax() {
		return carePlanMax;
	}

	public void setCarePlanMax(Long carePlanMax) {
		this.carePlanMax = carePlanMax;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	 

	 
	
	
	
}
