package com.dachen.health.pack.income.entity.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.dachen.health.pack.income.entity.po.Settle;

public class SettleVO extends Settle implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7901648955582804676L;
	
	private String groupName;
	
	private String doctorName;
	
	private String telephone;
	
	private String settleTableName;//报表名称
	
	private String settleStatus;//结算状态
	
	private String fommatSettleTime;//格式化后的结算时间
	
	 /* 开户人姓名 */
    private String userRealName;

    /* 银行卡号 */
    private String bankNo;

    /* 银行id */
    private Integer bankId;

    /* 银行名称 */
    private String bankName;

    /* 支行 */
    private String subBank;
    
    private List<Integer> doctorIncomeIdList = new ArrayList<Integer>();
    private List<Integer> doctorDivisionIdList = new ArrayList<Integer>();
    private List<Integer> groupDivisionIdList = new ArrayList<Integer>();

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getSettleTableName() {
		return settleTableName;
	}

	public void setSettleTableName(String settleTableName) {
		this.settleTableName = settleTableName;
	}

	public String getSettleStatus() {
		return settleStatus;
	}

	public void setSettleStatus(String settleStatus) {
		this.settleStatus = settleStatus;
	}

	public String getFommatSettleTime() {
		return fommatSettleTime;
	}

	public void setFommatSettleTime(String fommatSettleTime) {
		this.fommatSettleTime = fommatSettleTime;
	}

	public String getUserRealName() {
		return userRealName;
	}

	public void setUserRealName(String userRealName) {
		this.userRealName = userRealName;
	}

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public Integer getBankId() {
		return bankId;
	}

	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getSubBank() {
		return subBank;
	}

	public void setSubBank(String subBank) {
		this.subBank = subBank;
	}

	public List<Integer> getDoctorIncomeIdList() {
		return doctorIncomeIdList;
	}

	public void setDoctorIncomeIdList(List<Integer> doctorIncomeIdList) {
		this.doctorIncomeIdList = doctorIncomeIdList;
	}

	public List<Integer> getDoctorDivisionIdList() {
		return doctorDivisionIdList;
	}

	public void setDoctorDivisionIdList(List<Integer> doctorDivisionIdList) {
		this.doctorDivisionIdList = doctorDivisionIdList;
	}

	public List<Integer> getGroupDivisionIdList() {
		return groupDivisionIdList;
	}

	public void setGroupDivisionIdList(List<Integer> groupDivisionIdList) {
		this.groupDivisionIdList = groupDivisionIdList;
	}
	
	
	

}
