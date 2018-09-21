package com.dachen.line.stat.dao;

import java.text.ParseException;
import java.util.List;

import org.bson.types.ObjectId;

import com.dachen.line.stat.entity.vo.PatientOrder;
import com.dachen.line.stat.entity.vo.VSPTracking;

/**
 * 护士订单服务
 * 
 * @author weilit 2015 12 04
 */
public interface INurseOrderDao {

	/**
	 * 发送订单给用户
	 * 
	 * @param orderId
	 *            订单id
	 * @return
	 */
	public void sendOrder(String orderId);

	/**
	 * 具体查询订单的接口
	 * 
	 * @param userId
	 * @param hospitonList
	 * @param departList
	 * @param timeList
	 * @param serviceList
	 * @return
	 */
	public List<PatientOrder> getUserOrder(List<String> hospitalList,
			List<String> departList, List<String> timeList,
			List<String> serviceList);

	/**
	 * 查询制定字段条件下的下面的医院
	 * 
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<PatientOrder> getPatientServiceOrderList(List<ObjectId> orderIds);

	/**
	 * 超过系统设置时间尚未接单的订单
	 * 查询条件--1业务编码  2、创建时间
	 * @return
	 */
	public List<VSPTracking> getOderOvertopHour();

	/**
	 * 超过预约时间的前一天晚上10点尚未接单的订单作为异常订单
	 * 
	 * @return
	 */
	public List<VSPTracking> getBeforeExceptionOrder() throws ParseException;

	/**
	 * 服务已经结束 但是患者依然没有对服务进行评价
	 * 
	 * @return
	 */
	public List<VSPTracking> getExceptionOrderNoEvaluate()
			throws ParseException;

	/**
	 * 异常列表之--护士在预约时间开始之前就点击了开始服务的按钮
	 * 
	 * @return
	 */
	public List<VSPTracking> getExceptionOfNurseService()
			throws ParseException;

	/**
	 * 异常列表之--护士在过了预约时间还未点击开始服务
	 * 
	 * @return
	 */
	public List<VSPTracking> getExceptionOfNurseServiceNoClick()
			throws ParseException;

	/**
	 * 查询某个用户的某种状态的某种类型的所有的订单
	 * 
	 * @param status
	 * @param type
	 * @param userId
	 * @return
	 */
	public List<PatientOrder> getPatientOrderList(Integer status, Integer type,
			Integer userId);

	/**
	 * 获取单个订单对象
	 * 
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public PatientOrder getPatientOrderById(String orderId);
	

	/**
	 *更新业务订单中的基础订单id
	 * @param basicId
	 * @param status
	 */
	public void updatePatientOrderBasicId(String orderId, String basicId);

	public void updatePatientOrderStatus(String orderId, Integer status);

	public Object insertOrder(PatientOrder order);

	/**
	 * 根据订单id查询患者电话
	 * 
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public String getPatientTelById(String orderId);

	/**
	 * 根据订单id查询患者id
	 */
	public Integer getPatientIdById(String orderId);

	public void canclePatientOrderStatus(String orderId, Integer type);

	public PatientOrder getPatientBasicOrderById(String basicOrderId);
	
	public PatientOrder getPatientOrderByCheckId(String orderId) ;

	public void updatePatientOrderTypeByBasicId(String basicOrderId,
			Integer type);
	
	public String getCheckIdIdById(String orderId);
}
