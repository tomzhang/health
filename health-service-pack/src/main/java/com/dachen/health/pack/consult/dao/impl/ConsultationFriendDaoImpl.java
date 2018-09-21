package com.dachen.health.pack.consult.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.UserEnum.RelationStatus;
import com.dachen.health.friend.entity.po.DoctorFriend;
import com.dachen.health.pack.consult.dao.ConsultationFriendDao;
import com.dachen.health.pack.consult.entity.po.ConsultationApplyFriend;
import com.dachen.health.pack.consult.entity.po.ConsultationFriend;
import com.dachen.health.pack.consult.entity.vo.ConsultationEnum;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class ConsultationFriendDaoImpl extends NoSqlRepository implements ConsultationFriendDao{

	@Override
	public void insertApplyFriend(ConsultationApplyFriend consultationApplyFriend) {
		dsForRW.insert(consultationApplyFriend);
	}

	@Override
	public ConsultationApplyFriend getApplyFriendById(String consultationApplyFriendId) {
		return dsForRW.createQuery(ConsultationApplyFriend.class).field("_id").equal(new ObjectId(consultationApplyFriendId)).get();
	}

	@Override
	public ConsultationFriend getDoctorFriendByDoctorIdAndRoleType(Integer doctorId,Integer friendType) {
		return dsForRW.createQuery(ConsultationFriend.class).field("doctorId").equal(doctorId).field("friendType").equal(friendType).get();
	}

	@Override
	public void insertDoctorFriend(ConsultationFriend cf) {
		dsForRW.insert(cf);
	}

	@Override
	public void addFriends(Integer queryDoctorId, Integer pushDoctorId,Integer friendType) {
		Query<ConsultationFriend> query = 
				dsForRW.createQuery(ConsultationFriend.class).field("doctorId").equal(queryDoctorId).field("friendType").equal(friendType);
		UpdateOperations<ConsultationFriend> ops = dsForRW.createUpdateOperations(ConsultationFriend.class);
		ops.add("doctorIdFriendIds", pushDoctorId);
		dsForRW.updateFirst(query, ops);
	}

	@Override
	public void updateFriendsApply(String consultationApplyFriendId, Integer applyStatus) {
		Query<ConsultationApplyFriend> query = 
				dsForRW.createQuery(ConsultationApplyFriend.class).field("_id").equal(new ObjectId(consultationApplyFriendId));
		UpdateOperations<ConsultationApplyFriend> ops = dsForRW.createUpdateOperations(ConsultationApplyFriend.class);
		ops.set("status", applyStatus);
		ops.set("updateTime", System.currentTimeMillis());
		dsForRW.updateFirst(query, ops);
	}

	@Override
	public Integer getFriendCount(Integer doctorId, int doctorRole) {
		ConsultationFriend cf = 
				dsForRW.createQuery(ConsultationFriend.class).field("doctorId").equal(doctorId).field("friendType").equal(doctorRole).get();
		if(cf != null){
			return cf.getDoctorIdFriendIds().size();
		}
		return 0;
	}

	@Override
	public Integer getFriendApplyCount(Integer doctorId, Integer applyType) {
		Query<ConsultationApplyFriend> q1 = 
				dsForRW.createQuery(ConsultationApplyFriend.class);
		
		Query<ConsultationApplyFriend> q2 = 
				dsForRW.createQuery(ConsultationApplyFriend.class);
		
		if(applyType != null){
			if(ConsultationEnum.FriendApply.cApplyu.getIndex() == applyType.intValue()){
				q1.field("unionDoctorId").equal(doctorId);
			}
			if(ConsultationEnum.FriendApply.uApplyc.getIndex() == applyType.intValue()){
				q1.field("consultationDoctorId").equal(doctorId);
			}
			q1.field("applyType").equal(applyType);
			q1.field("status").equal(ConsultationEnum.ApplyStatus.applying.getIndex());
			return (int) q1.countAll();
		}else{
			q1.field("unionDoctorId").equal(doctorId);
			q1.field("applyType").equal(ConsultationEnum.FriendApply.cApplyu.getIndex());
			q1.field("status").equal(ConsultationEnum.ApplyStatus.applying.getIndex());

			q2.field("consultationDoctorId").equal(doctorId);
			q2.field("applyType").equal(ConsultationEnum.FriendApply.uApplyc.getIndex());
			q2.field("status").equal(ConsultationEnum.ApplyStatus.applying.getIndex());
			return (int) (q1.countAll() + q2.countAll());
		}
	}

	@Override
	public ConsultationApplyFriend getApplyFriendByDoctorId(Integer consultationDoctorId, int unionDoctorId) {
		return dsForRW.createQuery(ConsultationApplyFriend.class).field("consultationDoctorId").equal(consultationDoctorId)
				.field("unionDoctorId").equal(unionDoctorId).get();
	}

	@Override
	public void removeFriendsApply(String consultationApplyFriendId) {
		dsForRW.delete(dsForRW.createQuery(ConsultationApplyFriend.class).field("_id").equal(new ObjectId(consultationApplyFriendId)));
	}

	@Override
	public long getApplyFriendCountByRoleType(Integer doctorId, Integer roleType) {
		Query<ConsultationApplyFriend> query = dsForRW.createQuery(ConsultationApplyFriend.class);
		query.field("status").equal(ConsultationEnum.ApplyStatus.applying.getIndex());
		if(roleType.intValue() == 1){
			query.field("consultationDoctorId").equal(doctorId);
			query.field("applyType").equal(ConsultationEnum.FriendApply.uApplyc.getIndex());
		}else if(roleType.intValue() == 2){
			query.field("unionDoctorId").equal(doctorId);
			query.field("applyType").equal(ConsultationEnum.FriendApply.cApplyu.getIndex());
		}
		return query.countAll();
	}

	@Override
	public List<ConsultationApplyFriend> getApplyFriendByRoleType(Integer doctorId, Integer roleType,
			Integer pageIndex, Integer pageSize) {
		Query<ConsultationApplyFriend> query = dsForRW.createQuery(ConsultationApplyFriend.class);
		query.field("status").equal(ConsultationEnum.ApplyStatus.applying.getIndex());
		if(roleType.intValue() == 1){
			query.field("consultationDoctorId").equal(doctorId);
			query.field("applyType").equal(ConsultationEnum.FriendApply.uApplyc.getIndex());
		}else if(roleType.intValue() == 2){
			query.field("unionDoctorId").equal(doctorId);
			query.field("applyType").equal(ConsultationEnum.FriendApply.cApplyu.getIndex());
		}
		query.order("-createTime");
		query.offset(pageIndex * pageSize);
		query.limit(pageSize);
		return query.asList();
	}

	@Override
	public void collectFriend(Integer unionDoctorId, Integer consultationDoctorId) {
		Query<ConsultationFriend> query = 
				dsForRW.createQuery(ConsultationFriend.class).field("doctorId").equal(unionDoctorId).field("friendType").equal(ConsultationEnum.DoctorRole.assistant.getIndex());
		UpdateOperations<ConsultationFriend> ops = dsForRW.createUpdateOperations(ConsultationFriend.class);
		ops.add("specialFriendIds", consultationDoctorId);
		dsForRW.updateFirst(query, ops);
	}

	@Override
	public void cancelCollectFriend(Integer unionDoctorId, Integer consultationDoctorId) {
		Query<ConsultationFriend> query = 
				dsForRW.createQuery(ConsultationFriend.class).field("doctorId").equal(unionDoctorId).field("friendType").equal(ConsultationEnum.DoctorRole.assistant.getIndex());
		UpdateOperations<ConsultationFriend> ops = dsForRW.createUpdateOperations(ConsultationFriend.class);
		ops.removeAll("specialFriendIds", consultationDoctorId);
		dsForRW.updateFirst(query, ops);
	}

	@Override
	public boolean isCollect(Integer currDoctorId, Integer friendDoctorId,Integer currentDoctorRole) {
		DBCollection dbCollection = dsForRW.getDB().getCollection("t_consultation_friend");
		DBObject query = new BasicDBObject();
		DBObject qAll = new BasicDBObject();
		List<Integer> list = new ArrayList<Integer>();
		list.add(friendDoctorId);
		qAll.put("$all", list);
		query.put("specialFriendIds",qAll);
		query.put("doctorId",currDoctorId);
		query.put("friendType",currentDoctorRole);
		DBObject projection = new BasicDBObject("doctorIds",1);
		DBCursor cursor = dbCollection.find(query, projection);
		return cursor.hasNext();
	}

	@Override
	public List<Integer> findAllDoctorFriendIdByDoctorId(int userId) {
		Query<DoctorFriend> q = dsForRW.createQuery(DoctorFriend.class)
				.field("userId").equal(userId)
				.field("status").equal(RelationStatus.normal.getIndex());
		q.retrievedFields(true, "toUserId");
		List<DoctorFriend> list = q.asList();
		List<Integer> data = new ArrayList<Integer>();
		if(list != null && list.size() > 0){
			for (DoctorFriend d : list) {
				data.add(d.getToUserId());
			}
		}
		return data;
	}
	
}
