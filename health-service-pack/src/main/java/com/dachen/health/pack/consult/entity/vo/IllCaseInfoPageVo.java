package com.dachen.health.pack.consult.entity.vo;

import java.util.List;

import com.dachen.health.pack.patient.model.CareSummary;
import com.dachen.util.JSONUtil;

public class IllCaseInfoPageVo {

	private String patientName;
	
	private Integer patientId;
	
	private String ageStr;
	
	private Integer age;
	
	private String sex;
	
	private String telephone;
	
	private String telephoneOk;
	
	private String area;
	
	private String height;
	
	private String weight;
	
	private String illCaseInfoId;
	
	private String orderNo;
	
	private Integer orderStatus;
	
	private Integer treateType;
	
	private List<IllCaseTypeContentPageVo> baseContentList;
	
	private List<SeekIllInfoPageVo> seekInfoList;
	
	private List<CareSummary> careSummaryList;
	
	private Integer doctorId;
	
	private Integer userId;
	
	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getIllCaseInfoId() {
		return illCaseInfoId;
	}

	public void setIllCaseInfoId(String illCaseInfoId) {
		this.illCaseInfoId = illCaseInfoId;
	}

	public List<IllCaseTypeContentPageVo> getBaseContentList() {
		return baseContentList;
	}

	public void setBaseContentList(List<IllCaseTypeContentPageVo> baseContentList) {
		this.baseContentList = baseContentList;
	}

	public List<SeekIllInfoPageVo> getSeekInfoList() {
		return seekInfoList;
	}

	public void setSeekInfoList(List<SeekIllInfoPageVo> seekInfoList) {
		this.seekInfoList = seekInfoList;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	
	public String getAgeStr() {
		return ageStr;
	}

	public void setAgeStr(String ageStr) {
		this.ageStr = ageStr;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public Integer getTreateType() {
		return treateType;
	}

	public void setTreateType(Integer treateType) {
		this.treateType = treateType;
	}
	
	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getTelephoneOk() {
		return telephoneOk;
	}

	public void setTelephoneOk(String telephoneOk) {
		this.telephoneOk = telephoneOk;
	}
	
	public List<CareSummary> getCareSummaryList() {
		return careSummaryList;
	}

	public void setCareSummaryList(List<CareSummary> careSummaryList) {
		this.careSummaryList = careSummaryList;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
}
