package com.dachen.health.teachCenter.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.teachCenter.dao.IDiseaseTreeDao;
import com.dachen.health.teachCenter.entity.po.GroupDisease;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository
public class DiseaseTreeDaoImpl extends NoSqlRepository implements
		IDiseaseTreeDao {
	private static Logger logger = LoggerFactory
			.getLogger(DiseaseTreeDaoImpl.class);

	@Override
	public List<GroupDisease> getGroupDiseaseTree(String groupId) {
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		// if(!groupId.equals("system")){
		query.put("count", new BasicDBObject("$gt", 0));
		// }
		DBCursor cursor = dsForRW.getDB().getCollection("i_group_disease")
				.find(query).sort(new BasicDBObject("weight", -1));
		List<GroupDisease> gdList = new ArrayList<GroupDisease>();
		List<String> aList = null;
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			aList = new ArrayList<String>();
			GroupDisease vo = new GroupDisease();
			vo.setDiseaseId(obj.get("diseaseId").toString());
			vo.setGroupId(obj.get("groupId").toString());
			vo.setName(obj.get("name").toString());
			vo.setParent(obj.get("parent").toString());
			vo.setCount(Integer.parseInt(obj.get("count").toString()));
			Object aid = obj.get("articleId");
			if (aid != null) {
				List<Object> oList = (BasicDBList) aid;
				for (Object temp : oList) {
					aList.add(temp.toString());
				}
			}
			vo.setArticleId(aList);
			vo.setId(new ObjectId(obj.get("_id").toString()));
			int weight = -1;
			if (obj.get("weight") != null) {
				weight = MongodbUtil.getInteger(obj, "weight");
			}
			vo.setWeight(weight);
			gdList.add(vo);
		}
		cursor.close();
		return gdList;
	}

	@Override
	public boolean updateCount(GroupDisease gd) {
		boolean result = false;
		DBObject query = new BasicDBObject();
		query.put("diseaseId", gd.getDiseaseId());
		query.put("groupId", gd.getGroupId());
		DBObject update = new BasicDBObject();
		update.put("count", gd.getCount());
		update.put("diseaseId", gd.getDiseaseId());
		update.put("groupId", gd.getGroupId());
		update.put("name", gd.getName());
		update.put("parent", gd.getParent());
		update.put("articleId", gd.getArticleId());
		int n = dsForRW.getDB().getCollection("i_group_disease")
				.update(query, update, false, false).getN();
		if (n > 0) {
			result = true;
		}
		return result;
	}

	@Override
	public GroupDisease getGroupDisease(GroupDisease gd) {
		DBObject query = new BasicDBObject();
		logger.error("getGroupDisease===" + "disease:" + gd.getDiseaseId()
				+ ";" + "groupId:" + gd.getGroupId());
		query.put("diseaseId", gd.getDiseaseId().trim().toUpperCase());
		query.put("groupId", gd.getGroupId().trim());
		DBObject obj = dsForRW.getDB().getCollection("i_group_disease")
				.findOne(query);
		GroupDisease vo = null;
		if (obj != null) {
			vo = new GroupDisease();
			vo.setDiseaseId(MongodbUtil.getString(obj, "diseaseId"));
			Object aid = obj.get("articleId");
			List<String> aList = new ArrayList<String>();
			if (aid != null) {
				List<Object> oList = (BasicDBList) aid;
				for (Object temp : oList) {
					aList.add(temp.toString());
				}
			}
			vo.setArticleId(aList);
			vo.setGroupId(MongodbUtil.getString(obj, "groupId"));
			vo.setCount(MongodbUtil.getInteger(obj, "count"));
			vo.setParent(MongodbUtil.getString(obj, "parent"));
			vo.setName(MongodbUtil.getString(obj, "name"));
			int weight = -1;
			if (obj.get("weight") != null) {
				weight = MongodbUtil.getInteger(obj, "weight");
			}
			vo.setWeight(weight);
		}
		return vo;
	}

	@Override
	public GroupDisease saveGroupDisease(GroupDisease gd) {
		GroupDisease vo = getGroupDisease(gd);
		if (vo == null) {
			vo = new GroupDisease();
			vo.setDiseaseId(gd.getDiseaseId());
			vo.setGroupId(gd.getGroupId());
			vo.setArticleId(new ArrayList<String>());
			vo.setCount(0);
			logger.error("saveGroupDisease" + "disease:" + gd.getDiseaseId()
					+ ";" + "groupId:" + gd.getGroupId());
			DBCursor cursor = dsForRW.getDB().getCollection("b_disease_type")
					.find();
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				DBObject savaDate = new BasicDBObject();
				savaDate.put("diseaseId", MongodbUtil.getString(obj, "_id"));
				savaDate.put("groupId", gd.getGroupId());
				savaDate.put("name", MongodbUtil.getString(obj, "name"));
				savaDate.put("parent", MongodbUtil.getString(obj, "parent"));
				savaDate.put("articleId", new ArrayList<String>());
				savaDate.put("count", 0);
				savaDate.put("weight", MongodbUtil.getInteger(obj, "weight"));
				if (gd.getDiseaseId().equals(MongodbUtil.getString(obj, "_id"))) {
					vo.setParent(MongodbUtil.getString(obj, "parent"));
					vo.setName(MongodbUtil.getString(obj, "name"));
					vo.setWeight(MongodbUtil.getInteger(obj, "weight"));
				}
				dsForRW.getDB().getCollection("i_group_disease").save(savaDate);
			}
			cursor.close();
			gd.setDiseaseId("QT");
			GroupDisease qt = saveQTDisease(gd);// 保存“其它”
			// 保存其它（没有输入病种分类）
			if (StringUtil.isEmpty(vo.getName())) {
				System.err.println("diseaseID:" + vo.getDiseaseId());
				System.err.println("groupID:" + vo.getGroupId());
				vo.setParent(qt.getParent());
				vo.setName(qt.getName());
				vo.setWeight(qt.getWeight());
			}
		}
		return vo;
	}

	@Override
	public DiseaseTypeVO getDiseaseById(String id) {
		if (StringUtil.isEmpty(id)) {
			return null;
		}
		DBObject query = new BasicDBObject();
		query.put("_id", id);
		DBObject obj = dsForRW.getDB().getCollection("b_disease_type")
				.findOne(query);
		DiseaseTypeVO vo = new DiseaseTypeVO();
		if (obj != null) {
			vo.setId(MongodbUtil.getString(obj, "_id"));
			vo.setLeaf(Boolean.valueOf(MongodbUtil.getString(obj, "isLeaf")));
			vo.setName(MongodbUtil.getString(obj, "name"));
			vo.setParent(MongodbUtil.getString(obj, "parent"));
		}
		return vo;
	}

	@Override
	public List<GroupDisease> getAllGroupDisease(String groupId) {
		DBObject query = new BasicDBObject();
		query.put("groupId", groupId);
		DBCursor cursor = dsForRW.getDB().getCollection("i_group_disease")
				.find(query).sort(new BasicDBObject("weight", -1));
		List<GroupDisease> gdList = new ArrayList<GroupDisease>();
		List<String> aList = null;
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			aList = new ArrayList<String>();
			GroupDisease vo = new GroupDisease();
			vo.setDiseaseId(obj.get("diseaseId").toString());
			vo.setGroupId(obj.get("groupId").toString());
			vo.setName(obj.get("name").toString());
			vo.setParent(obj.get("parent").toString());
			vo.setCount(Integer.parseInt(obj.get("count").toString()));
			Object aid = obj.get("articleId");
			if (aid != null) {
				List<Object> oList = (BasicDBList) aid;
				for (Object temp : oList) {
					aList.add(temp.toString());
				}
			}
			vo.setArticleId(aList);
			vo.setId(new ObjectId(obj.get("_id").toString()));
			int weight = -1;
			if (obj.get("weight") != null) {
				weight = MongodbUtil.getInteger(obj, "weight");
			}
			vo.setWeight(weight);
			gdList.add(vo);
		}
		cursor.close();
		return gdList;

	}

	public GroupDisease saveQTDisease(GroupDisease gd) {
		if (gd == null) {
			throw new ServiceException("saveQTDisease gd is null");
		}
		gd.setDiseaseId("QT");
		GroupDisease vo = getGroupDisease(gd);
		logger.error("saveQTDisease" + "disease:" + gd.getDiseaseId() + ";"
				+ "groupId:" + gd.getGroupId());
		if (vo == null) {
			logger.error("saveQTDisease== null" + "disease:"
					+ gd.getDiseaseId() + ";" + "groupId:" + gd.getGroupId());
			vo = new GroupDisease();
			vo.setDiseaseId("QT");
			vo.setName("其它");
			vo.setParent("0");
			vo.setGroupId(gd.getGroupId().trim());
			vo.setArticleId(new ArrayList<String>());
			vo.setCount(0);
			vo.setWeight(0);

			DBObject savaDate = new BasicDBObject();
			savaDate.put("diseaseId", "QT");
			savaDate.put("groupId", gd.getGroupId().trim());
			savaDate.put("name", "其它");
			savaDate.put("parent", "0");
			savaDate.put("count", 0);
			savaDate.put("articleId", new ArrayList<String>());
			savaDate.put("weight", -1);
			dsForRW.getDB().getCollection("i_group_disease").save(savaDate);
		}
		return vo;

	}

	@Override
	public boolean updateBatchGroup(GroupDisease gd, List<String> list,
			String type) {
		if (list == null) {
			return false;
		}
		BasicDBList values = new BasicDBList();
		for (String groupId : list) {
			values.add(groupId);
		}
		DBObject groupId = new BasicDBObject("$in", values);
		DBObject query = new BasicDBObject();
		query.put("diseaseId", gd.getDiseaseId());
		query.put("groupId", groupId);

		DBObject update = new BasicDBObject();
		if (type.equals("+")) {
			update.put("$inc", new BasicDBObject("count", 1));
			update.put("$addToSet", new BasicDBObject("articleId", gd
					.getArticleId().get(0)));
		} else if (type.equals("-")) {
			update.put("$inc", new BasicDBObject("count", -1));
			update.put("$pull", new BasicDBObject("articleId", gd
					.getArticleId().get(0)));
		} else {
			return false;
		}
		WriteResult result = dsForRW.getDB().getCollection("i_group_disease")
				.update(query, update, false, true);
		if (result.getN() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 转换List对象
	 * 
	 * @param groupIds
	 * @return
	 */
	private BasicDBList getGroupBasicList(List<String> groupIds) {
		BasicDBList userIds = new BasicDBList();

		if (null != groupIds && groupIds.size() > 0) {
			for (String groupId : groupIds) {
				userIds.add(groupId);
			}
		}
		return userIds;
	}

	@Override
	public List<GroupDisease> getMoreGroupDiseaseTree(List<String> groupIds) {

		DBObject query = new BasicDBObject();
		query.put("groupId", new BasicDBObject("$in",
				getGroupBasicList(groupIds)));
		query.put("count", new BasicDBObject("$gt", 0));
		DBCursor cursor = dsForRW.getDB().getCollection("i_group_disease")
				.find(query).sort(new BasicDBObject("weight", -1));
		List<GroupDisease> gdList = new ArrayList<GroupDisease>();
		List<String> aList = null;
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			aList = new ArrayList<String>();
			GroupDisease vo = new GroupDisease();
			vo.setDiseaseId(obj.get("diseaseId").toString());
			vo.setGroupId(obj.get("groupId").toString());
			vo.setName(obj.get("name").toString());
			vo.setParent(obj.get("parent").toString());
			vo.setCount(Integer.parseInt(obj.get("count").toString()));
			Object aid = obj.get("articleId");
			if (aid != null) {
				List<Object> oList = (BasicDBList) aid;
				for (Object temp : oList) {
					aList.add(temp.toString());
				}
			}
			vo.setArticleId(aList);
			vo.setId(new ObjectId(obj.get("_id").toString()));
			int weight = -1;
			if (obj.get("weight") != null) {
				weight = MongodbUtil.getInteger(obj, "weight");
			}
			vo.setWeight(weight);
			gdList.add(vo);
		}
		cursor.close();
		return gdList;
	}

	@Override
	public List<GroupDisease> getAllMoreGroupDisease(List<String> groupIds) {

		DBObject query = new BasicDBObject();
		query.put("groupId", new BasicDBObject("$in",
				getGroupBasicList(groupIds)));
		DBCursor cursor = dsForRW.getDB().getCollection("i_group_disease")
				.find(query).sort(new BasicDBObject("weight", -1));
		List<GroupDisease> gdList = new ArrayList<GroupDisease>();
		List<String> aList = null;
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			aList = new ArrayList<String>();
			GroupDisease vo = new GroupDisease();
			vo.setDiseaseId(obj.get("diseaseId").toString());
			vo.setGroupId(obj.get("groupId").toString());
			vo.setName(obj.get("name").toString());
			vo.setParent(obj.get("parent").toString());
			vo.setCount(Integer.parseInt(obj.get("count").toString()));
			Object aid = obj.get("articleId");
			if (aid != null) {
				List<Object> oList = (BasicDBList) aid;
				for (Object temp : oList) {
					aList.add(temp.toString());
				}
			}
			vo.setArticleId(aList);
			vo.setId(new ObjectId(obj.get("_id").toString()));
			int weight = -1;
			if (obj.get("weight") != null) {
				weight = MongodbUtil.getInteger(obj, "weight");
			}
			vo.setWeight(weight);
			gdList.add(vo);
		}
		cursor.close();
		return gdList;

	}

}
