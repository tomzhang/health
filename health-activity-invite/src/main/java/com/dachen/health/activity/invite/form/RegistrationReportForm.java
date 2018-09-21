package com.dachen.health.activity.invite.form;

import org.springframework.context.annotation.Scope;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/22 14:47 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Scope("prototype")
public class RegistrationReportForm {
    /**
     * 医生姓名
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
     * 开始时间
     */
    private Long startTime;
    /**
     * 结束时间
     */
    private Long endTime;
    /**
     * 邀请方式（短信，微信，二维码）
     * @see com.dachen.health.commons.constants.UserEnum.InviteWayEnum
     */
    private String way;

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

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }
}
