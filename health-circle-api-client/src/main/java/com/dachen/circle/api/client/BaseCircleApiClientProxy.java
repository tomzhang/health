package com.dachen.circle.api.client;

import com.dachen.sdk.spring.cloud.AbstractRemoteServiceClientProxy;
import com.dachen.sdk.util.SdkJsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

public abstract class BaseCircleApiClientProxy extends AbstractRemoteServiceClientProxy {
	
	@Override
	public String getAppName() {
		return "health";
	}

	@Override
	public String getApiRequestDir() {
		return "api";
	}

	protected void putIfNotBlank(Map<String, String> params, String key, String val) {
		if (null == val || "".equals(val.toString().trim())) {
			return;
		}
		params.put(key, val.toString());
	}
	
	protected void putIfNotBlank(Map<String, String> params, String key, Long val) {
		if (null == val) {
			return;
		}
		params.put(key, val.toString());
	}
	
	protected void putIfNotBlank(Map<String, String> params, String key, Integer val) {
		if (null == val) {
			return;
		}
		params.put(key, val.toString());
	}
	
	protected void putIfNotBlank(Map<String, String> params, String key, Boolean val) {
		if (null == val) {
			return;
		}
		params.put(key, val.toString());
	}
	
	protected void putJsonStrIfNotBlank(Map<String, String> params, String key, Map<?, ?> val) {
		if (null == val || "".equals(val.toString().trim())) {
			return;
		}
		params.put(key, SdkJsonUtils.toJSONString(val));
	}
	
	protected <T> void putJsonStrIfNotBlank(Map<String, String> params, String key, T val) {
		if (null == val) {
			return;
		}
		params.put(key, SdkJsonUtils.toJSONString(val));
	}
	
	protected void putArrayIfNotBlank(Map<String, String> params, String key, Collection<?> list) {
		if (null == list || list.isEmpty()) {
			return;
		}
		params.put(key, StringUtils.join(list, ","));
	}
	

}
