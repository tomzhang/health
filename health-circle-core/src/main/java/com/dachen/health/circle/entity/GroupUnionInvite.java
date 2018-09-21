package com.dachen.health.circle.entity;

import org.mongodb.morphia.annotations.Entity;

@Entity(value = "c_group_union_invite", noClassnameStored = true)
public class GroupUnionInvite extends BaseGroupUnionApplyOrInvite {

    public GroupUnionInvite() {
    }

    public GroupUnionInvite(GroupUnion groupUnion, Group2 group) {
        super(groupUnion, group);
    }

    public GroupUnionInvite(String unionId, Group2 group) {
        super(unionId, group);
    }

    public GroupUnionInvite(GroupUnion groupUnion, String groupId) {
      super(groupUnion, groupId);
    }


}
