package com.dachen.health.user.entity.vo;

/**
 * ProjectName： health-service<br>
 * ClassName： DoctorDetailVO<br>
 * Description： 医生详细信息<br>
 * 
 * @author fanp
 * @crateTime 2015年7月8日
 * @version 1.0.0
 */
public class DoctorDetailVO {

    private Integer userId;

    private String name;

    private String nickname;

    private String telephone;

    /* 头像 */
    private String headPicFileName;

    /* 医院 */
    private String hospital;

    /* 医院 Id */
    private String hospitalId;

    /* 科室 */
    private String departments;

    /* 职称 */
    private String title;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getHeadPicFileName() {
        return headPicFileName;
    }

    public void setHeadPicFileName(String headPicFileName) {
        this.headPicFileName = headPicFileName;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
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

}
