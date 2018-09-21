package com.dachen.health.pack.consult.entity.vo;

public class OrderUserInfoVo {

	private Integer userId;
	
	private Integer patientId;
	
	private String name;
	
	private String title;
	
	private String hospital;
	
	private String departments;
	
	private String doctorGroupName;
	
	private String headPicFileName;
	
	private String skill;
	
    private String introduction;
	
	//3:医生，1：患者
	private Integer roleType;
	
	private Integer cureNum;
	
	private String ageStr;
	
	private Integer sex;// 性别1男，2女 3 保密

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getDoctorGroupName() {
		return doctorGroupName;
	}

	public void setDoctorGroupName(String doctorGroupName) {
		this.doctorGroupName = doctorGroupName;
	}

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}

	public Integer getRoleType() {
		return roleType;
	}

	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
	}

	public Integer getCureNum() {
		return cureNum;
	}

	public void setCureNum(Integer cureNum) {
		this.cureNum = cureNum;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getAgeStr() {
		return ageStr;
	}

	public void setAgeStr(String ageStr) {
		this.ageStr = ageStr;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	
}
