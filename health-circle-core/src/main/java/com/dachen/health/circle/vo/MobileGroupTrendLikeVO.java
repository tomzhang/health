package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.GroupTrendLike;

import java.io.Serializable;

public class MobileGroupTrendLikeVO implements Serializable {
    private String likeId;
    private String trendId;
    private String groupId;
    private Long createTime;
    private Integer userId;
    private MobileDoctorVO user;

    public MobileGroupTrendLikeVO() {

    }

    public MobileGroupTrendLikeVO(GroupTrendLike like) {
        this.likeId = like.getId().toString();
        this.trendId = like.getTrendId();
        this.groupId = like.getGroupId();
        this.createTime = like.getCreateTime();
        this.userId = like.getUserId();
        if (null != like.getUser()) {
            this.user = new MobileDoctorVO(like.getUser());
        }
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public String getTrendId() {
        return trendId;
    }

    public void setTrendId(String trendId) {
        this.trendId = trendId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public MobileDoctorVO getUser() {
        return user;
    }

    public void setUser(MobileDoctorVO user) {
        this.user = user;
    }
}
