package com.dachen.health.activity.invite.entity;

import com.dachen.health.activity.invite.form.CircleInviteForm;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/22 13:49 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Entity(value = "c_circle_invite", noClassnameStored = true)
public class CircleInvite {
    @Id
    private String id;

    /**
     * 邀请人Id（医生或者医药代表）
     */
    private Integer userId;
    /**
     * 邀请人姓名（冗余字段）
     */
    private String userName;
    /**
     * 活动Id
     */
    private String activityId;
    /**
     * 活动名称（冗余字段）
     */
    private String activityName;
    /**
     * 来源子系统（医生圈、药企圈）
     * @see com.dachen.health.commons.constants.UserEnum.Source
     */
    private Integer subsystem;
    /**
     * 邀请方式（短信，微信，二维码）
     * @see com.dachen.health.commons.constants.UserEnum.InviteWayEnum
     */
    private String way;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;

    public CircleInvite(){
    }

    public CircleInvite(CircleInviteForm form){
        this.userId = form.getUserId();
        this.activityId = form.getInviteActivityId();
        this.activityName = form.getActivityName();
        this.subsystem = form.getSubsystem();
        this.way = form.getWay();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(Integer subsystem) {
        this.subsystem = subsystem;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

}
