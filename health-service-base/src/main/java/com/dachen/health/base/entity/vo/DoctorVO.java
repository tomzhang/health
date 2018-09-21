package com.dachen.health.base.entity.vo;

import java.util.List;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

import com.dachen.health.commons.constants.UserEnum;

public class DoctorVO {
	
	private String name;

	private Integer userId;
	/**
	 * 医生审核结果
	 * @see UserEnum.UserStatus
	 */
	private Integer status;
	
	private Long modifyTime;

	private Long lastLoginTime;

    /* 所属医院 */
    private String hospital;

    /* 所属医院Id */
    private String hospitalId;

    /* 所属科室 */
    private String departments;
    
    /* 科室Id */
    private String deptId;

    /* 科室Id */
    private String deptPhone;

    /* 职称 */
    private String title;
    
    /*职称排行*/
    private String titleRank;

    /* 入职时间 */
    private Long entryTime;

    /* 医生号 */
    private String doctorNum;

    /* 个人介绍 */
    private String introduction;

    /* 擅长领域 :用户手工输入的专长*/

    private String skill;

    /* 职业区域，根据医生审核医院确定 */
    private Integer provinceId;

    private Integer cityId;

    private Integer countryId;

    private String province;

    private String city;

    private String country;
    
    /*医生治疗人数*/
    private Integer cureNum;

    // 医生设置：免打扰（1：正常，2：免打扰）
    private String troubleFree;
    
    /**
     * 专长:用户选择的专长
     */
    @Indexed(value = IndexDirection.ASC)
    private List<String> expertise;

    //医生助手id
    private Integer assistantId;
    
    /* 审核信息 */
    @Embedded
    private Check check;
    
  //UserEnum.DoctorRole  医生角色
    private Integer role;
    
    public Integer getRole() {
		//默认是医生
		if(null==role || role.intValue()==0)
		{	
			role=UserEnum.DoctorRole.doctor.getIndex();	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Long modifyTime) {
		this.modifyTime = modifyTime;
	}

    public String getDeptPhone() {
        return deptPhone;
    }

    public void setDeptPhone(String deptPhone) {
        this.deptPhone = deptPhone;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }


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

    }

}
