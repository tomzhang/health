package com.dachen.health.friend.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;

/**
 * ProjectName： health-service<br>
 * ClassName： DoctorFriend<br>
 * Description：医生好友关系实体 <br>
 * 
 * @author fanp
 * @crateTime 2015年6月29日
 * @version 1.0.0
 */
@Entity(value = "u_doctor_friend", noClassnameStored = true)
@Indexes(@Index("userId,status"))
public class DoctorFriend {

    @Id
    private ObjectId id;

    private Integer userId;

    private Integer toUserId;

    /* 标签 */
    private String[] tags;

    /* 添加时间 */
    private Long createTime;

    /* 状态 */
    @Indexed
    private Integer status;
    
    /**
     * 好友设置
     */
    @Embedded
    private FriendSetting setting;

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

    public Integer getToUserId() {
        return toUserId;
    }

    public void setToUserId(Integer toUserId) {
        this.toUserId = toUserId;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public FriendSetting getSetting() {
		return setting;
	}

	public void setSetting(FriendSetting setting) {
		this.setting = setting;
	}

    
}
