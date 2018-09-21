package com.dachen.health.api.client.order.entity;

import java.io.Serializable;

public class COrderDoctor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;

	private Integer orderId;

	private Integer doctorId;

	private Integer splitRatio;

	// （1接收提醒、0否）
	private Integer receiveRemind;

	private Double splitMoney;

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

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getSplitRatio() {
		return splitRatio;
	}

	public void setSplitRatio(Integer splitRatio) {
		this.splitRatio = splitRatio;
	}

	public Integer getReceiveRemind() {
		return receiveRemind;
	}

	public void setReceiveRemind(Integer receiveRemind) {
		this.receiveRemind = receiveRemind;
	}

	public Double getSplitMoney() {
		return splitMoney;
	}

	public void setSplitMoney(Double splitMoney) {
		this.splitMoney = splitMoney;
	}
	
	
}
