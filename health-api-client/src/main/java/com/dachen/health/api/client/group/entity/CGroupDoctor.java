package com.dachen.health.api.client.group.entity;

import java.io.Serializable;

public class CGroupDoctor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	/**
	 * 集团Id
	 */
	private String groupId;

	/**
	 * 医生Id
	 */
	private Integer doctorId;

	/**
	 * 帐号状态 C：正在使用，I：已邀请待确认， S：已停用（已离职），O：已踢出 N：已拒绝
	 * {@link GroupEnum.GroupDoctorStatus}
	 */
	private String status;

	/**
	 * 推荐人Id
	 */
	private Integer referenceId;

	/**
	 * 邀请信息记录
	 */
	private String recordMsg;

	/**
	 * 申请加入留言
	 */
	private String applyMsg;

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

	/* 邀请关系线 */
	private String treePath;

	/**
	 * 在线状态1在线，2离线
	 * 
	 * @see GroupEnum.OnLineState
	 */
	private String onLineState;

	// 在线时长（任务）/秒
	private Long taskDuration;

	// 免打扰（1：正常，2：免打扰）
	private String troubleFree;

	// 门诊价格（分） ，默认初始值为1元，即100保存
	private Integer outpatientPrice = new Integer(100);

	// 最后一次上线时间
	private Long onLineTime;

	// 值班时长（秒）
	private Long dutyDuration;

	// 最后一次下线时间
	private Long offLineTime;

	/**
	 * 冗余医生设置的科室，和医生设置的科室保持一致
	 */
	private String deptName;

	/**
	 * 是否主集团
	 */
	private boolean isMain;
	/**
	 * 集团还是医院
	 */
	private String type;

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

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getApplyMsg() {
		return applyMsg;
	}

	public void setApplyMsg(String applyMsg) {
		this.applyMsg = applyMsg;
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

	public String getTreePath() {
		return treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;
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

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public boolean isMain() {
		return isMain;
	}

	public void setMain(boolean isMain) {
		this.isMain = isMain;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
