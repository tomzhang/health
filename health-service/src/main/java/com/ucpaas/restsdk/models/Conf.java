package com.ucpaas.restsdk.models;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "conf")
public class Conf {
	
	private String appId;
	private String mediaType;//会议类型：1，语音会议；默认：语音会议。
	private String maxMember;
	private String duration;
	private String playTone;
	private String confId;
	private String memberList;
	private String callId;
	private String playFile;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public String getMaxMember() {
		return maxMember;
	}
	public void setMaxMember(String maxMember) {
		this.maxMember = maxMember;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getPlayTone() {
		return playTone;
	}
	public void setPlayTone(String playTone) {
		this.playTone = playTone;
	}
	public String getConfId() {
		return confId;
	}
	public void setConfId(String confId) {
		this.confId = confId;
	}
	public String getMemberList() {
		return memberList;
	}
	public void setMemberList(String memberList) {
		this.memberList = memberList;
	}
	public String getCallId() {
		return callId;
	}
	public void setCallId(String callId) {
		this.callId = callId;
	}
	public String getPlayFile() {
		return playFile;
	}
	public void setPlayFile(String playFile) {
		this.playFile = playFile;
	}
	
}
