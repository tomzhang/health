package com.dachen.health.pack.consult.entity.vo;

public class IllHistoryInfoItem {

	private String infoId;
	
	private String illDesc;
	
	private String doctorName;
	
	private Integer doctorId;
	
	private Long updateTime;
	/**
	 * 创建时间
	 */
	private Long createTime;

	//初步诊断疾病名称
	private String diseaseName;
	
	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}

	public String getIllDesc() {
		return illDesc;
	}

	public void setIllDesc(String illDesc) {
		this.illDesc = illDesc;
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

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getDiseaseName() {
		return diseaseName;
	}

	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}
	
}
