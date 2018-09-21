package com.dachen.medice.vo;

public class RemindPatient {

	private Integer userId;//对应用户Id
	
	private Integer patientId;
	
	private Integer reminderIntervals;//提醒间隔天数

	private Integer reminderDays;//提醒持续天数
	
	private String goodsId;//药品Id
	
	private String reminderRinging = "班得瑞钢琴曲";//提醒铃声
	
	private Long startDate;//开始时间
	
	private String[] reminderTimes;
	
	private String patientName;//患者名
	
	private String goodsName;//药品名

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public Integer getReminderIntervals() {
		return reminderIntervals;
	}

	public void setReminderIntervals(Integer reminderIntervals) {
		this.reminderIntervals = reminderIntervals;
	}

	public Integer getReminderDays() {
		return reminderDays;
	}

	public void setReminderDays(Integer reminderDays) {
		this.reminderDays = reminderDays;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getReminderRinging() {
		return reminderRinging;
	}

	public void setReminderRinging(String reminderRinging) {
		this.reminderRinging = reminderRinging;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public String[] getReminderTimes() {
		return reminderTimes;
	}

	public void setReminderTimes(String[] reminderTimes) {
		this.reminderTimes = reminderTimes;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	


}
