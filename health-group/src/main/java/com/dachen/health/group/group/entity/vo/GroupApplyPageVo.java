package com.dachen.health.group.group.entity.vo;

public class GroupApplyPageVo {

    private String groupApplyId;
    
    private String groupName;
    
    private String title;
    
    private String hospitalName;
    
    private String level;
    
    private String telephone;
    
    private Long applyDate;
    
    private Long auditDate;
    
    private String logoUrl;
    
    private String adminName;
    
    /***集团的id（2016-05-18傅永德）***/
    private String groupId;
    
    /** active 已激活 ,inactive 未激活 （2016-06-02 谭永芳） */
    private String groupActive;
    /** 屏蔽状态 */
    private String skip;
    /** 集团下的成员数 （2016-06-02 谭永芳） */
    private Long memberNum;
    private String status;
    
    public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSkip() {
		return skip;
	}
	public void setSkip(String skip) {
		this.skip = skip;
	}

	public Long getMemberNum() {
		return memberNum;
	}

	public void setMemberNum(Long memberNum) {
		this.memberNum = memberNum;
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

	/**
     * 申请医生姓名
     * @author wangqiao
     * @date 2016年3月7日
     */
    private String applyDoctorName;

	public String getGroupApplyId() {
		return groupApplyId;
	}

	public void setGroupApplyId(String groupApplyId) {
		this.groupApplyId = groupApplyId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Long getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Long applyDate) {
		this.applyDate = applyDate;
	}

	public Long getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Long auditDate) {
		this.auditDate = auditDate;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getApplyDoctorName() {
		return applyDoctorName;
	}

	public void setApplyDoctorName(String applyDoctorName) {
		this.applyDoctorName = applyDoctorName;
	}
	
}
