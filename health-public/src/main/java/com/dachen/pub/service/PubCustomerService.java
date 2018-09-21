package com.dachen.pub.service;

import java.util.List;

import com.dachen.pub.model.po.PubPO;
import com.dachen.sdk.exception.HttpApiException;

public interface PubCustomerService {
	
	public void welcome(int userId) throws HttpApiException;
}
