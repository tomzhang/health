package com.dachen.health.circle.entity;

import com.dachen.health.circle.CircleEnum;
import com.dachen.health.commons.vo.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.NotSaved;

public abstract class BaseGroupDoctorApplyOrInvite {
    @Id
    private ObjectId id;

    @Indexed
    private String groupId;
    private String groupType;
    @Indexed
    private Integer userId;
    private Integer userType;

    @Indexed
    private Integer statusId;
    private Long statusTime;

    @NotSaved
    private String groupDoctorStatus;

    private Integer createUserId;
    private Long createTime;

    @NotSaved
    private User user;
    @NotSaved
    private Group2 group;


    public BaseGroupDoctorApplyOrInvite() {
    }

    public BaseGroupDoctorApplyOrInvite(User user, Group2 group) {
        this.groupId = group.getId().toString();
        this.groupType = group.getType();
        this.userId = user.getUserId();
        this.userType = user.getUserType();

        this.user = user;
        this.group = group;
    }

    public void setStatus(CircleEnum.GroupUnionApplyStatus status) {
        this.statusId = status.getIndex();
        this.statusTime = System.currentTimeMillis();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group2 getGroup() {
        return group;
    }

    public void setGroup(Group2 group) {
        this.group = group;
    }

    public String getGroupDoctorStatus() {
        return groupDoctorStatus;
    }

    public void setGroupDoctorStatus(String groupDoctorStatus) {
        this.groupDoctorStatus = groupDoctorStatus;
    }
}
