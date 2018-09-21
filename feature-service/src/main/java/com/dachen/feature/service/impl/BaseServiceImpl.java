package com.dachen.feature.service.impl;

import com.dachen.sdk.async.task.AsyncTaskPool;
import com.dachen.sdk.db.template.ServiceMongoTemplate;

import javax.annotation.Resource;

public abstract class BaseServiceImpl extends ServiceMongoTemplate {

	@Resource
	protected AsyncTaskPool asyncTaskPool;

}
