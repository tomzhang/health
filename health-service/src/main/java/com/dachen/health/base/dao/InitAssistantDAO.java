package com.dachen.health.base.dao;

import java.util.List;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.entity.po.DrugPersonInfo;
import com.dachen.health.base.entity.po.DrupHospitalInfo;
import com.dachen.health.commons.vo.User;

@Repository
public class InitAssistantDAO extends NoSqlRepository{
	
	public void addAssistant(User user)
	{
		dsForRW.insert(user);
	}
	
	public List<DrugPersonInfo>getAllAssistant()
	{
		return dsForRW.createQuery(DrugPersonInfo.class).asList();
	}
	
	public void updateUserIdForDrupHospital(int userId,String drupPersonId)
	{
		
		Query<DrupHospitalInfo> q = dsForRW.createQuery(DrupHospitalInfo.class).field("_id").equal(drupPersonId);
		UpdateOperations<DrupHospitalInfo> ops = dsForRW.createUpdateOperations(DrupHospitalInfo.class);
		ops.set("userId", userId);
//		dsForHealth.findAndModify(q, ops);
		dsForRW.update(q, ops);
	}
}
