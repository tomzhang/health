package com.dachen.health.community.dao;

import java.util.List;
import java.util.Map;

public interface BaseDao<T> {
public T save(T obj);
	
	public T insert(T obj);
	
	public T update(final Class<T> clazz,String id,Map<String,Object>updateFieldMap);
		
	public T get(final Class<T> clazz,String condition,Object value);
	
	public T getByPK(final Class<T> clazz,String pk);
	
	public Long getByCount(final Class<T> clazz,Map<String,Object> param,String sort);
	
	public List<T> getByList(final Class<T> clazz,Map<String,Object> param,String sort,int pageStart,int pageSize);
	

}
