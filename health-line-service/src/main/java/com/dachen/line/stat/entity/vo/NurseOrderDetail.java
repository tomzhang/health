package com.dachen.line.stat.entity.vo;

import java.util.List;

public class NurseOrderDetail {
	
	/**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 订单类型（1：套餐订单；2：报到；3：门诊订单）
     */
    private String orderType;
    
    private String nurseName;
    
    private String patientName;
    
    private String nurseTelephone;
    
    private String patientTelephone;
    
    private String visitingTime;
    
    private String attachmentDoctorName;
    
    private String checkItems;
    
    private String message;
    
    private Object hospitalList;
    
    private Object departList;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public void setVisitingTime(String visitingTime) {
		this.visitingTime = visitingTime;
	}

	public String getNurseName() {
		return nurseName;
	}

	public void setNurseName(String nurseName) {
		this.nurseName = nurseName;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getNurseTelephone() {
		return nurseTelephone;
	}

	public void setNurseTelephone(String nurseTelephone) {
		this.nurseTelephone = nurseTelephone;
	}

	public String getPatientTelephone() {
		return patientTelephone;
	}

	public void setPatientTelephone(String patientTelephone) {
		this.patientTelephone = patientTelephone;
	}

	public String getVisitingTime() {
		return visitingTime;
	}

	public String getAttachmentDoctorName() {
		return attachmentDoctorName;
	}

	public void setAttachmentDoctorName(String attachmentDoctorName) {
		this.attachmentDoctorName = attachmentDoctorName;
	}

	public String getCheckItems() {
		return checkItems;
	}

	public void setCheckItems(String checkItems) {
		this.checkItems = checkItems;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getHospitalList() {
		return hospitalList;
	}

	public void setHospitalList(Object hospitalList) {
		this.hospitalList = hospitalList;
	}

	public Object getDepartList() {
		return departList;
	}

	public void setDepartList(Object departList) {
		this.departList = departList;
	}

}
