package com.dachen.health.group.common.entity.vo;

import com.dachen.health.base.helper.UserHelper;
import com.dachen.util.StringUtil;

/**
 * 医生基本信息
 * @author pijingwei
 * @date 2015/8/15
 */
public class DoctorBasicInfo {

	/** 从user表获取的属性 **/
	private Integer doctorId;
	
	/* 头像名称 */
    private String headPicFileName;
    
    /* 头像地址 */
    private String headPicFilePath;
	
	/* 医生审核结果 */
	private Integer status;

	/* 所属医院 */
    private String hospital;
    
    /* 所属科室 */
    private String departments;
	
	/* 用户类型 */
	private Integer userType;

	private Integer sex;
	
	/* 手机号码 */
	private String telephone;
	
	/* 医生名称 */
	private String name;
	
	/* 职称 */
    private String position;
    
	/* 医生号 */
    private String doctorNum;
    
	/* 擅长领域 */
    private String skill;
    
    /* 个人介绍 */
    private String introduction;
    
    /* 用户类型 */
	private Integer role;
	
	private long modifyTime;

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public long getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(long modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}
	
	public String getHeadPicFileName() {
		return UserHelper.buildHeaderPicPath(headPicFileName, doctorId, sex, userType);
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}

	public String getHeadPicFilePath() {
		return UserHelper.buildHeaderPicPath(headPicFileName, doctorId, sex, userType);
	}

	public void setHeadPicFilePath(String headPicFilePath) {
		this.headPicFilePath = headPicFilePath;
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

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getName() {
		if(StringUtil.isEmpty(name)) {
			name = "";
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getDoctorNum() {
		return doctorNum;
	}

	public void setDoctorNum(String doctorNum) {
		this.doctorNum = doctorNum;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
    
}
