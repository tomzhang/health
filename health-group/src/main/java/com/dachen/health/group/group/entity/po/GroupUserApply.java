package com.dachen.health.group.group.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "c_group_user_apply", noClassnameStored = true)
public class GroupUserApply {

	@Id
	private String id;
	
	private String groupId;
	
	private Integer inviteUserId;
	
	private Long inviteDate;
	
	private Integer confirmUserId;
	
	private Long confirmDate;
	
	//（A=待确认，P=确认通过，NP=确认未通过，E=已过期）
	private String status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getInviteUserId() {
		return inviteUserId;
	}

	public void setInviteUserId(Integer inviteUserId) {
		this.inviteUserId = inviteUserId;
	}

	public Long getInviteDate() {
		return inviteDate;
	}

	public void setInviteDate(Long inviteDate) {
		this.inviteDate = inviteDate;
	}

	public Integer getConfirmUserId() {
		return confirmUserId;
	}

	public void setConfirmUserId(Integer confirmUserId) {
		this.confirmUserId = confirmUserId;
	}

	public Long getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Long confirmDate) {
		this.confirmDate = confirmDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "GroupUserApply [id=" + id + ", groupId=" + groupId + ", inviteUserId=" + inviteUserId + ", inviteDate="
				+ inviteDate + ", confirmUserId=" + confirmUserId + ", confirmDate=" + confirmDate + ", status="
				+ status + "]";
	}
	
}
