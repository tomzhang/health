package com.dachen.health.circle.vo;

import com.dachen.health.circle.CircleEnum;
import com.dachen.health.circle.entity.GroupTrendComment;
import com.dachen.health.circle.entity.GroupTrendCommentLike;
import com.dachen.health.circle.entity.GroupTrendCommentReply;
import com.dachen.sdk.util.SdkUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MobileGroupTrendCommentVO implements Serializable {
    private String trendId;
    private String commentId;
    private String[] imageList;
    private String content;
    private Long createTime;
    private Integer userId;
    private MobileDoctorVO user;

    private Integer totalReply;
    private Integer totalLike;

    private List<MobileGroupTrendCommentReplyVO> recentReplyList;

    private List<MobileGroupTrendCommentLikeVO> recentLikeList;
    private Boolean ifLike = false;

    public MobileGroupTrendCommentVO() {

    }

    public MobileGroupTrendCommentVO(GroupTrendComment comment) {
        this.commentId = comment.getId().toString();
        this.imageList=comment.getImageList();
        this.content = comment.getContent();
        this.userId = comment.getUserId();
        this.trendId = comment.getTrendId();
        if (null != comment.getUser()) {
            this.user = new MobileDoctorVO(comment.getUser());
        }
        this.createTime = CircleEnum.TrendCommentStatusEnum.Passed == CircleEnum.TrendCommentStatusEnum.eval(comment.getStatusId())?comment.getStatusTime():comment.getCreateTime();
        this.totalLike = comment.getTotalLike();
        this.totalReply = comment.getTotalReply();

        if (SdkUtils.isNotEmpty(comment.getRecentReplyList())) {
            this.recentReplyList = new ArrayList<>(comment.getRecentReplyList().size());
            for (GroupTrendCommentReply reply:comment.getRecentReplyList()) {
                this.recentReplyList.add(new MobileGroupTrendCommentReplyVO(reply));
            }
        }

        if (SdkUtils.isNotEmpty(comment.getRecentLikeList())) {
            this.recentLikeList = new ArrayList<>(comment.getRecentLikeList().size());
            for (GroupTrendCommentLike like:comment.getRecentLikeList()) {
                this.recentLikeList.add(new MobileGroupTrendCommentLikeVO(like));
            }
        }
        if(null!=comment.getLike()){
            this.ifLike=true;
        }
    }

    public List<MobileGroupTrendCommentLikeVO> getRecentLikeList() {
        return recentLikeList;
    }

    public void setRecentLikeList(List<MobileGroupTrendCommentLikeVO> recentLikeList) {
        this.recentLikeList = recentLikeList;
    }

    public String getTrendId() {
        return trendId;
    }

    public void setTrendId(String trendId) {
        this.trendId = trendId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getImageList() {
        return imageList;
    }

    public void setImageList(String[] imageList) {
        this.imageList = imageList;
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

    public Integer getTotalReply() {
        return totalReply;
    }

    public void setTotalReply(Integer totalReply) {
        this.totalReply = totalReply;
    }

    public Integer getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(Integer totalLike) {
        this.totalLike = totalLike;
    }

    public List<MobileGroupTrendCommentReplyVO> getRecentReplyList() {
        return recentReplyList;
    }

    public void setRecentReplyList(List<MobileGroupTrendCommentReplyVO> recentReplyList) {
        this.recentReplyList = recentReplyList;
    }
    public Boolean getIfLike() {
        return ifLike;
    }

    public void setIfLike(Boolean ifLike) {
        this.ifLike = ifLike;
    }
}
