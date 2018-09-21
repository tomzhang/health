package com.dachen.health.pack.incomeNew.entity.po;

public class WXMapVO {
	private int id;
	private String thrid_refund_id;
	private Double money;
	private int tid;
	private int orderId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getThrid_refund_id() {
		return thrid_refund_id;
	}
	public void setThrid_refund_id(String thrid_refund_id) {
		this.thrid_refund_id = thrid_refund_id;
	}
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	
	
}
