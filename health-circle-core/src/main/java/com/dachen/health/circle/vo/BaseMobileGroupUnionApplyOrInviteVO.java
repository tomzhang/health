package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.BaseGroupUnionApplyOrInvite;

import java.io.Serializable;
import java.util.Objects;

public abstract class BaseMobileGroupUnionApplyOrInviteVO implements Serializable{
    private String unionId;
    private String groupId;
    private String groupType;
    private Long createTime;
    private Integer statusId;

    private MobileGroupUnionVO union;
    private MobileGroupVO group;
    private MobileUserVO user;

    private String msg;

    public BaseMobileGroupUnionApplyOrInviteVO() {
    }

    public BaseMobileGroupUnionApplyOrInviteVO(BaseGroupUnionApplyOrInvite applyOrInvite) {
        this.unionId = applyOrInvite.getUnionId();
        this.groupId = applyOrInvite.getGroupId();
        this.groupType = applyOrInvite.getGroupType();
        this.createTime = applyOrInvite.getCreateTime();
        this.statusId = applyOrInvite.getStatusId();

        if (null != applyOrInvite.getUnion()) {
            this.union = new MobileGroupUnionVO(applyOrInvite.getUnion());
        }
        if (null != applyOrInvite.getGroup()) {
            this.group = new MobileGroupVO(applyOrInvite.getGroup());
        }
        if (null != applyOrInvite.getUser()) {
            this.user = new MobileUserVO(applyOrInvite.getUser());
        }
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public MobileGroupUnionVO getUnion() {
        return union;
    }

    public void setUnion(MobileGroupUnionVO union) {
        this.union = union;
    }

    public MobileGroupVO getGroup() {
        return group;
    }

    public void setGroup(MobileGroupVO group) {
        this.group = group;
    }

    public MobileUserVO getUser() {
        return user;
    }

    public void setUser(MobileUserVO user) {
        this.user = user;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
}
