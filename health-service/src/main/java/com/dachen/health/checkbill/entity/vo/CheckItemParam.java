package com.dachen.health.checkbill.entity.vo;

import java.util.List;

import com.dachen.health.checkbill.entity.po.CheckItem;

public class CheckItemParam {

	private String checkItemString;
	
	private List<CheckItem> checkItemList;

	public String getCheckItemString() {
		return checkItemString;
	}

	public void setCheckItemString(String checkItemString) {
		this.checkItemString = checkItemString;
	}

	public List<CheckItem> getCheckItemList() {
		return checkItemList;
	}

	public void setCheckItemList(List<CheckItem> checkItemList) {
		this.checkItemList = checkItemList;
	}

	@Override
	public String toString() {
		return "CheckItemParam [checkItemString=" + checkItemString + ", checkItemList=" + checkItemList + "]";
	}
	
}
