package com.dachen.line.stat.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.line.stat.dao.IMessageDao;
import com.dachen.line.stat.entity.vo.Message;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Repository
public class MessagesDaoImpl  extends NoSqlRepository implements IMessageDao {

	@Override
	public Message getMessageById(String id) {
		Query<Message> query = dsForRW.createQuery(Message.class).field("_id")
				.equal(new ObjectId(id));
		return query.get();
	}

	@Override
	public List<Message> getMessageList(Integer userId) {
		List<Message> result = new ArrayList<Message>();
		Query<Message> uq = dsForRW.createQuery(Message.class).filter("userId", userId);//查询搜有的数据
		result = uq.asList();
		return result;
	}

	@Override
	public void updateUserMessage(String id, String content) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		
		BasicDBObject update = new BasicDBObject();

		if (null != content) {// idCard
			update.put("content", content);
		}
		if (!update.isEmpty()) {
			dsForRW.getDB().getCollection("v_message").update(query, new BasicDBObject("$set", update));
		}
		
	}

	@Override
	public void deleteUserMessage(String messageId) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(messageId));
		dsForRW.getDB().getCollection("v_message").remove(query);
		
	}

	@Override
	public Object insertUserMessage(Message message) {
		Object messagid = dsForRW.insert(message).getId();
		
		return messagid;
	}

	@Override
	public List<Message> getMessageList(Integer[] userId) {
		List<Message> result = new ArrayList<Message>();
		Query<Message> uq = dsForRW.createQuery(Message.class).filter("userId in", userId);//查询搜有的数据
		result = uq.asList();
		return result;
	}

}
