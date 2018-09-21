package com.dachen.health.commons.dao.mongo;

import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import com.dachen.health.commons.dao.TelephoneAccountRepository;
import com.dachen.health.commons.entity.TelephoneAccount;

@Repository
public class TelephoneAccountRepositoryImpl extends BaseRepositoryImpl<TelephoneAccount, String>
		implements TelephoneAccountRepository {

	@Override
	public TelephoneAccount findByTelePhone(String telephone) {
		Query<TelephoneAccount>	 query=dsForRW.createQuery(TelephoneAccount.class);
		query.filter("telephone", telephone);
		TelephoneAccount account=query.get();
		return account;
	}

}
