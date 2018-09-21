package com.dachen.health.msg.entity.vo;

import java.util.Map;

import com.dachen.im.server.data.response.MsgListResult;

public class MsgPollingVO {
	private EventResult event=null;
	private MsgListResult msgList=null;
	private Map business=null;
	public EventResult getEvent() {
		return event;
	}
	public void setEvent(EventResult event) {
		this.event = event;
	}
	public MsgListResult getMsgList() {
		return msgList;
	}
	public void setMsgList(MsgListResult msgList) {
		this.msgList = msgList;
	}
	public Map getBusiness() {
		return business;
	}
	public void setBusiness(Map business) {
		this.business = business;
	}
	
}
