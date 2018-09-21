package com.dachen.health.activity.invite.form;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.activity.invite.entity.CircleInvite;
import com.dachen.util.StringUtil;
import java.util.Objects;
import org.springframework.context.annotation.Scope;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/22 14:47 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Scope("prototype")
public class CircleInviteForm {
    /**
     * 邀请人Id（医生或者医药代表）
     */
    private Integer userId;
    /**
     * 活动Id
     */
    private String inviteActivityId;
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

    public CircleInvite toCircleInvite(){
        this.check();
        return new CircleInvite(this);
    }

    public void check() {
        if (Objects.isNull(this.userId)){
            throw new ServiceException("邀请人Id不能为空");
        }
        if (StringUtil.isBlank(this.inviteActivityId)){
            throw new ServiceException("邀请活动Id不能为空");
        }
        if (Objects.isNull(subsystem)){
            throw new ServiceException("来源子系统不能为空");
        }
        if (StringUtil.isBlank(this.way)){
            throw new ServiceException("邀请方式不能为空");
        }
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getInviteActivityId() {
        return inviteActivityId;
    }

    public void setInviteActivityId(String inviteActivityId) {
        this.inviteActivityId = inviteActivityId;
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

    @Override
    public String toString() {
        return "CircleInviteForm{" +
            "userId=" + userId +
            ", inviteActivityId='" + inviteActivityId + '\'' +
            ", activityName='" + activityName + '\'' +
            ", subsystem=" + subsystem +
            ", way='" + way + '\'' +
            '}';
    }
}
