package com.dachen.health.base.entity.vo;

import org.bson.types.ObjectId;

/**
 * Created by fuyongde on 2016/12/20.
 */
public class CheckSuggestItemVo {

    private ObjectId id;

    private String checkupId;

    /* 中文名*/
    private String name;
    /*缩写或别名*/
    private String alias;
    /*单位*/
    private String unit;

    private String regionLeft;

    private String regionRight;

    /**
     * 适用性别
     * 0通用，1男，2女
     */
    private Integer fitSex;

    private Long createTime;

    private Long updateTime;

    /**医生是否关注**/
    private boolean doctorCare;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getCheckupId() {
        return checkupId;
    }

    public void setCheckupId(String checkupId) {
        this.checkupId = checkupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRegionLeft() {
        return regionLeft;
    }

    public void setRegionLeft(String regionLeft) {
        this.regionLeft = regionLeft;
    }

    public String getRegionRight() {
        return regionRight;
    }

    public void setRegionRight(String regionRight) {
        this.regionRight = regionRight;
    }

    public Integer getFitSex() {
        return fitSex;
    }

    public void setFitSex(Integer fitSex) {
        this.fitSex = fitSex;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isDoctorCare() {
        return doctorCare;
    }

    public void setDoctorCare(boolean doctorCare) {
        this.doctorCare = doctorCare;
    }

}
