package com.dachen.health.pack.pay.service;



import java.util.Map;

import com.alipay.entity.PayNotifyReqData;
import com.alipay.entity.SingleRefundReqData;
import com.alipay.entity.SingleRefundResData;
import com.dachen.commons.exception.ServiceException;
import com.dachen.sdk.exception.HttpApiException;

/**
 * ProjectName： health-service-trade<br>
 * ClassName： AlipayService<br>
 * Description： 支付宝支付接口<br>
 * @author fanp
 * @createTime 2015年8月5日
 * @version 1.0.0
 */
public interface IAlipayPayService {

	/**
	 * 验证是否为正常的请求
	 * @param request
	 * @return
	 */
	boolean isValidate(Map<String,String> params);

	/**
	 * 处理支付宝请求
	 * @param request
	 * @return PaymentVO
	 */
	public PayNotifyReqData alipayRequsetHandle(Map<String,String> params) throws ServiceException;

	/**
	 * 订单处理
	 * @param paymentVo
	 */
	public void handleCallBackOrderHandelFunction(PayNotifyReqData payNotifyReqData) throws HttpApiException;

	/**
	 * 生成支付宝支付连接
	 * @param payNo 订单编号
	 * @param payFee 订单总金额
	 * @param payTimespam 订单时间
	 * @param body 订单描述
	 * @param subject 订单标题
	 * @return
	 */
	public String takeOrderSignatrue(String payNo,Long payFee,Long payTimespam,String body,String subject);

	/**
	 * 单笔交易支付状态查询
	 * @param payNo
	 * @param alipayNo
	 * @return false 未支付，true 已支付
	 */
	public Boolean singleTransactionQueryHandelFunction(String payNo,String alipayNo) throws ServiceException;

	/**
	 * 获取退款有密接口参数
	 * @param payNo
	 * @param alipayNo
	 * @return false 未支付，true 已支付
	 */
	public SingleRefundResData refundAlipayRequsetHandle(Map<String,String> params) throws ServiceException;

	/**
	 * 处理退款有密平台业务接口
	 * @param payNo
	 * @param alipayNo
	 * @return false 未支付，true 已支付
	 */
	public void handleCallBackRefundOrderHandelFunction(SingleRefundResData singleRefundResData);

	public String takeRefundOrderSignatrue(SingleRefundReqData singleRefundReqData);
}
