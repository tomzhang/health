package com.dachen.health.system.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.NotSaved;

@Entity(value = "s_feed_back", noClassnameStored = true)
@Indexes({ @Index(fields = { @Field(value = "userId") }) })
public class FeedBack {
	@Id
    private ObjectId id;
	/**
	 * 反馈的用户id
	 */
	private Integer userId;
	/**
	 * 反馈的用户姓名，根据userId计算
	 */
	@NotSaved
	private String userName;
	/**
	 * 反馈的手机型号
	 */
	private String phoneModel;
	/**
	 * 反馈的手机操作系统
	 */
	private String phoneSystem;
	/**
	 * 反馈的客户端版本
	 */
	private String clientVersion;
	/**
	 * 反馈内容
	 */
	private String content;
	/**
	 * 反馈的用户的类型，根据userId计算出
	 */
	private String userTypeTiltle;

	/**
	 * 反馈时间
	 */
	private long createTime;
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
	public String getPhoneModel() {
		return phoneModel;
	}
	public void setPhoneModel(String phoneModel) {
		this.phoneModel = phoneModel;
	}
	public String getPhoneSystem() {
		return phoneSystem;
	}
	public void setPhoneSystem(String phoneSystem) {
		this.phoneSystem = phoneSystem;
	}
	public String getClientVersion() {
		return clientVersion;
	}
	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserTypeTiltle() {
		return userTypeTiltle;
	}
	public void setUserTypeTiltle(String userTypeTiltle) {
		this.userTypeTiltle = userTypeTiltle;
	}
 
	
	
	
}
