package com.dachen.health.checkbill.entity.vo;

public class CheckItemCount {

	private String checkUpId ;
	
	private String itemName;
	
	private Long count;

	public String getCheckUpId() {
		return checkUpId;
	}

	public void setCheckUpId(String checkUpId) {
		this.checkUpId = checkUpId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "CheckItemCount [checkUpId=" + checkUpId + ", itemName=" + itemName + ", count=" + count + "]";
	}
	
}
