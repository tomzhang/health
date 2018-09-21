package com.dachen.health.circle.entity;

import com.dachen.health.commons.vo.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

/**
 * 科室动态评论的点赞
 */
@Entity(value = "c_group_trend_comment_like", noClassnameStored = true)
@Indexes({ @Index(fields = { @Field("commentId"),@Field("userId") },options=@IndexOptions(unique=true)) })
public class GroupTrendCommentLike {

    @Id
    private ObjectId id;
    /**
     * 回复到评论id
     */
    private String commentId;
    /**
     * 所属的trendId
     */
    private String trendId;

    /**
     * 所属的group（冗余）
     */
    private String groupId;

    private Integer userId;
    private Integer userType;

    private Long createTime;

    // 包装时的参数 start
    @NotSaved
    private User user;
    @NotSaved
    private Group2 userDept;
    // 包装时的参数 end


    public GroupTrendCommentLike() {
    }

    public GroupTrendCommentLike(GroupTrendComment groupTrendComment, User user) {
        this.userId = user.getUserId();
        this.userType = user.getUserType();
        this.user = user;

        this.commentId=groupTrendComment.getId().toString();
        this.trendId=groupTrendComment.getTrendId();
        this.groupId=groupTrendComment.getGroupId();

    }

    public Group2 getUserDept() {
        return userDept;
    }

    public void setUserDept(Group2 userDept) {
        this.userDept = userDept;
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