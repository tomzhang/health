package com.dachen.health.meeting.vo;

public class MeetHttpParams {

	private String subject;
	
	private String startTime;
	
	/*组织者加入直播口令*/
	private String organizerToken;
	
	/*嘉宾加入直播口令*/
	private String panelistToken;
	
	/*普通参加者加入直播口令*/
	private String attendeeToken;
	
	private String loginName;
	
	private String password;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getOrganizerToken() {
		return organizerToken;
	}

	public void setOrganizerToken(String organizerToken) {
		this.organizerToken = organizerToken;
	}

	public String getPanelistToken() {
		return panelistToken;
	}

	public void setPanelistToken(String panelistToken) {
		this.panelistToken = panelistToken;
	}

	public String getAttendeeToken() {
		return attendeeToken;
	}

	public void setAttendeeToken(String attendeeToken) {
		this.attendeeToken = attendeeToken;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "MeetHttpParams [subject=" + subject + ", startTime=" + startTime + ", organizerToken=" + organizerToken
				+ ", panelistToken=" + panelistToken + ", attendeeToken=" + attendeeToken + ", loginName=" + loginName
				+ ", password=" + password + "]";
	}
	
}
