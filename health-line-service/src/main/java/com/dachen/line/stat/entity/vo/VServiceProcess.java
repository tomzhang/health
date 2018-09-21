package com.dachen.line.stat.entity.vo;

import java.util.Date;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "v_service_process", noClassnameStored = true)
public class VServiceProcess {
	
	@Id	
	private String id;
	private Integer userId;//护士用户编号
	private Integer status;// 1是开始服务  2是等待上传检查结果 3是结束服务  4 申请关闭  5 关闭
	private int from;//0是V小护 1系统自动分配 2客服手动 3其它
	private long time;//创建时间
	private long updTime;//更新时间
	private String orderId;
	public long getUpdTime() {
		return updTime;
	}
	public void setUpdTime(long updTime) {
		this.updTime = updTime;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
}