package com.dachen.health.pack.order.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.incomeNew.entity.vo.RefundOrderVO;
import com.dachen.health.pack.order.entity.param.OrderParam;

public interface IOrderRefundService {
	
	/**
	 * 获取退款批次号
	 * @return
	 */
	String nextRefundNo();

	/**
	 * 取消已支付订单
	 * @param orderIds
	 * @return
	 */
	Map<Integer, Object> addRefundOrder(Integer... orderIds);
	
	/**
	 * 处理退款订单
	 * @param param
	 */
	String addRefund(Integer orderId);
	
	/**
	 * 获取退款详情
	 * @param orderId
	 * @return
	 */
	List<RefundOrderVO> getRefundDetail(Integer orderId);
	
	/**
	 * 微信退款结果查询
	 */
	void autoQueryWechat(Integer refundId);
	
	/**
	 * 查询符合退款条件的订单
	 * @param param
	 * @return
	 */
	PageVO getRefundOrders(OrderParam param);
	
	void refundSuccess(Integer refundId,Integer orderId, String log);
	
	void refundFail(Integer refundId, Integer orderId, String log);
}
