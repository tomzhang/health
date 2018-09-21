package com.dachen.health.pack.guide.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.util.JSONUtil;

@Entity(value = "t_customer_patient_record",noClassnameStored = true)
public class CustomerPatientRecord {
	
	public static final int SERVICE_ING = 1;
	
	public static final int SERVICE_END = 2;

	@Id
	private String id;
	
	/**
	 * 会话id
	 */
	private String gid;
	
	/**
	 * 以前的导医id，现在叫做客服
	 */
	private Integer customerId;
	
	/**
	 * 患者的用户id
	 */
	private Integer patientUserId;
	
	private Long dateTime;
	
	private Long startTime;
	
	private Long finishTime;
	
	/**
	 * 服务状态
	 * 1：接单中
	 * 2：已完成
	 */
	private Integer status;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getPatientUserId() {
		return patientUserId;
	}

	public void setPatientUserId(Integer patientUserId) {
		this.patientUserId = patientUserId;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Long finishTime) {
		this.finishTime = finishTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}
	
	public Long getDateTime() {
		return dateTime;
	}

	public void setDateTime(Long dateTime) {
		this.dateTime = dateTime;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
	
	
	
}
