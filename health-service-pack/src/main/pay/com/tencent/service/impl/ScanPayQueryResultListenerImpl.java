package com.tencent.service.impl;

import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.tencent.business.ScanPayQueryBusiness.ResultListener;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryResData;

@Service
public class ScanPayQueryResultListenerImpl implements ResultListener{

	
	
	public void onFailByQuerySignInvalid(ScanPayQueryResData scanPayQueryResData) throws ServiceException {
		throw new ServiceException("微信交易订单查询返回数据签名错误");
		
	}
	
	public Boolean onFail() {
		return false;
	}

	public Boolean onSuccess(String transactionID) {
		return true;
	}

}
