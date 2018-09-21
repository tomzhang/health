package com.dachen.health.circle.entity;

import com.dachen.health.circle.CircleEnum;
import com.dachen.health.commons.vo.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

public abstract class BaseGroupUnionApplyOrInvite {
    @Id
    private ObjectId id;

    @Indexed
    private String unionId;
    @Indexed
    private String groupId;
    private String groupType;

    private Integer userId;

    @Indexed
    private Integer statusId;
    private Long statusTime;

    private Integer createUserId;
    private Long createTime;

    @NotSaved
    private User user;
    @NotSaved
    private GroupUnion union;
    @NotSaved
    private Group2 group;


    public BaseGroupUnionApplyOrInvite() {
    }

    public BaseGroupUnionApplyOrInvite(GroupUnion groupUnion, Group2 group) {
        this.unionId = groupUnion.getId().toString();
        this.groupId = group.getId().toString();
        this.groupType = group.getType();

        this.union = groupUnion;
        this.group = group;
    }

    public BaseGroupUnionApplyOrInvite(String unionId, Group2 group) {
        this.unionId = unionId;
        this.groupId = group.getId().toString();
        this.groupType = group.getType();

        this.group = group;
    }

    public BaseGroupUnionApplyOrInvite(GroupUnion groupUnion, String groupId) {
        this.unionId = groupUnion.getId().toString();
        this.groupId = groupId;
//        this.groupType = group.getType();

        this.union = groupUnion;
    }


    public void setStatus(CircleEnum.GroupUnionApplyStatus status) {
        this.statusId = status.getIndex();
        this.statusTime = System.currentTimeMillis();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
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

    public Group2 getGroup() {
        return group;
    }

    public void setGroup(Group2 group) {
        this.group = group;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public Long getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Long statusTime) {
        this.statusTime = statusTime;
    }

    public GroupUnion getUnion() {
        return union;
    }

    public void setUnion(GroupUnion union) {
        this.union = union;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
}
