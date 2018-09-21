package com.dachen.circle.api.client.group.entity;

import java.io.Serializable;
import java.util.List;

public class CUserGroupAndUnionMap implements Serializable{
    private Integer userId;
    private List<CGroupDoctor> groupDoctors;
    private List<CGroupUnionMember> unionMembers;
    private List<CGroupUnion> unions;

    public List<CGroupUnion> getUnions() {
        return unions;
    }

    public void setUnions(List<CGroupUnion> unions) {
        this.unions = unions;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<CGroupDoctor> getGroupDoctors() {
        return groupDoctors;
    }

    public void setGroupDoctors(List<CGroupDoctor> groupDoctors) {
        this.groupDoctors = groupDoctors;
    }

    public List<CGroupUnionMember> getUnionMembers() {
        return unionMembers;
    }

    public void setUnionMembers(List<CGroupUnionMember> unionMembers) {
        this.unionMembers = unionMembers;
    }
}
