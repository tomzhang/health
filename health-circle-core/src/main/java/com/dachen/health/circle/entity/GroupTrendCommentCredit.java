package com.dachen.health.circle.entity;

import com.dachen.health.commons.vo.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

/**
 * 科室动态评论的学分打赏
 */
@Entity(value = "c_group_trend_comment_credit", noClassnameStored = true)
@Deprecated
public class GroupTrendCommentCredit {

    @Id
    private ObjectId id;
    /**
     * 评论id
     */
    private String commentId;

    /**
     * 打赏的学分
     */
    private Integer credit;
    /**
     * 打赏的流水id 暂时多余字段
     */
    private String creditId;
    /**
     * 被打赏人的userId
     */
    private Integer creditUserId;
    /**
     * 所属的trendId
     */
    private String trendId;

    /**
     * 所属的group（冗余）
     */
    private String groupId;
    /**
     * 打赏人的userId
     */
    private Integer userId;
    /**
     * 打赏人的用户类型
     */
    private Integer userType;

    private Long createTime;

    // 包装时的参数 start
    /**
     * 打赏人信息
     */
    @NotSaved
    private User user;
    // 包装时的参数 end


    public GroupTrendCommentCredit() {
    }

    public GroupTrendCommentCredit(GroupTrendComment groupTrendComment, User user) {
        this.userId = user.getUserId();
        this.userType = user.getUserType();
        //this.user = user;

        this.commentId = groupTrendComment.getId().toString();
        this.trendId = groupTrendComment.getTrendId();
        this.creditUserId = groupTrendComment.getUserId();
        this.groupId = groupTrendComment.getGroupId();

    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public Integer getCreditUserId() {
        return creditUserId;
    }

    public void setCreditUserId(Integer creditUserId) {
        this.creditUserId = creditUserId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}