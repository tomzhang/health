package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.GroupUser2;

import java.io.Serializable;
import java.util.Objects;

public class MobileGroupUserVO implements Serializable {

    private String groupUserId;

    private String groupId;
    private Integer doctorId;

    private MobileDoctorVO doctor;
    private MobileGroupVO group;

    public MobileGroupUserVO() {
    }

    public MobileGroupUserVO(GroupUser2 groupUser) {
        this.groupUserId = groupUser.getId().toString();
        this.groupId = Objects.toString(groupUser.getId(), null);
        this.groupId = groupUser.getObjectId();
        this.doctorId = groupUser.getDoctorId();
        if (null != groupUser.getUser()) {
            this.doctor = new MobileDoctorVO(groupUser.getUser());
        }
        if (null != groupUser.getGroup()) {
            this.group = new MobileGroupVO(groupUser.getGroup());
        }
    }

    public String getGroupUserId() {
        return groupUserId;
    }

    public void setGroupUserId(String groupUserId) {
        this.groupUserId = groupUserId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public MobileDoctorVO getDoctor() {
        return doctor;
    }

    public void setDoctor(MobileDoctorVO doctor) {
        this.doctor = doctor;
    }

    public MobileGroupVO getGroup() {
        return group;
    }

    public void setGroup(MobileGroupVO group) {
        this.group = group;
    }
}
