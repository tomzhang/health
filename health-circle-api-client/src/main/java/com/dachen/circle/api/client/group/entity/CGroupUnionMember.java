package com.dachen.circle.api.client.group.entity;

import java.io.Serializable;

public class CGroupUnionMember implements Serializable {

    private String id;

    private String unionId;

    private String groupId;
    private String groupType;

    /**
     * 是否主体
     */
    private Integer ifMain;

    private Integer createUserId;
    private Long createTime;

    private CGroupUnion union;
    private CGroup group;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Integer getIfMain() {
        return ifMain;
    }

    public void setIfMain(Integer ifMain) {
        this.ifMain = ifMain;
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

    public CGroupUnion getUnion() {
        return union;
    }

    public void setUnion(CGroupUnion union) {
        this.union = union;
    }

    public CGroup getGroup() {
        return group;
    }

    public void setGroup(CGroup group) {
        this.group = group;
    }
}
