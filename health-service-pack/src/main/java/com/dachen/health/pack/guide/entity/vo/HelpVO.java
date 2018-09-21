package com.dachen.health.pack.guide.entity.vo;

import com.dachen.careplan.api.entity.CIllnessAnswerSheet;
import com.dachen.health.pack.guide.entity.po.DoctorTimePO;
import com.dachen.health.pack.patient.model.Patient;

public class HelpVO {
	
	private String id;
	/**
	 * 关怀名称
	 */
	private String careName;
	/**
	 * 患者信息
	 */
	private Patient patient;
	
	/**
	 * 关怀类型
	 */
	private String careType;
	/**
	 * 创建时间
	 */
	private Long createTime;
	//等待时长
	private Long waitTime;
	
	private Integer orderId;
	
	
	private String fromId;
	
	private CIllnessAnswerSheet answer;
	
	private String helpMsg;
	
	
	private DoctorTimePO remark;
	
	//关怀计划id
	private String careTemplateId;
	
	public String getCareTemplateId() {
		return careTemplateId;
	}
	public void setCareTemplateId(String careTemplateId) {
		this.careTemplateId = careTemplateId;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCareName() {
		return careName;
	}
	public void setCareName(String careName) {
		this.careName = careName;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public String getCareType() {
		return careType;
	}
	public void setCareType(String careType) {
		this.careType = careType;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public DoctorTimePO getRemark() {
		return remark;
	}
	public void setRemark(DoctorTimePO remark) {
		this.remark = remark;
	}
	public String getHelpMsg() {
		return helpMsg;
	}
	public void setHelpMsg(String helpMsg) {
		this.helpMsg = helpMsg;
	}
	public Long getWaitTime() {
		return waitTime;
	}
	public void setWaitTime(Long waitTime) {
		this.waitTime = waitTime;
	}
	public String getFromId() {
		return fromId;
	}
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public CIllnessAnswerSheet getAnswer() {
		return answer;
	}

	public void setAnswer(CIllnessAnswerSheet answer) {
		this.answer = answer;
	}
}
