package com.dachen.health.pack.illhistory.entity.vo;

public class DiagnosisVO {
	
	/**创建时间**/
	private Long time;
	/**疾病名称**/
	private String diseaseName;
	/**诊断内容**/
	private String content;
	/**用户类型 1:患者 3:医生**/
	private Integer userType;
	/**用户名称**/
	private String userName;

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDiseaseName() {
		return diseaseName;
	}

	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
