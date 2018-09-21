package com.dachen.health.circle.entity;

import com.dachen.health.circle.CircleEnum;
import com.dachen.health.circle.form.TrendCommentAddForm;
import com.dachen.health.commons.vo.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.NotSaved;

import java.util.List;

/**
 * 科室动态的评论的回复
 */
@Entity(value = "c_group_trend_comment_reply", noClassnameStored = true)
public class GroupTrendCommentReply {

    @Id
    private ObjectId id;

    /**
     * 回复到评论id
     */
    @Indexed
    private String commentId;

    /**
     * 所属的groupTrend（冗余）
     */
    private String trendId;

    /**
     * 回复的内容
     */
    private String content;

    /**
     * 0表示审核中，1表示审核通过，9表示已删除
     */
    @Indexed
    private Integer statusId;
    /**
     * 状态改变的时间
     */
    private Long statusTime;

    private Integer userId;
    private Long createTime;

    private String replyToId;
    private Integer replyToUserId;

    private String replyToSourceId;
    private Integer replyToSourceUserId;

    // 包装时的参数 start
    @NotSaved
    private User user;
    @NotSaved
    private User replyToUser;
    // 包装时的参数 end

    public GroupTrendCommentReply() {
    }

    public GroupTrendCommentReply(String commentId, String content, Integer userId, String replyToId) {
        this.commentId = commentId;
        this.content = content;
        this.userId = userId;
        this.replyToId = replyToId;
        this.createTime = System.currentTimeMillis();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getTrendId() {
        return trendId;
    }

    public void setTrendId(String trendId) {
        this.trendId = trendId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Long getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Long statusTime) {
        this.statusTime = statusTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getReplyToUser() {
        return replyToUser;
    }

    public void setReplyToUser(User replyToUser) {
        this.replyToUser = replyToUser;
    }

    public String getReplyToSourceId() {
        return replyToSourceId;
    }

    public void setReplyToSourceId(String replyToSourceId) {
        this.replyToSourceId = replyToSourceId;
    }

    public Integer getReplyToSourceUserId() {
        return replyToSourceUserId;
    }

    public void setReplyToSourceUserId(Integer replyToSourceUserId) {
        this.replyToSourceUserId = replyToSourceUserId;
    }

    public void setStatus(CircleEnum.TrendCommentStatusEnum status) {
        this.statusId = status.getId();
        this.statusTime = System.currentTimeMillis();
    }
}