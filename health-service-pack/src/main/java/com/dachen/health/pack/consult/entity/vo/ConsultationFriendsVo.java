package com.dachen.health.pack.consult.entity.vo;

import com.dachen.util.JSONUtil;

public class ConsultationFriendsVo {

	private Integer userId;
	
	private String name;
	
	private String title;
	
	private String hospital;
	
	private String headPicFilleName;
	
	private Integer consultationPrice;
	
	private String departments;
	
	private String groupName;
	
	private Integer consultationCount;
	
	private String consultationRequired;
	
	//1、已申请，2、已被对方申请，3、已是好友，4、没关系
	private Integer applyType;

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

	public String getHeadPicFilleName() {
		return headPicFilleName;
	}

	public void setHeadPicFilleName(String headPicFilleName) {
		this.headPicFilleName = headPicFilleName;
	}

	public Integer getConsultationPrice() {
		return consultationPrice;
	}

	public void setConsultationPrice(Integer consultationPrice) {
		this.consultationPrice = consultationPrice;
	}
	
	public Integer getApplyType() {
		return applyType;
	}

	public void setApplyType(Integer applyType) {
		this.applyType = applyType;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
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

	public Integer getConsultationCount() {
		return consultationCount;
	}

	public void setConsultationCount(Integer consultationCount) {
		this.consultationCount = consultationCount;
	}

	public String getConsultationRequired() {
		return consultationRequired;
	}

	public void setConsultationRequired(String consultationRequired) {
		this.consultationRequired = consultationRequired;
	}
	
	
}
