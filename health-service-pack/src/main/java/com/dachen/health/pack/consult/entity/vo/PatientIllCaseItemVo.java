package com.dachen.health.pack.consult.entity.vo;

public class PatientIllCaseItemVo {

	private String illcaseInfoId;
	
	private Integer doctorId;
	
	private String doctorName;
	
	private String mainCondition;
	
	private Long updateTime;

	public String getIllcaseInfoId() {
		return illcaseInfoId;
	}

	public void setIllcaseInfoId(String illcaseInfoId) {
		this.illcaseInfoId = illcaseInfoId;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getMainCondition() {
		return mainCondition;
	}

	public void setMainCondition(String mainCondition) {
		this.mainCondition = mainCondition;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	
}
