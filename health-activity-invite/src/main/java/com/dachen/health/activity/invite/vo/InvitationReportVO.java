package com.dachen.health.activity.invite.vo;

import org.springframework.context.annotation.Scope;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/22 14:47 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Scope("prototype")
public class InvitationReportVO {
    private String openId;
    private Integer userId;
    /**
     * 邀请人姓名
     */
    private String userName;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 来源子系统（医生圈-17、药企圈-16）
     * @see com.dachen.health.commons.constants.UserEnum.Source
     */
    private Integer subsystem;
    /**
     * 医院
     */
    private String hospital;
    /**
     * 职称
     */
    private String title;
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

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
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

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    @Override
    public String toString() {
        return "InvitationReportVO{" +
            "userName='" + userName + '\'' +
            ", activityName='" + activityName + '\'' +
            ", subsystem=" + subsystem +
            ", hospital='" + hospital + '\'' +
            ", title='" + title + '\'' +
            ", wechatCount=" + wechatCount +
            ", smsCount=" + smsCount +
            ", qrcodeCount=" + qrcodeCount +
            ", registeredCount=" + registeredCount +
            ", autherizedCount=" + autherizedCount +
            '}';
    }
}
