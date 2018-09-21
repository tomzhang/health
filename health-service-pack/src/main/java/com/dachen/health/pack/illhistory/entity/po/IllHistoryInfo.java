package com.dachen.health.pack.illhistory.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

/**
 * 病历，一个患者和一个医生对应一个病例
 * @author fuyongde
 *
 */
@Entity(value = "t_ill_history_info",noClassnameStored = true)
public class IllHistoryInfo {
	@Id
	private String id;
	/**医生id**/
	private Integer doctorId;
	/**用户id**/
	private Integer userId;
	/**患者id**/
	private Integer patientId;
	/**患者信息id**/
	private String patientInfoId;
	/**创建时间**/
	private Long createTime;
	/**更新时间**/
	private Long updateTime;
	/**病情资料**/
	private IllContentInfo illContentInfo;
	/**初步诊断**/
	private List<Diagnosis> diagnosis;
	/**是否为第一次治疗**/
	private Boolean isFirstTreat;
	/** 简要病史**/
	private String briefHistroy;

	/**
	 * 拥有此病历的医生集合
	 */
	private List<Integer> doctorIds;



	public List<Integer> getDoctorIds() {
		return doctorIds;
	}

	public void setDoctorIds(List<Integer> doctorIds) {
		this.doctorIds = doctorIds;
	}

	public Boolean getFirstTreat() {
		return isFirstTreat;
	}

	public void setFirstTreat(Boolean firstTreat) {
		isFirstTreat = firstTreat;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public Integer getPatientId() {
		return patientId;
	}
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	public IllContentInfo getIllContentInfo() {
		return illContentInfo;
	}
	public void setIllContentInfo(IllContentInfo illContentInfo) {
		this.illContentInfo = illContentInfo;
	}
	public List<Diagnosis> getDiagnosis() {
		return diagnosis;
	}
	public void setDiagnosis(List<Diagnosis> diagnosis) {
		this.diagnosis = diagnosis;
	}
	public String getPatientInfoId() {
		return patientInfoId;
	}
	public void setPatientInfoId(String patientInfoId) {
		this.patientInfoId = patientInfoId;
	}

	public String getBriefHistroy() {
		return briefHistroy;
	}

	public void setBriefHistroy(String briefHistroy) {
		this.briefHistroy = briefHistroy;
	}

}
