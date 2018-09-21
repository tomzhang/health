package com.dachen.health.pack.consult.entity.vo;

public class ConsultationOrderVo {
	
	private Integer orderId;
	
	private Integer doctorId;
	
	private String illCaseInfoId;
	
	private String name;
	
	private String headPicFileName;
	
	private String departments;
	
	private String hospital;
	
	private String patientName;
	
	private Integer orderStatus;
	
	private Integer roleType;
	
	private String msgGroupId;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getIllCaseInfoId() {
		return illCaseInfoId;
	}

	public void setIllCaseInfoId(String illCaseInfoId) {
		this.illCaseInfoId = illCaseInfoId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
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

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getRoleType() {
		return roleType;
	}

	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getMsgGroupId() {
		return msgGroupId;
	}

	public void setMsgGroupId(String msgGroupId) {
		this.msgGroupId = msgGroupId;
	}

	@Override
	public String toString() {
		return "ConsultationOrderVo [orderId=" + orderId + ", illCaseInfoId=" + illCaseInfoId + ", name=" + name
				+ ", headPicFileName=" + headPicFileName + ", departments=" + departments + ", hospital=" + hospital
				+ ", patientName=" + patientName + ", orderStatus=" + orderStatus + ", roleType=" + roleType + "]";
	}
	
}
