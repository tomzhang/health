package com.dachen.health.commons.service.impl;

import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.service.AdminManager;
import com.mongodb.BasicDBObject;

/**
 * @author Cwei
 *
 */
@Service
public class AdminManagerImpl extends NoSqlRepository implements AdminManager {


	@Override
	public BasicDBObject getConfig() {
		return (BasicDBObject) dsForRW.getDB().getCollection("config")
				.findOne();
	}

}
