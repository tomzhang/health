package com.dachen.health.activity.invite.vo;

import com.dachen.health.commons.constants.UserEnum;
import org.springframework.context.annotation.Scope;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/22 14:47 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Scope("prototype")
public class RegistrationReportVO {
    /**
     * 注册时间
     */
    private Long registrationTime;
    /**
     * 医生姓名
     */
    private String userName;
    /**
     * 医院
     */
    private String hospital;
    /**
     * 科室
     */
    private String dept;
    /**
     * 职称
     */
    private String title;
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
     * 邀请方式（短信，微信，二维码）
     * @see com.dachen.health.commons.constants.UserEnum.InviteWayEnum
     */
    private String way;
    /**
     * 认证状态
     * @see UserEnum.UserStatus
     */
    private Integer status;
    /**
     * 邀请人
     */
    private String inviter;
    /**
     * 邀请人Id
     */
    private Integer inviterId;
    /**
     * 邀请人openId
     */
    private String inviterOpenId;

    public Long getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Long registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public Integer getInviterId() {
        return inviterId;
    }

    public void setInviterId(Integer inviterId) {
        this.inviterId = inviterId;
    }

    public String getInviterOpenId() {
        return inviterOpenId;
    }

    public void setInviterOpenId(String inviterOpenId) {
        this.inviterOpenId = inviterOpenId;
    }
}
