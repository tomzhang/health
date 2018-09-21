package com.dachen.health.enterprise.entity;

import java.util.List;
import java.util.Map;


public class EnterpriseUserVO {
	
	/***企业id***/
	private String enterpriseId;
	/***企业名称和***/
	private String companyName;
	
	
	/***企业id***/
	private Integer userId;
	
	/**
	 * 组织id
	 */
	private String id;
	
	
	/**
	 * 组织名称
	 */
	private String department;
	
	/**
	 * 姓名
	 */
	private String name;
	
	/**
	 * 职位
	 */
	private String position;
	
	/**
	 * 手机号码
	 */
	private String telephone;
	
	/**
	 * 医生Id
	 */
	private List<Map<String,Object>> role;
	
	private Integer status;
	
	private  String  remarks;
	
	private String headPicFileName;
	

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getName() {
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

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public List<Map<String, Object>> getRole() {
		return role;
	}

	public void setRole(List<Map<String, Object>> role) {
		this.role = role;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
}
