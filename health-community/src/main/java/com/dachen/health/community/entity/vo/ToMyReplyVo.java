package com.dachen.health.community.entity.vo;

public class ToMyReplyVo {
	/**
	 * 回复的id
	 */
	private String replyId;
	/**
	 * 回复人的用户id
	 */
	private Integer toUserId;
	/**
	 * 回复人的姓名
	 */
	private String toUserName;
	/**
	 * 回复人的姓名
	 */
	private String toUserHeadUrl;
	/**
	 * 回复人回复的内容
	 */
	private String toContent;
	/**
	 * 回复主帖的id
	 */
	private String topicId;
	/**
	 * 回复人的姓名
	 */
	private String userName;
	/**
	 * 我的用户id
	 */
	private Integer myUserId;
	/**
	 * id
	 */
	private Integer userId;
	/**
	 * 回复的内容
	 */
	private String content;
	
	private String time;
	public String getReplyId() {
		return replyId;
	}
	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	
	public String getToContent() {
		return toContent;
	}
	public void setToContent(String toContent) {
		this.toContent = toContent;
	}
	public String getTopicId() {
		return topicId;
	}
	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Integer getToUserId() {
		return toUserId;
	}
	public void setToUserId(Integer toUserId) {
		this.toUserId = toUserId;
	}
	public String getToUserHeadUrl() {
		return toUserHeadUrl;
	}
	public void setToUserHeadUrl(String toUserHeadUrl) {
		this.toUserHeadUrl = toUserHeadUrl;
	}
	public Integer getMyUserId() {
		return myUserId;
	}
	public void setMyUserId(Integer myUserId) {
		this.myUserId = myUserId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	
}
