package com.dachen.health.circle.service.impl;

import com.dachen.sdk.async.task.AsyncTaskPool;
import com.dachen.sdk.db.template.ServiceMongoTemplate;

import javax.annotation.Resource;

public abstract class IntegerBaseServiceImpl extends IntegerServiceMongoTemplate {

	@Resource
	protected AsyncTaskPool asyncTaskPool;

}
