package com.dachen.line.stat.entity.vo;
//@Table(name = "v_service_config")
public class ServiceConfig {

	private int id;
	private String userId;
	private int weights;// 权重	 0-100分，默认为100，逐渐降低
//	private int uhospitals;//护士用户接单过的医院表	 关联表
//	private int udepartments;// 护士用户接单过的科室表	 关联表
//	private int udoctors;// 护士用户接单过的科室表	 关联表
//	private int dutyTimes;// 护士用户接单过的科室表	 关联表
	private int isAuto;// 0是yes , 1是no
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getWeights() {
		return weights;
	}

	public void setWeights(int weights) {
		this.weights = weights;
	}


	public int getIsAuto() {
		return isAuto;
	}

	public void setIsAuto(int isAuto) {
		this.isAuto = isAuto;
	}

	

}
