package com.dachen.health.meeting.dao.mongo;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.meeting.dao.MeetingDao;
import com.dachen.health.meeting.po.Meeting;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Repository
public class MeetingDaoImpl extends NoSqlRepository implements MeetingDao {

	@Override
	public Meeting insertMeeting(Meeting meeting) {
		String id = dsForRW.insert(meeting).getId().toString();
		return getMeetingById(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Long getDbAttendeesCount(Long startTime,Long endTime,String id) {
		Query<Meeting> q = dsForRW.createQuery(Meeting.class);
		
		q.or(
				q.criteria("startTime").lessThanOrEq(startTime).criteria("endTime").greaterThan(startTime),
				q.criteria("startTime").lessThan(endTime).criteria("endTime").greaterThanOrEq(endTime)
			);
		q.field("isStop").equal(0);
		if(StringUtils.isNotBlank(id)){
			q.field("_id").notEqual(new ObjectId(id));
		}
		
		String reduce = "function(doc,aggr){"
					+ 		"aggr.total += doc.attendeesCount;"
					+ 	 "}";
		DBObject result = dsForRW.getCollection(Meeting.class)
				.group(new BasicDBObject(), 
							q.getQueryObject(),
							new BasicDBObject("total",0), 
							reduce);
		Map<String,BasicDBObject> map = result.toMap();
		if(map.size() > 0){
			BasicDBObject dbObj = map.get("0");
			if(dbObj != null && dbObj.get("total") != null){
				return dbObj.getLong("total");
			}
		}
		return 0l;
	}

	@Override
	public Meeting updateMeeting(Meeting m) {
		Query<Meeting> q = 
				dsForRW.createQuery(Meeting.class).field("_id").equal(new ObjectId(m.getId()));
		UpdateOperations<Meeting> ops = dsForRW.createUpdateOperations(Meeting.class);
		
		ops.set("updateTime", m.getUpdateTime());
		
		if(StringUtils.isNotBlank(m.getCompany())){
			ops.set("company", m.getCompany());
		}
		
		if(StringUtils.isNotBlank(m.getSubject())){
			ops.set("subject", m.getSubject());
		}
		
		if(m.getStartDate() != null){
			ops.set("startDate", m.getStartDate());
		}
		
		if(m.getStartTime() != null){
			ops.set("startTime", m.getStartTime());
		}
		
		if(m.getEndTime() != null){
			ops.set("endTime", m.getEndTime());
		}
		
		if(m.getIsStop() != null){
			ops.set("isStop", m.getIsStop());
		}
		
		if(m.getAttendeesCount() != null){
			ops.set("attendeesCount", m.getAttendeesCount());
		}
		
		if(m.getPrice() != null){
			ops.set("price", m.getPrice());
		}
		
		if(StringUtils.isNotBlank(m.getOrganizerToken())){
			ops.set("organizerToken", m.getOrganizerToken());
		}
		
		if(StringUtils.isNotBlank(m.getPanelistToken())){
			ops.set("panelistToken", m.getPanelistToken());
		}
		
		if(StringUtils.isNotBlank(m.getAttendeeToken())){
			ops.set("attendeeToken", m.getAttendeeToken());
		}
		
		dsForRW.updateFirst(q, ops);
		return getMeetingById(m.getId());
	}

	@Override
	public Meeting getMeetingById(String meetingId) {
		return dsForRW.createQuery(Meeting.class).field("_id").equal(new ObjectId(meetingId)).get();
	}

	@Override
	public Long getMeetingCount(String companyId) {
		return dsForRW.createQuery(Meeting.class)
				.field("endTime").greaterThan(System.currentTimeMillis())
				.field("isStop").equal(0)
				.field("companyId").equal(companyId)
				.countAll();
	}

	@Override
	public List<Meeting> getMeetingList(String companyId,Integer pageIndex, Integer pageSize) {
		Query<Meeting> q = dsForRW.createQuery(Meeting.class);
		q.field("endTime").greaterThan(System.currentTimeMillis());
		q.field("isStop").equal(0);
		q.field("companyId").equal(companyId);
		q.limit(pageSize);
		q.offset(pageIndex * pageSize);
		q.order("-startTime");
		return q.asList();
	}

}
