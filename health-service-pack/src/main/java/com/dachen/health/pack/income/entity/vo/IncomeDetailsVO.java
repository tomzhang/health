package com.dachen.health.pack.income.entity.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.pack.income.entity.po.IncomeDetails;

public class IncomeDetailsVO extends IncomeDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5523054045382482528L;
	
	private Integer settleId;
	
	private Integer type;
	
	private Integer orderType;
	
	private Integer orderNo;
	
	private Integer orderStatus;
	
	private Integer packType;
	
	private Long finishTime;
	
	private Long createTime;
	
	private String groupName;
	
	private String doctorName;
	
	private String telephone;
	
	private String orderTypeName;
	
	private double groupMoney;
	
	private double finishedMoney;
	private double unfinishedMoney;
	private double ProportionMoney;
	
	//提成收入的贡献医生id，即下级医生
	private Integer incomeDoctorId;
	
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

    
	public Integer getSettleId() {
		return settleId;
	}

	public void setSettleId(Integer settleId) {
		this.settleId = settleId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getPackType() {
		return packType;
	}

	public void setPackType(Integer packType) {
		this.packType = packType;
	}

	public Long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Long finishTime) {
		this.finishTime = finishTime;
	}

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

	public String getOrderTypeName() {
		return orderTypeName;
	}

	public void setOrderTypeName(String orderTypeName) {
		this.orderTypeName = orderTypeName;
	}

	public double getGroupMoney() {
		return groupMoney;
	}

	public void setGroupMoney(double groupMoney) {
		this.groupMoney = groupMoney;
	}

	public double getFinishedMoney() {
		return finishedMoney;
	}

	public void setFinishedMoney(double finishedMoney) {
		this.finishedMoney = finishedMoney;
	}

	public double getUnfinishedMoney() {
		return unfinishedMoney;
	}

	public void setUnfinishedMoney(double unfinishedMoney) {
		this.unfinishedMoney = unfinishedMoney;
	}

	public double getProportionMoney() {
		return ProportionMoney;
	}

	public void setProportionMoney(double proportionMoney) {
		ProportionMoney = proportionMoney;
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
	
	public static BigDecimal getValue(double a,double b,char type){
		BigDecimal result = null;
		BigDecimal bigA = new BigDecimal(a);
		BigDecimal bigB = new BigDecimal(b);
		switch (type) {
		case '+':
			result = bigA.add(bigB);
			break;
		case '-':
			result = bigA.subtract(bigB);
			break;
		case '*':
			result = bigA.multiply(bigB);
			break;
		case '/':
			result = bigA.divide(bigB, 5, BigDecimal.ROUND_HALF_UP);
			break;
		default:
			result = new BigDecimal(0);
			break;
		}
		return result;
	}
	
	public static String getTypeName(Integer orderType,Integer packType){
		String result = "";
		if(orderType == null){
			
		}
		if(orderType == OrderEnum.OrderType.order.getIndex()){
			if(packType == null){
				return result;
			}
			if(packType == PackEnum.PackType.message.getIndex()){
				result = PackEnum.PackType.message.getTitle();
			}else if(packType == PackEnum.PackType.phone.getIndex()){
				result = PackEnum.PackType.phone.getTitle();
			}else if(packType == PackEnum.PackType.careTemplate.getIndex()){
				result = PackEnum.PackType.careTemplate.getTitle();
			}
		}else if(orderType == OrderEnum.OrderType.checkIn.getIndex()){
			result = OrderEnum.OrderType.checkIn.getTitle();
		}else if(orderType == OrderEnum.OrderType.outPatient.getIndex()){
			result = OrderEnum.OrderType.outPatient.getTitle();
		}else if(orderType == OrderEnum.OrderType.care.getIndex()){
			result = OrderEnum.OrderType.care.getTitle();
		}else if(orderType == OrderEnum.OrderType.followUp.getIndex()){
			result = OrderEnum.OrderType.followUp.getTitle();
		}else if(orderType == OrderEnum.OrderType.throughTrain.getIndex()){
			result = OrderEnum.OrderType.throughTrain.getTitle();
		}else if(orderType == OrderEnum.OrderType.consultation.getIndex()){
			result = OrderEnum.OrderType.consultation.getTitle();
		}
		
		return result;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getIncomeDoctorId() {
		return incomeDoctorId;
	}

	public void setIncomeDoctorId(Integer incomeDoctorId) {
		this.incomeDoctorId = incomeDoctorId;
	}
	
	
}
