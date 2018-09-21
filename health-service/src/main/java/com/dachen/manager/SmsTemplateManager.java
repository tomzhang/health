package com.dachen.manager;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.dachen.health.commons.dao.SmsTemplateRepository;

@Component
public class SmsTemplateManager   {

	private static Map<String, String> templateMap = new HashMap<String, String>();

	@Resource
	private SmsTemplateRepository smsRepository;

	public static String getTemplateContent(String key) {
		return templateMap.get(key);
	}

	public static String put(String key,String content) {
		return templateMap.put(key, content);
	}
	


}
