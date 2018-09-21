package com.dachen.health.pack.patient.model;

public class DiseaseParam {
	
	private Integer diseaseId;
	
	private Integer patientId;
	
	private String diseaseDesc;
	
	private String voice;
	
	private String telephone;
	
	private String[] imagePaths;
	
	private Integer userId;
	//现病史
	
	/**
     * 所在地区
     */
    private String area;

    /**
     * 关系
     */
    private String relation;

    /**
     * 生日
     */
    private Long birthday;

    /**
     * 性别
     */
    private String userName;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别
     */
    private Integer sex;
    
    /***begin add  by  liwei  2016年1月21日   ********/
    //现病史
    private String diseaseInfo_now;
    //既往史
    private String diseaseInfo_old;
    //家族史
    private String familydiseaseInfo;
    //月经史
    private String menstruationdiseaseInfo;
    
  //就诊情况 
    private String seeDoctorMsg;   
    //是否就诊  false  没有  true  有
    private Boolean isSeeDoctor;   
    
    /**
     * 就诊时间
     */
    private Long visitTime;
	/***end add  by  liwei  2016年1月21日********/
	
	public String getArea() {
		return area;
	}

	public Long getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Long visitTime) {
		this.visitTime = visitTime;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(Integer diseaseId) {
		this.diseaseId = diseaseId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getDiseaseDesc() {
		return diseaseDesc;
	}

	public void setDiseaseDesc(String diseaseDesc) {
		this.diseaseDesc = diseaseDesc;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public String[] getImagePaths() {
		return imagePaths;
	}

	public void setImagePaths(String[] imagePaths) {
		this.imagePaths = imagePaths;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getDiseaseInfo_now() {
		return diseaseInfo_now;
	}

	public void setDiseaseInfo_now(String diseaseInfo_now) {
		this.diseaseInfo_now = diseaseInfo_now;
	}

	public String getDiseaseInfo_old() {
		return diseaseInfo_old;
	}

	public void setDiseaseInfo_old(String diseaseInfo_old) {
		this.diseaseInfo_old = diseaseInfo_old;
	}

	public String getFamilydiseaseInfo() {
		return familydiseaseInfo;
	}

	public void setFamilydiseaseInfo(String familydiseaseInfo) {
		this.familydiseaseInfo = familydiseaseInfo;
	}

	public String getMenstruationdiseaseInfo() {
		return menstruationdiseaseInfo;
	}

	public void setMenstruationdiseaseInfo(String menstruationdiseaseInfo) {
		this.menstruationdiseaseInfo = menstruationdiseaseInfo;
	}

	public String getSeeDoctorMsg() {
		return seeDoctorMsg;
	}

	public void setSeeDoctorMsg(String seeDoctorMsg) {
		this.seeDoctorMsg = seeDoctorMsg;
	}

	public Boolean getIsSeeDoctor() {
		return isSeeDoctor;
	}

	public void setIsSeeDoctor(Boolean isSeeDoctor) {
		this.isSeeDoctor = isSeeDoctor;
	}

	
}
