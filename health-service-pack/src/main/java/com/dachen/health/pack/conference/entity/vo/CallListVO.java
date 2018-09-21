package com.dachen.health.pack.conference.entity.vo;

import java.util.List;

public class CallListVO {
	private String date;
	private List<CallRecordVO> list;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<CallRecordVO> getList() {
		return list;
	}
	public void setList(List<CallRecordVO> list) {
		this.list = list;
	}
	
	

}
