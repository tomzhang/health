package com.dachen.health.pack.guide.dao;

import java.util.List;

import com.dachen.health.pack.guide.entity.po.ConsultOrderDoctorPO;

public interface IConsultOrderDoctorDAO {
	
	/**
	 * 添加医生信息
	 * @param order
	 * @return
	 */
	public void addConsultOrder(ConsultOrderDoctorPO order);
	
	/**
	 * 获取医生信息
	 * @param id
	 * @return
	 */
	public ConsultOrderDoctorPO getConsultOrderDoctor(String id);
	
	/**
	 * 查询医生信息
	 * @param doctorId 医生id
	 * @param id       咨询订单id
	 * @return
	 */
	public List<ConsultOrderDoctorPO> getOrderByUser(int doctorId,String id);
	/**
	 * 查询医生信息
	 * @param doctorId 医生id
	 * @param id       咨询订单id
	 * @return
	 */
	public ConsultOrderDoctorPO getOrderByUserById(int doctorId,String id);
	/**
	 * 更新信息状态
	 * @param doctorId 医生id
	 * @param id       咨询订单id
	 * @return
	 */
	public boolean updateOrderState(String doctorId,String id);
	
	/**
	 * 更新信息状态为关闭
	 * @param doctorId 医生id
	 * @param id       咨询订单id
	 * @return
	 */
	public void colseOrderState(String ids,String id);
	
}
