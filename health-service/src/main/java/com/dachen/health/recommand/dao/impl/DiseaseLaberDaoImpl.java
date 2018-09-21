package com.dachen.health.recommand.dao.impl;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.entity.DiseaseLaber;
import com.dachen.health.commons.entity.UserDiseaseLaber;
import com.dachen.health.recommand.dao.IDiseaseLaberDao;
import com.dachen.util.MongodbUtil;
import com.dachen.util.ReqUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/5.
 */
@Repository
public class DiseaseLaberDaoImpl extends NoSqlRepository implements IDiseaseLaberDao {

    @Override
    public List<String> getDiseaseIdsByStatus(Integer status) {
        List<String> result = new ArrayList<>();

        DBObject query = new BasicDBObject();
        query.put("status", status);
        DBObject projection = new BasicDBObject();
        projection.put("diseaseId", 1);
        DBObject orderBy = new BasicDBObject();
        orderBy.put("diseaseId", -1);

        DBCursor cursor = dsForRW.getDB().getCollection("t_disease_laber").find(query,projection).sort(orderBy);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            result.add(MongodbUtil.getString(obj,"diseaseId"));
        }
        return result;
    }
    @Override
    public List<DiseaseLaber> getDiseaseLaber(){
        Query<DiseaseLaber> query=dsForRW.createQuery(DiseaseLaber.class);
        query.filter("status",1);//确定为启用状态的标签
        return query.asList();
    }

    @Override
    public void save(DiseaseLaber diseaseLaber) {
        if (null != diseaseLaber) {
            dsForRW.save(diseaseLaber);
        }
        return;
    }

    @Override
    public DiseaseLaber findByDiseaseId(String diseaseId) {
        return dsForRW.createQuery(DiseaseLaber.class).filter("diseaseId", diseaseId).get();
    }

    @Override
    public DiseaseLaber updateLaber(String id,Map<String, Object> updateFieldMap){
        Query<DiseaseLaber> q = dsForRW.createQuery(DiseaseLaber.class).filter("_id", new ObjectId(id));
        UpdateOperations<DiseaseLaber> ops = dsForRW.createUpdateOperations(DiseaseLaber.class);
        for( Map.Entry<String, Object> eachObj:updateFieldMap.entrySet()){
            ops.set(eachObj.getKey(), eachObj.getValue());
        }
        return dsForRW.findAndModify(q, ops);
    }

	@Override
	public List<UserDiseaseLaber> findByUserId(Integer userId) {
		return dsForRW.createQuery(UserDiseaseLaber.class).filter("userId", userId).order("weight").asList();
	}
	
	@Override
	public List<UserDiseaseLaber> sortByCreateTime(Integer userId) {
		return dsForRW.createQuery(UserDiseaseLaber.class).filter("userId", userId).order("createTime").asList();
	}

	@Override
	public UserDiseaseLaber findByUserIdAndDiseaseId(Integer userId, String diseaseId) {
		return dsForRW.createQuery(UserDiseaseLaber.class).filter("userId", userId).filter("diseaseId", diseaseId).get();
	}

	@Override
	public void saveUserLaber(UserDiseaseLaber laber) {
		if (null != laber) {
			dsForRW.save(laber);
		}
		
	}

	@Override
	public void delUserLaber(List<String> diseaseIds) {
		if (CollectionUtils.isEmpty(diseaseIds)) {
			return;
		}
		
		dsForRW.delete(dsForRW.createQuery(UserDiseaseLaber.class).filter("userId", ReqUtil.instance.getUserId()).filter("diseaseId in", diseaseIds));
		
	}
	
    @Override
    public UserDiseaseLaber updateUserLaber(String id,Map<String, Object> updateFieldMap){
        Query<UserDiseaseLaber> q = dsForRW.createQuery(UserDiseaseLaber.class).filter("_id", new ObjectId(id));
        UpdateOperations<UserDiseaseLaber> ops = dsForRW.createUpdateOperations(UserDiseaseLaber.class);
        for( Map.Entry<String, Object> eachObj:updateFieldMap.entrySet()){
            ops.set(eachObj.getKey(), eachObj.getValue());
        }
        return dsForRW.findAndModify(q, ops);
    }

	@Override
	public void delAllLaber() {
		dsForRW.getDB().getCollection("t_disease_laber").drop();
	}
    @Override
    public Long getUserLaberByDiseaseId(String diseaseId){
        Query<UserDiseaseLaber> query=dsForRW.createQuery(UserDiseaseLaber.class);
        query.filter("diseaseId",diseaseId);
        return query.countAll();
    }

}
