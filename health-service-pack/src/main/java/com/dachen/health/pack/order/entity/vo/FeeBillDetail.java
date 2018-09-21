package com.dachen.health.pack.order.entity.vo;

import java.util.List;

public class FeeBillDetail {

	private Long orderAmt;
	
	private List<ServiceItemVO> serviceItem;

	public Long getOrderAmt() {
		return orderAmt;
	}

	public void setOrderAmt(Long orderAmt) {
		this.orderAmt = orderAmt;
	}

	public List<ServiceItemVO> getServiceItem() {
		return serviceItem;
	}

	public void setServiceItem(List<ServiceItemVO> serviceItem) {
		this.serviceItem = serviceItem;
	}
}
