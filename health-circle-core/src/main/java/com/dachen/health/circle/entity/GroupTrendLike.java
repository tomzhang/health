package com.dachen.health.circle.entity;

import com.dachen.health.commons.vo.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

/**
 * 科室动态的点赞
 */
@Entity(value = "c_group_trend_like", noClassnameStored = true)
@Indexes({ @Index(fields = { @Field("trendId"),@Field("userId") },options=@IndexOptions(unique=true)) })
public class GroupTrendLike {

    @Id
    private ObjectId id;

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
    // 包装时的参数 end

    public GroupTrendLike() {
    }

    public GroupTrendLike(User user2, GroupTrend trend) {
        this.userId = user2.getUserId();
        this.userType = user2.getUserType();
        this.user = user2;

        this.trendId = trend.getId().toString();
        this.groupId = trend.getGroupId();

        this.createTime = System.currentTimeMillis();
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
}