package com.dachen.health.controller.api.sms;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.web.ApiBaseController;
import com.mobsms.sdk.MobSmsSdk;

@RestController
@RequestMapping("/api/sms")
public class ApiSmsController extends ApiBaseController {

	@Resource
	protected MobSmsSdk mobSmsSdk;

	@RequestMapping(value = "send/{phone}", method = RequestMethod.POST)
	public JSONMessage send(@PathVariable String phone, @RequestParam String content) {
		String tag = "send/{phone}";
		logger.info("{}. phone={}, content={}", tag, phone, content);
		Map<String, String> ret = mobSmsSdk.send(phone, content);
		logger.info("{}. ret={}", tag, ret);
		return JSONMessage.success(null, true);
	}
	
	@RequestMapping(value = "sendPhones", method = RequestMethod.POST)
	public JSONMessage sendBatch(@RequestParam String[] phones, @RequestParam String content) {
		String tag = "sendPhones";
		logger.info("{}. phones={}, content={}", tag, phones, content);
		int count = phones.length;
		for (String phone:phones) {
			Map<String, String> ret = mobSmsSdk.send(phone, content);
			logger.info("{}. ret={}", tag, ret);	
		}
		
		return JSONMessage.success(null, count);
	}
}
