package com.dachen.health.circle.vo;

import com.dachen.health.circle.CircleEnum;
import com.dachen.health.circle.entity.GroupTrendComment;
import com.dachen.health.circle.entity.GroupTrendCommentReply;
import com.dachen.sdk.util.SdkUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MobileGroupTrendCommentReplyVO implements Serializable {
    private String replyId;
    private String commentId;
    private String content;
    private Long createTime;
    private Integer userId;
    private MobileUserVO user;

    private String replyToId;
    private Integer replyToUserId;
    private MobileUserVO replyToUser;

    public MobileGroupTrendCommentReplyVO() {

    }

    public MobileGroupTrendCommentReplyVO(GroupTrendCommentReply reply) {
        this.replyId = reply.getId().toString();
        this.commentId = reply.getCommentId();
        this.content = reply.getContent();
        this.userId = reply.getUserId();
        if (null != reply.getUser()) {
            this.user = new MobileUserVO(reply.getUser());
        }
        this.createTime = CircleEnum.TrendCommentStatusEnum.Passed == CircleEnum.TrendCommentStatusEnum.eval(reply.getStatusId())?reply.getStatusTime():reply.getCreateTime();
        this.replyToId = reply.getReplyToId();
        this.replyToUserId = reply.getReplyToUserId();
        if (null != reply.getReplyToUser()) {
            this.replyToUser = new MobileUserVO(reply.getReplyToUser());
        }
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
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

    public void setUser(MobileUserVO user) {
        this.user = user;
    }

    public String getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(String replyToId) {
        this.replyToId = replyToId;
    }

    public Integer getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(Integer replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public MobileUserVO getReplyToUser() {
        return replyToUser;
    }

    public void setReplyToUser(MobileUserVO replyToUser) {
        this.replyToUser = replyToUser;
    }
}
