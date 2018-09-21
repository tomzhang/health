package com.dachen.line.stat.entity.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
@Entity(value = "v_vsptracking", noClassnameStored = true)
public class VSPTracking {
	@Id
	private String id;
	private String orderId;//订单id
	private String serviceId;//服务id--关联服务流程表id
	private int code;//业务编码
	private String phoneId;//电话id
	private String sms_content;//短信内容
	private String appointmentTime;//预约时间
	private Long createTime;//数据创建时间
	private String patientId;//患者id
	private String patientTel;//患者电话
	
	
	
	public String getPatientTel() {
		return patientTel;
	}
	public void setPatientTel(String patientTel) {
		this.patientTel = patientTel;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getPhoneId() {
		return phoneId;
	}
	public void setPhoneId(String phoneId) {
		this.phoneId = phoneId;
	}
	public String getSms_content() {
		return sms_content;
	}
	public void setSms_content(String sms_content) {
		this.sms_content = sms_content;
	}
	public String getAppointmentTime() {
		return appointmentTime;
	}
	public void setAppointmentTime(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
	
	
	
	
	/*
	
	
	private String vspId;
	private String type;//0是电话  1是短信
	private long time;//创建时间
	private String messageId;//关联短信ID
	private String callId;//关联通话记录的ID
	private Integer userId;//关联通话记录的ID
	private String messageContent;
	public String getVspId() {
		return vspId;
	}
	public void setVspId(String vspId) {
		this.vspId = vspId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getCallId() {
		return callId;
	}
	public void setCallId(String callId) {
		this.callId = callId;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	*/
}
