package com.dachen.health.pack.order.entity.param;

public class OrderDrugParam {
	
	private Integer orderId;
	
	private String drugReiceJson;
	
	private String access_tonke;
	
	private String drugId;
	
	
	public String getAccess_tonke() {
		return access_tonke;
	}

	public void setAccess_tonke(String access_tonke) {
		this.access_tonke = access_tonke;
	}

	public String getDrugId() {
		return drugId;
	}

	public void setDrugId(String drugId) {
		this.drugId = drugId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getDrugReiceJson() {
		return drugReiceJson;
	}

	public void setDrugReiceJson(String drugReiceJson) {
		this.drugReiceJson = drugReiceJson;
	}
	
}
