package com.dachen.health.pack.patient.model;

import java.util.List;

public class PatientDiseaseParam {
	private List<Integer> doctorIds;
	private List<Integer> userIds;
	private String groupId;
	private Integer doctorId;
	public List<Integer> getDoctorIds() {
		return doctorIds;
	}
	public void setDoctorIds(List<Integer> doctorIds) {
		this.doctorIds = doctorIds;
	}
	public List<Integer> getUserIds() {
		return userIds;
	}
	public void setUserIds(List<Integer> userIds) {
		this.userIds = userIds;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public Integer getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}
}
