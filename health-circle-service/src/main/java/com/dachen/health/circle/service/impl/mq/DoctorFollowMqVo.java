package com.dachen.health.circle.service.impl.mq;

/**
 * Created with IntelliJ IDEA
 * Created By lim
 * Date: 2017/8/8
 * Time: 14:40
 */
public class DoctorFollowMqVo {
    /**
     * 操作人用户id
     */
    Integer userId;
    /**
     * 被操作人用户id
     */
    Integer toUserId;
    /**
     * 操作类型 1关注 2取消关注
     */
    Integer type;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
