package com.dachen.health.activity.invite.entity;

import com.dachen.health.activity.invite.enums.InviteEnum.ActivityTypeEnum;
import com.dachen.health.activity.invite.form.ActivityForm;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/22 14:04 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Entity(value = "c_circle_activity", noClassnameStored = true)
public class Activity {
    @Id
    private String id;

    /**
     * 活动名称
     */
    private String name;
    /**
     * 活动码6至12位字符串
     */
    private String code;
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
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 是否删除
     */
    private Boolean deleted;
    /**
     * 是否生效 1-生效； 0-不生效
     */
    private Boolean valid;

    public Activity() {

    }

    public Activity(ActivityForm form) {
        this.name = form.getName();
        this.content = form.getContent();
        this.type = form.getType();
        this.startTime = form.getStartTime();
        this.endTime = form.getEndTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
