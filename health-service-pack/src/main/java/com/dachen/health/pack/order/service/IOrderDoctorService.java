package com.dachen.health.pack.order.service;

import java.util.List;

import com.dachen.health.pack.order.entity.po.OrderDoctor;
import com.dachen.health.pack.order.entity.vo.DoctoreRatioVO;

/**
 * 健康关怀医生组服务
 * @author Administrator
 *
 */
public interface IOrderDoctorService {
	/**
	 * 发送短信给医生团队
	 * @param orderId
	 * @param messageContent
	 */
	public void sendMessageToAll(Integer orderId,Integer userId,String messageContent);
	
	public void sendMessageToAll(Integer orderId,Integer userId,String messageContent,String toLinkApp);
	
	/**
	 * 获取所有医生
	 * @param orderId
	 * @return
	 */
	public List<OrderDoctor> findOrderDoctors(Integer orderId);

	List<DoctoreRatioVO> getDoctorRatiosByOrder(Integer orderId, Integer mainDoctorId);
	
	int deleteByIdList(List<Integer> list);
	
	int updateSelective(OrderDoctor record);
	
	int add(OrderDoctor record);
	
	
	
}
