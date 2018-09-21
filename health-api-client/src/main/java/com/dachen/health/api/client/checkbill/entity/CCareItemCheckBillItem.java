package com.dachen.health.api.client.checkbill.entity;

import java.io.Serializable;
import java.util.List;

public class CCareItemCheckBillItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 冗余，用于比对
	 */
	private String careItemId;
	
	private String checkUpId;
	private String checkUpName;
	
	/**
	 * 医生关注的单项指标列表
	 */
	private List<String> concernedItemIds;
	

	public String getCareItemId() {
		return careItemId;
	}

	public void setCareItemId(String careItemId) {
		this.careItemId = careItemId;
	}

	public String getCheckUpId() {
		return checkUpId;
	}

	public void setCheckUpId(String checkUpId) {
		this.checkUpId = checkUpId;
	}

	public String getCheckUpName() {
		return checkUpName;
	}

	public void setCheckUpName(String checkUpName) {
		this.checkUpName = checkUpName;
	}

	public List<String> getConcernedItemIds() {
		return concernedItemIds;
	}

	public void setConcernedItemIds(List<String> concernedItemIds) {
		this.concernedItemIds = concernedItemIds;
	}
	
	
}