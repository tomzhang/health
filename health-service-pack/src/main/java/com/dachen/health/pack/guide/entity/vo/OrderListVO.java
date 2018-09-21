package com.dachen.health.pack.guide.entity.vo;

import java.util.List;

public class OrderListVO {
	private String day;
	
	private int count;
	
	private  List<ConsultOrderVO> orderList;

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<ConsultOrderVO> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<ConsultOrderVO> orderList) {
		this.orderList = orderList;
	}
}
