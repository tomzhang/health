package com.dachen.health.user.dao.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.user.dao.IFastandReplyDao;
import com.dachen.health.user.entity.param.FastandReplyParam;
import com.dachen.health.user.entity.po.FastandReply;
import com.dachen.health.user.entity.vo.FastandReplyVO;
import com.dachen.util.MongodbUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


@Repository
public class FastandReplyDaoImpl extends NoSqlRepository implements IFastandReplyDao{

	public FastandReplyVO getOne(FastandReplyParam param) {
		FastandReplyVO fastandReply = new FastandReplyVO();
		DBObject query = new BasicDBObject();
		query.put("_id", param.getReplyId());
		query.put("userId", param.getUserId());
		DBObject obj = dsForRW.getDB().getCollection("u_fastReply").findOne(query);
		if(obj!=null) {
			fastandReply.setReplyId(MongodbUtil.getString(obj, "_id"));
			fastandReply.setReplyContent(MongodbUtil.getString(obj, "replyContent"));
			fastandReply.setUserId(MongodbUtil.getInteger(obj, "userId"));
			fastandReply.setReplyTime(MongodbUtil.getLong(obj, "replyTime"));
			fastandReply.setReplyType(MongodbUtil.getInteger(obj, "userType"));
		}
		return fastandReply;
	}
	
	
	public List<FastandReplyVO> getAll(FastandReplyParam param) {
		
		DBObject query = new BasicDBObject();
		query.put("userId", param.getUserId());
		DBObject orderBy = new BasicDBObject();
		orderBy.put("replyType", 1);
		DBCursor cursor = dsForRW.getDB().getCollection("u_fastReply").find(query).sort(orderBy);
		List<FastandReplyVO> fastandReplyVos = new ArrayList<FastandReplyVO>();
		while(cursor.hasNext()){
			FastandReplyVO fastandReply = new FastandReplyVO();
	        DBObject obj=cursor.next();
	        fastandReply.setReplyId(MongodbUtil.getString(obj, "_id"));
			fastandReply.setReplyContent(MongodbUtil.getString(obj, "replyContent"));
			fastandReply.setUserId(MongodbUtil.getInteger(obj, "userId"));
			fastandReply.setReplyTime(MongodbUtil.getLong(obj, "replyTime"));
			fastandReply.setIs_system(MongodbUtil.getInteger(obj, "is_system"));
			fastandReply.setReplyType(MongodbUtil.getInteger(obj, "userType"));
			fastandReplyVos.add(fastandReply);
		}
		
		DBObject sys_query = new BasicDBObject();
		sys_query.put("is_system", 0);
		DBCursor sys_cursor = dsForRW.getDB().getCollection("u_fastReply").find(sys_query);
		List<FastandReplyVO> list_sys = new ArrayList<FastandReplyVO>();
		while(sys_cursor.hasNext()){
			FastandReplyVO sys = new FastandReplyVO();
	        DBObject obj=sys_cursor.next();
	        sys.setReplyId(MongodbUtil.getString(obj, "_id"));
	        sys.setReplyContent(MongodbUtil.getString(obj, "replyContent"));
	        sys.setIs_system(MongodbUtil.getInteger(obj, "is_system"));
	        sys.setReplyType(MongodbUtil.getInteger(obj, "userType"));
	        list_sys.add(sys);
		}
		
		fastandReplyVos.addAll(list_sys);
		if(fastandReplyVos.size()>0){
			fastandReplyVos.sort(new Comparator<FastandReplyVO>() {
				@Override
				public int compare(FastandReplyVO o1, FastandReplyVO o2) {
					return Integer.valueOf(o1.getIs_system()).compareTo(Integer.valueOf(o2.getIs_system()));
				}
				
			});
		}
		return fastandReplyVos;
	}

	@Override
	public void delete(FastandReplyParam param) {
		
		DBObject query = new BasicDBObject();
		ObjectId id = new ObjectId(param.getReplyId());
		query.put("_id", id);
		query.put("userId", param.getUserId());
		dsForRW.getDB().getCollection("u_fastReply").remove(query);
	}

	@Override
	public void update(FastandReplyParam param) {
		DBObject query = new BasicDBObject();
		ObjectId id = new ObjectId(param.getReplyId());
		query.put("_id", id);
		query.put("userId", param.getUserId());
		DBObject update = new BasicDBObject();
		update.put("replyContent", param.getReplyContent());
		dsForRW.getDB().getCollection("u_fastReply").update(query, new BasicDBObject("$set", update));
	}

	public FastandReply add(FastandReply param) {
		dsForRW.insert(param);
		return param;
	}
	
	
	
}
