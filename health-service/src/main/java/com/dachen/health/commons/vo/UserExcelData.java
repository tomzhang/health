package com.dachen.health.commons.vo;

public class UserExcelData {
	
	//这里是指字节长度
	public final static int NAME_LENGTH = 20;
	public final static int INTRODUCTION_LENGTH = 8000;
	public final static int SKILL_LENGTH = 8000;
	
	private String name;
	private String phone;
	private String hospitalId;
	private String hospital;
	private String departmentId;
	private String department;
	private String titleRank;
	private String title;
	private Integer sex;
	private String introduction;
	private String skill;
	
    private Integer province;
    private String provinceName;
    private Integer city;
    private String cityName;
    private Integer country;
    private String countryName;
    
    private String doctorNum;
    
    /**医生来源（1表示农牧项目）**/
    private Integer doctorSource;
    
    public Integer getDoctorSource() {
		return doctorSource;
	}
	public void setDoctorSource(Integer doctorSource) {
		this.doctorSource = doctorSource;
	}
	public String getDoctorNum() {
		return doctorNum;
	}
	public void setDoctorNum(String doctorNum) {
		this.doctorNum = doctorNum;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	private Integer assistantId;
	
	public Integer getAssistantId() {
		return assistantId;
	}
	public void setAssistantId(Integer assistantId) {
		this.assistantId = assistantId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getHospital() {
		return hospital;
	}
	public void setHospital(String hospital) {
		this.hospital = hospital;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
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
	public String getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getTitleRank() {
		return titleRank;
	}
	public void setTitleRank(String titleRank) {
		this.titleRank = titleRank;
	}
	public Integer getProvince() {
		return province;
	}
	public void setProvince(Integer province) {
		this.province = province;
	}
	public Integer getCity() {
		return city;
	}
	public void setCity(Integer city) {
		this.city = city;
	}
	public Integer getCountry() {
		return country;
	}
	public void setCountry(Integer country) {
		this.country = country;
	}
	
}
