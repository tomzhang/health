package com.dachen.health.pack.consult.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.util.JSONUtil;

@Entity(value = "t_ill_case_patient_info",noClassnameStored = true)
public class IllCasePatientInfo {

	@Id
	private String id;
	
	private Integer patientId;
	//病历资料id

	private String illCaseInfoId;
	
	/*推荐医生Id*/
	private Integer doctorId;
	
	private String patientName;
	
	// 性别1男，2女 3 保密
	private String sex;
	
	private Integer age;
	
	private String ageStr;
	
	private String height;
	
	private String weight;
	
	private String area;
	
	private String telephone;
	
	private String isMarried;
	
	private String job;
	
	private Long createTime;

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getIllCaseInfoId() {
		return illCaseInfoId;
	}

	public void setIllCaseInfoId(String illCaseInfoId) {
		this.illCaseInfoId = illCaseInfoId;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}



	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
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

	public String getIsMarried() {
		return isMarried;
	}

	public void setIsMarried(String isMarried) {
		this.isMarried = isMarried;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	
	public String getAgeStr() {
		return ageStr;
	}

	public void setAgeStr(String ageStr) {
		this.ageStr = ageStr;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

}
