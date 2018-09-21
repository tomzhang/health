package com.dachen.health.group.company.entity.vo;

import com.dachen.health.group.common.entity.vo.DoctorBasicInfo;

/**
 * 
 * @author pijingwei
 * @date 2015/8/12
 */
public class GroupUserVO {

	/* 用户类型 */
	private Integer userType;
	/* 医生信息 */
	private DoctorBasicInfo doctor;
	
	/**
	 * Id
	 */
	private String id;
	
	/**
	 * 用户Id
	 */
	private Integer doctorId;
	
	/**
	 * 集团Id或公司Id
	 */
	private String objectId;
	
	/**
	 * 集团Id
	 */
	private String groupId;
	
	/**
	 * 账户类型    1：公司用户   2：集团用户
	 */
	private Integer type;
	
	/**
	 * 状态	I：邀请待通过，C：正常使用， S：已离职
	 */
	private String status;
	
	/**
	 * 创建人
	 */
	private Integer creator;
	
	/**
	 * 创建时间
	 */
	private Long creatorDate;
	
	/**
	 * 更新人
	 */
	private Integer updator;
	
	/**
	 * 更新时间
	 */
	private Long updatorDate;
	
	/**
	 * 管理员级别
	 */
	private String rootAdmin;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public DoctorBasicInfo getDoctor() {
		return doctor;
	}

	public void setDoctor(DoctorBasicInfo doctor) {
		this.doctor = doctor;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Long getCreatorDate() {
		return creatorDate;
	}

	public void setCreatorDate(Long creatorDate) {
		this.creatorDate = creatorDate;
	}

	public Integer getUpdator() {
		return updator;
	}

	public void setUpdator(Integer updator) {
		this.updator = updator;
	}

	public Long getUpdatorDate() {
		return updatorDate;
	}

	public void setUpdatorDate(Long updatorDate) {
		this.updatorDate = updatorDate;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getRootAdmin() {
		return rootAdmin;
	}

	public void setRootAdmin(String rootAdmin) {
		this.rootAdmin = rootAdmin;
	}
	
	
}
