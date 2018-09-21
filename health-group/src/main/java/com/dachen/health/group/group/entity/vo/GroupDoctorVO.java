package com.dachen.health.group.group.entity.vo;

import com.dachen.health.group.common.entity.vo.DoctorBasicInfo;

/**
 * @author pijingwei
 * @date 2015/8/10
 */
public class GroupDoctorVO {

	/**
	 * id
	 */
	private String id;
	
	/**
	 * id
	 */
	private String groupDoctorId;
	
	/* 手机号码 */
	private String telephone;
	
	/**
	 * 集团Id
	 */
	private String groupId;
	
	/**
	 * 集团name
	 */
	private String groupName;
	
	/**
	 * 医生Id
	 */
	private Integer doctorId;
	
	/**
	 * 科室id
	 */
	private String departmentId;
	
	/**
	 * 帐号状态  C：正在使用，I：已邀请待确认， S：已停用（已离职），O：已踢出   N：已拒绝
	 */
	private String status;
	
	private String type;
	
	/**
	 * 推荐人Id
	 */
	private Integer referenceId;
	
	/**
	 * 邀请信息记录
	 */
	private String recordMsg;
	
	/**
	 * 联系方式
	 */
	private String contactWay;
	
	/**
	 * 备注
	 */
	private String remarks;
	
	/**
	 * 创建人
	 */
	private Integer creator;
	
	/**
	 * 创建时间（加入日期）
	 */
	private Long creatorDate;
	
	/**
	 * 更新人
	 */
	private Integer updator;
	
	/**
	 * 更新日期
	 */
	private Long updatorDate;
	
	/** 医生基本信息 **/
	private DoctorBasicInfo doctor;
	
	/* 邀请关系 */
    private InviteRelation relation;
    
	//在线状态1，在线，2离线
	private String onLineState;
	
	//在线时长（任务）/秒
	private Long taskDuration;
	
	//免打扰（1：正常，2：免打扰）
	private String troubleFree;
	
	//门诊价格（分）
	private Integer outpatientPrice;
	
	
	// 最后一次上线时间
	private Long onLineTime;
	
	// 值班时长（秒）
	private Long dutyDuration;
	

	// 最后一次下线时间
	private Long offLineTime;
	
	private boolean isMain;
	
	private  Integer  role;

	private Integer inviterId;// 邀请人Id
	private String inviterName;// 邀请人姓名
	
	private Integer applyStatus;// 审核状态
	private String applyStatusName;// 审核状态名称
	
	
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

	public Integer getRole() {
		
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public DoctorBasicInfo getDoctor() {
		return doctor;
	}

	public void setDoctor(DoctorBasicInfo doctor) {
		this.doctor = doctor;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDoctorId() {
		return groupDoctorId;
	}

	public void setGroupDoctorId(String groupDoctorId) {
		this.groupDoctorId = groupDoctorId;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}

	public String getRecordMsg() {
		return recordMsg;
	}

	public void setRecordMsg(String recordMsg) {
		this.recordMsg = recordMsg;
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

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Long getCreatorDate() {
		return creatorDate;
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

	public InviteRelation getRelation() {
		return relation;
	}

	public void setRelation(InviteRelation relation) {
		this.relation = relation;
	}

	public String getOnLineState() {
		return onLineState;
	}

	public void setOnLineState(String onLineState) {
		this.onLineState = onLineState;
	}

	public Long getTaskDuration() {
		return taskDuration;
	}

	public void setTaskDuration(Long taskDuration) {
		this.taskDuration = taskDuration;
	}

	public String getTroubleFree() {
		return troubleFree;
	}

	public void setTroubleFree(String troubleFree) {
		this.troubleFree = troubleFree;
	}

	public Integer getOutpatientPrice() {
		return outpatientPrice;
	}

	public void setOutpatientPrice(Integer outpatientPrice) {
		this.outpatientPrice = outpatientPrice;
	}

	public Long getOnLineTime() {
		return onLineTime;
	}

	public void setOnLineTime(Long onLineTime) {
		this.onLineTime = onLineTime;
	}

	public Long getDutyDuration() {
		return dutyDuration;
	}

	public void setDutyDuration(Long dutyDuration) {
		this.dutyDuration = dutyDuration;
	}

	public Long getOffLineTime() {
		return offLineTime;
	}

	public void setOffLineTime(Long offLineTime) {
		this.offLineTime = offLineTime;
	}

	public boolean isMain() {
		return isMain;
	}

	public void setMain(boolean isMain) {
		this.isMain = isMain;
	}

	
}
