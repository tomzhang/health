package com.dachen.health.pack.pay.service;

import com.dachen.commons.exception.ServiceException;
import com.tencent.protocol.pay_protocol.AppPayReqData;
import com.tencent.protocol.pay_protocol.UnifiedOrderPayReqData;
import com.tencent.protocol.pay_protocol.UnifiedOrderPayResData;
import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_query_protocol.RefundQueryReqData;

/**
 * ProjectName： health-service-trade<br>
 * ClassName： WechatPayService<br>
 * Description：微信支付接口 <br>
 * @author fanp
 * @createTime 2015年8月5日
 * @version 1.0.0
 */
public interface IWechatPayService{

    
    /**
     * </p>统一下单</p>
     * @return
     * @author fanp
     * @date 2015年8月5日
     */
	UnifiedOrderPayResData unifiedOrder(UnifiedOrderPayReqData unifiedOrderPayReqData)throws ServiceException;
    
    /**
     * </p>生成WX app支付Map</p>
     * @param prepayId
     * @return
     * @throws ServiceException
     */
    AppPayReqData generateAppPayParam(UnifiedOrderPayResData unifiedOrderPayResData,String payNo) throws ServiceException;
    
    
    
    /**
	 * 单笔交易支付状态查询
	 * @param payNo
	 * @param WXpayNo
	 * @return false 未支付，true 已支付
	 */
	public Boolean singleTransactionQueryHandelFunction(String payNo,String alipayNo) throws ServiceException;
	
	
	/**
	 * 处理微信回调通知
	 * @param noitfyPayResDataParam
	 * @return 
	 */
	public String handleCallBackOrderHandelFunction(String resultxml) throws ServiceException;
	
	
	/**
	 * 处理微信退款发送请求
	 * @param noitfyPayResDataParam
	 * @return 
	 */
	public String refundOrder(RefundReqData refundReqData);
	
	
	/**
	 * 退款结果查询
	 */
	public String singleRefundqueryHandelFunction(RefundQueryReqData refundQueryReqData);
    
	
}
