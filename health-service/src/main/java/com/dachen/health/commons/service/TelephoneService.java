package com.dachen.health.commons.service;

import com.dachen.health.commons.entity.TelephoneAccount;

public interface TelephoneService {
	/**
	 * 
	 * @param telephone
	 * @return
	 */
	
	TelephoneAccount findByTelePhone(String telephone);
	
}
