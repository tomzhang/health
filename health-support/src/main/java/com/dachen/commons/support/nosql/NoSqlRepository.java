package com.dachen.commons.support.nosql;

import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.dachen.commons.support.jedis.JedisTemplate;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public abstract class NoSqlRepository implements INoSqlRepository {

	@Resource(name = "dsForRW")
	protected AdvancedDatastore dsForRW;
//	@Resource(name = "jedisPool")
//	protected JedisPool jedisPool;
	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	@Resource(name = "morphia")
	protected Morphia morphia;
	
//	@Resource
//	protected IdxManager idxManager;
	// @Resource(name = "shardedJedisPool")
	// protected ShardedJedisPool shardedJedisPool;
	


	/**
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	@Override
	public BasicDBObject findAndModify(String name, DBObject query, DBObject update) {
		return (BasicDBObject) dsForRW.getDB().getCollection(name).findAndModify(query, update);
	}

	/**
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	@Override
	public void insert(String name, DBObject... arr) {
		dsForRW.getDB().getCollection(name).insert(arr);
	}

	/**
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	@Override
	public List<Object> selectId(String name, DBObject q) {
		List<Object> idList = Lists.newArrayList();

		DBCursor cursor = dsForRW.getDB().getCollection(name).find(q, new BasicDBObject("_id", 1));
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		return idList;
	}

	/**
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	@Override
	public <T> DBCollection getCollection(Query<T> q) {
		DBCollection dbColl = q.getCollection();
		if (dbColl == null) {
			dbColl = dsForRW.getCollection(q.getEntityClass());
		}
		return dbColl;
	}

	/**
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	@Override
	public List<Object> findAndUpdate(String name, DBObject q, DBObject ops, DBObject keys, Callback callback) {
		List<Object> idList = Lists.newArrayList();

		DBCollection dbColl = dsForRW.getDB().getCollection(name);
		DBCursor cursor = null == keys ? dbColl.find(q) : dbColl.find(q, keys);
		while (cursor.hasNext()) {
			BasicDBObject dbObj = (BasicDBObject) cursor.next();

			callback.execute(dbObj);

			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		dbColl.update(q, ops, false, true);

		return idList;
	}

	/**
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	@Override
	public <T> List<Object> findAndUpdate(Query<T> q, UpdateOperations<T> ops, DBObject keys, Callback callback) {
		List<Object> idList = Lists.newArrayList();

		DBCursor cursor = getCollection(q).find(q.getQueryObject(), keys);
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();

			// 执行推送
			callback.execute(dbObj);

			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		// 执行批量更新
		dsForRW.update(q, ops);

		return idList;
	}

	/**
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	@Override
	public List<Object> handlerAndReturnId(String name, DBObject q, DBObject keys, Callback callback) {
		List<Object> idList = Lists.newArrayList();

		DBCursor cursor = dsForRW.getDB().getCollection(name).find(q, keys);
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			callback.execute(dbObj);
			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		return idList;
	}

	/**
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	@Override
	public List<Object> selectId(String name, QueryBuilder qb) {
		return selectId(name, qb.get());
	}

	/**
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	@Override
	public List<Object> findAndDelete(String name, DBObject q) {
		List<Object> idList = selectId(name, q);

		dsForRW.getDB().getCollection(name).remove(q);

		return idList;
	}

}
