package com.dachen.commons.component;

import org.springframework.stereotype.Component;

import com.dachen.sdk.api.ApiClientConfig;
import com.dachen.util.PropertiesUtil;

@Component
public class AppConfig implements ApiClientConfig {

	@Override
	public String getLocalAppName() {
		return "health";
	}

	@Override
	public String getToken(String appName) {
		return this.getKeyValue(String.format("api.%s.token", appName));
	}

	@Override
	public String getServerUrl(String appName) {
		return this.getKeyValue(String.format("api.%s.baseurl", appName));
	}
	
	public String getKeyValue(String key) {
		return PropertiesUtil.getContextProperty(key);
	}

}
