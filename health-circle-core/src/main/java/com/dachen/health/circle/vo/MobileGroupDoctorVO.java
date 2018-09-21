package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.BaseGroupDoctorApplyOrInvite;
import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.entity.GroupDoctor2;
import com.dachen.health.circle.entity.GroupDoctorApply;

import java.io.Serializable;
import java.util.Objects;

public class MobileGroupDoctorVO implements Serializable {
    private String groupDoctorId;
    private String groupId;
    private String groupType;
    private Integer doctorId;
    private String role;
    private String status;

    private MobileGroupVO group;
    private MobileDoctorVO doctor;

    private String msg;
    private String deptName;
    public MobileGroupDoctorVO() {
    }

    public MobileGroupDoctorVO(GroupDoctor2 groupDoctor2) {
        this.groupDoctorId = Objects.toString(groupDoctor2.getId(), null);
        this.groupId = groupDoctor2.getGroupId();
        this.groupType = groupDoctor2.getType();
        this.doctorId = groupDoctor2.getDoctorId();
        this.role = groupDoctor2.getRole();
        this.status = groupDoctor2.getStatus();
        this.deptName = groupDoctor2.getDeptName();

        if (null != groupDoctor2.getUser()) {
            this.doctor = new MobileDoctorVO(groupDoctor2.getUser(), groupDoctor2.getUserDept());
        }
        if (null != groupDoctor2.getGroup() && null != groupDoctor2.getGroup().getId()) {
            this.group = new MobileGroupVO(groupDoctor2.getGroup());
        }
        if (null != groupDoctor2.getGroup() && null == groupDoctor2.getGroup().getId() && null != groupDoctor2.getGroup().getDeptName() ) {
            this.group = new MobileGroupVO(groupDoctor2.getGroup().getDeptName());
        }
    }


    public MobileGroupDoctorVO(BaseGroupDoctorApplyOrInvite applyOrInvite) {
        this.groupDoctorId = Objects.toString(applyOrInvite.getId(), null);
        this.groupId = applyOrInvite.getGroupId();
        this.groupType = applyOrInvite.getGroupType();
        this.doctorId = applyOrInvite.getUserId();
//        this.role = groupDoctor2.getRole();
        this.status = applyOrInvite.getGroupDoctorStatus();
//        this.deptName = groupDoctor2.getDeptName();

        if (null != applyOrInvite.getUser()) {
//            this.doctor = new MobileDoctorVO(apply.getUser(), apply.getUserDept());
            this.doctor = new MobileDoctorVO(applyOrInvite.getUser(), null);
        }
        if (null != applyOrInvite.getGroup() && null != applyOrInvite.getGroup().getId()) {
            this.group = new MobileGroupVO(applyOrInvite.getGroup());
        }
        if (null != applyOrInvite.getGroup() && null == applyOrInvite.getGroup().getId() && null != applyOrInvite.getGroup().getDeptName() ) {
            this.group = new MobileGroupVO(applyOrInvite.getGroup().getDeptName());
        }
    }

    public MobileGroupDoctorVO(Integer doctorId, Group2 group) {
        this.groupId = group.getId().toString();
        this.groupType = group.getType();
        this.doctorId = doctorId;
    }


    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MobileGroupVO getGroup() {
        return group;
    }

    public void setGroup(MobileGroupVO group) {
        this.group = group;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroupDoctorId() {
        return groupDoctorId;
    }

    public void setGroupDoctorId(String groupDoctorId) {
        this.groupDoctorId = groupDoctorId;
    }
}
