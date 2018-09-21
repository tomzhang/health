package com.dachen.health.knowledge.dao.impl;

import java.util.List;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.knowledge.dao.IKnowledgeCategoryDao;
import com.dachen.health.knowledge.entity.po.KnowledgeCategory;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository
public class KnowledgeCategoryDaoImpl extends NoSqlRepository implements IKnowledgeCategoryDao{

	
	@Override
	public KnowledgeCategory insertKnowledgeCategory(KnowledgeCategory category) {
		String id = dsForRW.insert(category).getId().toString();
		return dsForRW.createQuery(KnowledgeCategory.class).field("_id").equal(new ObjectId(id)).get();
	}

	@Override
	public boolean delKnowledgeCategoryById(String id) {
		Query<KnowledgeCategory> q = dsForRW.createQuery(KnowledgeCategory.class).field("_id").equal(new ObjectId(id));
		dsForRW.delete(q);
		return true;
	}

	@Override
	public KnowledgeCategory selectDefaultKnowledgeCategoryById(String groupId, String name) {
		return dsForRW.createQuery(KnowledgeCategory.class).field("groupId").equal(groupId).field("name").equal(name).get();
	}

	@Override
	public KnowledgeCategory updateKnowledgeCategoryById(KnowledgeCategory category) {
		Query<KnowledgeCategory> q = dsForRW.createQuery(KnowledgeCategory.class).field("_id").equal(new ObjectId(category.getId()));
		 UpdateOperations<KnowledgeCategory>  ops = dsForRW.createUpdateOperations(KnowledgeCategory.class);
		 if(!StringUtil.isEmpty(category.getName())){
			 ops.set("name", category.getName());
		 }
		 if(category.getKnowledgeIds() !=null){
			 ops.set("knowledgeIds", category.getKnowledgeIds());
			 ops.set("count", category.getKnowledgeIds().length);
		 }
		 dsForRW.update(q, ops);
		return q.get();
	}

	@Override
	public List<KnowledgeCategory> selectKnowledgeCategoryListByGroupId(String groupId,boolean knowledgeIds) {
		Query<KnowledgeCategory> query = dsForRW.createQuery(KnowledgeCategory.class).field("groupId").equal(groupId).order("-weight");
		if(!knowledgeIds){
			query.retrievedFields(false, "knowledgeIds");
		}
		return query.asList();
	}

	@Override
	public KnowledgeCategory selectKnowledgeCategoryById(String Id) {
		return dsForRW.createQuery(KnowledgeCategory.class).field("_id").equal(new ObjectId(Id)).get();
	}

	@Override
	public boolean insertKnowledgeintoCategory(String categoryId, String knowledgeId) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(categoryId));

		DBObject update = new BasicDBObject();
		update.put("$inc", new BasicDBObject("count", 1));
		update.put("$addToSet", new BasicDBObject("knowledgeIds", knowledgeId));
		WriteResult result = dsForRW.getCollection(KnowledgeCategory.class).update(query, update, false, true);
		if (result.getN() > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public List<KnowledgeCategory> searchCategory(List<String> groupIds,String keyword){
		Query<KnowledgeCategory> q = dsForRW.createQuery(KnowledgeCategory.class);
		if (groupIds!=null) {
			q.filter("groupId in", groupIds);
		}
		if (StringUtil.isNotEmpty(keyword)) {
			//搜索匹配集团名称或公司名称
			q.field("name").equal(keyword);
		}
		//按创建时间排序  add by wangqiao
		return q.asList();
	}
}


