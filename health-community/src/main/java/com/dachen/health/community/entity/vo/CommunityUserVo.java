package com.dachen.health.community.entity.vo;

public class CommunityUserVo {
	/**
	 * 用户id
	 */
	private Integer userId;
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 用户头像
	 */
	private String headUrl;
	/**
	 * 发表的帖子的数目
	 */
	private Long  topicAmount;
	/**
	 * 回帖和回复评论的数量
	 */
	private Long replyAmount;
	/**
	 * 收藏的数量
	 */
	private Long collectAmount;
	/**
	 * 主集团的id
	 */
	private String groupId;
	/**
	 * 是否有未读消息
	 */
	private String message;
	/**
	 * 累计点赞数
	 */
	private Long LikeCount;
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
	public Long getTopicAmount() {
		return topicAmount;
	}
	public void setTopicAmount(Long topicAmount) {
		this.topicAmount = topicAmount;
	}
	public Long getReplyAmount() {
		return replyAmount;
	}
	public void setReplyAmount(Long replyAmount) {
		this.replyAmount = replyAmount;
	}
	public Long getCollectAmount() {
		return collectAmount;
	}
	public void setCollectAmount(Long collectAmount) {
		this.collectAmount = collectAmount;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getLikeCount() {
		return LikeCount;
	}
	public void setLikeCount(Long likeCount) {
		LikeCount = likeCount;
	}
	
	
}
