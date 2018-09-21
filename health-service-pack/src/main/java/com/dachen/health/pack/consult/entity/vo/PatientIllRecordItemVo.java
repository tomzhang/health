package com.dachen.health.pack.consult.entity.vo;

import java.util.List;

public class PatientIllRecordItemVo {

	private Integer patientId;
	
	private String patientName;
	
	private Short sex;
	
	private String ageStr;

	private List<IllRecordItem> illRecordList;
	
	private List<IllHistoryInfoItem> illHistoryInfoItems;
	
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

	public Short getSex() {
		return sex;
	}

	public void setSex(Short sex) {
		this.sex = sex;
	}

	public String getAgeStr() {
		return ageStr;
	}

	public void setAgeStr(String ageStr) {
		this.ageStr = ageStr;
	}

	public List<IllRecordItem> getIllRecordList() {
		return illRecordList;
	}

	public void setIllRecordList(List<IllRecordItem> illRecordList) {
		this.illRecordList = illRecordList;
	}

	public List<IllHistoryInfoItem> getIllHistoryInfoItems() {
		return illHistoryInfoItems;
	}

	public void setIllHistoryInfoItems(List<IllHistoryInfoItem> illHistoryInfoItems) {
		this.illHistoryInfoItems = illHistoryInfoItems;
	}

	@Override
	public String toString() {
		return "PatientIllRecordItemVo [patientId=" + patientId + ", patientName=" + patientName + ", sex=" + sex
				+ ", ageStr=" + ageStr + ", illRecordList=" + illRecordList + "]";
	}

}
