package com.dachen.health.api.client.order.entity;

import java.io.Serializable;

public class COrderSession implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * 订单
     */
    private Integer orderId;

    /**
     * 会话组id(原会话 仅代表医患)
     */
    private String msgGroupId;

    /**
     * 助患会话id
     */
    private String assistantPatientGroupId ;
    
    /**
     * 医助会话id
     */
    private String assistantDoctorGroupId ;
    
    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 订单对应的会话完成时间
     */
    private Long finishTime;

    /**
     * 最后修改时间
     */
    private Long lastModifyTime;

    /**
     * 预约时间
     */
    private Long appointTime;

    /**
     * 服务开始时间
     */
    private Long serviceBeginTime;

    /**
     * 服务结束时间
     */
    private Long serviceEndTime;
    
    /**
     * 叫号开始时间
     */
    private Long treatBeginTime;

    /**
     * 患者可发
     */
    private Boolean patientCanSend;
    
    
    private Boolean isSendOverTime;
    
    private Integer timeLong;
    
    private String toUserIds;

    private String firstMessageId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getMsgGroupId() {
		return msgGroupId;
	}

	public void setMsgGroupId(String msgGroupId) {
		this.msgGroupId = msgGroupId;
	}

	public String getAssistantPatientGroupId() {
		return assistantPatientGroupId;
	}

	public void setAssistantPatientGroupId(String assistantPatientGroupId) {
		this.assistantPatientGroupId = assistantPatientGroupId;
	}

	public String getAssistantDoctorGroupId() {
		return assistantDoctorGroupId;
	}

	public void setAssistantDoctorGroupId(String assistantDoctorGroupId) {
		this.assistantDoctorGroupId = assistantDoctorGroupId;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Long finishTime) {
		this.finishTime = finishTime;
	}

	public Long getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(Long lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public Long getAppointTime() {
		return appointTime;
	}

	public void setAppointTime(Long appointTime) {
		this.appointTime = appointTime;
	}

	public Long getServiceBeginTime() {
		return serviceBeginTime;
	}

	public void setServiceBeginTime(Long serviceBeginTime) {
		this.serviceBeginTime = serviceBeginTime;
	}

	public Long getServiceEndTime() {
		return serviceEndTime;
	}

	public void setServiceEndTime(Long serviceEndTime) {
		this.serviceEndTime = serviceEndTime;
	}

	public Long getTreatBeginTime() {
		return treatBeginTime;
	}

	public void setTreatBeginTime(Long treatBeginTime) {
		this.treatBeginTime = treatBeginTime;
	}

	public Boolean getPatientCanSend() {
		return patientCanSend;
	}

	public void setPatientCanSend(Boolean patientCanSend) {
		this.patientCanSend = patientCanSend;
	}

	public Boolean getIsSendOverTime() {
		return isSendOverTime;
	}

	public void setIsSendOverTime(Boolean isSendOverTime) {
		this.isSendOverTime = isSendOverTime;
	}

	public Integer getTimeLong() {
		return timeLong;
	}

	public void setTimeLong(Integer timeLong) {
		this.timeLong = timeLong;
	}

	public String getToUserIds() {
		return toUserIds;
	}

	public void setToUserIds(String toUserIds) {
		this.toUserIds = toUserIds;
	}

	public String getFirstMessageId() {
		return firstMessageId;
	}

	public void setFirstMessageId(String firstMessageId) {
		this.firstMessageId = firstMessageId;
	}
    
    
}
