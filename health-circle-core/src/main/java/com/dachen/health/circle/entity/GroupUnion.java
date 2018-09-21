package com.dachen.health.circle.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.NotSaved;

@Entity(value = "c_group_union", noClassnameStored = true)
public class GroupUnion {
    @Id
    private ObjectId id;

    @Indexed
    private String name;
    private String intro;
    private String logoPicUrl;

    @Indexed
    private Integer statusId;
    private Long statusTime;

    /**
     * 主体（科室或圈子）
     */
    @Indexed
    private String groupId;
    /**
     * 主体类型（冗余）
     */
    private String groupType;

    private Long createTime;
    private Integer createUserId;
    private String createGroupId;
    private Long updateTime;
    private Integer updateUserId;

    private Integer totalMember = 0;

    @NotSaved
    private Integer totalDoctor;
    @NotSaved
    private Group2 group;
    @NotSaved
    private GroupUnionMember member;
    @NotSaved
    private GroupUnionApply apply;

    public Integer getTotalDoctor() {
        return totalDoctor;
    }

    public void setTotalDoctor(Integer totalDoctor) {
        this.totalDoctor = totalDoctor;
    }

    public GroupUnionMember getMember() {
        return member;
    }

    public void setMember(GroupUnionMember member) {
        this.member = member;
    }

    public GroupUnionApply getApply() {
        return apply;
    }

    public void setApply(GroupUnionApply apply) {
        this.apply = apply;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getLogoPicUrl() {
        return logoPicUrl;
    }

    public void setLogoPicUrl(String logoPicUrl) {
        this.logoPicUrl = logoPicUrl;
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

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Integer updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Integer getTotalMember() {
        return totalMember;
    }

    public void setTotalMember(Integer totalMember) {
        this.totalMember = totalMember;
    }

    public Group2 getGroup() {
        return group;
    }

    public void setGroup(Group2 group) {
        this.group = group;
    }

    public String getCreateGroupId() {
        return createGroupId;
    }

    public void setCreateGroupId(String createGroupId) {
        this.createGroupId = createGroupId;
    }
}
