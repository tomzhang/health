package com.dachen.line.stat.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author tianhong
 * @Description 病例库修改用户信息接口参数实例
 * @date 2018/5/3 14:45 
 * @Copyright (c) 2018, DaChen All Rights Reserved.
 */
@ApiModel
public class UserRequestParam {

    @ApiModelProperty(dataType ="String",name ="communityId",notes = "圈子id")
    private String communityId;

    @ApiModelProperty(dataType = "String",name = "openId",notes ="openId")
    private String openId;

    @ApiModelProperty(dataType = "String",name="role",notes = "0表示非圈主 1表示圈主")
    private String role;

    @ApiModelProperty(dataType = "String",name = "name",notes = "成员名称")
    private String name;

    @ApiModelProperty(dataType = "String",name="departments",notes ="科室")
    private String departments;

    @ApiModelProperty(dataType = "String",name="title",notes = "职称")
    private String title;

    @ApiModelProperty(dataType = "String",name="hospital",notes = "医院")
    private String hospital;

    @ApiModelProperty(dataType = "String",name="headPicFileName",notes = "成员头像url")
    private String headPicFileName;

    @ApiModelProperty(dataType = "String",name="token",notes = "token")
    private String token;

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartments() {
        return departments;
    }

    public void setDepartments(String departments) {
        this.departments = departments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHeadPicFileName() {
        return headPicFileName;
    }

    public void setHeadPicFileName(String headPicFileName) {
        this.headPicFileName = headPicFileName;
    }
}

