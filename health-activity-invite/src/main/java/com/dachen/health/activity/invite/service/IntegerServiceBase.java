package com.dachen.health.activity.invite.service;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.Map;


public interface IntegerServiceBase {
    <T> T findById(Integer id);
    <T> List<T> findByIds(List<Integer> ids);

    <T> Integer saveEntity(T entity);
    <T> T saveEntityAndFind(T entity);

    <T> List<Integer> saveEntityBatch(List<T> entityList);
    <T> List<T> saveEntityBatchAndFind(List<T> entityList);

    <T> Query<T> createQuery();
    <T> Query<T> createQueryByPKs(List<Integer> idList);
    <T> Query<T> createQueryByPK(Integer id);
    <T> UpdateOperations<T> createUpdateOperations();

    <T> int update(Query<T> q, UpdateOperations<T> ops);
    <T> boolean update(Integer id, Map<String, Object> map);
    <T> int update(Integer id, String fieldName, Object fieldValue);
    <T> int update(List<Integer> idList, String fieldName, Object fieldValue);


    <T> int deleteByQuery(Query<T> q);
    <T> int deleteByIds(List<Integer> ids);
    <T> boolean deleteById(Integer id);
}
