package com.dachen.health.activity.invite.entity;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/5 19:41 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Entity(value = "c_circle_invite_report", noClassnameStored = true)
public class CircleInviteReport {
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
     * 来源子系统（医生圈-17、药企圈-16）
     * @see com.dachen.health.commons.constants.UserEnum.Source
     */
    private Integer subsystem;
    /**
     * 微信邀请数
     */
    private Integer wechatCount;
    /**
     * 短信邀请数
     */
    private Integer smsCount;
    /**
     * 二维码邀请数
     */
    private Integer qrcodeCount;
    /**
     * 已注册用户数
     */
    private Integer registeredCount;
    /**
     * 已认证用户数
     */
    private Integer autherizedCount;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Integer getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(Integer subsystem) {
        this.subsystem = subsystem;
    }

    public Integer getWechatCount() {
        return wechatCount;
    }

    public void setWechatCount(Integer wechatCount) {
        this.wechatCount = wechatCount;
    }

    public Integer getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(Integer smsCount) {
        this.smsCount = smsCount;
    }

    public Integer getQrcodeCount() {
        return qrcodeCount;
    }

    public void setQrcodeCount(Integer qrcodeCount) {
        this.qrcodeCount = qrcodeCount;
    }

    public Integer getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(Integer registeredCount) {
        this.registeredCount = registeredCount;
    }

    public Integer getAutherizedCount() {
        return autherizedCount;
    }

    public void setAutherizedCount(Integer autherizedCount) {
        this.autherizedCount = autherizedCount;
    }
}
