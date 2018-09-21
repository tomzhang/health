package com.dachen.health.circle.vo;

import com.dachen.health.commons.vo.User;
import io.swagger.annotations.ApiModelProperty;

/**
 * 医生主页
 * Created By lim
 * Date: 2017/7/7
 * Time: 16:49
 */
public class MobileDoctorHomeVO {
    @ApiModelProperty(value = "用户类型")
    private Integer userType;
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
    @ApiModelProperty(value = "学术成就 ")
    private String scholarship;
    @ApiModelProperty(value = "社会任职 ")
    private String experience;
    @ApiModelProperty(value = "简介")
    private String introduction;
    @ApiModelProperty(value = "关注数")
    private Long followers;
    @ApiModelProperty(value = "粉丝数")
    private Long fans;
    @ApiModelProperty(value = "是否关注该医生 true: 是 false:否")
    private boolean ifFollower;/*
    @ApiModelProperty(value = "我的圈")
    private List<MobileGroupVO> myGroup;*/
    @ApiModelProperty(value = "查看医生的状态")
    private int status;
    @ApiModelProperty(value = "添加好友的id")
    private String fId;

    /* user.settings对象 */
    @ApiModelProperty(value = "个人设置")
    private User.UserSettings settings;

    public MobileDoctorHomeVO() {
    }

    public MobileDoctorHomeVO(User user){
        this.userType=user.getUserType();
        this.logoUrl=user.getHeadPicFileName();
        this.name=user.getName();
        if(user.getDoctor()!=null) {
            this.title = user.getDoctor().getTitle();
            this.departments=user.getDoctor().getDepartments();
            this.hospital=user.getDoctor().getHospital();
            this.skill=user.getDoctor().getSkill();
            this.scholarship = user.getDoctor().getScholarship();
            this.experience = user.getDoctor().getExperience();
            this.introduction=user.getDoctor().getIntroduction();
        }
        if(user.getSettings()!=null){
            this.settings=user.getSettings();
        }
    }


    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public User.UserSettings getSettings() {
        return settings;
    }

    public void setSettings(User.UserSettings settings) {
        this.settings = settings;
    }

    public String getfId() {
        return fId;
    }

    public void setfId(String fId) {
        this.fId = fId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }
    
    public String getScholarship() {
        return scholarship;
    }

    public void setScholarship(String scholarship) {
        this.scholarship = scholarship;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Long getFollowers() {
        return followers;
    }

    public void setFollowers(Long followers) {
        this.followers = followers;
    }

    public Long getFans() {
        return fans;
    }

    public void setFans(Long fans) {
        this.fans = fans;
    }

    public boolean isIfFollower() {
        return ifFollower;
    }

    public void setIfFollower(boolean ifFollower) {
        this.ifFollower = ifFollower;
    }

    @Override
    public String toString() {
        return "MobileDoctorHomeVO{" +
                "logoUrl='" + logoUrl + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", departments='" + departments + '\'' +
                ", hospital='" + hospital + '\'' +
                ", skill='" + skill + '\'' +
                ", introduction='" + introduction + '\'' +
                ", followers=" + followers +
                ", fans=" + fans +
                ", ifFollower=" + ifFollower +
                '}';
    }
}
