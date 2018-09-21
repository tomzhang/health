package com.dachen.health.commons.vo;

import java.util.List;

public class CarePlanDoctorVO {
	
	 /* 集团名称 */
    private String groupName;
    
    /* 擅长病种 */
    private String disease;
    
    /* 擅长介绍 */
    private String skill;
    
    /* 治疗数量 */
    private Integer cureNum;//就诊人数
    
    private Integer doctorId;//医生ID

    private String doctorName;//医生名称

    private String headPicFileName;//医生头像
    
    private String departments;
    
    private String title;//职称
    
    private Integer groupType=0;//1:主医生，0：副医生
    
    private List<String> diseases;
    
    
	public Integer getGroupType() {
		return groupType;
	}

	public void setGroupType(Integer groupType) {
		this.groupType = groupType;
	}

	public List<String> getDiseases() {
		return diseases;
	}

	public void setDiseases(List<String> diseases) {
		this.diseases = diseases;
	}

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public Integer getCureNum() {
		return cureNum;
	}

	public void setCureNum(Integer cureNum) {
		this.cureNum = cureNum;
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

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
    
}
