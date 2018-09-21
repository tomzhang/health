package com.dachen.health.meeting.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

import com.dachen.util.JSONUtil;

@Entity(value="t_meeting" , noClassnameStored=true)
public class Meeting {

	@Id
	private String id;
	
	private String company;
	
	private String companyId;
	
	private String subject;
	
	private Long startDate;
	
	private Long startTime;
	
	private Long endTime;
	/*观看人数*/
	private Integer attendeesCount;
	
	private Integer price;
	
	/*组织者加入直播口令*/
	private String organizerToken;
	
	/*嘉宾加入直播口令*/
	private String panelistToken;
	
	/*普通参加者加入直播口令*/
	private String attendeeToken;
	/*组织者加入URL*/
	private String organizerJoinUrl;
	/*嘉宾加入URL*/
	private String panelistJoinUrl;
	/*普通参加者加入URL*/
	private String attendeeJoinUrl;
	
	/*直播id*/
	private String liveId;
	
	private String number;
	
	/*第三方服务调用之后的结果说明*/
	private String message;
	
	/*是否取消或结束（0：未取消{默认}，1：已取消或结束）*/
	private Integer isStop = 0;
	
	private Long updateTime;
	
	private @NotSaved Integer status;
	
	private Integer createUserId;
	
	private @NotSaved String createUserName;
	
	private @NotSaved String headPicFileName;
	
	private @NotSaved String domain;
	
	private @NotSaved Integer isMyCreate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Integer getAttendeesCount() {
		return attendeesCount;
	}

	public void setAttendeesCount(Integer attendeesCount) {
		this.attendeesCount = attendeesCount;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
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

	public String getOrganizerJoinUrl() {
		return organizerJoinUrl;
	}

	public void setOrganizerJoinUrl(String organizerJoinUrl) {
		this.organizerJoinUrl = organizerJoinUrl;
	}

	public String getPanelistJoinUrl() {
		return panelistJoinUrl;
	}

	public void setPanelistJoinUrl(String panelistJoinUrl) {
		this.panelistJoinUrl = panelistJoinUrl;
	}

	public String getAttendeeJoinUrl() {
		return attendeeJoinUrl;
	}

	public void setAttendeeJoinUrl(String attendeeJoinUrl) {
		this.attendeeJoinUrl = attendeeJoinUrl;
	}

	public String getLiveId() {
		return liveId;
	}

	public void setLiveId(String liveId) {
		this.liveId = liveId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getIsStop() {
		return isStop;
	}

	public void setIsStop(Integer isStop) {
		this.isStop = isStop;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Integer getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	public Integer getIsMyCreate() {
		return isMyCreate;
	}

	public void setIsMyCreate(Integer isMyCreate) {
		this.isMyCreate = isMyCreate;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

	
	
	public static void main(String[] args) {
		
		String header = "null/1.023";
		int start = header.indexOf('/')+1;
		int end = header.indexOf('(');
		int last = header.length();
		if(end < 0)
			end = last;
		String version = header.substring(start, end).trim();
		System.out.println(version);
		if(version.matches("^[2-9]{1}.*") || version.matches("^[1-9]{1}\\.[1-9]{1,}.*")){
			System.out.println("ok");
		}else{
			System.out.println("no");
		}
		//^[2-9]{1}.*
		//^[1-9]{1}\\.[1-9]{1,}.*
	}
}
