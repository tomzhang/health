package com.dachen.health.community.dao.impl;

import java.util.List;

import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import com.dachen.health.commons.constants.CommunityEnum;
import com.dachen.health.community.dao.ICircleDao;
import com.dachen.health.community.entity.po.Circle;
@Repository
public class CircleDaoImpl  extends BaseDaoImpl<Circle> implements ICircleDao{

	@Override
	public List<Circle> getByGroupCircle(String groupId) {
		Query<Circle> q = dsForRW.createQuery(Circle.class);
		q.filter("groupId", groupId);
		q.filter("state", CommunityEnum.CommunityCircleState.正常.getIndex());
		q.order("-main,-top,_id");
		return q.asList();
	}

	@Override
	public Circle firstCircle(String groupId) {
		Query<Circle> q = dsForRW.createQuery(Circle.class);
		q.filter("groupId", groupId);
		q.filter("main exists", 0);
		q.filter("state", CommunityEnum.CommunityCircleState.正常.getIndex());
		q.order("-top");
		return q.get();
	}
	@Override
	public Circle upCircle(String groupId,Long top) {
		Query<Circle> q = dsForRW.createQuery(Circle.class);
		q.filter("groupId", groupId);
		q.filter("state", CommunityEnum.CommunityCircleState.正常.getIndex());
		q.filter("top >", top);
		q.order("top");
		return q.get();
	}
	@Override
	public Circle nextCircle(String groupId,Long top) {
		Query<Circle> q = dsForRW.createQuery(Circle.class);
		q.filter("groupId", groupId);
		q.filter("state", CommunityEnum.CommunityCircleState.正常.getIndex());
		q.filter("top <", top);
		q.order("-top");
		return q.get();
	}

	@Override
	public Circle mainCircle(String groupId) {
		Query<Circle> q = dsForRW.createQuery(Circle.class);
		q.filter("groupId", groupId);
		q.filter("state", CommunityEnum.CommunityCircleState.正常.getIndex());
		q.filter("main", "1");
		return q.get();
	}

	@Override
	public Circle getByNameCircle(String groupId, String name) {
		Query<Circle> q = dsForRW.createQuery(Circle.class);
		q.filter("groupId", groupId);
		q.filter("state", CommunityEnum.CommunityCircleState.正常.getIndex());
		q.filter("name",name);
		return q.get();
	}

}
