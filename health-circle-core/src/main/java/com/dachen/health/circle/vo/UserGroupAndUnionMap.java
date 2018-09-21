package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.GroupDoctor2;
import com.dachen.health.circle.entity.GroupUnion;
import com.dachen.health.circle.entity.GroupUnionMember;
import com.dachen.sdk.util.SdkUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserGroupAndUnionMap implements Serializable{

    private Integer userId;

    private List<GroupDoctor2> groupDoctors;

    private List<GroupUnionMember> unionMembers;

    private List<GroupUnion> unions;

    public UserGroupAndUnionMap() {
    }

    public UserGroupAndUnionMap(Integer userId) {
        this.userId = userId;
    }

    public UserGroupAndUnionMap(Integer userId, List<GroupDoctor2> groupDoctors, List<GroupUnionMember> unionMembers) {
        this.userId = userId;
        this.groupDoctors = groupDoctors;
        this.unionMembers = unionMembers;

        calcUnions();
    }

    private void calcUnions() {
        if (SdkUtils.isEmpty(unionMembers)) {
            return;
        }
        unions = new ArrayList<>(unionMembers.size());
        Set<String> unionIdSet = new HashSet<>(unionMembers.size());
        for (GroupUnionMember unionMember:unionMembers) {
            if (unionIdSet.add(unionMember.getUnionId())) {
                unions.add(unionMember.getUnion());
            }
        }
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void addGroupDoctor(GroupDoctor2 groupDoctor) {
        if (null == this.groupDoctors) {
            this.groupDoctors = new ArrayList<>();
        }
        this.groupDoctors.add(groupDoctor);
    }

    public List<GroupUnion> getUnions() {
        return unions;
    }

    public void setUnions(List<GroupUnion> unions) {
        this.unions = unions;
    }

    public List<GroupDoctor2> getGroupDoctors() {
        return groupDoctors;
    }

    public void setGroupDoctors(List<GroupDoctor2> groupDoctors) {
        this.groupDoctors = groupDoctors;
    }

    public List<GroupUnionMember> getUnionMembers() {
        return unionMembers;
    }

    public void setUnionMembers(List<GroupUnionMember> unionMembers) {
        this.unionMembers = unionMembers;
        calcUnions();
    }
}
