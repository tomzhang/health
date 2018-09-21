package com.dachen.health.commons.vo;

import java.util.List;

public class UserExcelError {
	private int row;
	private List<String> msg;
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public List<String> getMsg() {
		return msg;
	}
	public void setMsg(List<String> msg) {
		this.msg = msg;
	}
	
}
