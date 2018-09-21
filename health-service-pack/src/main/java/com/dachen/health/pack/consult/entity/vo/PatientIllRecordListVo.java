package com.dachen.health.pack.consult.entity.vo;

import java.util.List;

public class PatientIllRecordListVo {

	private Integer userId;
	
	private String userName;
	
	private String headPicFilleName;
	
	private Short sex;
	
	private String ageStr;
	
	private String area;
	
	private Object tags;
	
	private List<PatientIllRecordItemVo> patientIllRecordList;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getHeadPicFilleName() {
		return headPicFilleName;
	}

	public void setHeadPicFilleName(String headPicFilleName) {
		this.headPicFilleName = headPicFilleName;
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

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public List<PatientIllRecordItemVo> getPatientIllRecordList() {
		return patientIllRecordList;
	}

	public void setPatientIllRecordList(List<PatientIllRecordItemVo> patientIllRecordList) {
		this.patientIllRecordList = patientIllRecordList;
	}

	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Object getTags() {
		return tags;
	}

	public void setTags(Object tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "PatientIllRecordListVo [userId=" + userId + ", userName=" + userName + ", headPicFilleName="
				+ headPicFilleName + ", sex=" + sex + ", ageStr=" + ageStr + ", area=" + area + ", tags=" + tags
				+ ", patientIllRecordList=" + patientIllRecordList + "]";
	}

}
