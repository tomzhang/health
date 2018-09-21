package com.dachen.health.pack.consult.entity.vo;

import com.dachen.util.JSONUtil;

public class DoctorDetail {

	private Integer doctorId;
	
	private String name;
	
	private String title;
	
	private String hospital;
	
	private String doctorGroupName;

	private String introduction;
	
	private String departments;
	
	private String headPicFileName;
	
	private String skill;
	
	private Integer applyStatus;
	
	private String applyMessage;
	
	private Integer consultationPrice;
	
	private String doctorNum;
	
	private String consultationRequired;

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

	public String getDoctorGroupName() {
		return doctorGroupName;
	}

	public void setDoctorGroupName(String doctorGroupName) {
		this.doctorGroupName = doctorGroupName;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
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

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public Integer getApplyStatus() {
		return applyStatus;
	}

	public void setApplyStatus(Integer applyStatus) {
		this.applyStatus = applyStatus;
	}

	public String getApplyMessage() {
		return applyMessage;
	}

	public void setApplyMessage(String applyMessage) {
		this.applyMessage = applyMessage;
	}
	
	public Integer getConsultationPrice() {
		return consultationPrice;
	}

	public void setConsultationPrice(Integer consultationPrice) {
		this.consultationPrice = consultationPrice;
	}

	public String getDoctorNum() {
		return doctorNum;
	}

	public void setDoctorNum(String doctorNum) {
		this.doctorNum = doctorNum;
	}

	public String getConsultationRequired() {
		return consultationRequired;
	}

	public void setConsultationRequired(String consultationRequired) {
		this.consultationRequired = consultationRequired;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

}
