package com.dachen.health.pack.conference.entity.vo;

import java.util.List;

public class ConfListVO {
	private String time;
	private List<ConferenceStatusVO>  list;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<ConferenceStatusVO> getList() {
		return list;
	}
	public void setList(List<ConferenceStatusVO> list) {
		this.list = list;
	}
	
	

}
