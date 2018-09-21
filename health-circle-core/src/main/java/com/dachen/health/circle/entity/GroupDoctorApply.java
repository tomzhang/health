package com.dachen.health.circle.entity;

import com.dachen.health.commons.vo.User;
import org.mongodb.morphia.annotations.Entity;

@Entity(value = "c_group_doctor_apply", noClassnameStored = true)
public class GroupDoctorApply extends BaseGroupDoctorApplyOrInvite {

    private String msg;

    public GroupDoctorApply() {
    }

    public GroupDoctorApply(User user, Group2 group, String msg) {
        super(user, group);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
