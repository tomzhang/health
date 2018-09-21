package com.dachen.health.community.entity.po;

import java.io.Serializable;
import java.util.Set;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * 我的收藏
 * 
 * @Description
 * @title CommunityUsers
 * @author liminng
 * @data 2016年7月26日
 */
@Entity(value = "c_community_users", noClassnameStored = true)
public class CommunityUsers implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Integer userId;
	/**
	 * 收藏帖子id
	 */
	private Set<CollectIds> collects;
	/**
	 * 是否有新回复
	 */
	private String message;
	/**
	 * 医生集团id
	 */
	private String groupId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Set<CollectIds> getCollects() {
		return collects;
	}

	public void setCollects(Set<CollectIds> collects) {
		this.collects = collects;
	}

}
