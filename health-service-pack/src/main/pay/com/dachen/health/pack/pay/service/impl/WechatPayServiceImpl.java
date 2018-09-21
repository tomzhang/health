package com.dachen.health.pack.pay.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.lock.RedisLock;
import com.dachen.commons.lock.RedisLock.LockType;
import com.dachen.health.commons.constants.AccountEnum;
import com.dachen.health.commons.constants.PackConstants;
import com.dachen.health.pack.account.entity.param.RechargeParam;
import com.dachen.health.pack.account.entity.vo.RechargeVO;
import com.dachen.health.pack.account.service.IRechargeService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.pay.entity.PaymentVO;
import com.dachen.health.pack.pay.service.IPayHandleBusiness;
import com.dachen.health.pack.pay.service.IWechatPayService;
import com.dachen.util.StringUtil;
import com.tencent.WXPay;
import com.tencent.business.AsyNotifyPayBusiness;
import com.tencent.business.ScanPayQueryBusiness;
import com.tencent.common.Signature;
import com.tencent.common.Util;
import com.tencent.protocol.pay_protocol.AppPayReqData;
import com.tencent.protocol.pay_protocol.NoitfyPayResData;
import com.tencent.protocol.pay_protocol.UnifiedOrderPayReqData;
import com.tencent.protocol.pay_protocol.UnifiedOrderPayResData;
import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_query_protocol.RefundQueryReqData;
import com.tencent.service.impl.AsyNotifyPayBusinessResultListenerImpl;
import com.tencent.service.impl.ScanPayQueryResultListenerImpl;

/**
 * ProjectName： health-service-trade<br>
 * ClassName： WechatPayServiceImpl<br>
 * Description： 微信支付接口<br>
 * 
 * @author xieP
 * @createTime 2015年8月5日
 * @version 1.0.0
 */
@Service
public class WechatPayServiceImpl implements IWechatPayService {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected IOrderService orderService;
	
	@Autowired
	protected IRechargeService rechargeService;
    
    @Autowired
	protected IPackService packService;
    
    @Autowired
	protected IPayHandleBusiness payHandleBusiness;
    
    @Autowired
    OrderRefundServiceByWxpayImpl resultListener;
    
    public String refundOrder(RefundReqData refundReqData) {
    	try {
			return WXPay.doRefundBusiness(refundReqData, resultListener);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
    	return "";
    }
    /**
     * </p>统一下单</p>
     * @return
     * @author peiX
     * @date 2015年8月19日
     */
    public UnifiedOrderPayResData unifiedOrder(UnifiedOrderPayReqData unifiedOrderPayReqData) throws ServiceException{
		//TODO 记录请求日志
		String payServiceResponseString;
		try {
			payServiceResponseString = WXPay.requestUnifiedOrderPayService(unifiedOrderPayReqData);
			logger.info("unifiedOrderPay return result :{}", payServiceResponseString);
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			throw new ServiceException("统一下单订单参数不合法");
		}
		//将从API返回的XML数据映射到Java对象
        UnifiedOrderPayResData unifiedOrderPayResData = (UnifiedOrderPayResData) Util.getObjectFromXML(payServiceResponseString, UnifiedOrderPayResData.class);
        
        if (unifiedOrderPayResData == null || unifiedOrderPayResData.getReturn_code() == null) {
            throw new ServiceException("统一下单订单参数不合法");
        }
        
        if("SUCCESS".equals(unifiedOrderPayResData.getReturn_code())) {
        	try {
				if (!Signature.checkIsSignValidFromResponseString(payServiceResponseString)) {
					throw new ServiceException("交易订单签名验证失败");
				}
			} catch (Exception e) {
				throw new ServiceException("交易订单签名验证失败"); 
			} 
        	
        	if("SUCCESS".equals(unifiedOrderPayResData.getResult_code())) {
        		String prepay_id = unifiedOrderPayResData.getPrepay_id();
                if(prepay_id != null){
             	   return unifiedOrderPayResData;
                }
                throw new ServiceException("交易未获取到微信支付订单号!");
        	}else {
        		 throw new ServiceException(unifiedOrderPayResData.getReturn_msg());
        	}
        }else {
        	throw new ServiceException(unifiedOrderPayResData.getReturn_msg());
        }
    }

	/**
	 * 生成APP支付对象
	 */
	public AppPayReqData generateAppPayParam(UnifiedOrderPayResData unifiedOrderPayResData,String payNo)
			throws ServiceException {
		
		AppPayReqData appPayReqData = new AppPayReqData(unifiedOrderPayResData.getPrepay_id(), Util.getTimeStamp());
		//TODO 生成微信支付数据 
		//LoggerUtils.printPayLog(appPayReqData.toMap().toString(),payNo,LoggerUtils.PayType.param);
		
		return appPayReqData;
	}
	
	/**
	 * 提供微信支付查询方法
	 */
	public Boolean singleTransactionQueryHandelFunction(String payNo,
			String transactionId) throws ServiceException {
		//TODO 在下面业务逻辑可以处理订单交易状态
		
		if(null==payNo && null ==transactionId) {
			throw new ServiceException("交易查询订单号不能为空");
		}
		
		try {
			ScanPayQueryBusiness.ResultListener resultListener= new ScanPayQueryResultListenerImpl();
			if(WXPay.doScanPayQueryBusiness(payNo,transactionId ,resultListener)){
				return true;
			}else {
				return false;
			}
			
		} catch (Exception e) {
			logger.error("微信服务器查询订单失败!" + e.getMessage(), e);
			return false;
		}
		
	}
	
	/**
	 * 获取微信通知参数业务处理方法
	 */
	public String handleCallBackOrderHandelFunction(String resultXml) throws ServiceException {
		String tag = "handleCallBackOrderHandelFunction";
		logger.info("{}. resultXml={}", tag, resultXml);
		try {
			
			if(null == resultXml) {
				throw new ServiceException("未获取微信支付通知信息！");
			}
			
			NoitfyPayResData noitfyPayResDataParam = (NoitfyPayResData)Util.getObjectFromXML(resultXml,NoitfyPayResData.class);
			logger.debug("{}. noitfyPayResDataParam={}", tag, noitfyPayResDataParam);

			AsyNotifyPayBusiness.ResultListener resultListener = new AsyNotifyPayBusinessResultListenerImpl();
			
			if(!singleTransactionQueryHandelFunction(noitfyPayResDataParam.getOut_trade_no(),noitfyPayResDataParam.getTransaction_id())) {
				return Util.setXML(Util.FAIL, "未确认微信支付服务器用户真实支付的订单");
			}
			
			if(WXPay.doNoitfyPayBusiness(noitfyPayResDataParam,resultXml,resultListener)) {
				PaymentVO paymentVo = new PaymentVO();
				paymentVo.setPartner(noitfyPayResDataParam.getAppid());
				paymentVo.setPayNo(noitfyPayResDataParam.getOut_trade_no());
				paymentVo.setPaymentMoney(Integer.valueOf(noitfyPayResDataParam.getTotal_fee()));
				paymentVo.setPayType(AccountEnum.PayType.wechat.getIndex());
				paymentVo.setTradeStatus(PackConstants.AIL_TRADE_SUCCESS);
				paymentVo.setPayAlftNo(noitfyPayResDataParam.getTransaction_id());
				paymentVo.setIsSuccess(true);
				
				RedisLock lock=new RedisLock();
				if(StringUtil.isBlank(noitfyPayResDataParam.getOut_trade_no())) {
					throw new ServiceException(300,"订单交易号为空!");
				}
				RechargeParam param = new RechargeParam();
				param.setPayNo(paymentVo.getPayNo());
				RechargeVO recharegVo = rechargeService.findRechargeByPayNo(param);
				logger.debug("{}. recharegVo={}", tag, recharegVo);
				if(recharegVo==null || recharegVo.getPayNo()==null) {
					throw new ServiceException(300,"找不到交易记录");
				}
				try {
					boolean locked=lock.lock(recharegVo.getUserId()+"_"+recharegVo.getPayNo(), LockType.orderpay);
					if(locked) {
						try {
							payHandleBusiness.handlePayBusinessLogic(paymentVo);
							lock.unlock(recharegVo.getUserId()+"_"+recharegVo.getPayNo(), LockType.orderpay);
						}catch(Exception e) {
							logger.error(e.getMessage(), e);
							lock.unlock(recharegVo.getUserId()+"_"+recharegVo.getPayNo(), LockType.orderpay);
							throw e;
						}
					}else {
						throw new ServiceException(300,"未获取到订单交易号锁,订单交易号："+paymentVo.getPayNo()+"处理失败!");
					}
					
				}catch(ServiceException e) {
					logger.error(e.getMessage(), e);
					if(e.getResultCode()==200) {
						return Util.setXML(Util.SUCCESS, "OK");
					}else {
						logger.error("错误代码:"+e.getResultCode()+",错误信息:"+e.getMessage(), e);
						return Util.setXML(Util.FAIL, e.getMessage());
					}
				}catch(Exception e) {
					logger.error(e.getMessage(), e);
					return Util.setXML(Util.FAIL, e.getMessage());
				}
				return Util.setXML(Util.SUCCESS, "OK");
			}else {
				return Util.setXML(Util.FAIL, "检验微信通知业务有错!");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Util.setXML(Util.FAIL, e.getMessage());
		}
		
	}
	@Override
	public String singleRefundqueryHandelFunction(RefundQueryReqData refundQueryReqData) {
		try {
			return WXPay.requestRefundQueryService(refundQueryReqData);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
    
}
