package com.dachen.health.checkbill.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.util.JSONUtil;

/**
 * 
 * 患者的单项指标值
 * 
 * @author xiaowei
 *
 */
@Entity(value = "t_check_up_item",noClassnameStored = true)
public class CheckupItem {

	@Id
	private String id;
	
	private Integer userId;
	
	/**
	 * 患者id
	 */
	private Integer patientId;
	
	/**
	 * 检查指标id
	 */
	private String checkupItemId;
	
	/**
	 * 所属检查项（冗余）
	 */
	private String checkupId;
	
	/**
	 * 指标值
	 */
	private Object val;
	
	/**
	 * 检查时间
	 */
	private Long checkTime;
	
	private Long createTime;
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


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


	public String getCheckupItemId() {
		return checkupItemId;
	}


	public void setCheckupItemId(String checkupItemId) {
		this.checkupItemId = checkupItemId;
	}


	public String getCheckupId() {
		return checkupId;
	}


	public void setCheckupId(String checkupId) {
		this.checkupId = checkupId;
	}


	public Object getVal() {
		return val;
	}


	public void setVal(Object val) {
		this.val = val;
	}


	public Long getCheckTime() {
		return checkTime;
	}


	public void setCheckTime(Long checkTime) {
		this.checkTime = checkTime;
	}


	public Long getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}


	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

}
