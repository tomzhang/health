package com.dachen.health.pack.pay.service.impl;

import org.springframework.stereotype.Service;

import com.dachen.commons.logger.LoggerUtils;
import com.tencent.business.RefundBusiness;
import com.tencent.protocol.refund_protocol.RefundResData;

@Service
public class OrderRefundServiceByWxpayImpl implements RefundBusiness.ResultListener{

	@Override
	public void onFailByReturnCodeError(RefundResData refundResData) {
		LoggerUtils.printCommonLog("-------微信退款---------返回错误码错误!");
		
	}

	@Override
	public void onFailByReturnCodeFail(RefundResData refundResData) {
		LoggerUtils.printCommonLog("-------微信退款---------退款失败!");
		
	}

	@Override
	public void onFailBySignInvalid(RefundResData refundResData) {
		LoggerUtils.printCommonLog("-------微信退款---------签名失败!");
		
	}

	@Override
	public void onRefundFail(RefundResData refundResData) {
		LoggerUtils.printCommonLog("-------微信退款---------查询失败!");
		
	}

	@Override
	public void onRefundSuccess(RefundResData refundResData) {
		LoggerUtils.printCommonLog("-------微信退款---------成功!");
		
	}

}
