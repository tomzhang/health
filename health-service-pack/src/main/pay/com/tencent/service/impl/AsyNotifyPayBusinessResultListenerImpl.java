package com.tencent.service.impl;

import com.tencent.business.AsyNotifyPayBusiness.ResultListener;
import com.tencent.protocol.pay_protocol.NoitfyPayResData;

public class AsyNotifyPayBusinessResultListenerImpl implements ResultListener{

	public Boolean onFail() {
		
		return false;
	}

	@Override
	public Boolean onSuccess(NoitfyPayResData noitfyPayResDataParam,
			String transactionID) {
		
		return true;
	}

}
