package com.dachen.health.msg.entity.vo;

import java.util.HashMap;
import java.util.Map;

public class BusinessVO {

	/**
	 * 会话列表
	 */
	private MsgGroupVO msgGroupVO;
	
	private EventResult event=null;
	
	private Map business;

	public MsgGroupVO getMsgGroupVO() {
		return msgGroupVO;
	}

	public void setMsgGroupVO(MsgGroupVO msgGroupVO) {
		this.msgGroupVO = msgGroupVO;
	}

	public EventResult getEvent() {
		return event;
	}

	public void setEvent(EventResult event) {
		this.event = event;
	}

	public Map getBusiness() {
		return business;
	}

	public void setBusiness(Map business) {
		this.business = business;
	}

	
}
