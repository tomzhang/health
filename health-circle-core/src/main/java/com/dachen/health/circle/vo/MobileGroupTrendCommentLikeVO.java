package com.dachen.health.circle.vo;


import com.dachen.health.circle.entity.GroupTrendCommentLike;

import java.io.Serializable;

public class MobileGroupTrendCommentLikeVO implements Serializable {
    private String likeId;
    private String commentId;
    private Long createTime;
    private Integer userId;
    private MobileDoctorVO user;

    public MobileGroupTrendCommentLikeVO() {

    }

    public MobileGroupTrendCommentLikeVO(GroupTrendCommentLike groupTrendComment) {
        this.likeId = groupTrendComment.getId().toString();
        this.commentId = groupTrendComment.getCommentId();
        this.createTime = groupTrendComment.getCreateTime();
        this.userId = groupTrendComment.getUserId();
        if (null != groupTrendComment.getUser()) {
            this.user = new MobileDoctorVO(groupTrendComment.getUser(), groupTrendComment.getUserDept());
        }
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
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

    public MobileUserVO getUser() {
        return user;
    }

    public void setUser(MobileDoctorVO user) {
        this.user = user;
    }
}
