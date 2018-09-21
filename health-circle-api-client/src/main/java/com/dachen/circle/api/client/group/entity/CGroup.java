package com.dachen.circle.api.client.group.entity;

import java.io.Serializable;

public class CGroup implements Serializable {
    private String id;
    private String name;
    private String introduction;
    /**
     * 公司是否屏蔽
     */
    private String skip;

    /**
     * active=已激活，inactive=未激活
     */
    private String active;
    /**
     * 集团logo
     */
    private String logoUrl;

    /**
     * 医院id
     */
    private String hospitalId;

    /**
     * 科室id
     */
    private String deptId;

    /**
     * 数据类型type（hospital，group, dept）
     *
     * @return
     */
    private String type;

    /**
     * 医院名称（冗余）
     */
    private String hospitalName;
    /**
     * 科室名称（冗余）
     */
    private String deptName;
    /**
     * 子科室名称
     */
    private String childName;

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getSkip() {
        return skip;
    }

    public void setSkip(String skip) {
        this.skip = skip;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
