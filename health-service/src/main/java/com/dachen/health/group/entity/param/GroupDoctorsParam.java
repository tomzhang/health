package com.dachen.health.group.entity.param;

import java.util.List;

import com.dachen.commons.page.PageVO;

/**
 * ProjectName： health-service<br>
 * ClassName： DoctorCheckParam<br>
 * Description：医生审核参数实体 <br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
public class GroupDoctorsParam extends PageVO {

    private Integer userId;

    /* 姓名 */
    private String name;

    /* 开始时间 */
    private Long startTime;

    /* 结束时间 */
    private Long endTime;

    /* 是否审核 */
    private Integer status;

    /* 所属医院 */
    private String hospital;

    /* 所属医院Id */
    private String hospitalId;

    /* 所属科室 */
    private String departments;
    
    /* 所属科室Id */
    private String deptId;

    /* 职称 */
    private String title;

    /* 证书编号 */
    private String licenseNum;

    /* 证书到期日期 */
    private String licenseExpire;

    /* 审核时间 */
    private Long checkTime;

    /* 审核人员 */
    private String checker;

    /* 审核人员 */
    private Integer checkerId;

    /* 审核意见 */
    private String remark;
    
    /* 用户类型 */
    private Integer userType;
    
    /*护士短连接*/
    private String nurseShortLinkUrl;
    
    //医生护士角色
    private  Integer role;
    
    private String enentType;//时间类型
    /*头像*/
    private String headPicFileName;
    
    /** 集团ID */
    private String groupId;
    
    /** 医生ID 列表  add by tanyf */
    private List<Integer> doctorIds;
    
    public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<Integer> getDoctorIds() {
		return doctorIds;
	}

	public void setDoctorIds(List<Integer> doctorIds) {
		this.doctorIds = doctorIds;
	}

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
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

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getCheckerId() {
        return checkerId;
    }

    public void setCheckerId(Integer checkerId) {
        this.checkerId = checkerId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getNurseShortLinkUrl() {
		return nurseShortLinkUrl;
	}

	public void setNurseShortLinkUrl(String nurseShortLinkUrl) {
		this.nurseShortLinkUrl = nurseShortLinkUrl;
	}

	public String getEnentType() {
		return enentType;
	}

	public void setEnentType(String enentType) {
		this.enentType = enentType;
	}
}
