package com.dachen.health.cate.dao.impl;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.cate.dao.IServiceCategoryDao;
import com.dachen.health.cate.entity.param.ServiceCategoryParam;
import com.dachen.health.cate.entity.po.ServiceCategory;
import com.dachen.util.StringUtil;

@Repository
public class ServiceCategoryDaoImpl extends NoSqlRepository implements IServiceCategoryDao{

	@Override
	public ServiceCategory getServiceCategory(ServiceCategoryParam param) {
		 Query<ServiceCategory> query = dsForRW.createQuery(ServiceCategory.class);
		if(StringUtil.isNoneBlank(param.getId())){
			query.field("_id").equal(new ObjectId(param.getId()));
		}
		
		if(StringUtil.isNoneBlank(param.getGroupId())){
			query.field("groupId").equal(param.getGroupId());
		}
		return query.get();
	}

}
