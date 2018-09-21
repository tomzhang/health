package com.dachen.health.activity.invite.service.impl;

import com.dachen.sdk.async.task.AsyncTaskPool;
import javax.annotation.Resource;

public abstract class IntegerBaseServiceImpl extends IntegerServiceMongoTemplate {

	@Resource
	protected AsyncTaskPool asyncTaskPool;

}
