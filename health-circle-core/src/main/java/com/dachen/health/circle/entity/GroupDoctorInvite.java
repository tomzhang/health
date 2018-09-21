package com.dachen.health.circle.entity;

import com.dachen.health.commons.vo.User;
import org.mongodb.morphia.annotations.Entity;

@Entity(value = "c_group_doctor_invite", noClassnameStored = true)
public class GroupDoctorInvite extends BaseGroupDoctorApplyOrInvite {
    public GroupDoctorInvite() {
    }

    public GroupDoctorInvite(User user, Group2 group) {
        super(user, group);

    }

}
