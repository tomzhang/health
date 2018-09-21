package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.GroupUnionMember;
import java.io.Serializable;

public class MobileGroupUnionMemberVO implements Serializable {
    private String memberId;
    private String unionId;
    private String groupId;
    private Integer ifMain;
    private Long createTime;

    private MobileGroupVO group;
    private MobileGroupUnionVO union;

    private Boolean ifCanRemove;

    public MobileGroupUnionMemberVO() {
    }

    public MobileGroupUnionMemberVO(GroupUnionMember groupUnionMember) {
        this.memberId = groupUnionMember.getId().toString();
        this.unionId = groupUnionMember.getUnionId();
        this.groupId = groupUnionMember.getGroupId();
        this.ifMain = groupUnionMember.getIfMain();
        this.createTime = groupUnionMember.getCreateTime();
        if (null != groupUnionMember.getGroup()) {
            this.group = new MobileGroupVO(groupUnionMember.getGroup());
        }
        if (null != groupUnionMember.getUnion()) {
            this.union = new MobileGroupUnionVO(groupUnionMember.getUnion());
        }
        this.ifCanRemove = groupUnionMember.getIfCanRemove();
    }

    public Boolean getIfCanRemove() {
        return ifCanRemove;
    }

    public void setIfCanRemove(Boolean ifCanRemove) {
        this.ifCanRemove = ifCanRemove;
    }

    public MobileGroupUnionVO getUnion() {
        return union;
    }

    public void setUnion(MobileGroupUnionVO union) {
        this.union = union;
    }

    public Integer getIfMain() {
        return ifMain;
    }

    public void setIfMain(Integer ifMain) {
        this.ifMain = ifMain;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
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

    public MobileGroupVO getGroup() {
        return group;
    }

    public void setGroup(MobileGroupVO group) {
        this.group = group;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
