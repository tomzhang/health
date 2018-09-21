package com.dachen.health.group.department.entity.vo;

import com.dachen.health.group.common.entity.vo.DoctorBasicInfo;
import com.dachen.health.group.group.entity.vo.InviteRelation;

/**
 * @author pijingwei
 * @date 2015/8/10
 */
public class DepartmentDoctorVO {
	
	/* 集团医生唯一Id */
	private String groupDoctorId;

	/**
	 * id
	 */
	private String id;
	
	/**
	 * 集团Id
	 */
	private String groupId;
	
	/**
	 * 门诊Id
	 */
	private String departmentId;
	
	private String departmentFullName;
	
	/**
	 * 医生Id
	 */
	private Integer doctorId;
	
	/**
	 * 创建人（申请人）
	 */
	private Integer creator;
	
	/**
	 * 创建时间
	 */
	private Long creatorDate;
	
	/**
	 * 更新人
	 */
	private Integer updator;
	
	/**
	 * 更新时间（通过日期）
	 */
	private Long updatorDate;
	
	/**
	 * 医生基本信息
	 */
	private DoctorBasicInfo doctor;
	
	/**
	 * 
	 */
	private InviteRelation invite;
	
	/**
	 * 联系方式
	 */
	private String contactWay;
	
	/**
	 * 备注
	 */
	private String remarks;
	/**
	 * 医生审核状态
	 */
	private Integer applyStatus;
	/**
	 * 医生审核状态名称
	 */
	private String applyStatusName;
	/**
	 * 邀请人Id
	 */
	private Integer inviterId;
	/**
	 * 邀请人名称
	 */
	private String inviterName;

	public Integer getInviterId() {
		return inviterId;
	}

	public void setInviterId(Integer inviterId) {
		this.inviterId = inviterId;
	}

	public String getInviterName() {
		return inviterName;
	}

	public void setInviterName(String inviterName) {
		this.inviterName = inviterName;
	}

	public Integer getApplyStatus() {
		return applyStatus;
	}

	public void setApplyStatus(Integer applyStatus) {
		this.applyStatus = applyStatus;
	}

	public String getApplyStatusName() {
		return applyStatusName;
	}

	public void setApplyStatusName(String applyStatusName) {
		this.applyStatusName = applyStatusName;
	}

	public String getGroupDoctorId() {
		return groupDoctorId;
	}

	public void setGroupDoctorId(String groupDoctorId) {
		this.groupDoctorId = groupDoctorId;
	}

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

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Long getCreatorDate() {
		return creatorDate;
	}

	public DoctorBasicInfo getDoctor() {
		return doctor;
	}

	public void setDoctor(DoctorBasicInfo doctor) {
		this.doctor = doctor;
	}

	public InviteRelation getInvite() {
		return invite;
	}

	public void setInvite(InviteRelation invite) {
		this.invite = invite;
	}

	public void setCreatorDate(Long creatorDate) {
		this.creatorDate = creatorDate;
	}

	public Integer getUpdator() {
		return updator;
	}

	public void setUpdator(Integer updator) {
		this.updator = updator;
	}

	public Long getUpdatorDate() {
		return updatorDate;
	}

	public void setUpdatorDate(Long updatorDate) {
		this.updatorDate = updatorDate;
	}

	public String getContactWay() {
		return contactWay;
	}

	public void setContactWay(String contactWay) {
		this.contactWay = contactWay;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDepartmentFullName() {
		return departmentFullName;
	}

	public void setDepartmentFullName(String departmentFullName) {
		this.departmentFullName = departmentFullName;
	}

	 
 
	
}
