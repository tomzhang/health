package com.dachen.health.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-02-08
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class DoctorTopicVO {

    @ApiModelProperty(value = "数据来源类型")
    private String dsType;

    @ApiModelProperty(value = "医生Id")
    private String userId;

    @ApiModelProperty(value = "医生名称")
    private String username;

    @ApiModelProperty(value = "手机号")
    private String telephone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "出生日期")
    private String birthday;

    @ApiModelProperty(value = "医院Id")
    private String hospitalId;

    @ApiModelProperty(value = "所属医院")
    private String hospital;

    @ApiModelProperty(value = "科室Id")
    private String deptId;

    @ApiModelProperty(value = "所属科室")
    private String departments;

    @ApiModelProperty(value = "职称名")
    private String title;

    @ApiModelProperty(value = "省份Id")
    private String provinceId;

    @ApiModelProperty(value = "城市Id")
    private String cityId;

    @ApiModelProperty(value = "地区Id")
    private String areaId;

    @ApiModelProperty(value = "认证状态")
    private String status;

    @ApiModelProperty(value = "用户等级")
    private String userLevel;

    @ApiModelProperty(value = "邀请人Id")
    private String inviterId;

    @ApiModelProperty(value = "专长")
    private String skill;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "注册来源")
    private String regfrom;

    @ApiModelProperty(value = "使用设备")
    private String model;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "数据入库时间")
    private String lastUploadTime;

    @ApiModelProperty(value = "登录类型（0：免密登录，1：手动登录，2：注销）")
    private String loginType;

    @ApiModelProperty(value = "登录时间")
    private String loginTime;

    @ApiModelProperty(value = "客户端使用的接口版本号")
    private String apiVersion;

    @ApiModelProperty(value = "客户端设备操作系统版本号")
    private String osVersion;

    @ApiModelProperty(value = "客户端设备序列号")
    private String serial;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "位置描述")
    private String location;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "证书编号")
    private String licenseNum;

    @ApiModelProperty(value = "证书有效期")
    private String licenseExpire;

    @ApiModelProperty(value = "认证时间")
    private String certifyTime;

    @ApiModelProperty(value = "审核人员")
    private String checker;

    @ApiModelProperty(value = "审核人员Id")
    private String checkerId;

    @ApiModelProperty(value = "审核时间")
    private String checkTime;

    @ApiModelProperty(value = "审核意见")
    private String remark;


    public String getDsType() {
        return dsType;
    }

    public void setDsType(String dsType) {
        this.dsType = dsType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDepartments() {
        return departments;
    }

    public void setDepartments(String departments) {
        this.departments = departments;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getInviterId() {
        return inviterId;
    }

    public void setInviterId(String inviterId) {
        this.inviterId = inviterId;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRegfrom() {
        return regfrom;
    }

    public void setRegfrom(String regfrom) {
        this.regfrom = regfrom;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastUploadTime() {
        return lastUploadTime;
    }

    public void setLastUploadTime(String lastUploadTime) {
        this.lastUploadTime = lastUploadTime;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLicenseNum() {
        return licenseNum;
    }

    public void setLicenseNum(String licenseNum) {
        this.licenseNum = licenseNum;
    }

    public String getLicenseExpire() {
        return licenseExpire;
    }

    public void setLicenseExpire(String licenseExpire) {
        this.licenseExpire = licenseExpire;
    }

    public String getCertifyTime() {
        return certifyTime;
    }

    public void setCertifyTime(String certifyTime) {
        this.certifyTime = certifyTime;
    }

    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker;
    }

    public String getCheckerId() {
        return checkerId;
    }

    public void setCheckerId(String checkerId) {
        this.checkerId = checkerId;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
