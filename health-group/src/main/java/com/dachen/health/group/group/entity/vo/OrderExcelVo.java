package com.dachen.health.group.group.entity.vo;

import java.util.List;

public class OrderExcelVo {
	
	private Integer id;
	private Integer orderNo;
	private Long createTime;
	private String createTimeStr;
	private Integer doctorId;
	private Integer patientId;
	private Integer patientUserId;
	private Integer orderStatus;
	private String orderStatusStr;
	private Long payTime;
	private String payTimeStr;
	private String groupId;
	
	private String doctorName;
	private String doctorPhone;
	private String patientName;
	private String patientPhone;
	private String userName;
	private String hasPay;
	private Long price;

	private String groupName;
	
	/**关怀计划的名称**/
	private String careName;
	/**关怀计划的id**/
	private String careTemplateId;
	
	private List<String> careItemIds;
	
	private List<Long> answerTimes;
	/**回答次数**/
	private Integer answerCount;
	
	/**病情跟踪**/
	private List<ItemAnswerInfo> track;
	/**生活量表**/
	private List<ItemAnswerInfo> life;
	/**调查表**/
	private List<ItemAnswerInfo> survey;
	/**健康关怀的留言**/
	private List<MessageExcelVo> careMessage;
	
	/**留言信息**/
	private String message;
	
	/**健康关怀最后的回答时间**/
	private Long lastAnswerTime;
	/**健康关怀最后的回答时间**/
	private String lastAnswerTimeStr;
	
	/**图文咨询最后的状态**/
	private String picMessageStatus;
	
	public String getPicMessageStatus() {
		return picMessageStatus;
	}

	public void setPicMessageStatus(String picMessageStatus) {
		this.picMessageStatus = picMessageStatus;
	}

	public List<ItemAnswerInfo> getTrack() {
		return track;
	}

	public void setTrack(List<ItemAnswerInfo> track) {
		this.track = track;
	}

	public List<ItemAnswerInfo> getLife() {
		return life;
	}

	public void setLife(List<ItemAnswerInfo> life) {
		this.life = life;
	}

	public List<ItemAnswerInfo> getSurvey() {
		return survey;
	}

	public void setSurvey(List<ItemAnswerInfo> survey) {
		this.survey = survey;
	}

	public Integer getAnswerCount() {
		return answerCount;
	}

	public void setAnswerCount(Integer answerCount) {
		this.answerCount = answerCount;
	}

	public String getLastAnswerTimeStr() {
		return lastAnswerTimeStr;
	}

	public void setLastAnswerTimeStr(String lastAnswerTimeStr) {
		this.lastAnswerTimeStr = lastAnswerTimeStr;
	}

	public Long getLastAnswerTime() {
		return lastAnswerTime;
	}

	public void setLastAnswerTime(Long lastAnswerTime) {
		this.lastAnswerTime = lastAnswerTime;
	}

	public List<Long> getAnswerTimes() {
		return answerTimes;
	}

	public void setAnswerTimes(List<Long> answerTimes) {
		this.answerTimes = answerTimes;
	}

	public List<String> getCareItemIds() {
		return careItemIds;
	}

	public void setCareItemIds(List<String> careItemIds) {
		this.careItemIds = careItemIds;
	}

	public String getCareTemplateId() {
		return careTemplateId;
	}

	public void setCareTemplateId(String careTemplateId) {
		this.careTemplateId = careTemplateId;
	}

	public String getCareName() {
		return careName;
	}

	public void setCareName(String careName) {
		this.careName = careName;
	}

	public Integer getPatientUserId() {
		return patientUserId;
	}

	public void setPatientUserId(Integer patientUserId) {
		this.patientUserId = patientUserId;
	}

	public String getPatientPhone() {
		return patientPhone;
	}

	public void setPatientPhone(String patientPhone) {
		this.patientPhone = patientPhone;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderStatusStr() {
		return orderStatusStr;
	}

	public void setOrderStatusStr(String orderStatusStr) {
		this.orderStatusStr = orderStatusStr;
	}

	public Long getPayTime() {
		return payTime;
	}

	public void setPayTime(Long payTime) {
		this.payTime = payTime;
	}

	public String getPayTimeStr() {
		return payTimeStr;
	}

	public void setPayTimeStr(String payTimeStr) {
		this.payTimeStr = payTimeStr;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getDoctorPhone() {
		return doctorPhone;
	}

	public void setDoctorPhone(String doctorPhone) {
		this.doctorPhone = doctorPhone;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHasPay() {
		return hasPay;
	}

	public void setHasPay(String hasPay) {
		this.hasPay = hasPay;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<MessageExcelVo> getCareMessage() {
		return careMessage;
	}

	public void setCareMessage(List<MessageExcelVo> careMessage) {
		this.careMessage = careMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}