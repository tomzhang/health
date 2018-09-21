package com.dachen.health.commons.dao.mongo;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.constant.DownTaskEnum;
import com.dachen.health.commons.dao.DownTaskRepository;
import com.dachen.health.commons.entity.DownTask;
import com.dachen.util.StringUtil;

@Repository
public class DownTaskRepositoryImpl extends NoSqlRepository  implements DownTaskRepository {

	@Override
	public String sava(DownTask dt) {
		String result = null;
		 try {
			 Query<DownTask>  query = dsForRW.createQuery(DownTask.class);
			 query.field("recordId").equal(dt.getRecordId());
			 UpdateOperations<DownTask> update = dsForRW.createUpdateOperations(DownTask.class);
			 update.set("status", dt.getStatus());
			 update.set("createTime",dt.getCreateTime());
			 update.set("lastUpdateTime", dt.getLastUpdateTime());
			 update.set("sourceUrl", dt.getSourceUrl());
			 update.set("recordId", dt.getRecordId());
			 update.set("bussessType", dt.getBussessType());
			 update.set("orderId", dt.getOrderId());
			 result =  dsForRW.findAndModify(query, update, false, true).getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<DownTask> findAllTaskToDown(DownTask dt) {
		 Query<DownTask>  query = dsForRW.createQuery(DownTask.class);
		 query.field("status").lessThan(DownTaskEnum.DownStatus.recordUploadFail.getIndex());
		 if(dt != null && !StringUtil.isEmpty(dt.getBussessType())){
			 query.field("bussessType").equal(dt.getBussessType().trim());
		}
		return query.asList();
	}

	@Override
	public boolean updateDownTask(DownTask dt) {
		 Query<DownTask>  query = dsForRW.createQuery(DownTask.class);
		 query.field("id").equal(new ObjectId(dt.getId()));
		 UpdateOperations<DownTask> update = dsForRW.createUpdateOperations(DownTask.class);
		 update.set("status", dt.getStatus());
		 update.set("lastUpdateTime", System.currentTimeMillis());
		 
		 if(!StringUtil.isEmpty(dt.getFilePath())){
			 update.set("filePath", dt.getFilePath());
		 }
		 
		 if(dt.getDetails() != null){
			 update.set("details", dt.getDetails());
		 }
		 if(!StringUtil.isEmpty(dt.getToUrl())){
			 update.set("toUrl", dt.getToUrl());
		 }
		 UpdateResults result = dsForRW.update(query, update,false);
		 if(result.getWriteResult().getN()>0){
			 return true;
		 }
		 return false;
	}

	@Override
	public DownTask getDownTaskByUrl(String url) {
		Query<DownTask>  query = dsForRW.createQuery(DownTask.class);
		 query.field("sourceUrl").equal(url);
		return query.get();
	}

	@Override
	public DownTask getDownTaskByRecordId(String recordId) {
		Query<DownTask>  query = dsForRW.createQuery(DownTask.class);
		 query.field("recordId").equal(recordId);
		return query.get();
	}
}
