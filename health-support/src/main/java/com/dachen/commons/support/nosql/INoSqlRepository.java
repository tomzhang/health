package com.dachen.commons.support.nosql;

import java.util.List;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
/**
 * 
 * ProjectName： health-service<br>
 * ClassName： INoSqlRepository<br>
 * Description： <br>
 * @author limiaomiao
 * @crateTime 2015年7月1日
 * @version 1.0.0
 */
public interface INoSqlRepository {

	/**
	 * 
	 * </p>查询修改</p>
	 * @param name
	 * @param query
	 * @param update
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	public abstract BasicDBObject findAndModify(String name, DBObject query,
			DBObject update);

	/**
	 * 
	 * </p>插入</p>
	 * @param name
	 * @param arr
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	
	public abstract void insert(String name, DBObject... arr);

	/**
	 * 
	 * </p>查询id</p>
	 * @param name
	 * @param q
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	public abstract List<Object> selectId(String name, DBObject q);

	/**
	 * 
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @param q
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	public abstract <T> DBCollection getCollection(Query<T> q);

	/**
	 * 
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @param name
	 * @param q
	 * @param ops
	 * @param keys
	 * @param callback
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	public abstract List<Object> findAndUpdate(String name, DBObject q,
			DBObject ops, DBObject keys, Callback callback);

	/**
	 * 
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @param q
	 * @param ops
	 * @param keys
	 * @param callback
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	public abstract <T> List<Object> findAndUpdate(Query<T> q,
			UpdateOperations<T> ops, DBObject keys, Callback callback);

	/**
	 * 
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @param name
	 * @param q
	 * @param keys
	 * @param callback
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	public abstract List<Object> handlerAndReturnId(String name, DBObject q,
			DBObject keys, Callback callback);

	/**
	 * 
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @param name
	 * @param qb
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	public abstract List<Object> selectId(String name, QueryBuilder qb);

	/**
	 * 
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @param name
	 * @param q
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月1日
	 */
	public abstract List<Object> findAndDelete(String name, DBObject q);

}