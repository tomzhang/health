package com.dachen.health.repair.service;

public interface IDataRepairService {

	/**
	 * 修复医生状态（未认证->待审核）
	 * @return
	 */
	int repairDoctorStatus();

	/**
	 * 同步医生的医院信息
	 */
	void syncDoctorHospital();
	/**
	 * 删除不存在用户的相关数据
	 */
	void deleteUserData();
	
	/**
	 * 指定时间内订单收入重新计算
	 * @param timeStart
	 */
	void repairIncomes(Long timeStart);
	
	void delIncomesData();
}
