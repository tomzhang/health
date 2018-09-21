package com.dachen.health.pack.order.entity.vo;

import com.dachen.health.group.schedule.entity.po.OfflineItem;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.util.JSONUtil;

public class AppointmentOrderWebParams {

	private OfflineItem offlineItem;
	
	private Patient patient;
	
	private OrderParam  orderParam;
	
	public OfflineItem getOfflineItem() {
		return offlineItem;
	}

	public void setOfflineItem(OfflineItem offlineItem) {
		this.offlineItem = offlineItem;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public OrderParam getOrderParam() {
		return orderParam;
	}

	public void setOrderParam(OrderParam orderParam) {
		this.orderParam = orderParam;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
	
}
