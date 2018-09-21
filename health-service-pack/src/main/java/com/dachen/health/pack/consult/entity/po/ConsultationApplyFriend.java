package com.dachen.health.pack.consult.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.util.JSONUtil;

@Entity(value="t_consultation_apply_friend" , noClassnameStored = true)
public class ConsultationApplyFriend {

	@Id
	private String id;
	
	private Integer consultationDoctorId;
	
	private Integer unionDoctorId;
	
	//(1：c_doc --->u_doc , 2: u_doc --> c_doc )
	private Integer applyType;
	
	private String applyMessage;
	
	//(1: 已申请，2：已同意，3：已忽略)
	private Integer status;
	
	private Long  createTime;
	
	private Long updateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getConsultationDoctorId() {
		return consultationDoctorId;
	}

	public void setConsultationDoctorId(Integer consultationDoctorId) {
		this.consultationDoctorId = consultationDoctorId;
	}

	public Integer getUnionDoctorId() {
		return unionDoctorId;
	}

	public void setUnionDoctorId(Integer unionDoctorId) {
		this.unionDoctorId = unionDoctorId;
	}

	public Integer getApplyType() {
		return applyType;
	}

	public void setApplyType(Integer applyType) {
		this.applyType = applyType;
	}

	public String getApplyMessage() {
		return applyMessage;
	}

	public void setApplyMessage(String applyMessage) {
		this.applyMessage = applyMessage;
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

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

}
