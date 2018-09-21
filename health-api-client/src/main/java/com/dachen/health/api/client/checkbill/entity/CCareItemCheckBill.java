package com.dachen.health.api.client.checkbill.entity;

import java.io.Serializable;

public class CCareItemCheckBill implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String careItemId;
	/**建议检查时间**/
	private Long suggestCheckTime;
	/**注意事项**/
	private String attention;
	
	private Integer orderId;
	private Integer patientId;
	public String getCareItemId() {
		return careItemId;
	}
	public void setCareItemId(String careItemId) {
		this.careItemId = careItemId;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Integer getPatientId() {
		return patientId;
	}
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	public Long getSuggestCheckTime() {
		return suggestCheckTime;
	}
	public void setSuggestCheckTime(Long suggestCheckTime) {
		this.suggestCheckTime = suggestCheckTime;
	}
	public String getAttention() {
		return attention;
	}
	public void setAttention(String attention) {
		this.attention = attention;
	}
}



