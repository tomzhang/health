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

public class UserGroupAndUnionMapVO implements Serializable{

    private Integer userId;
    private List<MobileGroupDoctorVO> groupDoctors;
    private List<MobileGroupUnionMemberVO> unionMembers;
    //去过重的
    private List<MobileGroupUnionVO> unions;

    public UserGroupAndUnionMapVO() {
    }
    public UserGroupAndUnionMapVO(UserGroupAndUnionMap groupAndUnionMap) {
        this.userId = groupAndUnionMap.getUserId();
        if (SdkUtils.isNotEmpty(groupAndUnionMap.getGroupDoctors())) {
            this.groupDoctors = new ArrayList<>(groupAndUnionMap.getGroupDoctors().size());
            for (GroupDoctor2 groupDoctor2:groupAndUnionMap.getGroupDoctors()) {
                this.groupDoctors.add(new MobileGroupDoctorVO(groupDoctor2));
            }
        }

        if (SdkUtils.isNotEmpty(groupAndUnionMap.getUnionMembers())) {
            this.unionMembers = new ArrayList<>(groupAndUnionMap.getUnionMembers().size());
            for (GroupUnionMember member:groupAndUnionMap.getUnionMembers()) {
                this.unionMembers.add(new MobileGroupUnionMemberVO(member));
            }
        }
        if(SdkUtils.isNotEmpty(groupAndUnionMap.getUnions())){
            this.unions = new ArrayList<>(groupAndUnionMap.getUnions().size());
            for (GroupUnion groupUnion:groupAndUnionMap.getUnions()){
                unions.add(new MobileGroupUnionVO(groupUnion));
            }
        }
    }

    public List<MobileGroupUnionVO> getUnions() {
        return unions;
    }

    public void setUnions(List<MobileGroupUnionVO> unions) {
        this.unions = unions;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<MobileGroupDoctorVO> getGroupDoctors() {
        return groupDoctors;
    }

    public void setGroupDoctors(List<MobileGroupDoctorVO> groupDoctors) {
        this.groupDoctors = groupDoctors;
    }

    public void addGroupDoctor(MobileGroupDoctorVO groupDoctor) {
        if (null == this.groupDoctors) {
            this.groupDoctors = new ArrayList<>();
        }
        this.groupDoctors.add(groupDoctor);
    }

    public List<MobileGroupUnionMemberVO> getUnionMembers() {
        return unionMembers;
    }

    public void setUnionMembers(List<MobileGroupUnionMemberVO> unionMembers) {
        this.unionMembers = unionMembers;
    }
}
