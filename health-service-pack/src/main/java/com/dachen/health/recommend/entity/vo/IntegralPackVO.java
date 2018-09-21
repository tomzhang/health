package com.dachen.health.recommend.entity.vo;

public class IntegralPackVO {
	//积分套餐Id
	private Integer id;
	//品种组id（积分问诊使用）
	private String goodsGroupId;
	//医生Id
	private Integer doctorId;
	//积分套餐名称
	private String name;
	//套餐所需积分
	private Integer point;
	//患者所持有的积分与套餐所需积分的差额，有可能是负数
	private Integer balance;
	//患者可用积分
	private Integer patientPoint;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getPoint() {
		return point;
	}
	public void setPoint(Integer point) {
		this.point = point;
	}
	public Integer getBalance() {
		return balance;
	}
	public void setBalance(Integer balance) {
		this.balance = balance;
	}
	public String getGoodsGroupId() {
		return goodsGroupId;
	}
	public void setGoodsGroupId(String goodsGroupId) {
		this.goodsGroupId = goodsGroupId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}
	public Integer getPatientPoint() {
		return patientPoint;
	}
	public void setPatientPoint(Integer patientPoint) {
		this.patientPoint = patientPoint;
	}
}
