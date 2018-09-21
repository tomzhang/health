package com.dachen.health.pack.conference.entity.po;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name ="request")
public class AuthRequest {
	
	private String event;//值为：callreq
	private String callid;//呼叫的唯一标识（sdk组件生成）
	private String accountid;//开发者账号id
	private String appid;//应用id
	private int calltype;//0：直拨，1：免费，2：回拨
	private int callertype;//主叫号码类型，0：Client账号，1：普通电话
	private int callerchargetype;//必选 	主叫计费类型，0：开发者计费1：PAAS平台计费，默认为0
	private float callerbalance;//必选 	在PAAS平台主叫帐户钱包余额(单位:元)
	private String caller;//主叫号码
	private int calledtype;//被叫号码类型，0：Client账号，1：普通电话
	private String called;//被叫号码
	private String userData;//可选 	用户自定义数据字符串，最大长度128字节 
	
	
	@XmlElement
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	@XmlElement
	public String getCallid() {
		return callid;
	}
	public void setCallid(String callid) {
		this.callid = callid;
	}
	@XmlElement
	public String getAccountid() {
		return accountid;
	}
	public void setAccountid(String accountid) {
		this.accountid = accountid;
	}
	@XmlElement
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	@XmlElement
	public int getCalltype() {
		return calltype;
	}
	public void setCalltype(int calltype) {
		this.calltype = calltype;
	}
	@XmlElement
	public int getCallertype() {
		return callertype;
	}
	public void setCallertype(int callertype) {
		this.callertype = callertype;
	}
	@XmlElement
	public int getCallerchargetype() {
		return callerchargetype;
	}
	public void setCallerchargetype(int callerchargetype) {
		this.callerchargetype = callerchargetype;
	}
	@XmlElement
	public float getCallerbalance() {
		return callerbalance;
	}
	public void setCallerbalance(float callerbalance) {
		this.callerbalance = callerbalance;
	}
	@XmlElement
	public String getCaller() {
		return caller;
	}
	public void setCaller(String caller) {
		this.caller = caller;
	}
	@XmlElement
	public int getCalledtype() {
		return calledtype;
	}
	public void setCalledtype(int calledtype) {
		this.calledtype = calledtype;
	}
	@XmlElement
	public String getCalled() {
		return called;
	}
	public void setCalled(String called) {
		this.called = called;
	}
	@XmlElement
	public String getUserData() {
		return userData;
	}
	public void setUserData(String userData) {
		this.userData = userData;
	}
	
	
	
	

}
