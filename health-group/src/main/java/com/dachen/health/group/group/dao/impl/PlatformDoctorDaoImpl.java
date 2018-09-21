package com.dachen.health.group.group.dao.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.GroupEnum.OnLineState;
import com.dachen.health.group.group.dao.IPlatformDoctorDao;
import com.dachen.health.group.group.entity.po.PlatformDoctor;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Repository
public class PlatformDoctorDaoImpl extends NoSqlRepository implements IPlatformDoctorDao {

	@Override
	public PlatformDoctor save(PlatformDoctor pdoc) {
		String id = dsForRW.save(pdoc).getId().toString();
		return dsForRW.createQuery(PlatformDoctor.class).field("_id").equal(new ObjectId(id)).get();
	}
	
	public PlatformDoctor update(PlatformDoctor pdoc) {
		DBObject update = new BasicDBObject();
		if (StringUtil.isNotBlank(pdoc.getOnLineState())) {
			update.put("onLineState", pdoc.getOnLineState());
		}
		if (pdoc.getOffLineTime() != null) {
			update.put("offLineTime", pdoc.getOffLineTime());
		}
		if (pdoc.getDutyDuration() != null) {
			update.put("dutyDuration", pdoc.getDutyDuration());
		}
		
		DBObject query = new BasicDBObject();
		query.put("_id", pdoc.getId());
		
		dsForRW.getDB().getCollection("c_platform_doctor").update(query, new BasicDBObject("$set", update));
		return pdoc;
	}
	
	public PlatformDoctor findAndModify(PlatformDoctor pdoc, Long currentTime) {
		Query<PlatformDoctor> q = dsForRW.createQuery("c_platform_doctor", PlatformDoctor.class)
				.filter("groupId", pdoc.getGroupId())
				.filter("doctorId", pdoc.getDoctorId());
		UpdateOperations<PlatformDoctor> ops = dsForRW.createUpdateOperations(PlatformDoctor.class);
		ops.set("onLineState", "1");
		ops.set("onLineTime", currentTime);
				
		return dsForRW.findAndModify(q, ops, false, true);
	}

	public PlatformDoctor getById(PlatformDoctor pdoc) {
		Query<PlatformDoctor> query = dsForRW.createQuery(PlatformDoctor.class);
		
		if(null != pdoc.getDoctorId()) {
			query.field("doctorId").equal(pdoc.getDoctorId());
		}
		
		if(!StringUtil.isEmpty(pdoc.getGroupId())) {
			query.field("groupId").equal(pdoc.getGroupId());
		}
		
		if(pdoc.getId() != null) {
			query.field("_id").equal(pdoc.getId());
		}
		return query.get();
	}
	
	public List<PlatformDoctor> getByGroupId(String groupId) {
		return dsForRW.createQuery(PlatformDoctor.class).filter("groupId", groupId).asList();
	}
	
	public List<PlatformDoctor> getPlatformDoctor(PlatformDoctor pdoc) {
		Query<PlatformDoctor> q = dsForRW.createQuery(PlatformDoctor.class);
		if (StringUtil.isNotBlank(pdoc.getOnLineState())) {
			q.filter("onLineState", OnLineState.onLine.getIndex());
		}
		return q.asList();
	}
}
