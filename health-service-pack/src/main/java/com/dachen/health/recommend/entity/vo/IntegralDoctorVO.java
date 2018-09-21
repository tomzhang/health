package com.dachen.health.recommend.entity.vo;

public class IntegralDoctorVO {
	private Integer doctorId;
	private String name;
	private String departments;
	private String headPicFileName;
	public Integer getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepartments() {
		return departments;
	}
	public void setDepartments(String departments) {
		this.departments = departments;
	}
	public String getHeadPicFileName() {
		return headPicFileName;
	}
	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}
}
