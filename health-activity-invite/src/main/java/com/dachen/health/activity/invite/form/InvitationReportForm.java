package com.dachen.health.activity.invite.form;

import org.springframework.context.annotation.Scope;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/22 14:47 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Scope("prototype")
public class InvitationReportForm {
    /**
     * 邀请人姓名
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

    @Override
    public String toString() {
        return "InvitationReportForm{" +
            "userName='" + userName + '\'' +
            ", activityId='" + activityId + '\'' +
            ", subsystem=" + subsystem +
            '}';
    }
}
