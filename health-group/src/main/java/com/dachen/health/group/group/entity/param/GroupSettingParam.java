package com.dachen.health.group.group.entity.param;

import java.util.List;

public class GroupSettingParam {
	
	private String docGroupId;
	//专长
	private List<String> specialtyIds;
	
	private Integer weight;
	
	private List<Integer> expertIds;
	
	private Integer doctorId;
	
	//消息免打扰设置 
	private String isOpenMsg;

	public String getDocGroupId() {
		return docGroupId;
	}

	public void setDocGroupId(String docGroupId) {
		this.docGroupId = docGroupId;
	}

	public List<String> getSpecialtyIds() {
		return specialtyIds;
	}

	public void setSpecialtyIds(List<String> specialtyIds) {
		this.specialtyIds = specialtyIds;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public List<Integer> getExpertIds() {
		return expertIds;
	}

	public void setExpertIds(List<Integer> expertIds) {
		this.expertIds = expertIds;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getIsOpenMsg() {
		return isOpenMsg;
	}

	public void setIsOpenMsg(String isOpenMsg) {
		this.isOpenMsg = isOpenMsg;
	}
	
}
