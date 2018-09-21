package com.dachen.health.api.client.user.entity;

import java.io.Serializable;
import java.util.List;

public class CDoctor implements Serializable{

	private static final long serialVersionUID = 1L;

	/* 所属医院 */
    private String hospital;

    /* 所属医院Id */
    private String hospitalId;

    /* 所属科室 */
    private String departments;
    
    /* 科室Id */
    private String deptId;

    /* 职称 */
    private String title;
    
    /*职称排行*/
    private String titleRank;

    /* 入职时间 */
    private Long entryTime;

    /* 医生号 */
    private String doctorNum;

    /* 个人介绍 */
    private String introduction;

    /* 擅长领域 :用户手工输入的专长*/

    private String skill;

    /* 职业区域，根据医生审核医院确定 */
    private Integer provinceId;

    private Integer cityId;

    private Integer countryId;

    private String province;

    private String city;

    private String country;
    
    /*医生治疗人数*/
    private Integer cureNum;

    // 医生设置：免打扰（1：正常，2：免打扰）
    private String troubleFree;
    
    /**
     * 专长:用户选择的专长
     */
    private List<String> expertise;

    //医生助手id
    private Integer assistantId;
    
  //UserEnum.DoctorRole  医生角色
    private Integer role;
    
    //UserEnum.ServiceStatus 医生套餐的开通状态
    private Integer serviceStatus;
    
    /* 开启患者报道赠送服务 */
    private Integer checkInGive;

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleRank() {
		return titleRank;
	}

	public void setTitleRank(String titleRank) {
		this.titleRank = titleRank;
	}

	public Long getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(Long entryTime) {
		this.entryTime = entryTime;
	}

	public String getDoctorNum() {
		return doctorNum;
	}

	public void setDoctorNum(String doctorNum) {
		this.doctorNum = doctorNum;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getCureNum() {
		return cureNum;
	}

	public void setCureNum(Integer cureNum) {
		this.cureNum = cureNum;
	}

	public String getTroubleFree() {
		return troubleFree;
	}

	public void setTroubleFree(String troubleFree) {
		this.troubleFree = troubleFree;
	}

	public List<String> getExpertise() {
		return expertise;
	}

	public void setExpertise(List<String> expertise) {
		this.expertise = expertise;
	}

	public Integer getAssistantId() {
		return assistantId;
	}

	public void setAssistantId(Integer assistantId) {
		this.assistantId = assistantId;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public Integer getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(Integer serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public Integer getCheckInGive() {
		return checkInGive;
	}

	public void setCheckInGive(Integer checkInGive) {
		this.checkInGive = checkInGive;
	}

    
    
	
}
