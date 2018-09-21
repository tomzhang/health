package com.dachen.health.pack.guide.entity.vo;

import com.dachen.health.pack.guide.entity.po.DoctorTimePO;
import com.dachen.health.pack.patient.model.Disease;
import com.dachen.util.JSONUtil;

public class AppointmentGuideOrderDetail {

	private Integer orderId;
	
	private String orderNo;
	
	private Integer doctorId;
	
	private String doctorName;
	
	private String title;
	
	private String departments;
	
	private String doctorTel;
	
	private String headPicFileName;
	
	private Integer patientId;
	
	private String patientName;
	
	private String patientTel;
	
	private String patientAgeStr;
	
	private String patientSex;
	
	private String patientArea;
	
	private Disease disease;
	
	private DoctorTimePO remark;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

	public String getDoctorTel() {
		return doctorTel;
	}

	public void setDoctorTel(String doctorTel) {
		this.doctorTel = doctorTel;
	}

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientTel() {
		return patientTel;
	}

	public void setPatientTel(String patientTel) {
		this.patientTel = patientTel;
	}

	
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getPatientAgeStr() {
		return patientAgeStr;
	}

	public void setPatientAgeStr(String patientAgeStr) {
		this.patientAgeStr = patientAgeStr;
	}

	public String getPatientSex() {
		return patientSex;
	}

	public void setPatientSex(String patientSex) {
		this.patientSex = patientSex;
	}

	public String getPatientArea() {
		return patientArea;
	}

	public void setPatientArea(String patientArea) {
		this.patientArea = patientArea;
	}

	public DoctorTimePO getRemark() {
		return remark;
	}

	public void setRemark(DoctorTimePO remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

}
