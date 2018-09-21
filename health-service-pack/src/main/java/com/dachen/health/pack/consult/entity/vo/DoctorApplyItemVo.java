package com.dachen.health.pack.consult.entity.vo;

import com.dachen.util.JSONUtil;

public class DoctorApplyItemVo {

	private String  consultationApplyFriendId;
	
	private Integer doctorId;
	
	private String  name;
	
	private String title;
	
	private String departments;
	
	private String hospital;
	
	private String headPicFilleName;
	
	private String applyMessage;
	
	private Long  applyTime;
	
	//1、已申请，2、已被对方申请，3、已是好友，4、没关系
	private Integer applyType;

	public String getConsultationApplyFriendId() {
		return consultationApplyFriendId;
	}

	public void setConsultationApplyFriendId(String consultationApplyFriendId) {
		this.consultationApplyFriendId = consultationApplyFriendId;
	}

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

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getHeadPicFilleName() {
		return headPicFilleName;
	}

	public void setHeadPicFilleName(String headPicFilleName) {
		this.headPicFilleName = headPicFilleName;
	}

	public Long getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Long applyTime) {
		this.applyTime = applyTime;
	}

	public Integer getApplyType() {
		return applyType;
	}

	public void setApplyType(Integer applyType) {
		this.applyType = applyType;
	}

	public String getApplyMessage() {
		return applyMessage;
	}

	public void setApplyMessage(String applyMessage) {
		this.applyMessage = applyMessage;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
	
	
	
}
