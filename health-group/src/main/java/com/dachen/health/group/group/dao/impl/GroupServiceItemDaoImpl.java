package com.dachen.health.group.group.dao.impl;

import java.util.List;

import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.group.group.dao.IGroupServiceItemDao;
import com.dachen.health.group.group.entity.po.GroupServiceItem;
import com.dachen.util.StringUtil;

@Repository
public class GroupServiceItemDaoImpl extends NoSqlRepository implements IGroupServiceItemDao {

	public List<GroupServiceItem> getByFilter(String groupId, String hospitalId) {
		return getQueryByFilter(groupId, hospitalId, null).asList();
	}
	
	public GroupServiceItem getByFilter(String groupId, String hospitalId, String serviceItemId) {
		return getQueryByFilter(groupId, hospitalId, serviceItemId).get();
	}
	
	public Query<GroupServiceItem> getQueryByFilter(String groupId, String hospitalId, String serviceItemId) {
		Query<GroupServiceItem> q = dsForRW.createQuery(GroupServiceItem.class);
		if (StringUtil.isNotBlank(groupId)) {
			q.filter("groupId", groupId);
		}
		if (StringUtil.isNotBlank(hospitalId)) {
			q.filter("hospitalFee.hospitalId", hospitalId);
		}
		if (StringUtil.isNotBlank(serviceItemId)) {
			q.filter("serviceItemId", serviceItemId);
		}
		return q;
	}
	
}
