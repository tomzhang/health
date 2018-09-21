package com.dachen.health.pack.pay.service.impl;


import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.dachen.sdk.exception.HttpApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.entity.AppPayReqData;
import com.alipay.entity.PayNotifyReqData;
import com.alipay.entity.SingleRefundReqData;
import com.alipay.entity.SingleRefundResData;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.lock.RedisLock;
import com.dachen.commons.lock.RedisLock.LockType;
import com.dachen.health.commons.constants.AccountEnum;
import com.dachen.health.commons.constants.PackConstants;
import com.dachen.health.pack.account.entity.param.RechargeParam;
import com.dachen.health.pack.account.entity.vo.RechargeVO;
import com.dachen.health.pack.account.service.IRechargeService;
import com.dachen.health.pack.order.entity.po.Refund;
import com.dachen.health.pack.order.entity.po.RefundExample;
import com.dachen.health.pack.order.mapper.RefundMapper;
import com.dachen.health.pack.order.service.IOrderRefundService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.pay.entity.PaymentVO;
import com.dachen.health.pack.pay.service.IAlipayPayService;
import com.dachen.health.pack.pay.service.IPayHandleBusiness;
import com.dachen.util.StringUtil;

/**
 * ProjectName： health-service-trade<br>
 * ClassName： AlipayPayServiceImpl<br>
 * Description：支付宝支付接口 <br>
 * 
 * @author fanp
 * @createTime 2015年8月5日
 * @version 1.0.0
 */
@Service
public class AlipayPayServiceImpl implements IAlipayPayService {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IOrderService orderService;
	
	@Autowired
    private IRechargeService rechargeService;
    
    @Autowired
    private IPayHandleBusiness payHandleBusiness;
    
    @Autowired
    private RefundMapper refundMapper;
    
    @Autowired
    private IOrderRefundService orderRefundService;
    
	/**
	 * 验证请求是否合法
	 */
	public boolean isValidate(Map<String,String> params) {
		return AlipayNotify.verify(params);
	}
	
	/**
	 * 获取支付宝回调参数
	 */
	public PayNotifyReqData alipayRequsetHandle(Map<String,String> params) throws ServiceException {
		
		PayNotifyReqData payNotfyReqData = new PayNotifyReqData();
		try {
			payNotfyReqData.setAppid(new String(params.get("seller_id").getBytes("ISO-8859-1"),"utf-8"));
			payNotfyReqData.setOut_trade_no(new String(params.get("out_trade_no").getBytes("ISO-8859-1"),"utf-8"));
			payNotfyReqData.setTrade_no(new String(params.get("trade_no").getBytes("ISO-8859-1"),"utf-8"));
			payNotfyReqData.setTrade_status(new String(params.get("trade_status").getBytes("ISO-8859-1"),"utf-8"));
			payNotfyReqData.setTotal_fee(new String(params.get("total_fee").getBytes("ISO-8859-1"),"utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("支付宝转码错误", e.fillInStackTrace());
			logger.error(e.getMessage(), e);
			throw new ServiceException("支付宝通知获取参数转码异常");
		}
		
		return payNotfyReqData;
	}
	
	/**
	 * 获取支付宝退款回调参数
	 */
	public SingleRefundResData refundAlipayRequsetHandle(Map<String,String> params) throws ServiceException {
		
		SingleRefundResData singleRefundResData = new SingleRefundResData();
		try {
			singleRefundResData.setNotify_id(new String(params.get("notify_id").getBytes("ISO-8859-1"),"utf-8"));
			singleRefundResData.setNotify_type(new String(params.get("notify_type").getBytes("ISO-8859-1"),"utf-8"));
			singleRefundResData.setResult_details(new String(params.get("result_details").getBytes("ISO-8859-1"),"utf-8") );
			singleRefundResData.setBatch_no(new String(params.get("batch_no").getBytes("ISO-8859-1"),"utf-8"));
			singleRefundResData.setSuccess_num(new String(params.get("success_num").getBytes("ISO-8859-1"),"utf-8"));
			singleRefundResData.setSign_type(new String(params.get("sign_type").getBytes("ISO-8859-1"),"utf-8"));
			singleRefundResData.setNotify_time(new String(params.get("notify_time").getBytes("ISO-8859-1"),"utf-8"));
			singleRefundResData.setSign(new String(params.get("sign").getBytes("ISO-8859-1"),"utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("支付宝转码错误", e.fillInStackTrace());
			throw new ServiceException("支付宝通知获取参数转码异常");
		}
		
		return singleRefundResData;
	}
	
	
	public void handleCallBackRefundOrderHandelFunction(SingleRefundResData singleRefundResData) {
		
		//业务处理。。。
		RefundExample example = new RefundExample();
		example.createCriteria().andRefundNoEqualTo(singleRefundResData.getBatch_no());
		List<Refund> refunds = refundMapper.selectByExample(example);
		if (refunds.isEmpty()) {
			logger.error("找不到对应的批次号{}", singleRefundResData.getBatch_no());
			throw new ServiceException("找不到对应的批次号");
		}
		if (singleRefundResData.getSuccess_num().equals(String.valueOf(refunds.size()))) {
			for (Refund refund : refunds) {
				orderRefundService.refundSuccess(refund.getId(), refund.getOrderId(), singleRefundResData.getResult_details());
			}
		} else {
			//目前还不支持批量退款 
			orderRefundService.refundFail(refunds.get(0).getId(), refunds.get(0).getOrderId(), singleRefundResData.getResult_details());
			logger.error("支付宝退款失败：{}", singleRefundResData.getResult_details());
		}
	}
	
	
	
	/**
	 * 支付宝通知处理业务
	 */
	public void handleCallBackOrderHandelFunction(PayNotifyReqData payNotifyReqData) throws ServiceException, HttpApiException {
		String tag = "handleCallBackOrderHandelFunction";
		logger.info("{}. payNotifyReqData={}", tag, payNotifyReqData);

		PaymentVO paymentVo = new PaymentVO();
		paymentVo.setPartner(payNotifyReqData.getAppid());
		paymentVo.setTradeStatus(payNotifyReqData.getTrade_status());
		paymentVo.setPayNo(payNotifyReqData.getOut_trade_no());
		paymentVo.setPayAlftNo(payNotifyReqData.getTrade_no());
		//测试数据用
		Integer tol_fee = 0;
		try {
			tol_fee = Integer.valueOf(payNotifyReqData.getTotal_fee()); 
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			tol_fee =0;
		}
		
		paymentVo.setPaymentMoney(tol_fee);
		paymentVo.setPayType(AccountEnum.PayType.alipay.getIndex());

		Boolean ret = singleTransactionQueryHandelFunction(paymentVo.getPayNo(),payNotifyReqData.getTrade_no());
		logger.info("{}. singleTransactionQueryHandelFunction. ret={}", tag, ret);
		if(ret) {
			RedisLock lock=new RedisLock();
			if(StringUtil.isBlank(payNotifyReqData.getOut_trade_no())) {
				throw new ServiceException(300,"订单交易号为空!");
			}
			RechargeParam param = new RechargeParam();
			param.setPayNo(paymentVo.getPayNo());
			RechargeVO recharegVo =  rechargeService.findRechargeByPayNo(param);
			logger.info("{}. recharegVo={}", tag, recharegVo);
			if(recharegVo==null) {
				throw new ServiceException(300,"找不到交易记录");
			}
			try {
				logger.info("{}. paymentVo.getTradeStatus()={}", tag, paymentVo.getTradeStatus());
				if(PackConstants.AIL_WAIT_BUYER_PAY.equals(paymentVo.getTradeStatus())) {
					throw new ServiceException(200,"暂不处理支付宝该类型请求！");
				}
				
				boolean locked=lock.lock(recharegVo.getUserId()+"_"+recharegVo.getPayNo(), LockType.orderpay);
				logger.info("{}. locked={}", tag, locked);
				if(locked) {
					try{
						payHandleBusiness.handlePayBusinessLogic(paymentVo);
						lock.unlock(recharegVo.getUserId()+"_"+recharegVo.getPayNo(), LockType.orderpay);
					}catch(Exception e) {
						logger.error(e.getMessage(), e);
						lock.unlock(recharegVo.getUserId()+"_"+recharegVo.getPayNo(), LockType.orderpay);
						throw e;
					}
				}else {
					throw new ServiceException(300,"未获取到订单交易号锁"+paymentVo.getPayNo()+"处理失败!");
				}
			}catch(ServiceException e) {
				logger.error("handlePayBusinessLogic出现错误:" + e.getResultCode(), e);
				logger.error(e.getMessage(), e);
				if(e.getResultCode()==200) {
					throw new ServiceException(200,e.getMessage());
				}else {
					throw new ServiceException(300,e.getMessage());
				}
				
			}/*catch(Exception e) {
				throw e
			}*/
		}else {
			throw new ServiceException(300,"通过支付宝服务器验证订单号:"+paymentVo.getPayNo()+"失败!");
		}
	}
	
	/**
	 * 预生成支付宝APP支付链接
	 */
	public String takeOrderSignatrue(String payNo, Long payFee, Long payTimespam, String body, String subject) {
		try {
			AppPayReqData appPayReqData = new AppPayReqData(payNo, payFee.intValue(), body, subject, payTimespam);
			String payLinkParam = appPayReqData.toPayToLinkString();
			// TODO 打印支付宝支付日志
			// LoggerUtils.printPayLog(payLinkParam, Integer.valueOf(payNo),
			// LoggerUtils.PayType.param);
			return payLinkParam;
		} catch (Exception ex) {
			throw new ServiceException("生成签名失败!");
		}
	}
	
	/**
	 * 预生成支付宝APP退款链接
	 */
	public String takeRefundOrderSignatrue(SingleRefundReqData singleRefundReqData) {
		try {
//			return AlipaySubmit.buildRequestGet(singleRefundReqData.toMap());
			return AlipaySubmit.buildRequest(singleRefundReqData.toMap(), "post", "确认");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ServiceException("请求支付宝有密退款失败！");
		}
	}
	
	/**
	 * 查询支付宝单笔交易状态
	 */
	public Boolean singleTransactionQueryHandelFunction(String payNo,String alipayNo) throws ServiceException {
		return AlipaySubmit.doaliPayQueryLoop(3,payNo,alipayNo);
	}
}
