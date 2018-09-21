package com.dachen.health.community.entity.param;

import java.util.List;
import java.util.Set;

import com.dachen.commons.page.PageVO;

public class ReplyParam extends PageVO{
	private String id;
	/**
	 * 帖子id
	 */
	private String  topicId;
	/**
	 * 回复时间
	 */
	private Long time;
	/**
	 * 创建用户id
	 */
	private Integer createUserId;
	/**
	 * 图片url
	 */
	private List<String> imgUrls;
	/**
	 * 回复内容
	 */
	private String content;
	/**
	 * 回复的用户的id
	 */
	private Integer toUserId;
	/**
	 * 回复评论的id
	 */
	private String toReplyId;
	/**
	 * 集团id
	 */
	private String groupId;
	
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
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Integer getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}
	public List<String> getImgUrls() {
		return imgUrls;
	}
	public void setImgUrls(List<String> imgUrls) {
		this.imgUrls = imgUrls;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getToUserId() {
		return toUserId;
	}
	public void setToUserId(Integer toUserId) {
		this.toUserId = toUserId;
	}
	public String getToReplyId() {
		return toReplyId;
	}
	public void setToReplyId(String toReplyId) {
		this.toReplyId = toReplyId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	
	
	
}
