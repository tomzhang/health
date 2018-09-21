package com.dachen.health.pack.order.entity.vo;

public class DoctoreRatioVO {
	
	private String doctorePic;
	
	private String doctoreName;
	
	private Integer ratioNum;
	
	private Integer receiveRemind;
	
	private Integer userId;

	/**
	 * 类型 1：主医生，默认为0
	 */
	private Integer groupType=0;//1:主医生
	
	private String departments;//科室
	
	private String title;//职称
	
	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getGroupType() {
		return groupType;
	}

	public void setGroupType(Integer groupType) {
		this.groupType = groupType;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getDoctorePic() {
		return doctorePic;
	}

	public void setDoctorePic(String doctorePic) {
		this.doctorePic = doctorePic;
	}

	public String getDoctoreName() {
		return doctoreName;
	}

	public void setDoctoreName(String doctoreName) {
		this.doctoreName = doctoreName;
	}

	public Integer getRatioNum() {
		return ratioNum;
	}

	public void setRatioNum(Integer ratioNum) {
		this.ratioNum = ratioNum;
	}

	public Integer getReceiveRemind() {
		return receiveRemind;
	}

	public void setReceiveRemind(Integer receiveRemind) {
		this.receiveRemind = receiveRemind;
	}

}
