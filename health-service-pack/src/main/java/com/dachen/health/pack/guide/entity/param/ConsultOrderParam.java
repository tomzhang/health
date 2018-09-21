package com.dachen.health.pack.guide.entity.param;

public class ConsultOrderParam {
	/** 购买用户id */
	private Integer userId;

	/** 医生id */
	private Integer doctorId;

	/** 套餐id */
	private Integer packId;

	/**
	 * 病情描述
	 */
	private String diseaseDesc;

	/**
	 * 病情语音
	 */
	private String voice;

	/**
	 * 病人联系方式
	 */
	private String telephone;

	/**
	 * 病人Id
	 */
	private Integer patientId;

	/**
	 * 病情图
	 */
	private String[] imagePaths;

	private Boolean isSeeDoctor; // 是否就诊 false 没有 true 有

	private String seeDoctorMsg;//患者 在医院诊治情况
	
	/**
	 * 转诊之前的orderId
	 */
	private Integer preOrderId;
	
	private Integer transferDoctorId;
	/**
	 * 转诊时间
	 */
	private Long transferTime;
	
	private String illCaseInfoId;
	 private Boolean isIllCaseCommit=false;

	public Boolean getIsSeeDoctor() {
		return isSeeDoctor;
	}

	public void setIsSeeDoctor(Boolean isSeeDoctor) {
		this.isSeeDoctor = isSeeDoctor;
	}

	public String getSeeDoctorMsg() {
		return seeDoctorMsg;
	}

	public void setSeeDoctorMsg(String seeDoctorMsg) {
		this.seeDoctorMsg = seeDoctorMsg;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getPackId() {
		return packId;
	}

	public void setPackId(Integer packId) {
		this.packId = packId;
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

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String[] getImagePaths() {
		return imagePaths;
	}

	public void setImagePaths(String[] imagePaths) {
		this.imagePaths = imagePaths;
	}

	public Integer getPreOrderId() {
		return preOrderId;
	}

	public void setPreOrderId(Integer preOrderId) {
		this.preOrderId = preOrderId;
	}

	public Integer getTransferDoctorId() {
		return transferDoctorId;
	}

	public void setTransferDoctorId(Integer transferDoctorId) {
		this.transferDoctorId = transferDoctorId;
	}

	public Long getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(Long transferTime) {
		this.transferTime = transferTime;
	}

	public String getIllCaseInfoId() {
		return illCaseInfoId;
	}

	public void setIllCaseInfoId(String illCaseInfoId) {
		this.illCaseInfoId = illCaseInfoId;
	}

	public Boolean getIsIllCaseCommit() {
		return isIllCaseCommit;
	}

	public void setIsIllCaseCommit(Boolean isIllCaseCommit) {
		this.isIllCaseCommit = isIllCaseCommit;
	}
	
}
