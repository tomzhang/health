package com.dachen.health.circle.vo;

import com.dachen.health.commons.vo.User;
import io.swagger.annotations.ApiModelProperty;

/**
 * 医生粉丝 或者 关注 列表
 * Created By lim
 * Date: 2017/7/7
 * Time: 17:09
 */
public class MobileDoctorBriefVo {
    @ApiModelProperty(value = "医生id 跳转参数")
    private Integer userId;
    @ApiModelProperty(value = "头像地址")
    private String logoUrl;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "职称")
    private String title;
    @ApiModelProperty(value = "科室")
    private String departments;
    @ApiModelProperty(value = "医院")
    private String hospital;
    @ApiModelProperty(value = "擅长")
    private String skill;
    public MobileDoctorBriefVo() {
    }


    public MobileDoctorBriefVo(User user){
        this.userId=user.getUserId();
        this.logoUrl=user.getHeadPicFileName();
        this.name=user.getName();
        if(user.getDoctor()!=null) {
            this.title = user.getDoctor().getTitle();
            this.departments=user.getDoctor().getDepartments();
            this.hospital=user.getDoctor().getHospital();
            this.skill=user.getDoctor().getSkill();
        }
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "MobileDoctorFan{" +
                "userId='" + userId + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", departments='" + departments + '\'' +
                ", hospital='" + hospital + '\'' +
                '}';
    }
}
