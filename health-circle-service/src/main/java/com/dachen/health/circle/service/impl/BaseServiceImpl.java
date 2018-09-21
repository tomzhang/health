package com.dachen.health.circle.service.impl;

import com.dachen.sdk.async.task.AsyncTaskPool;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.db.template.ServiceMongoTemplate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseServiceImpl extends ServiceMongoTemplate {

	@Resource
	protected AsyncTaskPool asyncTaskPool;

	@Autowired
	protected ShortUrlComponent shortUrlComponent;

	protected List<ObjectId> convertToObjectId(List<String> idList) {
		List<ObjectId> objectIdList = new ArrayList<>(idList.size());
		for (int i = 0; i < idList.size(); i++) {
			objectIdList.add(new ObjectId(idList.get(i)));
		}
		return objectIdList;
	}

}
