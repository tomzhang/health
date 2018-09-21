package com.dachen.health.pack.consult.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.pack.consult.dao.ConsultationPackDao;
import com.dachen.health.pack.consult.entity.po.GroupConsultationPack;
import com.dachen.health.pack.consult.entity.vo.ConsultationPackParams;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class ConsultationPackDaoImpl extends NoSqlRepository implements ConsultationPackDao {

	@Override
	public long getTotal(ConsultationPackParams consultationPackParams) {
		return dsForRW.createQuery(GroupConsultationPack.class)
				.field("groupId").equal(consultationPackParams.getGroupId())
				.field("isDelete").equal(0)
				.countAll();
	}

	@Override
	public List<GroupConsultationPack> getConsultPackList(ConsultationPackParams consultationPackParams) {
		return dsForRW.createQuery(GroupConsultationPack.class)
						.field("groupId").equal(consultationPackParams.getGroupId())
						.field("isDelete").equal(0)
						.offset(consultationPackParams.getPageIndex()*consultationPackParams.getPageSize())
						.limit(consultationPackParams.getPageSize())
						.order("-createTime")
						.asList();
	}

	public long getTotal(ConsultationPackParams consultationPackParams, List<ObjectId> consultationIds,boolean isNotIn){
		Query<GroupConsultationPack>  qurey = dsForRW.createQuery(GroupConsultationPack.class)
				.field("isDelete").equal(0);
				if(StringUtil.isNotBlank(consultationPackParams.getGroupId())){
					qurey.field("groupId").equal(consultationPackParams.getGroupId());
				}
				if(consultationPackParams.getConsultationDoctor() != null){
					qurey.filter("consultationDoctor", consultationPackParams.getConsultationDoctor());
				}
				if(consultationPackParams.getDoctorId() != null){
					qurey.filter("doctorIds", consultationPackParams.getDoctorId());
				}
				if(consultationIds != null && consultationIds.size() > 0){
					if(isNotIn){
						qurey.filter("_id nin", consultationIds);
					}else{
						qurey.filter("_id in", consultationIds);
					}
					
				}
		return qurey.countAll();
	}
	
	public List<GroupConsultationPack> getConsultPackList(ConsultationPackParams consultationPackParams , List<ObjectId> consultationIds,int skipNum,boolean isNotIn){
		Query<GroupConsultationPack>  qurey = dsForRW.createQuery(GroupConsultationPack.class)
				.field("isDelete").equal(0);
				if(StringUtil.isNotBlank(consultationPackParams.getGroupId())){
					qurey.field("groupId").equal(consultationPackParams.getGroupId());
				}
				if(consultationPackParams.getConsultationDoctor() != null){
					qurey.filter("consultationDoctor", consultationPackParams.getConsultationDoctor());
				}
				if(consultationPackParams.getDoctorId() != null){
					qurey.filter("doctorIds", consultationPackParams.getDoctorId());
				}
				if(consultationIds != null && consultationIds.size() > 0){
					if(isNotIn){
						qurey.filter("_id nin", consultationIds);
					}else{
						qurey.filter("_id in", consultationIds);
					}
					
				}
		return qurey.offset(skipNum)
						.limit(consultationPackParams.getPageSize())
						.order("-createTime")
						.asList();
	}
	
	@Override
	public GroupConsultationPack getConsultPackDetail(String consultationPackId) {
		return dsForRW.createQuery(GroupConsultationPack.class)
				.field("_id").equal(new ObjectId(consultationPackId))
				.field("isDelete").equal(0)
				.get();
	}

	@Override
	public void updateConsultPack(GroupConsultationPack groupConsultationPack) {
		Query<GroupConsultationPack> query = 
				dsForRW.createQuery(GroupConsultationPack.class)
				.field("_id").equal(new ObjectId(groupConsultationPack.getId()))
				.field("isDelete").equal(0);
		UpdateOperations<GroupConsultationPack> ops = dsForRW.createUpdateOperations(GroupConsultationPack.class);
		if(groupConsultationPack.getConsultationPackTitle() != null)
			ops.set("consultationPackTitle", groupConsultationPack.getConsultationPackTitle());
		if(groupConsultationPack.getConsultationPackDesc() != null)
			ops.set("consultationPackDesc", groupConsultationPack.getConsultationPackDesc());
		if(groupConsultationPack.getConsultationPrice() != null)
			ops.set("consultationPrice", groupConsultationPack.getConsultationPrice());
		if(groupConsultationPack.getConsultationDoctorPercent() != null)
			ops.set("consultationDoctorPercent", groupConsultationPack.getConsultationDoctorPercent());
		if(groupConsultationPack.getDoctorIds() != null)
			ops.set("doctorIds",groupConsultationPack.getDoctorIds());
		if(groupConsultationPack.getDoctorPercents() != null)
			ops.set("doctorPercents", groupConsultationPack.getDoctorPercents());
		dsForRW.updateFirst(query, ops);
	}

	@Override
	public GroupConsultationPack addConsultPack(GroupConsultationPack groupConsultationPack) {
		String id = dsForRW.insert(groupConsultationPack).getId().toString();
		return getConsultPackDetail(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getDoctorIds(String consultationPackId) {
		DBCollection dbCollection = dsForRW.getDB().getCollection("t_group_consultation_pack");
		DBObject query = new BasicDBObject("_id",new ObjectId(consultationPackId));
		query.put("isDelete", 0);
		DBObject projection = new BasicDBObject("doctorIds",1);
		DBObject dbObj = dbCollection.findOne(query, projection);
		Object obj = null;
		if(dbObj != null){
			obj = dbObj.get("doctorIds");
		}
		return obj == null ? null : (List<Integer>)obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getNotInCurrentPackDoctorIds(String groupId, String consultationPackId) {
		DBCollection dbCollection = dsForRW.getDB().getCollection("t_group_consultation_pack");
		DBObject query = new BasicDBObject();
		if(StringUtil.isNotEmpty(consultationPackId)){
			DBObject noInId = new BasicDBObject("$ne",new ObjectId(consultationPackId));
			query.put("_id",noInId);
		}
		query.put("groupId",groupId);
		query.put("isDelete", 0);
		DBObject projection = new BasicDBObject("doctorIds",1);
		DBCursor cursor = dbCollection.find(query, projection);
		Set<Integer> set = new HashSet<Integer>();
		while(cursor.hasNext()){
			DBObject dbObj = cursor.next();
			Object obj = dbObj.get("doctorIds");
			if(obj != null){
				set.addAll((List<Integer>)obj);
			}
		}
		return new ArrayList<Integer>(set);
	}

	@Override
	public void deleteConsultPack(String consultationPackId) {
		Query<GroupConsultationPack> q = dsForRW.createQuery(GroupConsultationPack.class);
		UpdateOperations<GroupConsultationPack> ops = dsForRW.createUpdateOperations(GroupConsultationPack.class);
		q.field("_id").equal(new ObjectId(consultationPackId));
		ops.set("isDelete", 1);
		dsForRW.updateFirst(q, ops);
	}

	@Override
	public int getMinFeeByDoctorId(int doctorId) {
		DBCollection dbCollection = dsForRW.getDB().getCollection("t_group_consultation_pack");
		DBObject query = new BasicDBObject();
		DBObject qAll = new BasicDBObject();
		List<Integer> list = new ArrayList<Integer>();
		list.add(doctorId);
		qAll.put("$all",list );
		query.put("doctorIds",qAll);
		query.put("isDelete", 0);
		DBObject projection = new BasicDBObject("minFee",1);
		DBObject sort = new BasicDBObject("minFee",1);
		DBObject dbObj = dbCollection.findOne(query,projection,sort);
		if(dbObj != null){
			Object obj = dbObj.get("minFee");
			if(obj != null){
				return Integer.valueOf(obj.toString());
			}
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Integer> getAllConsultationDoctorIds() {
		DBCollection dbCollection = dsForRW.getDB().getCollection("t_group_consultation_pack");
		DBObject query = new BasicDBObject();
		query.put("isDelete", 0);
		DBObject projection = new BasicDBObject("doctorIds",1);
		DBCursor cursor = dbCollection.find(query, projection);
		Set<Integer> set = new HashSet<Integer>();
		while(cursor.hasNext()){
			DBObject dbObj = cursor.next();
			Object obj = dbObj.get("doctorIds");
			if(obj != null){
				set.addAll((List<Integer>)obj);
			}
		}
		return set;
	}

	@Override
	public int getMaxFeeByDoctorId(int doctorId) {
		DBCollection dbCollection = dsForRW.getDB().getCollection("t_group_consultation_pack");
		DBObject query = new BasicDBObject();
		DBObject qAll = new BasicDBObject();
		List<Integer> list = new ArrayList<Integer>();
		list.add(doctorId);
		qAll.put("$all", list);
		query.put("doctorIds",qAll);
		query.put("isDelete", 0);
		DBObject projection = new BasicDBObject("maxFee",1);
		DBObject sort = new BasicDBObject("maxFee",-1);
		DBObject dbObj = dbCollection.findOne(query,projection,sort);
		if(dbObj != null){
			Object obj = dbObj.get("maxFee");
			if(obj != null){
				return Integer.valueOf(obj.toString());
			}
		}
		return 0;
	}

	@Override
	public boolean existsConsultationPack(Integer doctorId) {
		DBCollection dbCollection = dsForRW.getDB().getCollection("t_group_consultation_pack");
		DBObject query = new BasicDBObject();
		DBObject qAll = new BasicDBObject();
		List<Integer> list = new ArrayList<Integer>();
		list.add(doctorId);
		qAll.put("$all", list);
		query.put("doctorIds",qAll);
		query.put("isDelete", 0);
		DBObject projection = new BasicDBObject("doctorIds",1);
		DBCursor cursor = dbCollection.find(query, projection);
		return cursor.hasNext();
	}

	@Override
	public List<String> getGroupIdsByDoctorId(Integer doctorId) {
		DBCollection dbCollection = dsForRW.getDB().getCollection("t_group_consultation_pack");
		DBObject query = new BasicDBObject();
		DBObject qAll = new BasicDBObject();
		List<Integer> qlist = new ArrayList<Integer>();
		qlist.add(doctorId);
		qAll.put("$all", qlist);
		query.put("doctorIds",qAll);
		query.put("isDelete", 0);
		DBObject projection = new BasicDBObject("groupId",1);
		DBCursor cursor = dbCollection.find(query, projection);
		List<String> list = new ArrayList<String>();
		while(cursor.hasNext()){
			DBObject dbObj = cursor.next();
			Object obj = dbObj.get("groupId");
			if(obj != null){
				list.add(String.valueOf(obj));
			}
		}
		return list;
	}

	@Override
	public void removeDoctor(String groupId, Integer doctorId) {
		DBCollection dbCollection = dsForRW.getDB().getCollection("t_group_consultation_pack");
		DBObject query = new BasicDBObject("groupId", groupId);
		query.put("isDelete", 0);
		DBObject update = new BasicDBObject("$pull", new BasicDBObject("doctorIds", doctorId));
		dbCollection.updateMulti(query, update);
	}

	@Override
	public GroupConsultationPack getConsultationPackByGroupIdAndDoctorId(String groupId, Integer doctorId) {
		DBCollection dbCollection = dsForRW.getDB().getCollection("t_group_consultation_pack");
		DBObject query = new BasicDBObject();
		DBObject projection = new BasicDBObject();
		DBObject sort = new BasicDBObject();
		DBObject qAll = new BasicDBObject();
		List<Integer> qlist = new ArrayList<Integer>();
		qlist.add(doctorId);
		qAll.put("$all", qlist);
		query.put("doctorIds",qAll);
		if(StringUtils.isNotBlank(groupId)){
			query.put("groupId", groupId);
		}
		
		projection.put("consultationDoctorPercent",1);
		projection.put("unionDoctorPercent",1);
		projection.put("groupPercent",1);
		projection.put("_id",1);
		
		sort.put("isDelete", 1);
		DBObject obj = dbCollection.findOne(query,projection,sort);
		GroupConsultationPack pack = null;
		if(obj != null){
			pack = new GroupConsultationPack();
			pack.setConsultationDoctorPercent(MongodbUtil.getInteger(obj, "consultationDoctorPercent"));
			pack.setUnionDoctorPercent(MongodbUtil.getInteger(obj, "unionDoctorPercent"));
			pack.setGroupPercent(MongodbUtil.getInteger(obj, "groupPercent"));
			pack.setId(MongodbUtil.getString(obj, "_id"));
		}
		return pack;
	}

	@Override
	public GroupConsultationPack getById(String consultationPackId) {
		return dsForRW.createQuery(GroupConsultationPack.class).field("_id").equal(new ObjectId(consultationPackId)).get();
	}

	@Override
	public Object findOneOkPack(Integer doctorId) {
		DBCollection dbCollection = dsForRW.getDB().getCollection("t_group_consultation_pack");
		DBObject query = new BasicDBObject();
		DBObject qAll = new BasicDBObject();
		List<Integer> qlist = new ArrayList<Integer>();
		qlist.add(doctorId);
		qAll.put("$all", qlist);
		query.put("doctorIds",qAll);
		query.put("isDelete",0);
		return dbCollection.findOne(query);
	}

	@Override
	public List<GroupConsultationPack> getConsultationPackList(Integer doctorId) {
		 Query<GroupConsultationPack> query = dsForRW.createQuery(GroupConsultationPack.class).filter("consultationDoctor", doctorId).filter("isDelete", 0);
		return query.asList();
	}
	
	
}
