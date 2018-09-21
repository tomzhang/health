package com.dachen.health.pack.order.dao.impl;

import com.dachen.health.auto.dao.impl.BaseDaoImpl;
import com.dachen.health.pack.order.dao.IAssistantSessionRelationDao;
import com.dachen.health.pack.order.entity.po.AssistantSessionRelation;
import com.dachen.util.ConvertUtil;
import com.mongodb.BasicDBObject;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by qinyuan.chen
 * Date:2017/1/4
 * Time:19:49
 */
@Repository
public class AssistantSessionRelationDao extends BaseDaoImpl<AssistantSessionRelation> implements IAssistantSessionRelationDao{

    @Override
    public AssistantSessionRelation add(AssistantSessionRelation asr) {
        return this.insert(asr);
    }

    @Override
    public AssistantSessionRelation queryByGId(String msgGroupId){
        Query<AssistantSessionRelation> query = dsForRW.createQuery(AssistantSessionRelation.class);
        query.filter("msgGroupId",msgGroupId);
        return query.get();
    }

    @Override
    public void update(AssistantSessionRelation asr) {
        Map<String,Object> map= ConvertUtil.objectToMap(asr);
        map.remove("id");
        this.update(AssistantSessionRelation.class,asr.getId(),map);
    }

    @Override
    public void updateByGid(AssistantSessionRelation asr) {
        Map<String,Object> map= ConvertUtil.objectToMap(asr);
        map.remove("msgGroupId");
        Query<AssistantSessionRelation> query = dsForRW.createQuery(AssistantSessionRelation.class).filter("msgGroupId",asr.getMsgGroupId());
        UpdateOperations<AssistantSessionRelation> ops = dsForRW.createUpdateOperations(AssistantSessionRelation.class);
        for( Map.Entry<String, Object> eachObj:map.entrySet()){
            ops.set(eachObj.getKey(), eachObj.getValue());
        }
        dsForRW.findAndModify(query, ops);
    }

    @Override
    public List<AssistantSessionRelation> queryByConditions(AssistantSessionRelation asr) {
    	Query<AssistantSessionRelation> query = dsForRW.createQuery(AssistantSessionRelation.class).filter("type", asr.getType());
    	if(asr.getAssistantId() !=null ){
    		query.filter("assistantId",asr.getAssistantId());
    	}
    	if(asr.getDoctorId() != null){
            query.filter("doctorId",asr.getDoctorId());
    	}
    	if(asr.getUserId()!=null){
            query.filter("userId",asr.getUserId());
        }
    	if(asr.getPatientId() != null){
    		query.filter("patientId",asr.getPatientId());
    	}
    	return query.asList();
    }
    
    public void deleteByIds(List<String> ids){
		BasicDBObject in = new BasicDBObject();
		List<ObjectId> list = new ArrayList<ObjectId>();
		for(String id :ids){
			list.add(new ObjectId(id));
		}
		in.put("$in", list);
    	dsForRW.getDB().getCollection("p_assistant_session_relation").remove(new BasicDBObject("_id", in));
    }
}
