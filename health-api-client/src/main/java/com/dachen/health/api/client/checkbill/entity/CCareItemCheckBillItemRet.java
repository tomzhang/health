package com.dachen.health.api.client.checkbill.entity;

import java.io.Serializable;

public class CCareItemCheckBillItemRet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String careItemId;
	private String checkBillId;
	
	private String checkUpId;
	private String checkItemId;
	public String getCareItemId() {
		return careItemId;
	}
	public void setCareItemId(String careItemId) {
		this.careItemId = careItemId;
	}
	public String getCheckBillId() {
		return checkBillId;
	}
	public void setCheckBillId(String checkBillId) {
		this.checkBillId = checkBillId;
	}
	public String getCheckUpId() {
		return checkUpId;
	}
	public void setCheckUpId(String checkUpId) {
		this.checkUpId = checkUpId;
	}
	public String getCheckItemId() {
		return checkItemId;
	}
	public void setCheckItemId(String checkItemId) {
		this.checkItemId = checkItemId;
	}
}