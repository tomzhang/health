package com.dachen.health.pack.stat.entity.vo;

public class DiseaseInfoVo {
	private Integer patientId;
	private String diseaseTypeId;
	private String diseaseTypeName;
	public Integer getPatientId() {
		return patientId;
	}
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	public String getDiseaseTypeId() {
		return diseaseTypeId;
	}
	public void setDiseaseTypeId(String diseaseTypeId) {
		this.diseaseTypeId = diseaseTypeId;
	}
	public String getDiseaseTypeName() {
		return diseaseTypeName;
	}
	public void setDiseaseTypeName(String diseaseTypeName) {
		this.diseaseTypeName = diseaseTypeName;
	}
}
