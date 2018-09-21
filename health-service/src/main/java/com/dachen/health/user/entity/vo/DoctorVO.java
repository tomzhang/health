package com.dachen.health.user.entity.vo;

public class DoctorVO {

    /* 姓名 */
    private String name;

    /* 医院 */
    private String hospital;

    /* 医院Id */
    private String hospitalId;

    /* 科室 */
    private String departments;

    /* 科室电话 */
    private String deptPhone;

    /* 职称 */
    private String title;

    /* 审核意见 */
    private String remark;

    /* 状态 */
    private Integer status;

    /* 认证信息状态(1：未处理；2：已处理) */
    private Integer infoStatus;

    /* 头像 */
    private String headPicFileName;

    private String telephone;

    private Integer userLevel;

    private Long limitedPeriodTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public Long getLimitedPeriodTime() {
        return limitedPeriodTime;
    }

    public void setLimitedPeriodTime(Long limitedPeriodTime) {
        this.limitedPeriodTime = limitedPeriodTime;
    }

    public String getDeptPhone() { return deptPhone; }

    public void setDeptPhone(String deptPhone) { this.deptPhone = deptPhone; }

    public Integer getInfoStatus() {
        return infoStatus;
    }

    public void setInfoStatus(Integer infoStatus) {
        this.infoStatus = infoStatus;
    }

    public String getHeadPicFileName() {
        return headPicFileName;
    }

    public void setHeadPicFileName(String headPicFileName) {
        this.headPicFileName = headPicFileName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

}
