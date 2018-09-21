package com.dachen.health.pack.incomeNew.entity.vo;

import com.dachen.health.pack.incomeNew.entity.po.SettleNew;

public class SettleDetailVO extends SettleNew {
	
	private String userName;
	private String telephone;
	private String bankUserName;
	private Integer objectType;
	private Integer doctorId;
	private String groupId;
	private String bankName;
	private String subBankName;
	private String bankNo;
	private Double noSettleMoney;//待结算金客额
	private Double monthMoney;
	private Double actualMoney;//实际结算金额
	private Integer status;
	private String userRealName;
	private String personNo;
	private boolean inofOK;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getBankUserName() {
		return bankUserName;
	}
	public void setBankUserName(String bankUserName) {
		this.bankUserName = bankUserName;
	}
	public Integer getObjectType() {
		return objectType;
	}
	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}
	public Integer getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getSubBankName() {
		return subBankName;
	}
	public void setSubBankName(String subBankName) {
		this.subBankName = subBankName;
	}
	
	public Double getNoSettleMoney() {
		return noSettleMoney;
	}
	public void setNoSettleMoney(Double noSettleMoney) {
		this.noSettleMoney = noSettleMoney;
	}
	public Double getMonthMoney() {
		return monthMoney;
	}
	public void setMonthMoney(Double monthMoney) {
		this.monthMoney = monthMoney;
	}
	public Double getActualMoney() {
		return actualMoney;
	}
	public void setActualMoney(Double actualMoney) {
		this.actualMoney = actualMoney;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	public String getUserRealName() {
		return userRealName;
	}
	public void setUserRealName(String userRealName) {
		this.userRealName = userRealName;
	}
	public String getPersonNo() {
		return personNo;
	}
	public void setPersonNo(String personNo) {
		this.personNo = personNo;
	}
	public boolean isInofOK() {
		return inofOK;
	}
	public void setInofOK(boolean inofOK) {
		this.inofOK = inofOK;
	}
	
}
