package com.dachen.health.community.dao.impl;

import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import com.dachen.health.community.dao.ICommunityUsersDao;
import com.dachen.health.community.entity.po.CommunityUsers;
@Repository
public class CommunityUsersDaoImpl extends BaseDaoImpl<CommunityUsers> implements ICommunityUsersDao{

	@Override
	public CommunityUsers getBygroupId(Integer userId, String groupId) {
		Query<CommunityUsers> q = dsForRW.createQuery(CommunityUsers.class);
		q.filter("userId", userId);
		q.filter("groupId", groupId);
		return q.get();
	}

}
