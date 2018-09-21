package com.dachen.health.commons.dao.mongo;

import java.util.List;

import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.dao.UserExpandRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.User.ThridPartyAccount;

@Service
public class UserExpandRepositoryImpl extends NoSqlRepository implements UserExpandRepository {

	@Override
	public JSONMessage roomAdd(Integer userId, BasicDBObject room) {
		JSONMessage jMessage;

		try {
			DBCollection photoCollection = dsForRW.getDB().getCollection("user_room");

			DBObject q = new BasicDBObject("_id", userId);

			if (0 == photoCollection.find(q).count()) {
				DBObject o = new BasicDBObject();
				o.put("_id", userId);
				o.put("rooms", new DBObject[] { room });
				o.put("roomCount", 1);

				photoCollection.insert(o);
			} else {
				DBObject o = new BasicDBObject();
				o.put("$addToSet", new BasicDBObject("rooms", room));
				o.put("$inc", new BasicDBObject("roomCount", 1));

				photoCollection.update(q, o);
			}

			jMessage = JSONMessage.success("新增用户房间成功");
		} catch (Exception e) {
			e.printStackTrace();

			jMessage = JSONMessage.failure("新增用户房间失败");
		}

		return jMessage;
	}

	@Override
	public JSONMessage roomDelete(Integer userId, Integer roomId) {
		JSONMessage jMessage;

		try {
			// ds.getDB().updateFirst(query, update, entityClass)
			// Query q=new Query();
			// q.eq(v1, v2)
			DBCollection photoCollection = dsForRW.getDB().getCollection("user_room");

			DBObject q = new BasicDBObject("_id", userId);
			DBObject o = new BasicDBObject();
			o.put("$pull", new BasicDBObject("rooms", new BasicDBObject("roomId", roomId)));
			o.put("$inc", new BasicDBObject("roomCount", -1));

			photoCollection.update(q, o);

			jMessage = JSONMessage.success("删除用户房间成功");
		} catch (Exception e) {
			e.printStackTrace();

			jMessage = JSONMessage.failure("删除用户房间失败");
		}

		return jMessage;
	}

	@Override
	public JSONMessage roomList(Integer userId) {
		JSONMessage jMessage;

		try {
			DBCollection photoCollection = dsForRW.getDB().getCollection("user_room");
			DBObject obj = photoCollection.findOne(new BasicDBObject("_id", userId));

			if (null == obj)
				jMessage = JSONMessage.success(null, new Object[] {});
			else {
				jMessage = JSONMessage.success(null, obj.get("rooms"));
			}
		} catch (Exception e) {
			e.printStackTrace();

			jMessage = JSONMessage.failure("获取用户房间列表失败");
		}

		return jMessage;
	}

	@Override
	public boolean deleteAccount(int userId, String tpName) {
		// 使用驱动操作
		// DBObject q = new BasicDBObject("_id", 100861);
		// DBObject o = new BasicDBObject();
		// o.put("$pull", new BasicDBObject("accounts", new BasicDBObject(
		// "tpName", "WEIBO")));

		// 使用morphia操作
		Query<User> query = dsForRW.createQuery(User.class).field(Mapper.ID_KEY).equal(userId);
		UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class).removeAll("accounts", new BasicDBObject("tpName", tpName));

		dsForRW.update(query, ops);

		return true;
	}

 

	@Override
	public boolean addAccount(int userId, ThridPartyAccount account) {
		Query<User> query = dsForRW.createQuery(User.class).field(Mapper.ID_KEY).equal(userId);
		UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class).add("accounts", account);

		dsForRW.update(query, ops);

		return true;
	}
}
