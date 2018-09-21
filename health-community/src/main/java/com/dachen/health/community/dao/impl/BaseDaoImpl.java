package com.dachen.health.community.dao.impl;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.community.dao.BaseDao;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class BaseDaoImpl<T> extends NoSqlRepository implements BaseDao<T >{

	public T save(T obj)
	{
		if(obj==null)
		{
			return null;
		}
		dsForRW.save(obj);
		return obj;
	}
	
	public T update(final Class<T> clazz,String id,Map<String,Object>updateFieldMap){
		Query<T> q = dsForRW.createQuery(clazz).filter("_id", new ObjectId(id));
		UpdateOperations<T> ops = dsForRW.createUpdateOperations(clazz);
		for( Entry<String, Object> eachObj:updateFieldMap.entrySet()){
			ops.set(eachObj.getKey(), eachObj.getValue());
		}
		return dsForRW.findAndModify(q, ops);
	}
	public T insert(T obj)
	{
		if(obj==null)
		{
			return null;
		}

		dsForRW.insert(obj);
		return obj;
	}
	
	
	public T get(final Class<T> clazz,String condition,Object value)
	{
		Query<T> query = dsForRW.createQuery(clazz).filter(condition,value);
		query.limit(1);
		return query.get();
	}
	
	public T getByPK(final Class<T> clazz,String pk){
		Query<T> query = dsForRW.createQuery(clazz).filter("_id",new ObjectId(pk));
		return query.get();
	}
	
	public List<T> getByList(final Class<T> clazz,Map<String,Object> param,String sort,int pageStart,int pageSize){
		Query<T> query = dsForRW.createQuery(clazz);
		 for (String key : param.keySet()) {
			   	query.filter(key,param.get(key));
			  }
		 if(StringUtils.isNotEmpty(sort)){
			 query.order(sort);
		 }
		 query.offset(pageStart).limit(pageSize);
		return query.asList();
	}
	
	public Long getByCount(final Class<T> clazz,Map<String,Object> param,String sort){
		Query<T> query = dsForRW.createQuery(clazz);
		 for (String key : param.keySet()) {
			   	query.filter(key,param.get(key));
			  }
		 if(StringUtils.isNotEmpty(sort)){
			 query.order(sort);
		 }
		return query.countAll();
	}
	
	private ObjectId createUuid()
	{
		UUID uuid = UUID.randomUUID();
		String uuidStr = uuid.toString().replace("-", "");
		return new ObjectId(uuidStr);
	}
}
