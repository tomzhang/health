package com.dachen.health.commons.dao.mongo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.dao.OnlineRecordRepository;
import com.dachen.health.commons.entity.OnlineRecord;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Repository
public class OnlineRecordRepositoryImpl extends
		BaseRepositoryImpl<OnlineRecord, String> implements OnlineRecordRepository {

	@Override
	public OnlineRecord findLastOneByGroupDoctor(String groupDoctorId) {
		if(StringUtil.isEmpty(groupDoctorId)) {
			throw new ServiceException(4005, "groupDoctorId is null !");
		}
		Query<OnlineRecord> query = dsForRW.createQuery(OnlineRecord.class);
		query.field("groupDoctorId").equal(groupDoctorId);
		query.order("-createTime");
		return query.get();
	}

	@Override
	public OnlineRecord update(OnlineRecord entity) {
		
		DBObject update = new BasicDBObject();
		
		if(null!=entity.getOffLineTime()){
		    update.put("offLineTime", entity.getOffLineTime());
		}
		if(null!=entity.getDuration()){
			update.put("duration", entity.getDuration());
		}
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(entity.getId()));
		dsForRW.getDB().getCollection("t_online_record").update(query, new BasicDBObject("$set",update));
		return entity;
	}

	@Override
	public Long findDurationOfOnDuty(String groupDoctorId) {
		Long monthStart=DateUtil.getCurrentMonthStart().getTime();

		
	     // 匹配条件
        DBObject matchFields = new BasicDBObject();
        matchFields.put("groupDoctorId",groupDoctorId );
        matchFields.put("onLineTime", new BasicDBObject("$ge",monthStart));
        DBObject match = new BasicDBObject("$match", matchFields);

        // 返回字段
        BasicDBObject projectFields = new BasicDBObject();
        projectFields.put("_id", 0);
        DBObject project = new BasicDBObject("$project", projectFields);
        
        // 分组条件
        DBObject groupFields = new BasicDBObject("_id", null);
        groupFields.put("value", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        
/*        // 排序条件
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("value",-1));*/
        
        List<DBObject> pipeline = new ArrayList<DBObject>();
        pipeline.add(match);
        pipeline.add(project);
        pipeline.add(group);
  //      pipeline.add(sort);
		
       DBCollection collection= dsForRW.getCollection(OnlineRecord.class);
	    AggregationOutput output = collection.aggregate(pipeline);

        Iterator<DBObject> it = output.results().iterator();
        while (it.hasNext()) {
            DBObject obj = it.next();

           Long ret= MongodbUtil.getLong(obj, "value"); 
           System.out.println(ret);
           return ret;

        }
		
	return 0L;
	}

	public static void main(String[] args) {
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND, 0);
		
		//cal.add(Calendar.DATE, -1);
		
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String x=sdf.format(cal.getTime());
		System.out.println(x);
	}
}
