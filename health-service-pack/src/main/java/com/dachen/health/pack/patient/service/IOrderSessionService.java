package com.dachen.health.pack.patient.service;

import java.util.List;
import java.util.Map;

import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.OrderSessionContainer;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.sdk.exception.HttpApiException;

/**
 * 
 * @author vincent
 *
 */
public interface IOrderSessionService extends IBaseService<OrderSession, Integer> {
	/**
	 * 预约服务时间
	 * @author 			李淼淼
	 * @date 2015年9月11日
	 */
	void appointServiceTime(Integer orderId,Long appointTime,boolean sendNotify,String hospitalId) throws HttpApiException;
	
	OrderSession appointTime(Order order, Long appointTime, String hospitalId);

	/**
	 * 开始服务
	 * @author 			李淼淼
	 * @date 2015年9月11日
	 */
	void beginService(Integer orderId) throws HttpApiException;
	
	void beginService4Plan(Integer orderId, String startDate) throws HttpApiException;

	/**
	 * 结束服务
	 * @author 			李淼淼
	 * @date 2015年9月11日
	 */
	void finishService(Integer orderId,Integer aotoStatus) throws HttpApiException;
	void finishServiceByPatient(Integer orderId,Integer patientId) throws HttpApiException;
	
	void updateSplit(String splitJson,Integer orderId,boolean finishService) throws HttpApiException;
	
	void finishServiceWhenPaySuccess(Integer orderId) throws HttpApiException;
	
	/**
	 * 开始门诊订单
	 * @param orderId
	 * @author 			张垠
	 * @date 2015年10月9日
	 * */
	void prepareService(Integer orderId) throws HttpApiException;
	
	
	/**
	 * 放弃服务
	 * @author 			李淼淼
	 * @date 2015年9月11日
	 */
	void abandonService(Integer orderId) throws HttpApiException;
	
	/**
	 * 根据订单id找对应会话关联数据
	 * @author 			李淼淼
	 * @date 2015年9月11日
	 */
	List<OrderSession> findByOrderId(Integer orderId);
	
	/**
	 * 根据订单id找对应会话关联数据
	 * @author 			李淼淼
	 * @date 2015年9月11日
	 */
	OrderSession findOneByOrderId(Integer orderId);
	List<OrderSession> findByOrderIds(List<Integer> orderIds);
	
	
	/**
	 * 根据订单id找对应会话关联数据
	 * @author 			李淼淼
	 * @date 2015年9月11日
	 */
	List<OrderSession> findByMsgGroupId(String msgGroupId);
	
	/**
	 * 根据订单id找对应会话关联数据
	 * @author 			李淼淼
	 * @date 2015年9月11日
	 */
	OrderSession findOneByMsgGroupId(String msgGroupId);

	public int updateByPrimaryKeySelective( OrderSession record) ;
	
	List<OrderSession> getAllMoringBeginConsultationOrders(List<Integer> orderIds, int intervalType);

	List<Integer> getTimeAreaOrderIds(Long start, Long end);

	void agreeAppointmentOrder(Integer orderId) throws HttpApiException;
	
	
	/**
	 * 根据会话ID找对应关联数据
	 * @author 			CQY
	 * @date 2016年7月26日
	 */
	OrderSession findOneByGroupId(String msgGroupId);

	Object getOrCreatePatientCustomerSession();
	
	void cacheBeServicedUser();

	void cacheSendMessageRecord(String messageGroupId);

	void addFreeReplyCount(String messageGroupId, Integer count) throws HttpApiException;

	void messageReplyCountChangeEvent(String messageGroupId, Integer orderId, Integer beUseCount);

	void cacheSessionMessageRecord(String messageGroupId, String messageId) throws HttpApiException;

	void addFreeMessageCount(String messageGroupId, Integer count) throws HttpApiException;

    OrderSessionContainer getOrderSessionWhenPaysuccess(Integer orderId);

    void repairOldCareOrderSession();
}
