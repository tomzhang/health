package com.dachen.health.community.entity.po;

import java.io.Serializable;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
@Entity(value="c_community_reply", noClassnameStored = true)
public class Reply implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2256112064514752474L;
	@Id
	private String id;
	private String topicId;
	private Integer createUserId;
	private Long time;
	private Accessory accessory;
	private String state;
	private Integer toUserId;
	private String content;
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
	public Integer getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Accessory getAccessory() {
		return accessory;
	}
	public void setAccessory(Accessory accessory) {
		this.accessory = accessory;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Integer getToUserId() {
		return toUserId;
	}
	public void setToUserId(Integer toUserId) {
		this.toUserId = toUserId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
