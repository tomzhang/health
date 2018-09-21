package com.dachen.health.pack.pay.service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.pack.order.entity.po.OrderSessionContainer;
import com.dachen.health.pack.order.entity.vo.PreOrderVO;
import com.dachen.health.pack.pay.entity.PaymentVO;
import com.dachen.sdk.exception.HttpApiException;

/**
 * 支付处理业务逻辑
 * @author Administrator
 *
 */
public interface IPayHandleBusiness {
	
	
	/**
	 * 处理支付业务订单逻辑
	 * @param paymentVo
	 * @throws ServiceException
	 */
	public void handlePayBusinessLogic(PaymentVO paymentVo) throws ServiceException, HttpApiException;
	
	public void handleBusinessWhenPaySuccess(Integer orderId) throws HttpApiException;

}
