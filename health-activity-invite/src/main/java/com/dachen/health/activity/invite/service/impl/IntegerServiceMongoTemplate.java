package com.dachen.health.activity.invite.service.impl;

import com.dachen.health.activity.invite.service.IntegerServiceBase;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.exception.ServiceException;
import com.dachen.sdk.util.SdkUtils;
import com.mongodb.WriteResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public abstract class IntegerServiceMongoTemplate implements InitializingBean, IntegerServiceBase{
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "dsForRW")
    protected AdvancedDatastore dsForRW;

    @Resource(name = "morphia")
    protected Morphia morphia;

    protected Class<?> entityClass;

    public IntegerServiceMongoTemplate() {
        Model annotation = this.getClass().getAnnotation(Model.class);

        this.entityClass = annotation.value();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T findById(Integer id) {
        Query<T> query = this.createQueryByPK(id);
        return query.get();
    }

    @Override
    public <T> List<T> findByIds(List<Integer> ids) {
        Query<T> query = this.createQueryByPKs(ids);
        return query.asList();
    }

    @Override
    public <T> Query<T> createQueryByPKs(List<Integer> idList) {
        if (SdkUtils.isEmpty(idList)) {
            throw new ServiceException("idList is empty!!!");
        }
        Query<T> query = this.createQuery();
        Criteria[] idParams = new Criteria[idList.size()];
        for (int i = 0; i < idList.size(); i++) {
            idParams[i] = query.criteria(Mapper.ID_KEY).equal(idList.get(i));
        }
        query.or(idParams);
//        query.field(Mapper.ID_KEY).in(idList);    // 不能用in，ObjectId.in strings？
        return query;
    }

    @Override
    public <T> Query<T> createQueryByPK(Integer id) {
        if (null == id) {
            throw new ServiceException("id is Null!");
        }
        Query<T> query = this.createQuery();
        query.field(Mapper.ID_KEY).equal(id);
        return query;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> Query<T> createQuery() {
        return (Query<T>) this.dsForRW.createQuery(this.entityClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> UpdateOperations<T> createUpdateOperations() {
        return (UpdateOperations<T>) this.dsForRW.createUpdateOperations(this.entityClass);
    }

    /**
     * 此方法不推荐使用！有可能存在的脏数据覆盖新数据的问题。
     * 如果要更新某些字段，请使用update(String id, Map<String, Object> map)方法
     * 如果确实要调用此方法，请先调用findById获取最新的entity再调用saveEntity
     *
     * @param entity
     * @param <T>
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Integer saveEntity(T entity) {
        if (null == entity) {
            throw new ServiceException("entity is null!!!");
        }
        return Integer.valueOf(dsForRW.save(entity).getId().toString());
    }

    /**
     * 此方法不推荐使用！有可能存在的脏数据覆盖新数据的问题。
     * 如果要更新某些字段，请使用update(String id, Map<String, Object> map)方法
     * 如果确实要调用此方法，请先调用findById获取最新的entity再调用saveEntity
     *
     * @param entity
     * @param <T>
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T saveEntityAndFind(T entity) {
        Integer id = Integer.valueOf(dsForRW.save(entity).getId().toString());
        return (T) findById(id);
    }

    @Override
    public <T> List<Integer> saveEntityBatch(List<T> entityList) {
        if (SdkUtils.isEmpty(entityList)) {
            return null;
        }
        List<Integer> idList = new ArrayList<>(entityList.size());
        Iterator<Key<T>> iterator = this.dsForRW.save(entityList).iterator();
        while (iterator.hasNext()) {
            idList.add(Integer.valueOf(iterator.next().getId().toString()));
        }
        return idList;
    }

    @Override
    public <T> List<T> saveEntityBatchAndFind(List<T> entityList) {
        if (SdkUtils.isEmpty(entityList)) {
            return null;
        }
        Iterable<Key<T>> ret = this.dsForRW.save(entityList);
        Iterator<Key<T>> iter = ret.iterator();
        List<Integer> idList = new ArrayList<>(entityList.size());
        while (iter.hasNext()) {
            Integer id = Integer.valueOf(iter.next().getId().toString());
            idList.add(id);
        }
        return findByIds(idList);
    }


    @Override
    public <T> boolean update(Integer id, Map<String, Object> map) {
        if (SdkUtils.isEmpty(map)) {
            return false;
        }

        Query<T> query = this.createQueryByPK(id);

        boolean needUpdate = false;

        UpdateOperations<T> ops = this.createUpdateOperations();
        // use entrySet rather than keySet to iterate
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Object val = entry.getValue();
            if (null != val) {
                ops.set(key, val);
                needUpdate = true;
            }
//            if (StringUtils.isNotBlank(key)) {
//                ops.set(key, Objects.toString(val, ""));    // 支持值置空（此语句有错，by xiaowei, 170406）
//                needUpdate = true;
//            }
        }

        if (!needUpdate) {
            return false;
        }

        UpdateResults ur = dsForRW.update(query, ops);
        return ur.getUpdatedCount()>0;
    }

    @Override
    public <T> int update(final Query<T> q, final UpdateOperations<T> ops) {
        // TODO: 判断T是否存在updateCreate字段，如有，更新之
        return this.dsForRW.update(q, ops).getUpdatedCount();
    }

    @Override
    public <T> int update(Integer id, String fieldName, Object fieldValue) {
        Query<T> query = this.createQueryByPK(id);

        UpdateOperations<T> ops = this.createUpdateOperations();
//        ops.set(fieldName, Objects.toString(fieldValue, "")); // 不能toString, fieldValue类型不一定是String，也可能是Integer
        if (null == fieldValue) {
            ops.set(fieldName, "");
        } else {
            ops.set(fieldName, fieldValue);
        }

        return this.update(query, ops);
    }

    @Override
    public <T> int update(List<Integer> idList, String fieldName, Object fieldValue) {
        Query<T> query = this.createQueryByPKs(idList);

        UpdateOperations<T> ops = this.createUpdateOperations();
//        ops.set(fieldName, Objects.toString(fieldValue, ""));
        if (null == fieldValue) {
            ops.set(fieldName, "");
        } else {
            ops.set(fieldName, fieldValue);
        }

        return this.update(query, ops);
    }

    @Override
    public <T> int deleteByQuery(Query<T> query) {
        return dsForRW.delete(query).getN();
    }

    @Override
    public <T> int deleteByIds(List<Integer> ids) {
        if (SdkUtils.isEmpty(ids)) {
            return 0;
        }
        Query<T> query = this.createQueryByPKs(ids);
        return this.deleteByQuery(query);
    }

    @Override
    public <T> boolean deleteById(Integer id) {
        Query<T> query = this.createQueryByPK(id);
        WriteResult wr = dsForRW.delete(query);
        return wr.getN()>0;
    }
}
