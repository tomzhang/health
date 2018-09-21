package com.dachen.health.pack.guide.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

@Entity(value = "t_order_relation",noClassnameStored = true)
public class OrderRelationPO {
	@Id
	private ObjectId id;
	
	@Indexed(value = IndexDirection.ASC)
	private String guideOrderId;
	
	@Indexed(value = IndexDirection.ASC,unique=true)
	private Integer orderId;
	
	/* 预约时间（患者与导医预约后反写该字段）*/
	private Long appointStartTime;
	private Long appointEndTime;
    
	private Integer doctorId;
    /* 支付状态  0:未支付 1、已支付 {@link PayStateEnum} */
    private Integer payState;
	
	public Integer getPayState() {
		return payState;
	}
	public void setPayState(Integer payState) {
		this.payState = payState;
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getGuideOrderId() {
		return guideOrderId;
	}
	public void setGuideOrderId(String guideOrderId) {
		this.guideOrderId = guideOrderId;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Long getAppointStartTime() {
		return appointStartTime;
	}
	public void setAppointStartTime(Long appointStartTime) {
		this.appointStartTime = appointStartTime;
	}
	public Long getAppointEndTime() {
		return appointEndTime;
	}
	public void setAppointEndTime(Long appointEndTime) {
		this.appointEndTime = appointEndTime;
	}
	public Integer getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}
}
