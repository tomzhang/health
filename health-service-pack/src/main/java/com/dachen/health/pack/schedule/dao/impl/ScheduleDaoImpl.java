package com.dachen.health.pack.schedule.dao.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.pack.schedule.dao.IScheduleDao;
import com.dachen.health.pack.schedule.entity.po.Schedule;
import com.dachen.health.pack.schedule.entity.vo.ScheduleParam;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
@Repository
public class ScheduleDaoImpl extends NoSqlRepository implements IScheduleDao {

	@Override
	public Schedule save(Schedule schedule) {
		String id = dsForRW.insert(schedule).getId().toString();
		return dsForRW.createQuery(Schedule.class).field("_id").equal(new ObjectId(id)).get();
	}

	@Override
	public boolean update(Schedule schedule) {
		DBObject update = new BasicDBObject();
		if (schedule.getScheduleTime() != null) {
			update.put("scheduleTime", schedule.getScheduleTime());
		}
		if (schedule.getSendTime() != null) {
			update.put("sendTime", schedule.getSendTime());
		}
		
		DBObject query = new BasicDBObject();
		query.put("_id", schedule.getId());
		
		WriteResult wr = dsForRW.getDB().getCollection("t_schedule").update(query, new BasicDBObject("$set", update));
		if (wr != null && wr.isUpdateOfExisting()) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean updateByCareItemId(String careItemId, Long scheduleTime) {
		DBObject update = new BasicDBObject();
		update.put("scheduleTime", scheduleTime);
		
		DBObject query = new BasicDBObject();
		query.put("careItemId", careItemId);
		
		WriteResult wr = dsForRW.getDB().getCollection("t_schedule").updateMulti(query, new BasicDBObject("$set", update));
		if (wr != null && wr.isUpdateOfExisting()) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean updateByRelationId(String relationId, Long scheduleTime) {
		DBObject update = new BasicDBObject();
		update.put("scheduleTime", scheduleTime);
		
		DBObject query = new BasicDBObject();
		query.put("relationId", relationId);
		
		WriteResult wr = dsForRW.getDB().getCollection("t_schedule").updateMulti(query, new BasicDBObject("$set", update));
		if (wr != null && wr.isUpdateOfExisting()) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean updateByRelationId(String relationId, Integer status) {
		DBObject update = new BasicDBObject();
		update.put("status", status);
		
		DBObject query = new BasicDBObject();
		query.put("relationId", relationId);
		
		WriteResult wr = dsForRW.getDB().getCollection("t_schedule").updateMulti(query, new BasicDBObject("$set", update));
		if (wr != null && wr.isUpdateOfExisting()) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean delete(String id) {
		WriteResult wr = dsForRW.delete(Schedule.class, new ObjectId(id));
		if (wr != null && wr.isUpdateOfExisting())
			return true;
		return false;
	}
	
	public Schedule getByRelationId(String relationId) {
		return dsForRW.createQuery(Schedule.class).field("relationId").equal(relationId).get();
	}
	
	public Schedule getByCareItemId(String careItemId, Long deadline) {
		return dsForRW.createQuery(Schedule.class).filter("careItemId", careItemId).filter("scheduleTime >=", deadline).get();
	}
	
	public Query<Schedule> getSchedules(ScheduleParam param) {
		Query<Schedule> q = dsForRW.createQuery(Schedule.class);
		if (param.getStartDate() != null) {
			q.field("scheduleTime").greaterThanOrEq(param.getStartDate());
		}
		if (param.getEndDate() != null) {
			q.field("scheduleTime").lessThan(param.getEndDate());
		}
		if (param.getUserId() != null) {
			q.field("userId").equal(param.getUserId());
		}
		if (param.getType() != null) {
			q.field("type").equal(param.getType().getIndex());
		}
		if (param.getRelationIds() != null && !param.getRelationIds().isEmpty()) {
			q.filter("relationId in", param.getRelationIds());
		}
		q.order("scheduleTime");
		return q;
	}
	
	public List<Schedule> getSendSchedule(Long leftTime, Long rigthTime) {
		Query<Schedule> q = dsForRW.createQuery(Schedule.class);
		q.or(q.criteria("sendTime").doesNotExist(), q.criteria("sendTime").equal(0));
		q.field("scheduleTime").greaterThanOrEq(leftTime);
		q.field("scheduleTime").lessThanOrEq(rigthTime);
		return q.asList();
	}
	public List<Schedule> getSendSchedule(Long leftTime,Long rigthTime,String title) {
		Query<Schedule> q = dsForRW.createQuery(Schedule.class);
		q.field("title").equal(title);
		//q.field("scheduleTime").equal(scheduleTime);
		q.or(q.criteria("sendTime").doesNotExist(), q.criteria("sendTime").equal(0));
		q.field("scheduleTime").greaterThanOrEq(leftTime);
		q.field("scheduleTime").lessThanOrEq(rigthTime);
		return q.asList();
	}

	@Override
	public Query<Schedule> getSchedulesCount(ScheduleParam param) {
		Query<Schedule> q = dsForRW.createQuery(Schedule.class);
		if (param.getUserId() != null) {
			q.field("userId").equal(param.getUserId());
		}
		if (param.getType() != null) {
			q.field("type").equal(param.getType().getIndex());
		}
		if (param.getRelationIds() != null && !param.getRelationIds().isEmpty()) {
			q.filter("relationId in", param.getRelationIds());
		}
		return q;
	}

}
