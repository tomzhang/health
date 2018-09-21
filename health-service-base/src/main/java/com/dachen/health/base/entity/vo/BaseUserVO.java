package com.dachen.health.base.entity.vo;

import java.util.Map;

import com.dachen.health.base.helper.UserHelper;
import com.dachen.util.DateUtil;

public class BaseUserVO {

    private Integer userId;

    private String name;

    private String telephone;
    
    private Integer age;
    
    private Integer sex;
    
    /* 头像 */
    private String headPicFileName;

    private Integer userType;

    private Integer status;

    private String doctorNum;

    private String hospital;
    
    private String hospitalId;

    private String departments;

    private String title;

    /* 集团id */
    private String groupId;

    /* 集团名称 */
    private String groupName;
    
    /* 集团联系方式 */
    private String contactWay;
    
    /* 组织id */
    private String departmentId;
    
    /* 组织名称 */
    private String departmentName;
    
    /* 加V认证 */
    private String certStatus;
    
    private String is3A;
    
    /**
     * 集团类型
     * @author wangqiao
     * @date 2016年3月29日
     */
    private String groupType;
    
    /* user.settings对象 */
    private Map<String, Object> settings;
    
    
    private Integer isConsultationMember;
    
    private Integer consultationPrice;
    
    private String consultationRequired;
    
    private String remarks;
    
    private Long birthday;
    
    private String checkRemark;
    
    private String doctorPageURL;
    
    private String doctorQrCodeURL;
    
    /* 开启患者报道赠送服务 */
    private Integer checkInGive;
    
    
    
    public Integer getCheckInGive() {
		return checkInGive;
	}

	public void setCheckInGive(Integer checkInGive) {
		this.checkInGive = checkInGive;
	}

	public String getCheckRemark() {
		return checkRemark;
	}

	public void setCheckRemark(String checkRemark) {
		this.checkRemark = checkRemark;
	}

	public String getAgeStr() {
    	if(birthday!=null) {
    		int ages=DateUtil.calcAge(birthday);
    		if (ages == 0 || ages == -1) {
    			return DateUtil.calcMonth(birthday)<=0?"1个月":DateUtil.calcMonth(birthday)+"个月";
    		}
    		return ages + "岁";
    	}else {
    		return null;
    	}
	}

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getHeadPicFileName() {
		return UserHelper.buildHeaderPicPath(headPicFileName, userId, sex, userType);
    }

    public void setHeadPicFileName(String headPicFileName) {
        this.headPicFileName = headPicFileName;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDoctorNum() {
        return doctorNum;
    }

    public void setDoctorNum(String doctorNum) {
        this.doctorNum = doctorNum;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

	public String getContactWay() {
		return contactWay;
	}

	public void setContactWay(String contactWay) {
		this.contactWay = contactWay;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Map<String, Object> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, Object> settings) {
		this.settings = settings;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCertStatus() {
		return certStatus;
	}

	public void setCertStatus(String certStatus) {
		this.certStatus = certStatus;
	}

	public String getIs3A() {
		return is3A;
	}

	public void setIs3A(String is3a) {
		is3A = is3a;
	}

	public Integer getIsConsultationMember() {
		return isConsultationMember;
	}

	public void setIsConsultationMember(Integer isConsultationMember) {
		this.isConsultationMember = isConsultationMember;
	}

	public Integer getConsultationPrice() {
		return consultationPrice;
	}

	public void setConsultationPrice(Integer consultationPrice) {
		this.consultationPrice = consultationPrice;
	}

	public String getConsultationRequired() {
		return consultationRequired;
	}

	public void setConsultationRequired(String consultationRequired) {
		this.consultationRequired = consultationRequired;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getDoctorPageURL() {
		return doctorPageURL;
	}

	public void setDoctorPageURL(String doctorPageURL) {
		this.doctorPageURL = doctorPageURL;
	}

	public String getDoctorQrCodeURL() {
		return doctorQrCodeURL;
	}

	public void setDoctorQrCodeURL(String doctorQrCodeURL) {
		this.doctorQrCodeURL = doctorQrCodeURL;
	}

	
	
}
