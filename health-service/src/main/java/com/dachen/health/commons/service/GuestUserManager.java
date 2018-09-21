package com.dachen.health.commons.service;

import java.util.Map;

public interface GuestUserManager {
	/**
	 * 用于获取临时用户token
	 * @param deviceID
	 * @return
	 * @author liming
	 */
	public Map<String,Object> getGuestToken(String deviceID,String guest_token);
}
