package com.dachen.line.stat.service;

import java.util.List;
import java.util.Map;

import com.dachen.health.commons.vo.User;
import com.dachen.line.stat.entity.vo.VSPTracking;
import com.dachen.line.stat.entity.vo.VServiceProcess;




/**
 * 护士订单服务
 * @author weilit
 * 2015 12 04 
 */
public interface IVServiceProcessService {
	
	/**
	 * 批量插入用户服务数据
	 * @param userId
	 * @return
	 */
	public void  updateVServiceProcess(String processServiceId,Integer status);
	/**
	 * 根据服务id更新护士订单状态
	 * @param userId
	 * @return
	 */
	public void updateVServiceById(String serverId,Integer status);
	
	
	/**
	 * 获取护士所有的服务列表
	 * @param userId
	 * @return
	 */
	public List<Map<String,Object>> getVServiceProcessList(Integer userId);
	
	
	/**
	 * 获取护士历史服务列表
	 * @param userId
	 * @return
	 */
	public List<Map<String,Object>> getHistoryVServiceProcessList(Integer userId);
	
	
	
	/**
	 *  获取护士历史服务列表
	 * @param userId
	 * @param pageIndex  返回的页码
	 * @param pageSize   每页的大小
	 * @return
	 */
	public Map<String,Object> getHistoryVServiceProcessList(Integer userId,Integer pageIndex,Integer pageSize);
	/**
	 * 很据订单id获取护士服务信息
	 * @param userId
	 * @return
	 */
	public VServiceProcess getHistoryByOrderId(String orderId);
	/**
	 * 查询护士抢单成功之后半小时依然没有给患者大电话 或者是发短信
	 * @return
	 */
	public List<VSPTracking> getExceptionNoCallPhone();
	
	/**
	 * Integer checkbillStatus 2 下单   3 预约
	 * { "data": 
	 * {
	 *  "productTitle": "检查直通车",
	 *  "orderTime": "2016-01-31 07:00",
	 *  "orderId": "3268", 
	 *  "checkBusDesc":
	 *  "您的检查直通车服务已购买成功，正在安排南山区人民医院的护士为您服务。预约成功后，我们会第一时间短信通知您。", 
	 *  "productPrice":125 
	 * }, 
	 * "resultCode": 1 }
	 */
	public Map<String,Object> getCheckBillService(String  checkId,Integer checkbillStatus);
	
	/**
	 * 记录是否进行了指引
	 * @param userId
	 * @param guideType
	 */
	public   User  updateUserGuide(Integer userId);
	
	
	/**
	 * 护士确认患者取消的订单
	 * @param userId
	 * @return
	 */
	public void  endNurseServiceProcess(String processServiceId);
}
