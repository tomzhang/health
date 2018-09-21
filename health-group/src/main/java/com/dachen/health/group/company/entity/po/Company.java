package com.dachen.health.group.company.entity.po;

import java.io.Serializable;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * 
 * @author pijingwei
 * @date 2015-08-04 16:22:15
 *
 */
@Entity(value="c_company", noClassnameStored = true)
public class Company implements Serializable {

	private static final long serialVersionUID = -5121007346499296607L;

	@Id
	private String id;

	/**
	 * 公司（集团）名称
	 */
	private String name;
	
	/**
	 * 公司描述
	 */
	private String description;
	 
	 /**
	  * 审核备注
	  */
	 private String checkRemarks;
	
	/**
	 * 法人
	 */
	private String corporation;
	
	/**
	 * 组织机构代码
	 */
	private String orgCode;

	/**
	 * 营业执照编号
	 */
	private String license;
	
	
	/**
	 * 银行账户名称
	 */
	private String bankAccount;
	
	/**
	 * 银行账号
	 */
	private String bankNumber;
	
	/**
	 * 开户行
	 */
	private String openBank;

	/**
	 * 状态：A：审核中，B，审核不通过，P：审核通过，O：临时冻结，  S：已停用
	 */
	private String status;
	
	/**
	 * 创建人（申请人）
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
	 * 更新时间（通过日期）
	 */
	private Long updatorDate;
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCheckRemarks() {
		return checkRemarks;
	}

	public void setCheckRemarks(String checkRemarks) {
		this.checkRemarks = checkRemarks;
	}

	public String getCorporation() {
		return corporation;
	}

	public void setCorporation(String corporation) {
		this.corporation = corporation;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBankNumber() {
		return bankNumber;
	}

	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}

	public String getOpenBank() {
		return openBank;
	}

	public void setOpenBank(String openBank) {
		this.openBank = openBank;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
	
}
