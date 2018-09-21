package com.dachen.health.api.client.user.entity;

import java.io.Serializable;

import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

public class CUser implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer userId;// 用户Id


	private String username;// 用户名

	private Integer userType;// 用户类型

	private String telephone;

	private String name;// 姓名

	private Long birthday;// 生日

	private Integer sex;// 性别1男，2女 3 保密

	@Indexed(value = IndexDirection.ASC)
	private Integer active;// 最后出现时间

	private String description;// 签名、说说、备注
	@Indexed(value = IndexDirection.DESC)
	private Long createTime;
	private Long modifyTime;
	
	/* 区域 */
	private String area;
	
	private String remarks;


	/**
	 * 医生审核结果
	 * @see UserEnum.UserStatus
	 */
	private Integer status;
	
	/**
	 * 医生加入博德嘉联集团的状态 
	 * @author fuyongde（0表示未加入，1表示已加入且为主集团，2表示已加入非主集团，3表示已邀请未同意加入博德嘉联医生集团）
	 * @date 2016年6月2日
	 */
	private Integer bdjlGroupStatus;
	
	/**
	 * 医生加入医院状态
	 * @author wangqiao
	 * @date 2016年3月26日
	 */
	private Integer hospitalStatus;
	
	//医生提价认证的时间
	private Long submitTime;
	
	 /**
     * 年龄
     */
    private int age;
    /**
     * 头像名称
     */
    private String headPicFileName;

	private CDoctor doctor;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}

	public CDoctor getDoctor() {
		return doctor;
	}

	public void setDoctor(CDoctor doctor) {
		this.doctor = doctor;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getActive() {
		return active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Long modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getBdjlGroupStatus() {
		return bdjlGroupStatus;
	}

	public void setBdjlGroupStatus(Integer bdjlGroupStatus) {
		this.bdjlGroupStatus = bdjlGroupStatus;
	}

	public Integer getHospitalStatus() {
		return hospitalStatus;
	}

	public void setHospitalStatus(Integer hospitalStatus) {
		this.hospitalStatus = hospitalStatus;
	}

	public Long getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Long submitTime) {
		this.submitTime = submitTime;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	
}
