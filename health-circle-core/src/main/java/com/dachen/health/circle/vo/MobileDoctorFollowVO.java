package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.DoctorFollow;
import com.dachen.health.circle.entity.Group2;
import com.dachen.health.commons.vo.User;
import com.dachen.sdk.util.SdkUtils;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 我关注的医生
 */
public class MobileDoctorFollowVO implements Serializable {
    @ApiModelProperty(value = "医生id")
    private Integer doctorId;
    @ApiModelProperty(value = "头像")
    private String logoUrl;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "职称名称")
    private String title;
    @ApiModelProperty(value = "科室名称")
    private String departments;
    @ApiModelProperty(value = "医院名称")
    private String hospital;
    @ApiModelProperty(value = "是否关注该医生 true: 是 false:否")
    private boolean ifFollower;

    public MobileDoctorFollowVO() {
    }

    public MobileDoctorFollowVO(User user) {
        this.doctorId=user.getUserId();
        this.logoUrl=user.getHeadPicFileName();
        this.name=user.getName();
        if(user.getDoctor()!=null) {
            this.title = user.getDoctor().getTitle();
            this.departments = user.getDoctor().getDepartments();
            this.hospital = user.getDoctor().getHospital();
        }
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public boolean isIfFollower() {
        return ifFollower;
    }

    public void setIfFollower(boolean ifFollower) {
        this.ifFollower = ifFollower;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
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
        return "MobileDoctorFollowVO{" +
                "logoUrl='" + logoUrl + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", departments='" + departments + '\'' +
                ", hospital='" + hospital + '\'' +
                '}';
    }
}
