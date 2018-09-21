package com.dachen.health.group.group.entity.vo;

import java.util.List;

public class DoctorExcelVo {
	private String id;
	private Long registerTime;
	private Long authTime;
	private String registerTimeStr;
	private String authTimeStr;
	private String phone;
	private String name;
	private String openId;
	private String isFull;
	private String hospital;
	private String hospitalId;
	private String department;
	private String title;
	private String checkRemark;
	private String hasPictureAdvise;
	private String hasPhoneAdvise;
	private String hasCareAdvise;
	private String source;
	private String registerGroup;
	private String inviter;
	private String inviteOpenId;
	private String[] groupIds;
	private String inviterId;
	private List<String> inviterNames;
	private List<String> groups;
	
	private Integer inviterIdInt;
	private Integer sourceType;
	private String sourceTypeName;
	private String inviterName;
	
	private String province;
	private String city;
	private String country;

	private Long lastLoginTime;
	private String lastLoginTimeStr;

    private Integer userLevel;
    /**
     * 用户级别有效期
     */
    private Long limitedPeriodTime;

    private String circleNames;

    private String deptNames;

	public Long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginTimeStr() {
		return lastLoginTimeStr;
	}

	public void setLastLoginTimeStr(String lastLoginTimeStr) {
		this.lastLoginTimeStr = lastLoginTimeStr;
	}

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
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
	private Long price;
	
	public Long getPrice() {
		return price;
	}
	public void setPrice(Long price) {
		this.price = price;
	}
	public Integer getInviterIdInt() {
		return inviterIdInt;
	}
	public void setInviterIdInt(Integer inviterIdInt) {
		this.inviterIdInt = inviterIdInt;
	}
	public Integer getSourceType() {
		return sourceType;
	}
	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}
	public String getSourceTypeName() {
		return sourceTypeName;
	}
	public void setSourceTypeName(String sourceTypeName) {
		this.sourceTypeName = sourceTypeName;
	}
	public String getInviterName() {
		return inviterName;
	}
	public void setInviterName(String inviterName) {
		this.inviterName = inviterName;
	}
	public String getHasPictureAdvise() {
		return hasPictureAdvise;
	}
	public void setHasPictureAdvise(String hasPictureAdvise) {
		this.hasPictureAdvise = hasPictureAdvise;
	}
	public String getHasPhoneAdvise() {
		return hasPhoneAdvise;
	}
	public void setHasPhoneAdvise(String hasPhoneAdvise) {
		this.hasPhoneAdvise = hasPhoneAdvise;
	}
	public String getHasCareAdvise() {
		return hasCareAdvise;
	}
	public void setHasCareAdvise(String hasCareAdvise) {
		this.hasCareAdvise = hasCareAdvise;
	}
	public String getCheckRemark() {
		return checkRemark;
	}
	public void setCheckRemark(String checkRemark) {
		this.checkRemark = checkRemark;
	}
	public List<String> getInviterNames() {
		return inviterNames;
	}
	public void setInviterNames(List<String> inviterNames) {
		this.inviterNames = inviterNames;
	}
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	public String getInviterId() {
		return inviterId;
	}
	public void setInviterId(String inviterId) {
		this.inviterId = inviterId;
	}
	public String[] getGroupIds() {
		return groupIds;
	}
	public void setGroupIds(String[] groupIds) {
		this.groupIds = groupIds;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(Long registerTime) {
		this.registerTime = registerTime;
	}
	public Long getAuthTime() {
		return authTime;
	}
	public void setAuthTime(Long authTime) {
		this.authTime = authTime;
	}
	public String getRegisterTimeStr() {
		return registerTimeStr;
	}
	public void setRegisterTimeStr(String registerTimeStr) {
		this.registerTimeStr = registerTimeStr;
	}
	public String getAuthTimeStr() {
		return authTimeStr;
	}
	public void setAuthTimeStr(String authTimeStr) {
		this.authTimeStr = authTimeStr;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIsFull() {
		return isFull;
	}
	public void setIsFull(String isFull) {
		this.isFull = isFull;
	}
	public String getHospital() {
		return hospital;
	}
	public void setHospital(String hospital) {
		this.hospital = hospital;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getRegisterGroup() {
		return registerGroup;
	}
	public void setRegisterGroup(String registerGroup) {
		this.registerGroup = registerGroup;
	}
	public String getInviter() {
		return inviter;
	}
	public void setInviter(String inviter) {
		this.inviter = inviter;
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

    public String getCircleNames() {
        return circleNames;
    }

    public void setCircleNames(String circleNames) {
        this.circleNames = circleNames;
    }

    public String getDeptNames() {
        return deptNames;
    }

    public void setDeptNames(String deptNames) {
        this.deptNames = deptNames;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getInviteOpenId() {
        return inviteOpenId;
    }

    public void setInviteOpenId(String inviteOpenId) {
        this.inviteOpenId = inviteOpenId;
    }
}
