package com.dachen.health.commons.vo;


import io.swagger.annotations.ApiModelProperty;

public class CircleVO {

    private String id;

    //圈子,科室)名称
    private String name;

    //1圈子 2虚拟科室
    private Integer type;

    //圈子,科室 简介
    private String introduction;

   //圈子,科室logo
    private String logo;
    //在该圈子的角色1：管理员 2：圈主（负责人）3：顾问 逗号拼接 多个角色 可同时为管理员，圈主，顾问
    private String role;

    @ApiModelProperty(value = "1正常 2欠费")
    private Integer arrearageStatus;

    @ApiModelProperty(value = "0 不收费 1收费")
    private Integer charge;

    @ApiModelProperty(value = "1私密圈子0非私密圈子")
    private Integer isprivate;

    public Integer getIsprivate() {
        return isprivate;
    }

    public void setIsprivate(Integer isprivate) {
        this.isprivate = isprivate;
    }

    public Integer getCharge() {
        return charge;
    }

    public void setCharge(Integer charge) {
        this.charge = charge;
    }

    public Integer getArrearageStatus() {
        return arrearageStatus;
    }

    public void setArrearageStatus(Integer arrearageStatus) {
        this.arrearageStatus = arrearageStatus;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
