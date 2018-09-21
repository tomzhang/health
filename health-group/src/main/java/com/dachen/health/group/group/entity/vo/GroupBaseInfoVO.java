package com.dachen.health.group.group.entity.vo;

public class GroupBaseInfoVO {
	private String docGroupId;
	
	private String groupName;
	
	private String groupDec;
	
	private Integer doctorNum;
	
	private Integer expertNum;
	
	private String headPicFileName;
	

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}

	public String getDocGroupId() {
		return docGroupId;
	}

	public void setDocGroupId(String docGroupId) {
		this.docGroupId = docGroupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDec() {
		return groupDec;
	}

	public void setGroupDec(String groupDec) {
		this.groupDec = groupDec;
	}

	public Integer getDoctorNum() {
		return doctorNum;
	}

	public void setDoctorNum(Integer doctorNum) {
		this.doctorNum = doctorNum;
	}

	public Integer getExpertNum() {
		return expertNum;
	}

	public void setExpertNum(Integer expertNum) {
		this.expertNum = expertNum;
	}
	
}
