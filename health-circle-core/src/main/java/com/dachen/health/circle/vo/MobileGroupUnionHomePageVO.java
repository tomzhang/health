package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.GroupUnion;

import java.io.Serializable;

public class MobileGroupUnionHomePageVO extends MobileGroupUnionVO {

    private String role;
    private MobileGroupUnionMemberVO member;
    private MobileGroupUnionApplyVO apply;

    public MobileGroupUnionHomePageVO() {
    }

    public MobileGroupUnionHomePageVO(GroupUnion groupUnion) {
        super(groupUnion);
        if (null != groupUnion.getMember()) {
            this.member = new MobileGroupUnionMemberVO(groupUnion.getMember());
        }
        if (null != groupUnion.getApply()) {
            this.apply = new MobileGroupUnionApplyVO(groupUnion.getApply());
        }
    }

    public MobileGroupUnionMemberVO getMember() {
        return member;
    }

    public void setMember(MobileGroupUnionMemberVO member) {
        this.member = member;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public MobileGroupUnionApplyVO getApply() {
        return apply;
    }

    public void setApply(MobileGroupUnionApplyVO apply) {
        this.apply = apply;
    }
}
