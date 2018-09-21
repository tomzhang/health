package com.dachen.health.user.entity.vo;

public class DrugVerifyInfo {
	/**
	 * 药监码
	 */
	private String drugCode;
	/**
	 * 药品名称
	 */
	private String drugName;
	/**
	 * 药品规格
	 */
	private String drugSpec;
	/**
	 * 药品批次号
	 */
	private String batchNo;
	/**
	 * 生产日期
	 */
	private String madeDate;
	/**
	 * 有效期
	 */
	private String validateDate; 
	/**
	 * 生产厂家
	 */
	private String supplier; 
	/**
	 * 发货日期
	 */
	private String outDate;
	/**
	 * 收货商家
	 */
	private String recMerchant;
	/**
	 * 备注说明
	 */
	private String remark;
	
	public String getDrugName() {
		return drugName;
	}
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	public String getDrugSpec() {
		return drugSpec;
	}
	public void setDrugSpec(String drugSpec) {
		this.drugSpec = drugSpec;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getMadeDate() {
		return madeDate;
	}
	public void setMadeDate(String madeDate) {
		this.madeDate = madeDate;
	}
	public String getValidateDate() {
		return validateDate;
	}
	public void setValidateDate(String validateDate) {
		this.validateDate = validateDate;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getOutDate() {
		return outDate;
	}
	public void setOutDate(String outDate) {
		this.outDate = outDate;
	}
	public String getRecMerchant() {
		return recMerchant;
	}
	public void setRecMerchant(String recMerchant) {
		this.recMerchant = recMerchant;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getDrugCode() {
		return drugCode;
	}
	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
	}
 
	 
	
}
