package com.dachen.health.knowledge.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.knowledge.dao.IMedicalKnowledgeDao;
import com.dachen.health.knowledge.entity.param.MedicalKnowledgeParam;
import com.dachen.health.knowledge.entity.po.KnowledgeCategory;
import com.dachen.health.knowledge.entity.po.MedicalKnowledge;
import com.dachen.health.knowledge.entity.po.MedicalKnowledgeTop;
import com.dachen.health.knowledge.entity.vo.MedicalKnowledgeVO;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.springframework.util.CollectionUtils;

@Repository
public class MedicalKnowledgeDaoImpl extends NoSqlRepository implements IMedicalKnowledgeDao{

	@Override
	public MedicalKnowledge insertMedicalKnowledge(MedicalKnowledge medicalKnowledge) {
		String id = dsForRW.insert(medicalKnowledge).getId().toString();
		return selectMedicalKnowledgeById(id);
	}

	@Override
	public MedicalKnowledge selectMedicalKnowledgeById(String id) {
		return dsForRW.createQuery(MedicalKnowledgeVO.class).field("_id").equal(new ObjectId(id)).field("enabled").equal(true).get();
	}

    @Override
    public MedicalKnowledge findByIdSimple(String id) {
        Query<MedicalKnowledge> query = dsForRW.createQuery(MedicalKnowledge.class)
                .field(Mapper.ID_KEY).equal(new ObjectId(id))
                .field("enabled").equal(true);
        query.retrievedFields(false, "content");
        return query.get();
    }

    @Override
    public List<MedicalKnowledge> findByIdsSimple(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return null;
        }

        Query<MedicalKnowledge> query = dsForRW.createQuery(MedicalKnowledge.class);
        Criteria[] idParams = new Criteria[idList.size()];
        for (int i = 0; i < idList.size(); i++) {
            idParams[i] = query.criteria(Mapper.ID_KEY).equal(new ObjectId(idList.get(i)));
        }
        query.or(idParams);

        query.field("enabled").equal(true);

        query.retrievedFields(false, "content");

        return query.asList();
    }

	@Override
	public boolean updateMedicalKnowledgeById(MedicalKnowledge medicalKnowledge) {
		Query<MedicalKnowledge> query = dsForRW.createQuery(MedicalKnowledge.class).field("_id").equal(new ObjectId(medicalKnowledge.getId())).field("enabled").equal(true);
		UpdateOperations<MedicalKnowledge> ops = dsForRW.createUpdateOperations(MedicalKnowledge.class);
		if(medicalKnowledge.getTitle() != null){
			ops.set("title", medicalKnowledge.getTitle());
		}
		if(medicalKnowledge.getAuthor()!=null){
			ops.set("author", medicalKnowledge.getAuthor());
		}
		if(medicalKnowledge.getIsShow() != null){
			ops.set("isShow", medicalKnowledge.getIsShow());
		}
		if(medicalKnowledge.getCopy() != null ){
			ops.set("copy", medicalKnowledge.getCopy());
		}
		if(medicalKnowledge.getContent() != null){
			ops.set("content", medicalKnowledge.getContent());
		}
		if(medicalKnowledge.getDescription() != null){
			ops.set("description", medicalKnowledge.getDescription());
		}
		if(medicalKnowledge.getVisitCount() != null){
			ops.set("visitCount", medicalKnowledge.getVisitCount());
		}
		if(medicalKnowledge.getShareCount() != null){
			ops.set("shareCount", medicalKnowledge.getShareCount());
		}
		if (StringUtil.isNotEmpty(medicalKnowledge.getUrl())) {
			ops.set("url", medicalKnowledge.getUrl());
		}
		if (StringUtil.isNotEmpty(medicalKnowledge.getShareUrl())) {
			ops.set("shareUrl", medicalKnowledge.getShareUrl());
		}
		dsForRW.updateFirst(query, ops);
		return true;
	}

	@Override
	public boolean delMedicalKnowledge(String id) {
		Query<MedicalKnowledge> query = dsForRW.createQuery(MedicalKnowledge.class).field("_id").equal(new ObjectId(id));
		UpdateOperations<MedicalKnowledge> ops = dsForRW.createUpdateOperations(MedicalKnowledge.class).set("enabled", false);
		dsForRW.update(query, ops);
		return true;
	}
	@Override
	public boolean delMedicalKnowledgePhysics(String id){
		WriteResult result = dsForRW.delete(MedicalKnowledge.class, new ObjectId(id));
		if (result.getN() > 0) {
			return true;
		}
		return false;
	}
	@Override
	public boolean upMedicalKnowledge(MedicalKnowledgeTop top) {
		Query<MedicalKnowledgeTop> query = dsForRW.createQuery(MedicalKnowledgeTop.class).field("_id").equal(new ObjectId(top.getId()));
		UpdateOperations<MedicalKnowledgeTop> ops = dsForRW.createUpdateOperations(MedicalKnowledgeTop.class).set("priority", top.getPriority());
		dsForRW.update(query, ops);
		return true;
	}

	@Override
	public List<MedicalKnowledgeTop> selectMedicalKnowledgeTopListByBizId(String bizId) {
		return dsForRW.createQuery(MedicalKnowledgeTop.class).field("bizId").equal(bizId).order("-priority").asList();
	}
	
	@Override
	public List<MedicalKnowledge> selectMedicalKnowledgeListByIds(List<ObjectId> ids,boolean content){
		if(ids==null ||ids.size() == 0){
			return new ArrayList<MedicalKnowledge>();
		}
		Query<MedicalKnowledge> query = dsForRW.createQuery(MedicalKnowledge.class).field("enabled").equal(true).filter("_id in", ids);
		if(!content){
			query.retrievedFields(false, "content");
		}
		return query.asList();
	}
	@Override
	public List<MedicalKnowledge> selectMedicalKnowledgeListByIds(List<ObjectId> ids,boolean content,Integer offset,Integer limit){
		//按时间排序和分页
		if(ids==null ||ids.size() == 0){
			return new ArrayList<MedicalKnowledge>();
		}
		Query<MedicalKnowledge> query = dsForRW.createQuery(MedicalKnowledge.class).field("enabled").equal(true).filter("_id in", ids).order("-updateTime").offset(offset).limit(limit);
		if(!content){
			query.retrievedFields(false, "content");
		}
		return query.asList();
	}

	@Override
	public List<KnowledgeCategory> selectMedicalKnowledgeListByGroupId(MedicalKnowledgeParam param) {
		return dsForRW.createQuery(KnowledgeCategory.class).field("groupId").equal(param.getGroupId()).asList();
	}

	@Override
	public long getMedicalKnowledgeTopCount(String bizId) {
		return dsForRW.createQuery(MedicalKnowledgeTop.class).filter("bizId", bizId).countAll();
	}

	@Override
	public List<MedicalKnowledgeTop> getMedicalKnowledgeTopIds(List<String> knowledgeIds,String bizId,Integer offset,Integer limit) {
		Query<MedicalKnowledgeTop> query = dsForRW.createQuery(MedicalKnowledgeTop.class);
		if(knowledgeIds != null){
			query.filter("knowledgeId in", knowledgeIds);
		}
		if(StringUtil.isNotEmpty(bizId)){
			query.filter("bizId", bizId);
		}
		query.order("-priority");
		if(offset != null){
			query.offset(offset);
		}
		if(limit != null){
			query.limit(limit);
		}
		return query.asList();
	}
	@Override
	public List<MedicalKnowledgeTop> getMedicalKnowledgeTopIds(List<String> knowledgeIds,String bizId){
		return getMedicalKnowledgeTopIds(knowledgeIds,bizId,null,null);
	}

	@Override
	public List<MedicalKnowledge> selectMedicalKnowledgeListByDoctorId(List<String> idList) {
		return dsForRW.createQuery(MedicalKnowledge.class).field("enabled").equal(true).filter("author in", idList).retrievedFields(true,"_id").asList();
	}

	@Override
	public boolean isAdminToGroup(Integer doctorId, String groupId) {
		BasicDBObject query = new BasicDBObject();
		query.put("doctorId", doctorId);
		query.put("status", "C");
		query.put("objectId", groupId);
//		BasicDBList in = new BasicDBList();
//		in.add("root");
//		in.add("admin");
//		query.put("rootAdmin", new BasicDBObject("$in", in));
		//已屏蔽的集团不进行过滤
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_user").find(query);
		return cursor.hasNext();
	}

	@Override
	public Integer getMaxPriority(String bizId) {
		MedicalKnowledgeTop top = dsForRW.createQuery(MedicalKnowledgeTop.class).field("bizId").equal(bizId)
				.order("-priority").get();
		Integer priority = 0;
		if (top!=null) {
			priority = top.getPriority();
		}
		return  priority;
	}

	@Override
	public MedicalKnowledgeTop insertMedicalKnowledgeTop(MedicalKnowledgeTop medicalKnowledgeTop) {
		String id = dsForRW.insert(medicalKnowledgeTop).getId().toString();
		return selectMedicalKnowledgeTopById(id);
	}
	
	@Override
	public MedicalKnowledgeTop selectMedicalKnowledgeTopById(String id){
		return dsForRW.createQuery(MedicalKnowledgeTop.class).field("_id").equal(new ObjectId(id)).get();
	}
	@Override
	public boolean updateMedicalKnowledgeTopById(MedicalKnowledgeTop medicalKnowledgeTop){
		Query<MedicalKnowledgeTop> query = dsForRW.createQuery(MedicalKnowledgeTop.class).field("_id").equal(new ObjectId(medicalKnowledgeTop.getId()));
		UpdateOperations<MedicalKnowledgeTop> ops = dsForRW.createUpdateOperations(MedicalKnowledgeTop.class);
		if(medicalKnowledgeTop.getPriority()!= null){
			ops.set("priority", medicalKnowledgeTop.getPriority());
		}
		
		dsForRW.updateFirst(query, ops);
		return true;
	}
	@Override
	public boolean delMedicalKnowledgeTop(String id,String bizId){
		Query<MedicalKnowledgeTop> q = dsForRW.createQuery(MedicalKnowledgeTop.class).field("knowledgeId").equal(id).field("bizId").equal(bizId);
		dsForRW.delete(q);
		return true;
	}

	@Override
	public boolean delKnowledgeTopByKnowledgeId(String knowledgeId) {
		Query<MedicalKnowledgeTop>  query = dsForRW.createQuery(MedicalKnowledgeTop.class).field("knowledgeId").equal(knowledgeId);
		dsForRW.delete(query);
		return true;
	}

	@Override
	public boolean delCategoryByKnowledgeId(String knowledgeId) {
		DBObject query = new BasicDBObject();
		query.put("knowledgeIds", knowledgeId);

		DBObject update = new BasicDBObject();
		update.put("$inc", new BasicDBObject("count", -1));
		update.put("$pull", new BasicDBObject("knowledgeIds", knowledgeId));
		WriteResult result = dsForRW.getDB().getCollection("i_knowledge_category").update(query, update, false, true);
		if (result.getN() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public Query<MedicalKnowledge> selectMedicalByCondition(List<ObjectId> idList,String title,String... author) {
		Query<MedicalKnowledge> query = dsForRW.createQuery(MedicalKnowledge.class).field("enabled").equal(true);
		if(idList!=null){
			query.filter("_id in", idList);
		}
		if(StringUtil.isNotEmpty(title) && (author != null && author.length > 0)){
			Pattern regExp = Pattern.compile("^.*" + title + ".*$", Pattern.CASE_INSENSITIVE);
			query.or(query.criteria("title").equal(regExp),
					query.criteria("author").in(Arrays.asList(author))
					);
		}else{
			if(StringUtil.isNotEmpty(title)){
				Pattern regExp = Pattern.compile("^.*" + title + ".*$", Pattern.CASE_INSENSITIVE);
				query.field("title").equal(regExp);
			}
			if(author != null && author.length > 0){
				query.filter("author in", Arrays.asList(author));
			}
		}
		return query;
	}
	
	@Override
	public MedicalKnowledgeTop selectMedicalKnowledgeTopBiggerThanPriority(String bizId,Integer priority){
		MedicalKnowledgeTop top = dsForRW.createQuery(MedicalKnowledgeTop.class).field("bizId").equal(bizId)
				.filter("priority >", priority).order("priority").limit(1).get();
		return top;
	}
	@Override
	public MedicalKnowledgeTop selectMedicalKnowledgeTopByKnowledgeId(String knowledgeId,String bizId){
		return dsForRW.createQuery(MedicalKnowledgeTop.class)
				.field("knowledgeId").equal(knowledgeId).filter("bizId",bizId).get();
	}

	@Override
	public DBObject getGroupInfoByGId(String id) {
		return dsForRW.getDB().getCollection("c_group").findOne(new BasicDBObject("_id", new ObjectId(id.trim())));
	}
	@Override
	public List<String> getGroupIdByDoctorId(Integer doctorId) {
			DBCollection collection = dsForRW.getDB().getCollection("c_group_doctor");
			DBObject query = new BasicDBObject();
			query.put("doctorId", doctorId);
			query.put("status", "C");
//			query.put("type", "group");
			DBObject orderBy = new BasicDBObject();
			orderBy.put("isMain", -1);
			orderBy.put("creatorDate", 1);
			List<String> GroupIdList = new ArrayList<String>();
			DBCursor cursor = collection.find(query).sort(orderBy);
			
			while (cursor.hasNext()) {
				DBObject object = cursor.next();
				String groupId = (String) object.get("groupId");
				GroupIdList.add(groupId);
			}
		return GroupIdList;
	}

	@Override
	public List<MedicalKnowledge> getKnowledgeListByCreaterId(String createrId, boolean content) {
		if(StringUtil.isEmpty(createrId)){
			return new  ArrayList<MedicalKnowledge>();
		}
		Query<MedicalKnowledge> query = dsForRW.createQuery(MedicalKnowledge.class).field("enabled").equal(true).filter("creater", createrId);
		if(!content){
			query.retrievedFields(false, "content");
		}
		return query.asList();
	}
	
}
