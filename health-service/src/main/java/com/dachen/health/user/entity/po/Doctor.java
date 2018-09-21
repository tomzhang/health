package com.dachen.health.user.entity.po;

import com.dachen.health.commons.constants.UserEnum;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

import java.util.List;

/**
 * ProjectName： health-service<br>
 * ClassName： Doctor<br>
 * Description： 医生信息<br>
 * 
 * @author fanp
 * @crateTime 2015年6月29日
 * @version 1.0.0
 */
public class Doctor {

    /** 所属医院 */
    private String hospital;

    /** 所属医院Id */
    @Indexed(value = IndexDirection.ASC)
    private String hospitalId;

    /** 所属科室 */
    private String departments;

    /** 科室Id */
    @Indexed(value = IndexDirection.ASC)
    private String deptId;

    /** 所属科室电话 */
    private String deptPhone;

    /** 职称 */
    @Indexed(value = IndexDirection.ASC)
    private String title;

    /** 职称排行 */
    private String titleRank;

    /** 入职时间 */
    private Long entryTime;

    /** 医生号 */
    private String doctorNum;

    /** 个人介绍 */
    private String introduction;

    /** 擅长领域 :用户手工输入的专长 */
    private String skill;

    /** 学术成就 */
    private String scholarship;

    /** 社会任职 */
    private String experience;

    /** 职业区域，根据医生审核医院确定 */
    private Integer provinceId;

    @Indexed(value = IndexDirection.ASC)
    private Integer cityId;

    private Integer countryId;

    private String province;

    private String city;

    private String country;

    /* 医生治疗人数 */
    private Integer cureNum;

    // 医生设置：免打扰（1：正常，2：免打扰）
    private String troubleFree;

    /**
     * 专长:用户选择的专长
     */
    @Indexed(value = IndexDirection.ASC)
    private List<String> expertise;

    // 医生助手id
    private Integer assistantId;

    /* 审核信息 */
    @Embedded
    private Check check;

    // UserEnum.DoctorRole 医生角色
    private Integer role;

    // UserEnum.ServiceStatus 医生套餐的开通状态
    private Integer serviceStatus;

    /* 开启患者报道赠送服务 */
    private Integer checkInGive;


    public Integer getCheckInGive() {
        return checkInGive;
    }

    public void setCheckInGive(Integer checkInGive) {
        this.checkInGive = checkInGive;
    }

    public Integer getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(Integer serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public Integer getRole() {
        // 默认是医生
        if (null == role || role.intValue() == 0) {
            role = UserEnum.DoctorRole.doctor.getIndex();
        }
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getTroubleFree() {
        return troubleFree;
    }

    public void setTroubleFree(String troubleFree) {
        this.troubleFree = troubleFree;
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

    public String getTitleRank() {
        return titleRank;
    }

    public void setTitleRank(String titleRank) {
        this.titleRank = titleRank;
    }

    public Long getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Long entryTime) {
        this.entryTime = entryTime;
    }

    public String getDoctorNum() {
        return doctorNum;
    }

    public void setDoctorNum(String doctorNum) {
        this.doctorNum = doctorNum;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Check getCheck() {
        return check;
    }

    public void setCheck(Check check) {
        this.check = check;
    }

    public Integer getCureNum() {
        return cureNum;
    }

    public void setCureNum(Integer cureNum) {
        this.cureNum = cureNum;
    }

    public List<String> getExpertise() {
        return expertise;
    }

    public void setExpertise(List<String> expertise) {
        this.expertise = expertise;
    }

    public Integer getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(Integer assistantId) {
        this.assistantId = assistantId;
    }
    
    public String getScholarship() {
        return scholarship;
    }

    public void setScholarship(String scholarship) {
        this.scholarship = scholarship;
    }

    public String getExperience() { return experience; }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getDeptPhone() { return deptPhone; }

    public void setDeptPhone(String deptPhone) { this.deptPhone = deptPhone; }

    // 审核信息
    public static class Check {

        /* 所属医院 */
        private String hospital;

        /* 所属医院Id */
        private String hospitalId;

        /* 所属科室 */
        private String departments;

        /* 所属科室 */
        private String deptId;

        /* 所属科室电话 */
        private String deptPhone;

        /* 职称 */
        private String title;

        /* 证书编号 */
        private String licenseNum;

        /* 证书有效期 */
        private String licenseExpire;

        /* 审核时间 */
        private Long checkTime;

        /* 审核人员 */
        private String checker;

        /* 审核人员Id */
        private String checkerId;

        /* 审核意见 */
        private String remark;

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

        public Long getCheckTime() {
            return checkTime;
        }

        public void setCheckTime(Long checkTime) {
            this.checkTime = checkTime;
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

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getDeptPhone() { return deptPhone; }

        public void setDeptPhone(String deptPhone) { this.deptPhone = deptPhone; }

    }

}
