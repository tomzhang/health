package com.dachen.health.group.group.entity.vo;

import java.util.List;

public class GroupApplyDetailPageVo {
	/**集团ID*/
	private String groupId; // add by tanyf 20160602
	private String groupName;
	
	private String doctorName;

	private Integer doctorId;
	
	private String telephone;
	
	private String hospitalName;
	
	private String level;
	
	private String departments;
	
	private String licenseNum;
	
	private String licenseExpire;
	
	private String applyStatus;
	
	private String adminName;
	
	private String logoUrl;
	
	private String auditMsg;
	
	private String title;
	
	private String introduction;
	
	private List<String> imageUrls;
	/** active 已激活 ,inactive 未激活 */
	private String groupActive;// add by tanyf 20160602
	/** 屏蔽状态  "N", "正常" "S", "屏蔽"*/
	private String skip;// add by tanyf 20160604
	 /** 集团下的成员数 （2016-06-02 谭永芳） */
    private Long memberNum;
    
    private String QRUrl; //二维码地址

	private Long auditDate;//审核时间

	/**集团创建者状态**/
	private Integer userStatus;

	public Integer getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(Integer userStatus) {
		this.userStatus = userStatus;
	}

	public String getQRUrl() {
		return QRUrl;
	}

	public void setQRUrl(String qRUrl) {
		QRUrl = qRUrl;
	}

	public Long getMemberNum() {
		return memberNum;
	}

	public void setMemberNum(Long memberNum) {
		this.memberNum = memberNum;
	}

	public String getSkip() {
		return skip;
	}

	public void setSkip(String skip) {
		this.skip = skip;
	}

	public String getGroupActive() {
		return groupActive;
	}

	public void setGroupActive(String groupActive) {
		this.groupActive = groupActive;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getLicenseNum() {
		return licenseNum;
	}

	public void setLicenseNum(String licenseNum) {
		this.licenseNum = licenseNum;
	}

	public String getLicenseExpire() {
		return licenseExpire;
	}

	public void setLicenseExpire(String licenseExpire) {
		this.licenseExpire = licenseExpire;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	public String getApplyStatus() {
		return applyStatus;
	}

	public void setApplyStatus(String applyStatus) {
		this.applyStatus = applyStatus;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getAuditMsg() {
		return auditMsg;
	}

	public void setAuditMsg(String auditMsg) {
		this.auditMsg = auditMsg;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "GroupApplyDetailPageVo [groupName=" + groupName + ", doctorName=" + doctorName + ", telephone="
				+ telephone + ", hospitalName=" + hospitalName + ", level=" + level + ", departments=" + departments
				+ ", licenseNum=" + licenseNum + ", licenseExpire=" + licenseExpire + ", applyStatus=" + applyStatus
				+ ", adminName=" + adminName + ", auditMsg=" + auditMsg + ", imageUrls=" + imageUrls + "]";
	}

	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public Long getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Long auditDate) {
		this.auditDate = auditDate;
	}
}
