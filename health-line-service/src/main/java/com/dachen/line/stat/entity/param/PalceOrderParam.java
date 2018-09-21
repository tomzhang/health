package com.dachen.line.stat.entity.param;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;

public class PalceOrderParam {
	private String productId;// 产品id
	private String checkId;// 检查项id
	private Integer patientId;// 患者id
	private String patientName;// 就诊人 默认为患者的名称
	private String patientTel;// 就诊人的电话 默认为患者的手机号
	private String doctorName;// 挂号医生 可以由患者用户修改
	private Integer doctorId;// 平台医生的ID 由平台医生推荐的线下服务
	private String appointmentTime;// 预约的时间戳 精确到分钟
	private String remark;// 订单备注

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	private List<String> hospital = new ArrayList<String>();
	private List<String> department = new ArrayList<String>();
	private List<String> checkItem = new ArrayList<String>();

	private String hospitals = null;
	private String departments = null;
	private String checkItems = null;

	public List<String> getHospital() {

		hospital = JSONArray.parseArray(hospitals, String.class);
		return hospital;
	}

	public void setHospital(List<String> hospital) {
		this.hospital = hospital;
	}

	public List<String> getDepartment() {

		department = JSONArray.parseArray(departments, String.class);
		return department;
	}

	public void setDepartment(List<String> department) {
		this.department = department;
	}

	public List<String> getCheckItem() {

		checkItem = JSONArray.parseArray(checkItems, String.class);
		return checkItem;
	}

	public void setCheckItem(List<String> checkItem) {
		this.checkItem = checkItem;
	}

	public String getHospitals() {
		return hospitals;
	}

	public void setHospitals(String hospitals) {
		this.hospitals = hospitals;
	}

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getCheckItems() {
		return checkItems;
	}

	public void setCheckItems(String checkItems) {
		this.checkItems = checkItems;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getCheckId() {
		return checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
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

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getAppointmentTime() {
		return appointmentTime;
	}

	public void setAppointmentTime(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
}
