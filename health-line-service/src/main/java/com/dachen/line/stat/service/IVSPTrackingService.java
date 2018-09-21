package com.dachen.line.stat.service;

import java.util.Map;

import com.dachen.line.stat.entity.vo.VSPTracking;



/**
 * 护士订单服务
 * @author weilit
 * 2015 12 04 
 */
public interface IVSPTrackingService {
	
	public Map<String, Object> sendMessage( String messageId,String content,String vspId,Integer userId)  ;
	
	
	public  Map<String, Object> callByTel(Integer userId,String vspId) ;
	
	/**
	 * 获取订单流程数据
	 * @param orderId
	 * @return
	 */
	public  Map<String, Object> getTrackingServiceByOrderId(String orderId) ;
}
