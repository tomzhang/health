package com.dachen.health.activity.invite.form;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.activity.invite.entity.Activity;
import com.dachen.health.activity.invite.enums.InviteEnum.ActivityTypeEnum;
import com.dachen.util.StringUtil;
import java.util.Objects;
import org.springframework.context.annotation.Scope;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/22 16:37 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Scope("prototype")
public class ActivityForm {
    /**
     * 活动名称
     */
    private String name;
    /**
     * 活动内容
     */
    private String content;
    /**
     * 活动类型（邀请活动、注册活动）
     * @see ActivityTypeEnum
     */
    private Integer type;
    /**
     * 活动开始时间
     */
    private Long startTime;
    /**
     * 活动结束时间
     */
    private Long endTime;

    public Activity toActivity() {
        this.check();
        return new Activity(this);
    }

    public void check() {
        if (StringUtil.isBlank(this.name)) {
            throw new ServiceException("活动名称不能为空");
        }
        if (StringUtil.isBlank(this.content)) {
            throw new ServiceException("活动内容不能为空");
        }
        if (Objects.isNull(this.type)) {
            throw new ServiceException("活动类型不能为空");
        }
        if (Objects.isNull(this.startTime)) {
            throw new ServiceException("活动开始时间不能为空");
        }
        if (Objects.isNull(this.endTime)) {
            throw new ServiceException("活动结束时间不能为空");
        }
        if (this.startTime.compareTo(this.endTime) > 0){
            throw new ServiceException("活动开始时间不能大于活动结束时间");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
}
