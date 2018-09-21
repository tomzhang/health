package com.dachen.health.commons.dao;

import com.dachen.health.commons.entity.TelephoneAccount;

public interface TelephoneAccountRepository extends BaseRepository<TelephoneAccount,String>{

	/**
	 * 根据电话号码查找
	 * @param telephone
	 * @return
	 */
	TelephoneAccount findByTelePhone(String telephone);

}
