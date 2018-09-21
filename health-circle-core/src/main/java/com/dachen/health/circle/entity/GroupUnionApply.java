package com.dachen.health.circle.entity;

import org.mongodb.morphia.annotations.Entity;

@Entity(value = "c_group_union_apply", noClassnameStored = true)
public class GroupUnionApply extends BaseGroupUnionApplyOrInvite {

    private String msg;

    public GroupUnionApply() {
    }

    public GroupUnionApply(GroupUnion groupUnion, Group2 group, String msg) {
        super(groupUnion, group);
        this.msg = msg;
    }

    public GroupUnionApply(String unionId, Group2 group) {
        super(unionId, group);
    }

    public GroupUnionApply(GroupUnion groupUnion, String groupId) {
        super(groupUnion, groupId);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
