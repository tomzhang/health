package com.dachen.health.pack.income.entity.vo;

import java.io.Serializable;

public class DoctorIncomeVO  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6121791814115739011L;

	/**
	 * 医生id
	 */
	private Integer doctorId;
	
	/**
	 * 另一个医生id（联合查询用）
	 */
	private Integer doctorId1;
	
	/**
	 * 集团id
	 */
	private String groupId;

	/**
	 * 集团名称
	 */
	private String groupName;
	
	/**
	 * 医生姓名
	 */
	private String doctorName;
	
	/**
	 * 医生电话
	 */
	private String telephone;
	
	/**
	 * 订单实际收入
	 */
	private double orderIncome;
	
	/**
	 * 订单金额
	 */
	private double orderPrice;
	
	/**
	 * 提成收入
	 */
	private double divisionIncome;
	
	/**
	 * 订单实际收入+提成收入
	 */
	private double totalIncome;
	
	/**
	 * 订单号
	 */
	private Integer orderNo;
	
	/**
	 * 订单id
	 */
	private Integer orderId;
	
	/**
	 * 订单类型
	 */
	private Integer orderType;
	
	/**
	 * 套餐类型
	 */
	private Integer packType;
	
	/**
	 * 创建时间
	 */
	private long createTime;
	
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
	public double getOrderIncome() {
		return orderIncome;
	}
	public void setOrderIncome(double orderIncome) {
		this.orderIncome = orderIncome;
	}
	public double getDivisionIncome() {
		return divisionIncome;
	}
	public void setDivisionIncome(double divisionIncome) {
		this.divisionIncome = divisionIncome;
	}
	public double getTotalIncome() {
		return totalIncome;
	}
	public void setTotalIncome(double totalIncome) {
		this.totalIncome = totalIncome;
	}

	public Integer getOrderType() {
		return orderType;
	}
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	public Integer getPackType() {
		return packType;
	}
	public void setPackType(Integer packType) {
		this.packType = packType;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public Integer getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public double getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(double orderPrice) {
		this.orderPrice = orderPrice;
	}
	public Integer getDoctorId1() {
		return doctorId1;
	}
	public void setDoctorId1(Integer doctorId1) {
		this.doctorId1 = doctorId1;
	}
	
	
	
}

