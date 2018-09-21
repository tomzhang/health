package com.dachen.health.circle.entity;

import com.dachen.health.circle.CircleEnum;
import com.dachen.health.commons.vo.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.NotSaved;

import java.util.List;

/**
 * 科室动态的评论
 */
@Entity(value = "c_group_trend_comment", noClassnameStored = true)
public class GroupTrendComment {

    @Id
    private ObjectId id;

    /**
     * 所属的trendId
     */
    @Indexed
    private String trendId;

    /**
     * 所属的group（冗余）
     */
    private String groupId;

    /**
     * 回复或评论的内容
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

    /**
     * 评论的最近回复id（冗余）
     */
    private List<String> recentReplyIdList;

    /**
     * 评论图片
     */
    private String[] imageList;

    private Integer userId;
    private Long createTime;

    private Integer totalReply = 0;
    private Integer totalLike = 0;
    /**
     * 最近点赞ids
     */
    private List<String> recentLikeIdList;
    // 包装时的参数 start
    @NotSaved
    private User user;
    @NotSaved
    private List<GroupTrendCommentReply> recentReplyList;
    @NotSaved
    private List<GroupTrendCommentLike> recentLikeList;
    @NotSaved
    private GroupTrendCommentLike like;
    // 包装时的参数 end

    public GroupTrendComment() {
    }

    public GroupTrendComment(String trendId, Integer userId, String content) {
        this.trendId = trendId;
        this.userId = userId;
        this.content = content;
    }

    public List<GroupTrendCommentLike> getRecentLikeList() {
        return recentLikeList;
    }

    public void setRecentLikeList(List<GroupTrendCommentLike> recentLikeList) {
        this.recentLikeList = recentLikeList;
    }

    public GroupTrendCommentLike getLike() {
        return like;
    }

    public void setLike(GroupTrendCommentLike like) {
        this.like = like;
    }

    public void setStatus(CircleEnum.TrendCommentStatusEnum status) {
        this.statusId = status.getId();
        this.statusTime = System.currentTimeMillis();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public List<String> getRecentReplyIdList() {
        return recentReplyIdList;
    }

    public void setRecentReplyIdList(List<String> recentReplyIdList) {
        this.recentReplyIdList = recentReplyIdList;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<GroupTrendCommentReply> getRecentReplyList() {
        return recentReplyList;
    }

    public void setRecentReplyList(List<GroupTrendCommentReply> recentReplyList) {
        this.recentReplyList = recentReplyList;
    }

    public List<String> getRecentLikeIdList() {
        return recentLikeIdList;
    }

    public void setRecentLikeIdList(List<String> recentLikeIdList) {
        this.recentLikeIdList = recentLikeIdList;
    }

    public String[] getImageList() {
        return imageList;
    }

    public void setImageList(String[] imageList) {
        this.imageList = imageList;
    }
}