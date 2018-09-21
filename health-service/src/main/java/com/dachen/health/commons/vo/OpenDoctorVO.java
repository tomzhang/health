package com.dachen.health.commons.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author 钟良
 * @desc
 * @date:2017/8/10 15:04 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class OpenDoctorVO {
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("性别：【1男/2女/3保密】")
    private Integer sex;
    @ApiModelProperty("手机号")
    private String telephone;
    @ApiModelProperty("职称")
    private String title;
    @ApiModelProperty("科室")
    private String departments;
    @ApiModelProperty("医疗机构")
    private String hospital;
    @ApiModelProperty("OpenId")
    private String openId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartments() {
        return departments;
    }

    public void setDepartments(String departments) {
        this.departments = departments;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
