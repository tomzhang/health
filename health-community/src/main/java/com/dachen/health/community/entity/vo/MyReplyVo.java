package com.dachen.health.community.entity.vo;

public class MyReplyVo {
	/**
	 * 姓名
	 */
	private String userName;
	/**
	 * 头像地址
	 */
	private String headUrl;
	/**
	 * 帖子的id
	 */
	private String topicId;
	/**
	 * 回复的内容
	 */
	private String content;
	/**
	 * 回复的时间
	 */
	private String time;
	/**
	 * 回复的用户名
	 */
	private String toUserName;
	/**
	 * 回复的帖子的标题或评论内容
	 */
	private String toContent;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getHeadUrl() {
		return headUrl;
	}
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}
	public String getTopicId() {
		return topicId;
	}
	public void setTopicId(String topicId) {
		this.topicId = topicId;
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
	
	
}
