package com.dachen.health.community.entity.vo;

import java.util.List;

public class ReplyVo {
	private String id;
	/**
	 * 主帖id
	 */
	private String topicId;
	/**
	 * 回复人id
	 */
	private Integer replyUserId;
	/**
	 * 回复人昵称
	 */
	private String  replyName;
	/**
	 * 回复人头像地址
	 */
	private String replyHeadUrl;
	/**
	 * 被回复人id
	 */
	private Integer toUsesId;
	
	private List<String> imgUrls;
	/**
	 * 被回复人姓名
	 */
	private String toUserName;
	/**
	 * 被回复人姓名
	 */
	private String toUserHeadUrl;
	/**
	 * 回复内容
	 */
	private String content;
	/**
	 * 回复时间
	 */
	private String time;
	
	private String  floor;
	/**
	 * 判断是否能够删除
	 */
	private String delete;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTopicId() {
		return topicId;
	}
	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
	
	public String getReplyName() {
		return replyName;
	}
	public void setReplyName(String replyName) {
		this.replyName = replyName;
	}
	public String getReplyHeadUrl() {
		return replyHeadUrl;
	}
	public void setReplyHeadUrl(String replyHeadUrl) {
		this.replyHeadUrl = replyHeadUrl;
	}
	public Integer getToUsesId() {
		return toUsesId;
	}
	public void setToUsesId(Integer toUsesId) {
		this.toUsesId = toUsesId;
	}
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getToUserHeadUrl() {
		return toUserHeadUrl;
	}
	public void setToUserHeadUrl(String toUserHeadUrl) {
		this.toUserHeadUrl = toUserHeadUrl;
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
	public String getDelete() {
		return delete;
	}
	public void setDelete(String delete) {
		this.delete = delete;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public Integer getReplyUserId() {
		return replyUserId;
	}
	public void setReplyUserId(Integer replyUserId) {
		this.replyUserId = replyUserId;
	}
	public List<String> getImgUrls() {
		return imgUrls;
	}
	public void setImgUrls(List<String> imgUrls) {
		this.imgUrls = imgUrls;
	}
	
	
}
