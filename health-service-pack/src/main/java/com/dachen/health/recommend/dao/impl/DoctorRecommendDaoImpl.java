package com.dachen.health.recommend.dao.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.health.recommend.constant.DoctorRecommendEnum;
import com.dachen.health.recommend.dao.IDoctorRecommendDao;
import com.dachen.health.recommend.entity.param.DoctorRecommendParam;
import com.dachen.health.recommend.entity.po.DoctorRecommend;
import com.dachen.health.recommend.entity.vo.DoctorRecommendVO;
import com.dachen.util.BeanUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;
import com.mongodb.WriteResult;

@Repository
public class DoctorRecommendDaoImpl extends NoSqlRepository implements IDoctorRecommendDao {

	@Override
	public String getGroupDoctorId(String groupId, String doctorId) {
		
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		query.put("doctorId", Integer.parseInt(doctorId));
		query.put("status" , "C");
		DBObject object = dsForRW.getDB().getCollection("c_group_doctor").findOne(query);
		
		if(object != null){
			return object.get("_id").toString();
		}
		return "";
	}
	

	@Override
	public String getAllGroupIds(Integer docId) {
		StringBuffer result = new StringBuffer();
		DBObject query = new BasicDBObject();
		query.put("doctorId", Integer.parseInt(docId+""));
		query.put("status" , "C");
		DBCursor dbCursor= dsForRW.getDB().getCollection("c_group_doctor").find(query);
		while (dbCursor.hasNext()) {
			DBObject object = dbCursor.next();
			result.append(object.get("_id").toString()).append(",");
		}
		return result.toString();
	}

	@Override
	public Integer getWeightByGroupId(String groupId, boolean isFirst) {
		List<DoctorRecommendVO> list = getWeightList(groupId);
		if(list.isEmpty()){
			return 1;
		}
		Integer weight = 1;
		if(isFirst){
			weight = list.get(0).getWeight();
		}else{
			weight = list.get(list.size() -1).getWeight();
		}
		return weight;
	}
	
	public List<DoctorRecommendVO> getWeightList(String groupId){
		return  dsForRW.createQuery(DoctorRecommendVO.class).filter("groupId", groupId).filter("isRecommend", "1").order("-weight").asList();
	}

	@Override
	public DoctorRecommendVO addDoctorRecommend(DoctorRecommendParam param) {
		Long time = System.currentTimeMillis();
		param.setCreateTime(time);
		param.setLastUpdateTime(time);
		DoctorRecommend dc = BeanUtil.copy(param, DoctorRecommend.class);
		String id = dsForRW.insert(dc).getId().toString();
		return getDoctorRecommendById(id);
	}

	@Override
	public DoctorRecommendVO getDoctorRecommendById(String Id) {
		return dsForRW.createQuery(DoctorRecommendVO.class).field("_id").equal(new ObjectId(Id)).get();
	}

	@Override
	public boolean delDoctorRecommendById(String id) {
		DBObject object = new BasicDBObject("_id",new ObjectId(id));
		WriteResult result = dsForRW.getDB().getCollection("t_doctor_recommend").remove(object);
		if(result.getN() > 0){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean updateDoctorRecommend(DoctorRecommend dr) {
		Query<DoctorRecommend> query = dsForRW.createQuery(DoctorRecommend.class).filter("_id", new ObjectId(dr.getId()));
		UpdateOperations<DoctorRecommend> ops = dsForRW.createUpdateOperations(DoctorRecommend.class);
		ops.set("isRecommend", dr.getIsRecommend());
		if(dr.getWeight() != null){
			ops.set("weight", dr.getWeight());
		}
		dsForRW.update(query, ops);
		return true;
	}

	@Override
	public Map<Integer, String> getGroupDoctorInfosByGroupId(String groupId) {
		Map<Integer, String> map = new HashMap<Integer, String>();
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		query.put("status", "C");
		DBObject projection = new BasicDBObject();
		projection.put("_id", 1);
		projection.put("doctorId", 1);
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query,projection);
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			map.put(MongodbUtil.getInteger(obj, "doctorId"), MongodbUtil.getString(obj, "_id"));
		}
		cursor.close();
		return map;
	}

	@Override
	public Query<DoctorRecommendVO> getDoctorRecommendQuery(List<String> list,boolean isApp) {
		if(list.isEmpty()){
			list.add("-1");
		}
		Query<DoctorRecommendVO> query = dsForRW.createQuery(DoctorRecommendVO.class).filter("groupDocId in", list);
		if(isApp){
			query = query.filter("isRecommend", DoctorRecommendEnum.IsRecommendStatus.recommend.getIndex()+"");
		}
		return query;
	}


	@Override
	public Query<DoctorRecommendVO> getDoctorRecommendInPlatform() {
		return dsForRW.createQuery(DoctorRecommendVO.class).filter("groupId", "platform");
	}


	@Override
	public Set<Integer> searchDoctorIdsByName(String keyword) {
		
		Set<Integer> doctorIds = Sets.newHashSet();
		
		Query<User> q = dsForRW.createQuery(User.class).filter("userType", UserEnum.UserType.doctor.getIndex())
				.filter("status", UserEnum.UserStatus.normal.getIndex());
		if (!StringUtil.isEmpty(keyword)) {
			Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
			q.or(q.criteria("name").equal(pattern));
		}
		q.order("-createTime");
		List<User> users = q.asList();
		if (users != null && users.size() > 0) {
			for(User user : users) {
				doctorIds.add(user.getUserId());
			}
		}
		return doctorIds;
	}


	@Override
	public List<Integer> getDoctorIds(String groupId, Integer pageIndex, Integer pageSize) {
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		//TODO 排序问题
		List<String> groupDoctorIds = Lists.newArrayList();
		DBCursor cursor = dsForRW.getDB().getCollection("t_doctor_recommend").find(query).skip(pageIndex * pageSize).limit(pageSize);
		while (cursor.hasNext()) {
			DBObject object = cursor.next();
			String groupDoctorId = MongodbUtil.getString(object, "groupDocId");
			if (StringUtils.isNotEmpty(groupDoctorId)) {
				groupDoctorIds.add(groupDoctorId);
			}
		}
		
		//根据groupDoctorIds查询到doctorId
		DBObject gDocQuery = new BasicDBObject();
		BasicDBList gDocIdIn = new BasicDBList();
		DBObject gDocIdQuery = new BasicDBObject();
		if (groupDoctorIds != null && groupDoctorIds.size() > 0) {
			for (String gDocId : groupDoctorIds) {
				gDocIdIn.add(new ObjectId(gDocId));
			}
		}
		gDocIdQuery.put(QueryOperators.IN, gDocIdIn);
		gDocQuery.put("_id", gDocIdQuery);
		
		List<Integer> doctorIds = Lists.newArrayList();
		DBCursor gDocCursor = dsForRW.getDB().getCollection("c_group_doctor").find(gDocQuery);
		while (gDocCursor.hasNext()) {
			DBObject object = gDocCursor.next();
			Integer doctorId = MongodbUtil.getInteger(object, "doctorId");
			doctorIds.add(doctorId);
		}
		return doctorIds;
	}

	public Long getDoctorIdsCount(String groupId){
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		return dsForRW.getDB().getCollection("t_doctor_recommend").count(query);
	}


	@Override
	public List<DoctorRecommendVO> getAllPlatformDoctors() {
		return  dsForRW.createQuery(DoctorRecommendVO.class).filter("groupId", "platform").order("-weight").asList();
	}
	
	@Override
	public DoctorRecommend getByGroupAndGroupDocId(String groupId, String groupDoctorId) {
		return dsForRW.createQuery(DoctorRecommendVO.class).field("groupId").equal(groupId)
														   .field("groupDocId").equal(groupDoctorId).get();
	}
	
	public List<Integer> getDoctorIdsByGroupId(String groupId) {
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		//TODO 排序问题
		List<String> groupDoctorIds = Lists.newArrayList();
		DBCursor cursor = dsForRW.getDB().getCollection("t_doctor_recommend").find(query);
		while (cursor.hasNext()) {
			DBObject object = cursor.next();
			String groupDoctorId = MongodbUtil.getString(object, "groupDocId");
			if (StringUtils.isNotEmpty(groupDoctorId)) {
				groupDoctorIds.add(groupDoctorId);
			}
		}
		
		//根据groupDoctorIds查询到doctorId
		DBObject gDocQuery = new BasicDBObject();
		BasicDBList gDocIdIn = new BasicDBList();
		DBObject gDocIdQuery = new BasicDBObject();
		if (groupDoctorIds != null && groupDoctorIds.size() > 0) {
			for (String gDocId : groupDoctorIds) {
				gDocIdIn.add(new ObjectId(gDocId));
			}
		}
		gDocIdQuery.put(QueryOperators.IN, gDocIdIn);
		gDocQuery.put("_id", gDocIdQuery);
		
		List<Integer> doctorIds = Lists.newArrayList();
		DBCursor gDocCursor = dsForRW.getDB().getCollection("c_group_doctor").find(gDocQuery);
		while (gDocCursor.hasNext()) {
			DBObject object = gDocCursor.next();
			Integer doctorId = MongodbUtil.getInteger(object, "doctorId");
			doctorIds.add(doctorId);
		}
		return doctorIds;
	}


	@Override
	public List<User> getUserByName(String name) {
		return dsForRW.createQuery("user", User.class).filter("name", name).filter("userType", UserEnum.UserType.doctor.getIndex())
				.filter("status", UserEnum.UserStatus.normal.getIndex()).asList();
		
	}
}
