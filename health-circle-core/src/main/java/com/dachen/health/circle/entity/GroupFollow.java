package com.dachen.health.circle.entity;

import com.dachen.health.commons.vo.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

/**
 * 我关注的科室
 */
@Entity(value = "c_group_follow", noClassnameStored = true)
@Indexes({ @Index(fields = { @Field("groupId"),@Field("userId") },options=@IndexOptions(unique=true)) })
public class GroupFollow {

    @Id
    private ObjectId id;

    private String groupId;
    /**
     * 冗余
     */
    private String groupType;

    private Integer userId;
    private Integer userType;

    private Long createTime;

    @NotSaved
    private Group2 group;

    public GroupFollow() {
    }

    public GroupFollow(User user, Group2 group) {
        this.userId = user.getUserId();
        this.userType = user.getUserType();
        this.groupId = group.getId().toString();
        this.groupType = group.getType();
        this.createTime = System.currentTimeMillis();
    }

    public GroupFollow(Integer currentUserId, Group2 group2) {
        this.userId = currentUserId;
        this.groupId = group2.getId().toString();
        this.groupType = group2.getType();
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Group2 getGroup() {
        return group;
    }

    public void setGroup(Group2 group) {
        this.group = group;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "GroupFollow{" +
                "id=" + id +
                ", userId=" + userId +
                ", groupId='" + groupId + '\'' +
                ", groupType='" + groupType + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
