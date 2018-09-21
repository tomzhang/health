package com.dachen.health.commons.vo;

public class OpenDoctorExtVO extends OpenDoctorVO {

    /**
     * userId--医生圈user表id
     */
    private Integer userId;
    /**
     * 医生编号（来源编号）
     */
    private String doctorNum;


    /**
     * 医生状态（1=正常，6=未激活，其它都不正常）
     */
    private Integer status;

    /**
     * 区别于用户审核
     * 挂起或禁用用户（0表示正常状态，1表示挂起状态，4表示暂时禁用状态）
     */
    private Integer suspend = 0;//默认值

    /**
     * 职称等级
     */
    private String titleRank;

    /**
     * 医院编码
     */
    private String hospitalId;

    /**
     * 科室编码
     */
    private String deptId;

    /**
     * 科室号码
     */
    private String deptPhone;

    /**
     * health系统中的最后更新时间
     * 直接保存Health数据库中的最后更新时间
     * 不需要修改
     */
    private Long modifyTime;

    /**
     * 最后登录时间
     */
    private Long lastLoginTime;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDoctorNum() {
        return doctorNum;
    }

    public void setDoctorNum(String doctorNum) {
        this.doctorNum = doctorNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSuspend() {
        return suspend;
    }

    public void setSuspend(Integer suspend) {
        this.suspend = suspend;
    }

    public String getTitleRank() {
        return titleRank;
    }

    public void setTitleRank(String titleRank) {
        this.titleRank = titleRank;
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

    public String getDeptPhone() {
        return deptPhone;
    }

    public void setDeptPhone(String deptPhone) {
        this.deptPhone = deptPhone;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
