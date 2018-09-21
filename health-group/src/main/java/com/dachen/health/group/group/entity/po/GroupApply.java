package com.dachen.health.group.group.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "c_group_apply", noClassnameStored = true)
public class GroupApply {

	@Id
	private String id;
	
	private String name;
	
	private String introduction;
	
	private String logoUrl;
	
	//(A=待审核，P=审核通过，NP=审核未通过)
	private String status;
	
	private Integer applyUserId;
	
	private Long applyDate;
	
	private Integer auditUserId;
	
	private Long auditDate;
	
	private String auditMsg;
	
	//审核通过之后更新给字段
	private String groupId;

	private Long updateTime;

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getIntroduction() {
		return introduction;
	}


	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}


	public String getLogoUrl() {
		return logoUrl;
	}


	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Integer getApplyUserId() {
		return applyUserId;
	}


	public void setApplyUserId(Integer applyUserId) {
		this.applyUserId = applyUserId;
	}


	public Long getApplyDate() {
		return applyDate;
	}


	public void setApplyDate(Long applyDate) {
		this.applyDate = applyDate;
	}


	public Integer getAuditUserId() {
		return auditUserId;
	}


	public void setAuditUserId(Integer auditUserId) {
		this.auditUserId = auditUserId;
	}


	public Long getAuditDate() {
		return auditDate;
	}


	public void setAuditDate(Long auditDate) {
		this.auditDate = auditDate;
	}


	public String getAuditMsg() {
		return auditMsg;
	}


	public void setAuditMsg(String auditMsg) {
		this.auditMsg = auditMsg;
	}


	public String getGroupId() {
		return groupId;
	}


	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}


	@Override
	public String toString() {
		return "GroupApply [id=" + id + ", name=" + name + ", introduction=" + introduction + ", logoUrl=" + logoUrl
				+ ", status=" + status + ", applyUserId=" + applyUserId + ", applyDate=" + applyDate + ", auditUserId="
				+ auditUserId + ", auditDate=" + auditDate + ", auditMsg=" + auditMsg + ", groupId=" + groupId + "]";
	}
}
