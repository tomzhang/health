package com.dachen.health.group.common.entity.param;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author liangcs
 * @desc
 * @date:2017/7/12 10:59
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class PublishCountParam implements Serializable{

    private String userId;

    private List<String> deptIds;

    private List<String> hospitalIds;

    private List<String> groupIds;

    private List<String> unionIds;

    //科室
    private Map<String, Long> dept;

    //医院
    private Map<String, Long> hospital;

    //圈子
    private Map<String, Long> group;

    //联盟
    private Map<String, Long> union;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(List<String> deptIds) {
        this.deptIds = deptIds;
    }

    public List<String> getHospitalIds() {
        return hospitalIds;
    }

    public void setHospitalIds(List<String> hospitalIds) {
        this.hospitalIds = hospitalIds;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    public List<String> getUnionIds() {
        return unionIds;
    }

    public void setUnionIds(List<String> unionIds) {
        this.unionIds = unionIds;
    }

    public Map<String, Long> getDept() {
        return dept;
    }

    public void setDept(Map<String, Long> dept) {
        this.dept = dept;
    }

    public Map<String, Long> getHospital() {
        return hospital;
    }

    public void setHospital(Map<String, Long> hospital) {
        this.hospital = hospital;
    }

    public Map<String, Long> getGroup() {
        return group;
    }

    public void setGroup(Map<String, Long> group) {
        this.group = group;
    }

    public Map<String, Long> getUnion() {
        return union;
    }

    public void setUnion(Map<String, Long> union) {
        this.union = union;
    }
}
