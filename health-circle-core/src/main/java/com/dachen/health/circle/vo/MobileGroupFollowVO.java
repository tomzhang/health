package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.GroupFollow;

import java.util.Objects;

public class MobileGroupFollowVO {

    private String followId;
    private String groupId;
    private String groupType;
    private Integer userId;

    private MobileGroupVO group;

    public MobileGroupFollowVO() {
    }

    public MobileGroupFollowVO(GroupFollow groupFollow) {
        this.followId = Objects.toString(groupFollow.getId(), null);
        this.groupId = groupFollow.getGroupId();
        this.groupType = groupFollow.getGroupType();
        this.userId = groupFollow.getUserId();
        if (null != groupFollow.getGroup()) {
            this.group = new MobileGroupVO(groupFollow.getGroup());
        }
    }

    public MobileGroupVO getGroup() {
        return group;
    }

    public void setGroup(MobileGroupVO group) {
        this.group = group;
    }

    public String getFollowId() {
        return followId;
    }

    public void setFollowId(String followId) {
        this.followId = followId;
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

}
