package com.dachen.health.group.group.entity.vo;

import com.dachen.health.commons.constants.UserEnum;

public class GroupDoctorInfoVO {
	
	private Integer doctorId;
	
	private String doctorName;
	
	private String doctorPath;
	
	private String skill;
	
	private String specialist;
	
	private String position;
	
	private Integer inquiryNum;
	
	private Integer expertNum;
	
	private String departments;
	
	private String isFree;
	
	/*职称排行*/
    public String titleRank;
    
    private String onLineState;
    
    // 是否开通了预约服务    1：是，非1：否
    private String isPackService;
    
    private Integer role;//医生角色
    
	public Integer getRole() {
		if(null==role || role.intValue()==0)
		{	
			role=UserEnum.DoctorRole.doctor.getIndex();
		}
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public String getIsPackService() {
		return isPackService;
	}

	public void setIsPackService(String isPackService) {
		this.isPackService = isPackService;
	}

	public String getOnLineState() {
		return onLineState;
	}

	public void setOnLineState(String onLineState) {
		this.onLineState = onLineState;
	}

	public String getTitleRank() {
		return titleRank;
	}

	public void setTitleRank(String titleRank) {
		this.titleRank = titleRank;
	}

	public String getIsFree() {
		return isFree;
	}

	public void setIsFree(String isFree) {
		this.isFree = isFree;
	}

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
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

	public String getDoctorPath() {
		return doctorPath;
	}

	public void setDoctorPath(String doctorPath) {
		this.doctorPath = doctorPath;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getSpecialist() {
		return specialist;
	}

	public void setSpecialist(String specialist) {
		this.specialist = specialist;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Integer getInquiryNum() {
		return inquiryNum;
	}

	public void setInquiryNum(Integer inquiryNum) {
		this.inquiryNum = inquiryNum;
	}

	public Integer getExpertNum() {
		return expertNum;
	}

	public void setExpertNum(Integer expertNum) {
		this.expertNum = expertNum;
	}
	
}
