package com.dachen.line.stat.entity.vo;



public class NurseServiceOrder {

	private PatientOrder order;

	private String serviceId;
	private Integer orderStatus;// 默认0 0是服务前的确认 1是开始服务  2是等待上传检查结果 3是结束服务  4 申请关闭  5 关闭
	
	private Integer userId;//护士用户编号
	private Integer status;// 默认0 0是服务前的确认 1是开始服务  2是等待上传检查结果 3是结束服务  4 申请关闭  5 关闭
	private int from;//0是V小护 1系统自动分配 2客服手动 3其它
	private long time;//创建时间

	public PatientOrder getOrder() {
		return order;
	}

	public void setOrder(PatientOrder order) {
		this.order = order;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	
}
