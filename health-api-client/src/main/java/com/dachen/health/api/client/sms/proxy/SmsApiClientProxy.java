package com.dachen.health.api.client.sms.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class SmsApiClientProxy extends HealthApiClientProxy {

	/**
	 * 往指定号码发送短信内容
	 * 
	 * @param phone
	 * @param content
	 * @return
	 * @throws HttpApiException
	 */
	public Boolean send(String phone, String content) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(1);
		params.put("content", content.toString());
		try {
			String url = "sms/send/{phone}";
			Boolean ret = this.postRequest(url, params, Boolean.class, phone.toString());
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 往指定号码列表发送短信内容
	 * 
	 * @param phones
	 * @param content
	 * @return 成功个数
	 * @throws HttpApiException
	 */
	public Integer sendPhones(List<String> phones, String content) throws HttpApiException {
		if (null == phones || 0 == phones.size()) {
			return 0;
		}
		Map<String, String> params = new HashMap<String, String>(2);
		putArrayIfNotBlank(params, "phones", phones);
		params.put("content", content.toString());
		try {
			String url = "sms/sendPhones";
			Integer ret = this.postRequest(url, params, Integer.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
