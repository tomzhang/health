package com.dachen.health.group.schedule.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.util.JSONUtil;

@Entity(value = "c_offline_item", noClassnameStored = true)
public class OfflineItem {
	
	@Id
	private String id;
	
	private Integer doctorId;
	
	private Integer patientId;
	
	private String hospitalId;
	
	private Integer orderId;
	

    /* 上午、中午、下午、晚上 {@link ScheduleEnum.Period} */
    private Integer period;
    
    /* 日期 */
    private Integer week;
	
	/**
	 * 获取预约时间段的开始时间点
	 */
	private Long startTime;
	
	/**
	 * 预约时间段的结束时间点
	 */
	private Long endTime;
	
	/**
	 * 预约时间的日期
	 */
	private Long dateTime;
	
	/*1: 待预约 ， 2：已预约 ， 3：已开始，4: 已完成*/
	private Integer status;
	
	/*1: 医生添加 ， 2：导医添加*/
	private Integer dataFrom;
	
	private Long createTime;
	
	/*1: 医生排班内的数据 ， 2: 医生排班外的数据（由导医添加）*/
	private Integer dataForm;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Long getDateTime() {
		return dateTime;
	}

	public void setDateTime(Long dateTime) {
		this.dateTime = dateTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public Integer getWeek() {
		return week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}
	
	public Integer getDataFrom() {
		return dataFrom;
	}

	public void setDataFrom(Integer dataFrom) {
		this.dataFrom = dataFrom;
	}

	public Integer getDataForm() {
		return dataForm;
	}

	public void setDataForm(Integer dataForm) {
		this.dataForm = dataForm;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
	
	
	
}
