package com.ucpaas.restsdk.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "voiceNotify")
public class VoiceNotify {

	private String appId;
	
	private String to;
	
	private String type;
	
	private String content;
	
	private String toSerNum;
	
	private String playTimes;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getToSerNum() {
		return toSerNum;
	}

	public void setToSerNum(String toSerNum) {
		this.toSerNum = toSerNum;
	}

	public String getPlayTimes() {
		return playTimes;
	}

	public void setPlayTimes(String playTimes) {
		this.playTimes = playTimes;
	}
}
