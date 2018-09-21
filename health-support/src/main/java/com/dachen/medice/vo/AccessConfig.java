package com.dachen.medice.vo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 接入配置类
 * @author tanyulin
 *
 */
@RefreshScope
@Component
@ConfigurationProperties(prefix = "access")
public class AccessConfig {
	
	/**
	 * userAgent,webAgent
	 */
	private String agent;
	
	/**
	 * heaer origin域
	 */
	private String origin;

	/**
	 * 签名前缀
	 */
	private String signPrefix;
	
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getSignPrefix() {
		return signPrefix;
	}
	public void setSignPrefix(String signPrefix) {
		this.signPrefix = signPrefix;
	}

	
}
