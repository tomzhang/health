package com.dachen.health.user.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;

@Entity(value = "u_fastReply", noClassnameStored = true)
@Indexes(@Index("userId,replyContent"))
public class FastandReply {
	
	
	@Id
    private ObjectId id;
	
	@Indexed
	private Integer userId;
	
	private String replyContent;
	
	private Long replyTime;
	
	private Integer replyType;
	
	private int is_system;//是否系统预制   默认为1：自己添加； 0：系统预置
	
	

	public int getIs_system() {
		return is_system;
	}

	public void setIs_system(int is_system) {
		this.is_system = is_system;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public Long getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(Long replyTime) {
		this.replyTime = replyTime;
	}

	public Integer getReplyType() {
		return replyType;
	}

	public void setReplyType(Integer replyType) {
		this.replyType = replyType;
	}
	
}
