package com.dachen.health.msg.entity.vo;

import java.util.List;

import com.dachen.im.server.data.EventVO;

public class EventResult {
	private Long ts;
	private List<EventVO> list=null;
	public Long getTs() {
		return ts;
	}
	public void setTs(Long ts) {
		this.ts = ts;
	}
	public List<EventVO> getList() {
		return list;
	}
	public void setList(List<EventVO> list) {
		this.list = list;
	}
}
