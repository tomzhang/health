package com.dachen.health.pack.order.entity.vo;


public class OrderSessionVO {

	private Integer orderStatus;
	
	private Integer sessionId;
	
	private Long createTime;
	
	private String msgGroupId;
	
	private Integer orderId;

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getSessionId() {
		return sessionId;
	}

	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getMsgGroupId() {
		return msgGroupId;
	}

	public void setMsgGroupId(String msgGroupId) {
		this.msgGroupId = msgGroupId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
	
}
