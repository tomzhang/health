package com.dachen.health.pack.order.entity.vo;

public class OrderDoctorDetail {

	private Integer userId;
	
	private String name;
	
	private String title;
	
	private String hospital;
	
	private String headPicFilleName;
	
	private String departments;
	
	private String groupName;
	
	private Integer doctorRole;
	
	private String telephone;

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

	public Integer getDoctorRole() {
		return doctorRole;
	}

	public void setDoctorRole(Integer doctorRole) {
		this.doctorRole = doctorRole;
	}
	
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@Override
	public String toString() {
		return "OrderDoctorDetail [userId=" + userId + ", name=" + name + ", title=" + title + ", hospital=" + hospital
				+ ", headPicFilleName=" + headPicFilleName + ", departments=" + departments + ", groupName=" + groupName
				+ "]";
	}
	
}
