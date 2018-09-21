package com.dachen.circle.api.client.group.entity;

import java.io.Serializable;

public class CGroupDoctor implements Serializable {
    private String id;

    private Integer doctorId;
    private String groupId;
    private String status;
    private Long creatorDate;

    private CGroup group;

    public CGroup getGroup() {
        return group;
    }

    public void setGroup(CGroup group) {
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatorDate() {
        return creatorDate;
    }

    public void setCreatorDate(Long creatorDate) {
        this.creatorDate = creatorDate;
    }
}
