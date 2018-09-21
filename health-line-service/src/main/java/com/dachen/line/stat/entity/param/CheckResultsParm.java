package com.dachen.line.stat.entity.param;

import java.util.List;

import com.alibaba.fastjson.JSONArray;


public class CheckResultsParm {
    
	
	private String serviceId;// 服务id
	
	//private String orderId;// 患者服务表

	private int from;      // 0是护士1是患者 2是客服 3是系统对接
	
	private String checkItemList;
	
	private List<CheckResultLineServiceParm> itemList = null;

//	public String getOrderId() {
//		
//		return orderId;
//	}
//
//	public void setOrderId(String orderId) {
//		this.orderId = orderId;
//	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public String getCheckItemList() {
		return checkItemList;
	}

	public void setCheckItemList(String checkItemList) {
		this.checkItemList = checkItemList;
	}

	public List<CheckResultLineServiceParm> getItemList() {
		
		itemList = JSONArray.parseArray(checkItemList, CheckResultLineServiceParm.class);
		return itemList;
	}

	public void setItemList(List<CheckResultLineServiceParm> itemList) {
		this.itemList = itemList;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
}
