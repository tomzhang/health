package com.dachen.health.teachCenter.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.constant.ArticleEnum;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.teachCenter.dao.IArticleDao;
import com.dachen.health.teachCenter.entity.param.ArticleParam;
import com.dachen.health.teachCenter.entity.po.ArticleCollect;
import com.dachen.health.teachCenter.entity.po.ArticleTop;
import com.dachen.health.teachCenter.entity.po.ArticleVisit;
import com.dachen.health.teachCenter.entity.po.GroupDisease;
import com.dachen.health.teachCenter.entity.vo.ArticleVO;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository
@Deprecated
public class ArticleDaoImpl extends NoSqlRepository implements IArticleDao {

	public Map<String, Object> findArticleById(String articleId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(articleId));
		query.put("enabled", "true");
		DBObject obj = dsForRW.getDB().getCollection("i_article")
				.findOne(query);
		if (obj != null) {
			resultMap
					.put("name",
							obj.get("title") == null ? "" : obj.get("title")
									.toString());
			resultMap.put("url", obj.get("url") == null ? "" : obj.get("url")
					.toString());
		}
		return resultMap;
	}

	public ArticleVO getArticleByIdNoEnabled(String articleId) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(articleId));
		DBObject obj = dsForRW.getDB().getCollection("i_article")
				.findOne(query);
		ArticleVO vo = null;
		if (obj != null) {
			Map<String, User> userMap =  null;
			if(MongodbUtil.getInteger(obj, "createType").intValue() != ArticleEnum.CreaterType.system.getIndex()){
				String author = MongodbUtil.getString(obj,"author");
				if(isNumeric(author)){
					userMap =  getAllUser(Integer.parseInt(author));
				}
			}
			
			Map<String, DiseaseTypeVO> disMap = getAllDisease(null);
			Map<String, ArticleTop> topMap = getArticleTop(new ArticleParam());
			vo = setField(obj, userMap, disMap, topMap);
			if (vo != null) {
				vo.setContent(MongodbUtil.getString(obj, "content"));
			}
		}
		return vo;
	}

	@Override
	public ArticleVO getArticleById(String articleId) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(articleId));
		query.put("enabled", "true");
		
		// 这里先获取到屏蔽的集团，再查询i_article表时过滤掉屏蔽的集团的(2016-6-6傅永德)
		List<String> skipGroupIds = Lists.newArrayList();
		DBObject skipGroupQuery = new BasicDBObject();
		skipGroupQuery.put("skip", "S");
		DBCursor cursor = dsForRW.getDB().getCollection("c_group").find(skipGroupQuery);
		
		while(cursor.hasNext()) {
			DBObject skipGroup = cursor.next();
			String id = MongodbUtil.getString(skipGroup, "_id");
			skipGroupIds.add(id);
		}
		
		query.put("groupId", new BasicDBObject("$nin", skipGroupIds));
		
		DBObject obj = dsForRW.getDB().getCollection("i_article").findOne(query);
		ArticleVO vo = null;
		if (obj != null) {
			Map<String, DiseaseTypeVO> disMap = getAllDisease(null);
			Map<String, ArticleTop> topMap = getArticleTop(new ArticleParam());
			vo = setField(obj, null, disMap, topMap);
			int type = MongodbUtil.getInteger(obj,"createType");
			if(type == ArticleEnum.CreaterType.system.getIndex()){
				vo.setAuthorName("玄关患教中心");
			}else{
				if(isNumeric(vo.getAuthor())){
					Map<String, User> userMap = getAllUser(Integer.parseInt(vo.getAuthor()));
					User user = userMap.get(vo.getAuthor());
					if (user != null) {
						vo.setDoctor(user.getDoctor());
						vo.setAuthorName(user.getName());
						vo.setGroupName(user.getGroupRemark());
					}
				}else{
					//集团ID
					DBObject gObj = getGroupById(vo.getAuthor());
					if(gObj != null){
						vo.setAuthorName(MongodbUtil.getString(gObj, "name"));
						vo.setGroupName(MongodbUtil.getString(gObj, "name"));
					}
				}
			}
			if (vo != null) {
				vo.setContent(MongodbUtil.getString(obj, "content"));
			}
		}
		return vo;
	}

	@Override
	public void saveArticleVisitor(ArticleParam articleParam) {
		DBObject saveData = new BasicDBObject();
		saveData.put("articleId", articleParam.getArticleId());
		saveData.put("visitorId", articleParam.getCreaterId());
		saveData.put("visitTime", new Date().getTime());
		dsForRW.getDB().getCollection("i_article_visit").save(saveData);
		DBObject obj = dsForRW.getDB().getCollection("i_article").findOne(new BasicDBObject("_id",new ObjectId(articleParam.getArticleId())));
		if(obj != null){
			Long count = MongodbUtil.getLong(obj, "visitCount");
			count = count==null?1:count +1;
			DBObject update = new BasicDBObject();
			update.put("visitCount", count);
			dsForRW.getDB().getCollection("i_article").update(new BasicDBObject("_id",new ObjectId(articleParam.getArticleId())), 
					new BasicDBObject("$set", update), false, true);
		}
		
	}

	@Override
	public boolean delArticleVisitor(String articleId) {
		DBObject query = new BasicDBObject();
		query.put("articleId", articleId);
		dsForRW.getDB().getCollection("i_article_visit").remove(query);
		return true;
	}

	@Override
	public Map<String, Object> getArticleByDoctId(ArticleParam articleParam) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<ArticleCollect> list = new ArrayList<ArticleCollect>();
		DBObject query = new BasicDBObject();
		query.put("collectorId", articleParam.getCreaterId());

		int pageIndex = articleParam.getPageIndex(), pageSize = articleParam
				.getPageSize();
		int asc = -1;
		if (articleParam.getSortType().equals("asc")) {
			asc = 1;
		}
		DBCollection collection = dsForRW.getDB().getCollection(
				"i_article_collect");
		DBCursor cursor = collection.find(query)
				.sort(new BasicDBObject(articleParam.getSortBy(), asc))
				.skip((pageIndex - 1) * pageSize).limit(pageSize);
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			ArticleCollect collect = new ArticleCollect();
			collect.setArticleId(MongodbUtil.getString(dbObj, "articleId"));
			collect.setCollectorId(MongodbUtil.getString(dbObj, "collectorId"));
			collect.setCollectTime(MongodbUtil.getLong(dbObj, "collectTime"));
			list.add(collect);
		}
		cursor.close();
		result.put("count", collection.find(query).count());
		result.put("list", list);
		return result;
	}

	@Override
	public boolean updateArticle(ArticleParam articleParam) {
		// 更新对应的静态文件
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(articleParam.getArticleId()));
		query.put("enabled", "true");
		DBObject update = dsForRW.getDB().getCollection("i_article")
				.findOne(query);
		if (update == null) {
			return false;
		}

		// 更新文章基本信息
		// if(!StringUtil.isEmpty(articleParam.getAuthor())) {
		update.put("author", articleParam.getAuthor());
		// }
		// if(!StringUtil.isEmpty(articleParam.getUrl())){
		update.put("url", articleParam.getUrl());
		// }

		// if(articleParam.getIsShare() !=null) {
		update.put("isShare", articleParam.getIsShare());
		// }

		// if(articleParam.getIsShow()!=null){
		update.put("isShow", articleParam.getIsShow());
		// }

		update.put("copyPath", articleParam.getCopyPath());
		update.put("copy_small", articleParam.getCopy_small());

		// if(!StringUtil.isEmpty(articleParam.getDiseaseId())){
		update.put("diseaseId", articleParam.getDiseaseId());
		// }

		// if(articleParam.getTags()!=null){
		update.put("tags", articleParam.getTags());
		// }
		// if(!StringUtil.isEmpty(articleParam.getDescription())) {
		update.put("description", articleParam.getDescription());
		// }

		// if(!StringUtil.isEmpty(articleParam.getContent())){
		update.put("content", articleParam.getContent());
		// }

		update.put("title", articleParam.getTitle());
		update.put("lastUpdateTime", new Date().getTime());
		WriteResult result = dsForRW.getDB().getCollection("i_article")
				.update(query, new BasicDBObject("$set", update), false, true);
		if (result.getN() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean delCollectArticleById(ArticleParam articleParam) {
		boolean result = false;
		BasicDBObject query = new BasicDBObject();
		query.put("articleId", articleParam.getArticleId());

		if (!StringUtil.isEmpty(articleParam.getCreaterId())) {
			query.put("collectorId", articleParam.getCreaterId());
		}
		if (articleParam.getCreateType() != null) {
			query.put("collectorType", articleParam.getCreateType());
		}
		if (articleParam.getCollectType() != null) {
			query.put("collectType", articleParam.getCollectType());
		}
		WriteResult writeResult = dsForRW.getDB()
				.getCollection("i_article_collect").remove(query);
		if (writeResult != null && writeResult.getN() > 0) {
			result = true;
		}
		return result;
	}

	@Override
	public Map<String, ArticleCollect> getCollectArticleByParam(
			ArticleParam articleParam) {
		Map<String, ArticleCollect> map = new HashMap<String, ArticleCollect>();
		DBObject query = new BasicDBObject();
		if (!StringUtil.isEmpty(articleParam.getArticleId())) {
			query.put("articleId", articleParam.getArticleId());
		}
		if (!StringUtil.isEmpty(articleParam.getCreaterId())) {
			query.put("collectorId", articleParam.getCreaterId());
		}
		// if(articleParam.getCreateType() != null){
		// query.put("collectorType",articleParam.getCreateType());
		// }
		// if(articleParam.getCollectType() != null){
		// query.put("collectType",articleParam.getCollectType());
		// }
		DBCursor cursor = dsForRW.getDB().getCollection("i_article_collect")
				.find(query);
		ArticleCollect collect = null;
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			collect = new ArticleCollect();
			collect.setArticleId(MongodbUtil.getString(obj, "articleId"));
			collect.setCollectorId(MongodbUtil.getString(obj, "collectorId"));
			collect.setCollectorType(MongodbUtil.getInteger(obj,
					"collectorType"));
			collect.setCollectType(MongodbUtil.getInteger(obj, "collectType"));
			String key = collect.getArticleId() + "-"
					+ collect.getCollectorId();
			map.put(key, collect);
		}
		cursor.close();
		return map;
	}

	@Override
	public boolean saveCollectArticle(ArticleParam articleParam) {
		boolean result = true;
		DBCollection collection = dsForRW.getDB().getCollection(
				"i_article_collect");
		DBObject saveData = new BasicDBObject();
		saveData.put("articleId", articleParam.getArticleId());
		saveData.put("collectorId", articleParam.getCreaterId());
		saveData.put("collectType", articleParam.getCollectType());
		saveData.put("collectorType", articleParam.getCreateType());
		if (articleParam.getCreateType() == 1) {
			saveData.put("collectorId", "system");
		}
		saveData.put("collectTime", new Date().getTime());
		collection.save(saveData);// 保存个人
		return result;
	}

	@Override
	public boolean delTopArticle(ArticleParam articleParam) {
		BasicDBObject queryObject = new BasicDBObject();
		queryObject.put("articleId", articleParam.getArticleId());
		dsForRW.getDB().getCollection("i_article_top").remove(queryObject);
		return true;
	}

	@Override
	public boolean saveTopArticle(ArticleParam articleParam) {
		boolean result = true;
		DBCollection collection = dsForRW.getDB()
				.getCollection("i_article_top");
		DBObject saveData = new BasicDBObject();
		saveData.put("articleId", articleParam.getArticleId());
		DBObject obj = collection.findOne(saveData);
		if (obj != null) {
			return false;
		}
		saveData.put("priority", articleParam.getPriority());
		saveData.put("lastUpdateTime", new Date().getTime());
		collection.save(saveData);
		return result;
	}

	@Override
	public List<ArticleTop> getAllTopArticle() {
		List<ArticleTop> list = new ArrayList<ArticleTop>();
		BasicDBObject sort = new BasicDBObject();
		sort.put("priority", 1);
		DBCursor cursor = dsForRW.getDB().getCollection("i_article_top").find()
				.sort(sort);
		ArticleTop top = null;
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			top = new ArticleTop();
			top.setArticleId(obj.get("articleId").toString());
			top.setPriority(Integer.parseInt(obj.get("priority").toString()));
			list.add(top);
		}
		cursor.close();
		return list;
	}

	@Override
	public ArticleTop getTopArticleById(String articleId) {
		BasicDBObject query = new BasicDBObject();
		query.put("articleId", articleId);
		DBObject obj = dsForRW.getDB().getCollection("i_article_top")
				.findOne(query);
		ArticleTop top = null;
		if (obj != null) {
			top = new ArticleTop();
			top.setArticleId(MongodbUtil.getString(obj, "articleId"));
			top.setPriority(MongodbUtil.getInteger(obj, "priority"));
			top.setLastUpdateTime(MongodbUtil.getLong(obj, "lastUpdateTime"));
		}
		return top;
	}

	@Override
	public boolean updateTopArticle(ArticleTop articleTop) {
		boolean result = false;
		DBObject query = new BasicDBObject();
		query.put("articleId", articleTop.getArticleId());
		DBObject update = new BasicDBObject();
		update.put("priority", articleTop.getPriority());
		update.put("lastUpdateTime", new Date().getTime());
		WriteResult writeResult = dsForRW.getDB()
				.getCollection("i_article_top")
				.update(query, new BasicDBObject("$set", update));
		if (writeResult != null && writeResult.getN() > 0) {
			result = true;
		}
		return result;
	}

	@Override
	public Map<String, Object> getNewArcticleByParam(ArticleParam articleParam) {
		Map<String, Object> result = new HashMap<String, Object>();

		DBObject queryArt = new BasicDBObject();
		List<String> colList = new ArrayList<String>();
		if (articleParam.getCreateType() == 0) {// 平台加集团
			colList.add("system");
			if (!StringUtil.isEmpty(articleParam.getGroupId())) {
				colList.addAll(articleParam.getGroupIds());
			}
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.group
				.getIndex()) {// 集团的
			colList.addAll(articleParam.getGroupIds());
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system
				.getIndex()) {
			colList.add("system");
		}else if(articleParam.getCreateType() == 4){
			//针对博得的
			colList.addAll(articleParam.getGroupIds());
		}else {
			result.put("count", 0);
			result.put("list", null);
			return result;
		}
		queryArt.put("collectorId", new BasicDBObject("$in", colList));
		
		List<ObjectId> list = new ArrayList<ObjectId>();
		DBCollection collection = dsForRW.getDB().getCollection("i_article_collect");
		DBCursor cursor = collection.find(queryArt);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			String articleId = MongodbUtil.getString(obj, "articleId");
			list.add(new ObjectId(articleId));
		}
		cursor.close();

		
		Map<String, DiseaseTypeVO> disMap = getAllDisease(null);
		Map<String, ArticleCollect> collectMap = getCollectArticleByParam(new ArticleParam());
		Map<String, ArticleTop> topMap = getArticleTop(new ArticleParam());
		DBObject isShare = new BasicDBObject();
		isShare.put("isShare", 1);
		isShare.put("enabled", "true");
		BasicDBObject collect = new BasicDBObject();
		collect.put("enabled", "true");
		if (!StringUtil.isEmpty(articleParam.getDiseaseId())) {
			DiseaseTypeVO disease = disMap.get(articleParam.getDiseaseId()
					.toUpperCase());
			if (disease != null && disease.getParent().equals("0")) {
				// 父节点名称匹配
				Pattern pattern = Pattern.compile(
						"^" + articleParam.getDiseaseId() + ".*$",
						Pattern.CASE_INSENSITIVE);
				collect.put("diseaseId", pattern);
				isShare.put("diseaseId", pattern);
			} else {
				// 二级则全等匹配
				collect.put("diseaseId", articleParam.getDiseaseId());
				isShare.put("diseaseId", articleParam.getDiseaseId());
			}
		}
		collect.put("_id", new BasicDBObject("$in", list));

		BasicDBList condList = new BasicDBList();
		condList.add(collect);
		if (articleParam.getCreateType() == 0
				|| articleParam.getCreateType() == 1) {
			condList.add(isShare);
		}
		int asc = -1;
		if (articleParam.getSortType().equals("asc")) {
			asc = 1;
		}
		collection = dsForRW.getDB().getCollection("i_article");
		DBCursor cursorArt = collection
				.find(new BasicDBObject("$or", condList))
				.sort(new BasicDBObject(articleParam.getSortBy(), asc))
				.skip((articleParam.getPageIndex() - 1)
						* articleParam.getPageSize())
				.limit(articleParam.getPageSize());
		List<ArticleVO> vList = new ArrayList<ArticleVO>();
		List<Integer> userList = new ArrayList<Integer>();
		
		while (cursorArt.hasNext()) {
			DBObject obj = cursorArt.next();
			ArticleVO vo = setField(obj, null, disMap, topMap);
			if (articleParam.getCreateType() == 1) {
				articleParam.setCreaterId("system");
			} else if (articleParam.getCreateType() == 2
					|| articleParam.getCreateType() == 4) {
				articleParam.setCreaterId(articleParam.getGroupId());
			}
			
			if(MongodbUtil.getInteger(obj, "createType").intValue() != ArticleEnum.CreaterType.system.getIndex()){
				String author = MongodbUtil.getString(obj,"author");
				if(isNumeric(author)){
					userList.add(Integer.parseInt(author));
				}
				
			}
			
			articleParam.setArticleId(vo.getId());
			setCollectArticle(articleParam, collectMap, vo);
			vList.add(vo);
		}
		cursorArt.close();
		if(vList.size() == 0){
			result.put("count", 0);
			result.put("list",new ArrayList<ArticleVO>());
			return result;
		}
		Map<String, User> userMap = userList.size() == 0 ? new HashMap<String, User>():getAllUser((Integer[])userList.toArray(new Integer[userList.size()]));
		for(ArticleVO vo :vList){
			if (vo.getCreateType() == ArticleEnum.CreaterType.system
					.getIndex()) {
				vo.setAuthorName("玄关患教中心");
			} else {
				if(isNumeric(vo.getAuthor())){
					User user = userMap.get(vo.getAuthor());
					if (user != null) {
						vo.setDoctor(user.getDoctor());
						vo.setAuthorName(user.getName());
						vo.setGroupName(user.getGroupRemark());
					}
				}else{
					//集团ID
					DBObject gObj = getGroupById(vo.getAuthor());
					if(gObj != null){
						vo.setAuthorName(MongodbUtil.getString(gObj, "name"));
						vo.setGroupName(MongodbUtil.getString(gObj, "name"));
					}
				}
			}
		}
		if (articleParam.getCreateType() == 1) {
			vList = setTop(vList);
		}
		result.put("count", collection.find(new BasicDBObject("$or", condList))
				.count());
		result.put("list", vList);
		return result;
	}

	@Override
	public Map<String, Object> getHotArcticleByParam(ArticleParam articleParam) {
		long s = System.currentTimeMillis();
		Map<String, Object> result = new HashMap<String, Object>();
		BasicDBList condList = new BasicDBList();

		DBObject share = new BasicDBObject();
		share.put("isShare", 1);
		share.put("enabled", "true");

		DBObject collect = new BasicDBObject();
		collect.put("enabled", "true");

		DBObject queryArt = new BasicDBObject();
		List<String> colList = new ArrayList<String>();
		if (articleParam.getCreateType() == 0) {// 平台加集团
			colList.add("system");
			if (!StringUtil.isEmpty(articleParam.getGroupId())) {
				colList.addAll(articleParam.getGroupIds());
			}
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.group
				.getIndex()) {// 集团的
			colList.addAll(articleParam.getGroupIds());
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system
				.getIndex()) {
			colList.add("system");
		} else if(articleParam.getCreateType() == 7){
			colList.addAll(articleParam.getGroupIds());
		}else {
			result.put("count", 0);
			result.put("list", null);
			return result;
		}
		queryArt.put("collectorId", new BasicDBObject("$in", colList));
		
		// 在收藏表查出所有收藏文章ID
		DBCollection collection = dsForRW.getDB().getCollection("i_article_collect");
		List<ObjectId> list = new ArrayList<ObjectId>();
		DBCursor cursor = collection.find(queryArt);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			String articleId = MongodbUtil.getString(obj, "articleId");
			list.add(new ObjectId(articleId));
		}
		cursor.close();
		collect.put("_id", new BasicDBObject("$in", list));

		
		Map<String, DiseaseTypeVO> disMap = getAllDisease(null);
		Map<String, ArticleCollect> collectMap = getCollectArticleByParam(new ArticleParam());
		Map<String, ArticleTop> topMap = getArticleTop(new ArticleParam());

		if (!StringUtil.isEmpty(articleParam.getDiseaseId())) {
			DiseaseTypeVO disease = disMap.get(articleParam.getDiseaseId()
					.toUpperCase());
			if (disease != null && disease.getParent().equals("0")) {
				// 父节点名称匹配
				Pattern pattern = Pattern.compile(
						"^" + articleParam.getDiseaseId() + ".*$",
						Pattern.CASE_INSENSITIVE);
				collect.put("diseaseId", pattern);
				share.put("diseaseId", pattern);
			} else {
				// 二级则全等匹配
				collect.put("diseaseId", articleParam.getDiseaseId());
				share.put("diseaseId", articleParam.getDiseaseId());
			}
		}

		if (articleParam.getCreatTime() != 0) {
			share.put("creatTime",
					new BasicDBObject("$gte", articleParam.getCreatTime()));
			collect.put("creatTime",
					new BasicDBObject("$gte", articleParam.getCreatTime()));
		}
		condList.add(collect);

		if (articleParam.getCreateType() == 0
				|| articleParam.getCreateType() == 1) {
			condList.add(share);
		}

		// 根据收藏ID以及病种查出所有文章
		collection = dsForRW.getDB().getCollection("i_article");

		Map<String, ArticleVO> maps = new HashMap<String, ArticleVO>();
		List<String> idList = new ArrayList<String>();
		List<Integer> userList = new ArrayList<Integer>();
		DBCursor cursorArt = collection
				.find(new BasicDBObject("$or", condList));
		while (cursorArt.hasNext()) {
			DBObject obj = cursorArt.next();
			
			if(MongodbUtil.getInteger(obj, "createType").intValue() != ArticleEnum.CreaterType.system.getIndex()){
				String author = MongodbUtil.getString(obj,"author");
				if(isNumeric(author)){
					userList.add(Integer.parseInt(author));
				}
			}
			
			ArticleVO vo = setField(obj, null, disMap, topMap);
			idList.add(vo.getId());
			articleParam.setArticleId(vo.getId());
			if (articleParam.getCreateType() == 1) {
				articleParam.setCreaterId("system");
			} else if (articleParam.getCreateType() == 2
					|| articleParam.getCreateType() == 4) {
				articleParam.setCreaterId(articleParam.getGroupId());
			}
			setCollectArticle(articleParam, collectMap, vo);
			maps.put(vo.getId(), vo);
		}
		cursorArt.close();
		Map<String, User> userMap = userList.size() == 0 ? new HashMap<String, User>():getAllUser((Integer[])userList.toArray(new Integer[userList.size()]));
		// 按文章浏览量降序存放所有文章
		List<ArticleVO> vList = new ArrayList<ArticleVO>();
		Map<String, Long> vMap = getVisitCount(idList, articleParam, true);
		Iterator<Entry<String, Long>> iterator = vMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Long> entry = iterator.next();
			String key = entry.getKey();
			Long value = entry.getValue();
			if (maps.containsKey(key)) {
				ArticleVO vo = maps.get(key);
				vo.setVisitCount(value);
				if (vo.getCreateType() == ArticleEnum.CreaterType.system.getIndex()) {
					vo.setAuthorName("玄关患教中心");
				} else {
					if(isNumeric(vo.getAuthor())){
						User user = userMap.get(vo.getAuthor());
						if (user != null) {
							vo.setDoctor(user.getDoctor());
							vo.setAuthorName(user.getName());
							vo.setGroupName(user.getGroupRemark());
						}
					}else{
						//就是集团ID
						DBObject gObj = getGroupById(vo.getAuthor());
						if(gObj != null){
							vo.setAuthorName(MongodbUtil.getString(gObj, "name"));
							vo.setGroupName(MongodbUtil.getString(gObj, "name"));
						}
					}
				}
				vList.add(vo);
			}
		}
		if (articleParam.getCreateType() == 1) {
			vList = setTop(vList);
		}
		result.put("count", vMap.get("count"));
		result.put("list", vList);

		long e = System.currentTimeMillis();
		System.out.println("Hot count is : " + (e - s));
		return result;
	}

	@Override
	public Map<String, Object> searchKeywordByParam(ArticleParam articleParam) {
		// 如果是根据发布时间排序,直接去i_article表里查
		Map<String, Object> result = new HashMap<String, Object>();
		List<ArticleVO> vList = new ArrayList<ArticleVO>();

		DBObject share = new BasicDBObject();
		share.put("enabled", "true");
		DBObject collect = new BasicDBObject();
		collect.put("enabled", "true");
		
		List<ObjectId> idList = new ArrayList<ObjectId>();
		for(String id : articleParam.getIds()){
			idList.add(new ObjectId(id));
		}
		
		collect.put("_id", new BasicDBObject("$in", idList));

		Pattern pattern = Pattern.compile("^.*" + articleParam.getTitle()
				+ ".*$", Pattern.CASE_INSENSITIVE);
		collect.put("title", pattern);
		share.put("title", pattern);

		BasicDBList condList = new BasicDBList();
		condList.add(collect);
		if (articleParam.getCreateType() == 0
				|| articleParam.getCreateType() == 1
				|| articleParam.getCreateType() == 5
				|| articleParam.getCreateType() == 4) {
			condList.add(share);// 只有平台才查分享的
		}
		DBObject query = new BasicDBObject("$or", condList);
		List<Integer> userList = new ArrayList<Integer>();
		Map<String, DiseaseTypeVO> disMap = getAllDisease(null);
		Map<String, ArticleCollect> collectMap = getCollectArticleByParam(new ArticleParam());
		Map<String, ArticleTop> topMap = getArticleTop(new ArticleParam());
		
		DBCollection collection  = dsForRW.getDB().getCollection("i_article");
		DBCursor cursor =  null;
		if (articleParam.getSortBy().equals("visit")) {
			// //根据收藏ID以及病种查出所有文章
			cursor = collection.find(query);
			Map<String, ArticleVO> maps = new HashMap<String, ArticleVO>();
			List<String> list = new ArrayList<String>();
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				
				if(MongodbUtil.getInteger(obj, "createType").intValue() != ArticleEnum.CreaterType.system.getIndex()){
					String author = MongodbUtil.getString(obj,"author");
					if(isNumeric(author)){
						userList.add(Integer.parseInt(author));
					}
				}
				
				
				ArticleVO vo = setField(obj, null, disMap, topMap);
				list.add(vo.getId());
				articleParam.setArticleId(vo.getId());
				if (articleParam.getCreateType() == 1) {
					articleParam.setCreaterId("system");
				} else if (articleParam.getCreateType() == 2
						|| articleParam.getCreateType() == 4) {
					articleParam.setCreaterId(articleParam.getGroupId());
				}
				// vo.setCollect(getCollectArticle(articleParam,collectMap));
				setCollectArticle(articleParam, collectMap, vo);
				maps.put(vo.getId().toString(), vo);
			}
			cursor.close();
			if(vList.size() == 0){
				result.put("count", 0);
				result.put("list",new ArrayList<ArticleVO>());
				return result;
			}
			// 按文章浏览量存放所有文章
			Map<String, User> userMap = userList.size() == 0 ? new HashMap<String, User>():getAllUser((Integer[])userList.toArray(new Integer[userList.size()]));
			Map<String, Long> vMap = getVisitCount(list, articleParam, false);
			Iterator<Entry<String, Long>> iterator = vMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Long> entry = iterator.next();
				String key = entry.getKey();
				Long value = entry.getValue();
				if (maps.containsKey(key)) {
					ArticleVO vo = maps.get(key);
					vo.setVisitCount(value);
					if (vo.getCreateType() == ArticleEnum.CreaterType.system
							.getIndex()) {
						vo.setAuthorName("玄关患教中心");
					} else {
						if(isNumeric(vo.getAuthor())){
							User user = userMap.get(vo.getAuthor());
							if (user != null) {
								vo.setDoctor(user.getDoctor());
								vo.setAuthorName(user.getName());
								vo.setGroupName(user.getGroupRemark());
							}
						}else{
							//集团ID
							DBObject gObj = getGroupById(vo.getAuthor());
							if(gObj != null){
								vo.setAuthorName(MongodbUtil.getString(gObj, "name"));
								vo.setGroupName(MongodbUtil.getString(gObj, "name"));
							}
						}
					}
					vList.add(vo);
				}
			}
			if (articleParam.getCreateType() == 1) {
				vList = setTop(vList);
			}
			// 分页
			int pageIndex = articleParam.getPageIndex();
			int pageSize = articleParam.getPageSize(); // 默认20条

			int start = (pageIndex - 1) * pageSize;
			int end = pageIndex * pageSize;
			int size = vList.size();
			if (start >= size) {
				result.put("count", size);
				result.put("list", null);
				return result;
			}
			if (end >= size) {
				end = size;
			}
			List<ArticleVO> data = vList.subList(start, end);
			result.put("count", size);
			result.put("list", data);
		} else {
			int asc = -1;
			if (articleParam.getSortType().equals("asc")) {
				asc = 1;
			}
			DBObject sort = new BasicDBObject();
			if (articleParam.getCreateType() == 1) {
				sort.put("isTop", asc);
			}
			sort.put(articleParam.getSortBy(), asc);
			List<String> idlist = new ArrayList<String>();
			cursor = collection
					.find(query)
					.sort(sort)
					.skip((articleParam.getPageIndex() - 1)
							* articleParam.getPageSize())
					.limit(articleParam.getPageSize());
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				
				if(MongodbUtil.getInteger(obj, "createType").intValue() != ArticleEnum.CreaterType.system.getIndex()){
					String author = MongodbUtil.getString(obj,"author");
					if(isNumeric(author)){
						userList.add(Integer.parseInt(author));
					}
				}
				ArticleVO vo = setField(obj, null, disMap, topMap);
				if (vo != null) {
					idlist.add(vo.getId());
					articleParam.setArticleId(vo.getId());
					if (articleParam.getCreateType() == 1) {
						articleParam.setCreaterId("system");
					} else if (articleParam.getCreateType() == 2
							|| articleParam.getCreateType() == 4) {
						articleParam.setCreaterId(articleParam.getGroupId());
					}
					setCollectArticle(articleParam, collectMap, vo);
					vList.add(vo);
				}
			}
			cursor.close();
			if(vList.size() == 0){
				result.put("count", 0);
				result.put("list",new ArrayList<ArticleVO>());
				return result;
			}
			Map<String, User> userMap = userList.size() == 0 ? new HashMap<String, User>():getAllUser((Integer[])userList.toArray(new Integer[userList.size()]));
			Map<String, Long> map = getVisitCount(idlist, articleParam, false);
			
			for (ArticleVO vo : vList) {
				long visit = map.get(vo.getId());
				vo.setVisitCount(visit);
				if (vo.getCreateType() == ArticleEnum.CreaterType.system
						.getIndex()) {
					vo.setAuthorName("玄关患教中心");
				} else {
					if(isNumeric(vo.getAuthor())){
						User user = userMap.get(vo.getAuthor());
						if (user != null) {
							vo.setDoctor(user.getDoctor());
							vo.setAuthorName(user.getName());
							vo.setGroupName(user.getGroupRemark());
						}
					}else{
						//集团ID
						DBObject gObj = getGroupById(vo.getAuthor());
						if(gObj != null){
							vo.setAuthorName(MongodbUtil.getString(gObj, "name"));
							vo.setGroupName(MongodbUtil.getString(gObj, "name"));
						}
					}
				}
			}
			result.put("count", collection.find(query).count());
			result.put("list", vList);
		}
		return result;
	}

	@Override
	public Map<String, Object> searchTagByParam(ArticleParam articleParam) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<ArticleVO> vList = new ArrayList<ArticleVO>();

		DBObject collect = new BasicDBObject();
		collect.put("enabled", "true");
		DBObject share = new BasicDBObject();
		share.put("isShare", 1);
		share.put("enabled", "true");

		BasicDBList collectorId = new BasicDBList();
		if (articleParam.getCreateType() == 0) {
			// 包含集团和平台
			collectorId.add(new BasicDBObject("collectorId", "system"));
			collectorId.add(new BasicDBObject("collectorId", articleParam
					.getGroupId()));
			share.put("isShare", 1);
		} else if (articleParam.getCreateType() == 1
				|| articleParam.getCreateType() == 4
				|| articleParam.getCreateType() == 5) {
			// 平台查平台 集团查平台，医生查平台
			collectorId.add(new BasicDBObject("collectorId", "system"));
			share.put("isShare", 1);
		} else if (articleParam.getCreateType() == 2
				|| articleParam.getCreateType() == 6) {
			// 集团查集团 医生查集团
			collectorId.add(new BasicDBObject("collectorId", articleParam
					.getCreaterId()));
			collectorId.add(new BasicDBObject("collectorId", articleParam
					.getGroupId()));
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.doctor
				.getIndex()) {
			// 医生
			collectorId.add(new BasicDBObject("collectorId", articleParam
					.getCreaterId()));
		}

		DBCollection collection = dsForRW.getDB().getCollection(
				"i_article_collect");
		List<ObjectId> idList = new ArrayList<ObjectId>();
		DBCursor cursor = collection
				.find(new BasicDBObject("$or", collectorId));
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			String articleId = MongodbUtil.getString(obj, "articleId");
			idList.add(new ObjectId(articleId));
		}
		cursor.close();
		collect.put("_id", new BasicDBObject("$in", idList));

		DBObject tag = new BasicDBObject();
		tag.put("$all", articleParam.getTags());
		collect.put("tags", tag);
		share.put("tags", tag);

		BasicDBList cond = new BasicDBList();
		cond.add(collect);
		if (articleParam.getCreateType() == 0
				|| articleParam.getCreateType() == 1
				|| articleParam.getCreateType() == 5
				|| articleParam.getCreateType() == 4) {
			cond.add(share);// 只有平台才查分享的
		}
		DBObject query = new BasicDBObject("$or", cond);

//		Map<String, User> userMap = getAllUser(null);
		Map<String, DiseaseTypeVO> disMap = getAllDisease(null);
		Map<String, ArticleCollect> collectMap = getCollectArticleByParam(new ArticleParam());
		collection = dsForRW.getDB().getCollection("i_article");
		Map<String, ArticleTop> topMap = getArticleTop(new ArticleParam());
		if (articleParam.getSortBy().equals("visit")) {
			// //根据收藏ID以及病种查出所有文章
			cursor = collection.find(query);
			Map<String, ArticleVO> maps = new HashMap<String, ArticleVO>();
			List<String> list = new ArrayList<String>();
			List<Integer> userList = new ArrayList<Integer>();
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				if(MongodbUtil.getInteger(obj, "createType").intValue() != ArticleEnum.CreaterType.system.getIndex()){
					String author = MongodbUtil.getString(obj,"author");
					if(isNumeric(author)){
						userList.add(Integer.parseInt(author));
					}
				}
				
				ArticleVO vo = setField(obj, null, disMap, topMap);
				list.add(vo.getId());
				if (articleParam.getCreateType() == 1) {
					articleParam.setCreaterId("system");
				} else if (articleParam.getCreateType() == 2
						|| articleParam.getCreateType() == 4) {
					articleParam.setCreaterId(articleParam.getGroupId());
				}
				articleParam.setArticleId(vo.getId());
				// vo.setCollect(getCollectArticle(articleParam,collectMap));
				setCollectArticle(articleParam, collectMap, vo);
				maps.put(vo.getId().toString(), vo);
			}
			cursor.close();
			// 按文章浏览量存放所有文章
			if(vList.size() == 0){
				result.put("count", 0);
				result.put("list",new ArrayList<ArticleVO>());
				return result;
			}
			Map<String, Long> vMap = getVisitCount(list, articleParam, false);
			Map<String, User> userMap = userList.size() == 0 ? new HashMap<String, User>():getAllUser((Integer[])userList.toArray(new Integer[userList.size()]));
			
			Iterator<Entry<String, Long>> iterator = vMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Long> entry = iterator.next();
				String key = entry.getKey();
				Long value = entry.getValue();
				if (maps.containsKey(key)) {
					ArticleVO vo = maps.get(key);
					vo.setVisitCount(value);
					if (vo.getCreateType() == ArticleEnum.CreaterType.system
							.getIndex()) {
						vo.setAuthorName("玄关患教中心");
					} else {
						if(isNumeric(vo.getAuthor())){
							User user = userMap.get(vo.getAuthor());
							if (user != null) {
								vo.setDoctor(user.getDoctor());
								vo.setAuthorName(user.getName());
								vo.setGroupName(user.getGroupRemark());
							}
						}else{
							//集团ID
							DBObject gObj = getGroupById(vo.getAuthor());
							if(gObj != null){
								vo.setAuthorName(MongodbUtil.getString(gObj, "name"));
								vo.setGroupName(MongodbUtil.getString(gObj, "name"));
							}
						}
					}
					vList.add(vo);
				}
			}
			if (articleParam.getCreateType() == 1) {
				vList = setTop(vList);
			}
			// 分页
			int pageIndex = articleParam.getPageIndex();
			int pageSize = articleParam.getPageSize(); // 默认20条

			int start = (pageIndex - 1) * pageSize;
			int end = pageIndex * pageSize;
			int size = vList.size();
			if (start >= size) {
				result.put("count", size);
				result.put("list", null);
				return result;
			}
			if (end >= size) {
				end = size;
			}
			List<ArticleVO> data = vList.subList(start, end);
			result.put("count", size);
			result.put("list", data);
		} else {
			int asc = -1;
			if (articleParam.getSortType().equals("asc")) {
				asc = 1;
			}
			DBObject sort = new BasicDBObject();
			if (articleParam.getCreateType() == 1) {
				sort.put("isTop", asc);
			}
			sort.put(articleParam.getSortBy(), asc);
			List<String> idlist = new ArrayList<String>();
			List<Integer> userList = new ArrayList<Integer>();
			cursor = collection
					.find(query)
					.sort(sort)
					.skip((articleParam.getPageIndex() - 1)
							* articleParam.getPageSize())
					.limit(articleParam.getPageSize());
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				
				if(MongodbUtil.getInteger(obj, "createType").intValue() != ArticleEnum.CreaterType.system.getIndex()){
					String author = MongodbUtil.getString(obj,"author");
					if(isNumeric(author)){
						userList.add(Integer.parseInt(author));
					}
				}
				
				ArticleVO vo = setField(obj, null, disMap, topMap);
				if (vo != null) {
					idlist.add(vo.getId());
					if (articleParam.getCreateType() == 1) {
						articleParam.setCreaterId("system");
					} else if (articleParam.getCreateType() == 2
							|| articleParam.getCreateType() == 4) {
						articleParam.setCreaterId(articleParam.getGroupId());
					}
					articleParam.setArticleId(vo.getId());
					vo.setCollect(getCollectArticle(articleParam, collectMap));
					vList.add(vo);
				}
			}
			cursor.close();
			if(vList.size() == 0){
				result.put("count", 0);
				result.put("list",new ArrayList<ArticleVO>());
				return result;
			}
//			Integer[] uList = userList.size() == 0 ? null:(Integer[])userList.toArray(new Integer[userList.size()]);
//			Map<String, User> userMap = getAllUser(uList);
			Map<String, User> userMap = userList.size() == 0 ? new HashMap<String, User>():getAllUser((Integer[])userList.toArray(new Integer[userList.size()]));
			Map<String, Long> map = getVisitCount(idlist, articleParam, false);
			for (ArticleVO vo : vList) {
				long visit = map.get(vo.getId());
				vo.setVisitCount(visit);
				if (vo.getCreateType() == ArticleEnum.CreaterType.system
						.getIndex()) {
					vo.setAuthorName("玄关患教中心");
				} else {
					if(isNumeric(vo.getAuthor())){
						User user = userMap.get(vo.getAuthor());
						if (user != null) {
							vo.setDoctor(user.getDoctor());
							vo.setAuthorName(user.getName());
							vo.setGroupName(user.getGroupRemark());
						}
					}else{
						//集团ID
						DBObject gObj = getGroupById(vo.getAuthor());
						if(gObj != null){
							vo.setAuthorName(MongodbUtil.getString(gObj, "name"));
							vo.setGroupName(MongodbUtil.getString(gObj, "name"));
						}
					}
				}
			}
			result.put("count", collection.find(query).count());
			result.put("list", vList);
		}

		return result;
	}

	@Override
	public ArticleVO saveArticle(ArticleParam articleParam) {
		BasicDBObject article = new BasicDBObject();
		article.put("author", articleParam.getAuthor());
		article.put("copyPath", articleParam.getCopyPath());
		article.put("copy_small", articleParam.getCopy_small());

		article.put("createrId", articleParam.getCreaterId());
		if (StringUtil.isEmpty(articleParam.getGroupId())) {
			article.put("groupId", "0");
		} else {
			article.put("groupId", articleParam.getGroupId());
		}
		article.put("createType", articleParam.getCreateType());
		if (articleParam.getCreateType() == 1) {
			article.put("createrId", "system");
		}
		article.put("diseaseId", articleParam.getDiseaseId());

		if (!StringUtil.isEmpty(articleParam.getDescription())) {
			article.put("description", articleParam.getDescription());
		}
		if (!StringUtil.isEmpty(articleParam.getContent())) {
			article.put("content", articleParam.getContent());
		}
		if (articleParam.getTags() != null) {
			article.put("tags", articleParam.getTags());
		}
		if (!StringUtil.isEmpty(articleParam.getUrl())) {
			article.put("url", articleParam.getUrl());
		}
		if (articleParam.getEdited() == null) {
			article.put("edited", 1);
		} else {
			article.put("edited", articleParam.getEdited());
		}

		article.put("title", articleParam.getTitle());
		Date now = new Date();
		article.put("creatTime", now.getTime());
		article.put("lastUpdateTime", now.getTime());
		article.put("isShare", articleParam.getIsShare());
		article.put("isShow", articleParam.getIsShow());
		article.put("enabled", "true");
		article.put("isTop", 0);
		dsForRW.getDB().getCollection("i_article").save(article);
		Map<String, DiseaseTypeVO> disMap = getAllDisease(null);
		Map<String, User> userMap = new HashMap<String, User>();
		if(articleParam.getCreateType() != ArticleEnum.CreaterType.system.getIndex()){
			if(isNumeric(articleParam.getAuthor())){
				userMap = getAllUser(Integer.parseInt(articleParam.getAuthor()));
			}
		}
		
		Map<String, ArticleTop> topMap = getArticleTop(new ArticleParam());
		DBObject obj = dsForRW.getDB().getCollection("i_article")
				.findOne(article);
		ArticleVO vo = setField(obj, userMap, disMap, topMap);
		if (vo != null) {
			vo.setContent(MongodbUtil.getString(obj, "content"));
		}
		return vo;
	}

	@Override
	public boolean delArticle(ArticleParam articleParam) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(articleParam.getArticleId()));
		dsForRW.getDB().getCollection("i_article").remove(query);
		return true;
	}

	@Override
	public int getCollectArticle(ArticleParam articleParam,
			Map<String, ArticleCollect> collectMap) {
		int type = 0;
		// DBCollection collection =
		// dsForRW.getDB().getCollection("i_article_collect");
		// DBObject query=new BasicDBObject();
		// query.put("articleId", articleParam.getArticleId());
		// query.put("collectorId", articleParam.getCreaterId());
		// DBObject obj = collection.findOne(query);
		// if(obj != null ){
		// type = MongodbUtil.getInteger(obj, "collectType");
		// }
		String key = articleParam.getArticleId() + "-"
				+ articleParam.getCreaterId();
		ArticleCollect collect = collectMap.get(key);
		if (collect != null) {
			type = collect.getCollectType();
		}
		return type;
	}

	@Override
	public void setCollectArticle(ArticleParam articleParam,
			Map<String, ArticleCollect> collectMap, ArticleVO vo) {
		if (collectMap == null || vo == null) {
			return;
		}
		int type = 0;
		String collectorId = "";
		String key = articleParam.getArticleId() + "-";
		if(articleParam.getCreateType()==null)
		{
			articleParam.setCreateType(3);
		}
		if(articleParam.getCreateType() == 0 || articleParam.getCreateType() == 3 || articleParam.getCreateType() == 5 || articleParam.getCreateType() == 6
				|| articleParam.getCreateType() == 7){
			key +=articleParam.getCreaterId();
		}else if(articleParam.getCreateType() == 2 || articleParam.getCreateType() == 4){
			key +=articleParam.getGroupId();
		}else if(articleParam.getCreateType() == 1 ){
			key +="system";
		}
		
		ArticleCollect collect = collectMap.get(key);
		if (collect != null) {
			type = collect.getCollectType();
			collectorId = collect.getCollectorId();
		}
		vo.setCollect(type);
		vo.setCollectorId(collectorId);
	}

	@Override
	public Map<String, Object> updateArticleUseCount(ArticleParam articleParam) {
		Map<String, Object> map = new HashMap<String, Object>();
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(articleParam.getArticleId()));
		query.put("enabled", "true");
		DBObject keys = new BasicDBObject();
		keys.put("useNum", 1);
		DBObject obj = dsForRW.getDB().getCollection("i_article")
				.findOne(query, keys);
		if (obj != null) {
			Object temp = obj.get("useNum");
			int num = temp == null ? 1 : Integer.parseInt(temp.toString()) + 1;
			DBObject update = new BasicDBObject();
			update.put("useNum", num);
			update.put("lastUpdateTime", new Date().getTime());
			dsForRW.getDB()
					.getCollection("i_article")
					.update(query, new BasicDBObject("$set", update), false,
							true);
			map.put("status", true);
		} else {
			map.put("status", false);
			map.put("msg", "未找到对应的文章");
		}
		return map;
	}

	public ArticleVO setField(DBObject obj, Map<String, User> userMap,
			Map<String, DiseaseTypeVO> disMap, Map<String, ArticleTop> topMap) {
		if (obj == null) {
			return null;
		}
		ArticleVO vo = new ArticleVO();
		String id = obj.get("_id").toString();
		vo.setId(id);

		vo.setTitle(MongodbUtil.getString(obj, "title"));
		vo.setCreaterId(MongodbUtil.getString(obj, "createrId"));
		vo.setCreateType(MongodbUtil.getInteger(obj, "createType"));
		vo.setGroupId(MongodbUtil.getString(obj, "groupId"));
		if (MongodbUtil.getInteger(obj, "edited") == null) {
			vo.setEdited(1);
		} else {
			vo.setEdited(MongodbUtil.getInteger(obj, "edited"));
		}

		String docId = MongodbUtil.getString(obj, "author");
		vo.setUseNum(MongodbUtil.getInteger(obj, "useNum"));
		vo.setAuthor(docId);
		if (MongodbUtil.getInteger(obj, "createType") == ArticleEnum.CreaterType.system
				.getIndex()) {
			vo.setAuthorName("玄关患教中心");
		} else {
			if(isNumeric(docId)){
				if(userMap != null){
					User user = userMap.get(docId);
					if (user != null) {
						vo.setDoctor(user.getDoctor());
						vo.setAuthorName(user.getName());
						vo.setGroupName(user.getGroupRemark());
					}
				}
			}else{
				//就是集团ID
				DBObject gObj = getGroupById(docId);
				if(gObj != null){
					vo.setAuthorName(MongodbUtil.getString(gObj, "name"));
					vo.setGroupName(MongodbUtil.getString(gObj, "name"));
				}
			}
			
			
		}

		vo.setCopyPath(MongodbUtil.getString(obj, "copyPath"));
		vo.setCopy_small(MongodbUtil.getString(obj, "copy_small"));
		vo.setDiseaseId(MongodbUtil.getString(obj, "diseaseId"));
		vo.setDisease(disMap.get(MongodbUtil.getString(obj, "diseaseId")
				.toUpperCase()));
		vo.setDescription(MongodbUtil.getString(obj, "description"));
		// vo.setContent(MongodbUtil.getString(obj, "content"));
		vo.setUrl(MongodbUtil.getString(obj, "url"));
		vo.setCreatTime(MongodbUtil.getLong(obj, "creatTime"));
		vo.setLastUpdateTime(MongodbUtil.getLong(obj, "lastUpdateTime"));
		Object oj = obj.get("tags");
		if (oj != null) {
			Object[] objs = ((BasicDBList) oj).toArray();
			String[] str = null;
			DiseaseTypeVO[] tag = null;
			if (objs.length > 0) {
				str = new String[objs.length];
				tag = new DiseaseTypeVO[objs.length];
				for (int i = 0; i < str.length; i++) {
					str[i] = objs[i].toString();
					tag[i] = disMap.get(str[i].toUpperCase());
				}
				vo.setTags(str);
				vo.setTag(tag);
			}
		}
		vo.setIsShow(Integer.parseInt(obj.get("isShow").toString()));
		vo.setIsShare(Integer.parseInt(obj.get("isShare").toString()));
		ArticleTop temp = topMap.get(vo.getId());
		if (temp != null) {
			vo.setTop(true);
			vo.setPriority(temp.getPriority());
		} else {
			vo.setTop(false);
		}
		
		Long vc = MongodbUtil.getLong(obj, "visitCount");
		if(vc == null){
			vo.setVisitCount(0);
		}else{
			vo.setVisitCount(vc);
		}
		return vo;
	}

	public boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}
	
	public Map<String, Long> getVisitCount(List<String> list,
			ArticleParam articleParam, boolean page) {
		Map<String, Long> vMap = new LinkedHashMap<String, Long>();

		DBObject matchFields = new BasicDBObject();
		if (!list.isEmpty()) {
			matchFields.put("articleId", new BasicDBObject("$in", list));
		}
		// 时间查询条件
		if (articleParam.getLastUpdateTime() > 0) {
			matchFields
					.put("visitTime",
							new BasicDBObject("$gte", articleParam
									.getLastUpdateTime()));
		}
		DBObject match = new BasicDBObject("$match", matchFields);
		// 返回字段
		BasicDBObject projectFields = new BasicDBObject();
		projectFields.put("articleId", 1);
		DBObject project = new BasicDBObject("$project", projectFields);
		// 分组条件
		DBObject groupFields = new BasicDBObject("_id", "$articleId");
		groupFields.put("value", new BasicDBObject("$sum", 1));// 代表统计(1，count)不是累加(sum，则是对应的某个字段如"$articleId"),
		DBObject group = new BasicDBObject("$group", groupFields);
		// 排序条件
		int asc = -1;
		if (articleParam.getSortType().equals("asc")) {
			asc = 1;
		}
		DBObject sort = new BasicDBObject("$sort", new BasicDBObject("value",
				asc));

		DBObject skip = new BasicDBObject("$skip",
				(articleParam.getPageIndex() - 1) * articleParam.getPageSize());

		DBObject limit = new BasicDBObject("$limit", articleParam.getPageSize());
		// {$sort: {ratio: -1}},
		// {$limit: 5}
		// {$skip : 5}
		List<DBObject> pipeline = new ArrayList<DBObject>();
		pipeline.add(match);
		pipeline.add(project);
		pipeline.add(group);
		pipeline.add(sort);
		if (page) {
			pipeline.add(skip);
			pipeline.add(limit);
		}

		// 按文章浏览量降序存放所有文章
		AggregationOutput output = dsForRW.getDB()
				.getCollection("i_article_visit").aggregate(pipeline);
		Iterator<DBObject> it = output.results().iterator();
		// Map<String,Long> visit = new HashMap<String,Long>();
		while (it.hasNext()) {
			DBObject obj = it.next();
			String key = MongodbUtil.getString(obj, "_id");
			long count = MongodbUtil.getLong(obj, "value");
			// visit.put(key, count);
			vMap.put(key, count);
		}
		long count = 0;
		if (page) {
			pipeline.remove(skip);
			pipeline.remove(limit);
		}
		output = dsForRW.getDB().getCollection("i_article_visit")
				.aggregate(pipeline);
		it = output.results().iterator();

		while (it.hasNext()) {
			it.next();
			count++;
		}
		vMap.put("count", count);
		return vMap;
	}

	@Override
	public Map<String, User> getAllUser(Integer... uid) {
		Map<String, User> userMap = new HashMap<String, User>();
		DBObject gdQuery = new BasicDBObject();
		gdQuery.put("userType", 3);
		if (uid != null && uid.length > 0) {
			try {
//				gdQuery.put("_id", Integer.parseInt(uid));
				gdQuery.put("_id", new BasicDBObject("$in", uid));
			} catch (NumberFormatException e) {
				return userMap;
			}
		}
		BasicDBObject keys = new BasicDBObject();
		keys.put("_id", 1);
		keys.put("name", 1);
		keys.put("doctor", 1);

		DBCursor cursor = dsForRW.getDB().getCollection("user")
				.find(gdQuery, keys);
		User user = null;
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			user = new User();
			user.setUserId(MongodbUtil.getInteger(obj, "_id"));
			user.setName(MongodbUtil.getString(obj, "name"));
			Object doctObj = obj.get("doctor");
			if (doctObj != null) {
				DBObject dbObj = (DBObject) doctObj;
				Doctor doc = new Doctor();
				doc.setHospital(MongodbUtil.getString(dbObj, "hospital"));
				doc.setTitle(MongodbUtil.getString(dbObj, "title"));
				user.setDoctor(doc);
			}
			gdQuery = new BasicDBObject();
			gdQuery.put("doctorId", user.getUserId());
			gdQuery.put("status", "C");
			DBObject gdObj = dsForRW.getDB().getCollection("c_group_doctor")
					.findOne(gdQuery);
			if (gdObj != null) {
				String gId = MongodbUtil.getString(gdObj, "groupId");
				if (!StringUtil.isEmpty(gId)) {
					DBObject temp =getGroupById(gId);
					if (temp != null) {
						user.setGroupRemark(MongodbUtil.getString(temp, "name"));
					}
				}
			}
			userMap.put(user.getUserId().toString(), user);
		}

		return userMap;
	}

	private DBObject getGroupById(String gId){
		return dsForRW.getDB().getCollection("c_group").findOne(new BasicDBObject("_id", new ObjectId(gId)));
	}
	@Override
	public Map<String, DiseaseTypeVO> getAllDisease(String id) {
		Map<String, DiseaseTypeVO> disMap = new HashMap<String, DiseaseTypeVO>();
		BasicDBObject keys = new BasicDBObject();
		DBObject query = new BasicDBObject();
		if (!StringUtil.isEmpty(id)) {
			query.put("_id", id);
		}
		keys.put("_id", 1);
		keys.put("name", 1);
		keys.put("parent", 1);
		DiseaseTypeVO vo = null;

		DBCursor cursor = dsForRW.getDB().getCollection("b_disease_type")
				.find(query, keys).sort(new BasicDBObject("weight", -1));
		while (cursor.hasNext()) {
			vo = new DiseaseTypeVO();
			DBObject obj = cursor.next();
			vo.setId(MongodbUtil.getString(obj, "_id"));
			vo.setName(MongodbUtil.getString(obj, "name"));
			vo.setParent(MongodbUtil.getString(obj, "parent"));
			disMap.put(MongodbUtil.getString(obj, "_id"), vo);
		}
		return disMap;
	}

	@Override
	public Map<String, Object> getArticleByDisease(ArticleParam articleParam) {
		Map<String, Object> result = new HashMap<String, Object>();

		// 树那里带上分享和收藏，所以这里查询也带收藏和分享
		BasicDBObject collect = new BasicDBObject();
		collect.put("enabled", "true");

		DBObject isShare = new BasicDBObject();
		isShare.put("enabled", "true");
		// 收藏条件
		BasicDBList collectorId = new BasicDBList();
		if (articleParam.getCreateType() == 0) {
			// 平台加集团
			isShare.put("isShare", 1);
			collectorId.add(new BasicDBObject("collectorId", "system"));
			collectorId.add(new BasicDBObject("collectorId", articleParam
					.getGroupId()));
			// collectorId.add(new BasicDBObject("collectorId",
			// articleParam.getCreaterId()));
		} else if (articleParam.getCreateType() == 1
				|| articleParam.getCreateType() == 4
				|| articleParam.getCreateType() == 5) {
			// 1:平台查平台,4:集团查平台，5:医生查平台
			isShare.put("isShare", 1);
			collectorId.add(new BasicDBObject("collectorId", "system"));
		} else if (articleParam.getCreateType() == 2
				|| articleParam.getCreateType() == 6) {
			// 2：集团查集团，6：医生查集团
			collectorId.add(new BasicDBObject("collectorId", articleParam
					.getGroupId()));
			// collectorId.add(new BasicDBObject("collectorId",
			// articleParam.getCreaterId()));
		} else if (articleParam.getCreateType() == 3) {
			// 是医生
			collectorId.add(new BasicDBObject("collectorId", articleParam
					.getCreaterId()));
		}
		DBObject queryArt = new BasicDBObject();
		queryArt.put("$or", collectorId);

		DBCollection collection = null;
		collection = dsForRW.getDB().getCollection("i_article_collect");
		List<ObjectId> list = new ArrayList<ObjectId>();
		DBCursor cursor = collection.find(queryArt);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			String articleId = MongodbUtil.getString(obj, "articleId");
			list.add(new ObjectId(articleId));
		}
		cursor.close();
		
		Map<String, DiseaseTypeVO> disMap = getAllDisease(null);
		Map<String, ArticleTop> topMap = getArticleTop(new ArticleParam());
		Map<String, ArticleCollect> collectMap = getCollectArticleByParam(new ArticleParam());

		collect.put("_id", new BasicDBObject("$in", list));

		if (!StringUtil.isEmpty(articleParam.getDiseaseId())) {
			DiseaseTypeVO disease = disMap.get(articleParam.getDiseaseId()
					.toUpperCase());
			if (disease != null && disease.getParent().equals("0")) {
				// 父节点名称匹配
				Pattern pattern = Pattern.compile(
						"^" + articleParam.getDiseaseId() + ".*$",
						Pattern.CASE_INSENSITIVE);
				collect.put("diseaseId", pattern);
				isShare.put("diseaseId", pattern);
			} else {
				// 二级则全等匹配
				collect.put("diseaseId", articleParam.getDiseaseId());
				isShare.put("diseaseId", articleParam.getDiseaseId());
			}
		}

		BasicDBList condList = new BasicDBList();
		condList.add(collect);
		if (articleParam.getCreateType() == 0
				|| articleParam.getCreateType() == 1
				|| articleParam.getCreateType() == 4
				|| articleParam.getCreateType() == 5) {
			condList.add(isShare);
		}
		int asc = -1;
		if (articleParam.getSortType().equals("asc")) {
			asc = 1;
		}
		collection = dsForRW.getDB().getCollection("i_article");
		DBObject sort = new BasicDBObject();
		if (articleParam.getCreateType() == 1) {
			sort.put("isTop", asc);
		}
		
		if (articleParam.getCreateType() == 2) {
			sort.put("creatTime", asc);
		}
		sort.put(articleParam.getSortBy(), asc);
		List<ArticleVO> vList = new ArrayList<ArticleVO>();
		List<String> idlist = new ArrayList<String>();
		List<Integer> userList = new ArrayList<Integer>();
		DBCursor cursorArt = collection
				.find(new BasicDBObject("$or", condList))
				.sort(sort)
				.skip((articleParam.getPageIndex() - 1)
						* articleParam.getPageSize())
				.limit(articleParam.getPageSize());
		while (cursorArt.hasNext()) {
			DBObject obj = cursorArt.next();
			if(MongodbUtil.getInteger(obj, "createType").intValue() != ArticleEnum.CreaterType.system.getIndex()){
				String author = MongodbUtil.getString(obj,"author");
				if(isNumeric(author)){
					userList.add(Integer.parseInt(author));
				}
			}
			ArticleVO vo = setField(obj, null, disMap, topMap);
			if (vo != null) {
				idlist.add(vo.getId());
				if (articleParam.getCreateType() == 1) {
					articleParam.setCreaterId("system");
				} else if (articleParam.getCreateType() == 2
						|| articleParam.getCreateType() == 4) {
					articleParam.setCreaterId(articleParam.getGroupId());
				}
				articleParam.setArticleId(vo.getId());
				setCollectArticle(articleParam, collectMap, vo);
				vList.add(vo);
			}
		}
		cursorArt.close();
		
		if(vList.size() == 0){
			result.put("count", 0);
			result.put("list",new ArrayList<ArticleVO>());
			return result;
		}
//		Integer[] uList = userList.size() == 0 ? null:(Integer[])userList.toArray(new Integer[userList.size()]);
//		Map<String, User> userMap = getAllUser(uList);
		Map<String, User> userMap = userList.size() == 0 ? new HashMap<String, User>():getAllUser((Integer[])userList.toArray(new Integer[userList.size()]));
		Map<String, Long> map = getVisitCount(idlist, articleParam, false);
		for (ArticleVO vo : vList) {
			Long visit = map.get(vo.getId());
			if (visit == null) {
				visit = 0L;
			}
			vo.setVisitCount(visit);
			if (vo.getCreateType() == ArticleEnum.CreaterType.system
					.getIndex()) {
				vo.setAuthorName("玄关患教中心");
			} else {
				if(isNumeric(vo.getAuthor())){
					User user = userMap.get(vo.getAuthor());
					if (user != null) {
						vo.setDoctor(user.getDoctor());
						vo.setAuthorName(user.getName());
						vo.setGroupName(user.getGroupRemark());
					}
				}else{
					//集团ID
					DBObject gObj = getGroupById(vo.getAuthor());
					if(gObj != null){
						vo.setAuthorName(MongodbUtil.getString(gObj, "name"));
						vo.setGroupName(MongodbUtil.getString(gObj, "name"));
					}
				}
			}
		}
		result.put("count", collection.find(new BasicDBObject("$or", condList))
				.count());
		sortList(vList);
		result.put("list", vList);
		return result;

	}

	public Map<String, ArticleTop> getArticleTop(ArticleParam parm) {
		Map<String, ArticleTop> map = new HashMap<String, ArticleTop>();
		DBObject query = new BasicDBObject();
		if (!StringUtil.isEmpty(parm.getArticleId())) {
			query.put("articleId", parm.getArticleId());
		}
		DBCursor cursor = dsForRW.getDB().getCollection("i_article_top")
				.find(query);
		ArticleTop top = null;
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			top = new ArticleTop();
			top.setArticleId(MongodbUtil.getString(obj, "articleId"));
			top.setPriority(MongodbUtil.getInteger(obj, "priority"));
			map.put(top.getArticleId(), top);
		}
		return map;
	}

	public List<ArticleVO> setTop(List<ArticleVO> vList) {
		Collections.sort(vList, new Comparator<ArticleVO>() {
			public int compare(ArticleVO arg0, ArticleVO arg1) {
				if (!arg0.isTop()) {
					arg0.setPriority(-1);
				}
				if (!arg1.isTop()) {
					arg1.setPriority(-1);
				}
				return arg1.getPriority().compareTo(arg0.getPriority());
			}
		});
		return vList;
	}

	@Override
	public boolean delArticleByLogic(String articleId) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(articleId));
		query.put("enabled", "true");
		DBObject update = new BasicDBObject();
		update.put("enabled", "false");
		update.put("lastUpdateTime", System.currentTimeMillis());
		dsForRW.getDB().getCollection("i_article")
				.update(query, new BasicDBObject("$set", update), false, false);
		// 还要删除对应的置顶文章
		ArticleParam articleParam = new ArticleParam();
		articleParam.setArticleId(articleId);
		boolean result = delTopArticle(articleParam);
		if (result) {
			articleParam.setTop(false);
			updateTopArticle(articleParam);
		}
		return true;
	}

	public static void main(String[] args) {
	}

	@Override
	public boolean updateTopArticle(ArticleParam articleParam) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(articleParam.getArticleId()));
		DBObject update = new BasicDBObject();
		if (articleParam.isTop()) {
			update.put("isTop", 1);
		} else {
			update.put("isTop", 0);
		}

		dsForRW.getDB().getCollection("i_article")
				.update(query, new BasicDBObject("$set", update), false, false);
		return true;
	}

	@Override
	public void addDActToGAct(String doctorId, String groupId) {
		if (StringUtil.isEmpty(doctorId) || StringUtil.isEmpty(groupId)) {
			return;
		}
		// 先获取医生收藏
		DBObject query = new BasicDBObject();
		query.put("collectorId", doctorId);
		DBObject project = new BasicDBObject();
		project.put("articleId", 1);
		DBCursor cursor = dsForRW.getDB().getCollection("i_article_collect")
				.find(query, project);
		List<ArticleVO> doctorList = new ArrayList<ArticleVO>();
		BasicDBList values = new BasicDBList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			values.add(new ObjectId(MongodbUtil.getString(obj, "articleId")));
		}
		cursor.close();

		query = new BasicDBObject();
		query.put("_id", new BasicDBObject("$in", values));
		project = new BasicDBObject();
		project.put("diseaseId", 1);
		project.put("_id", 1);
		cursor = dsForRW.getDB().getCollection("i_article").find(query);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			ArticleVO vo = new ArticleVO();
			vo.setDiseaseId(MongodbUtil.getString(obj, "diseaseId"));
			vo.setId(MongodbUtil.getString(obj, "_id"));
			System.err.println(vo.getId());
			doctorList.add(vo);
		}
		cursor.close();

		if (!doctorList.isEmpty()) {
			// 获取集团收藏
			query = new BasicDBObject();
			query.put("collectorId", groupId);
			List<String> groupList = new ArrayList<String>();
			cursor = dsForRW.getDB().getCollection("i_article_collect")
					.find(query);
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				groupList.add(MongodbUtil.getString(obj, "articleId"));
			}
			cursor.close();

			// 获取集团树
			cursor = dsForRW.getDB().getCollection("i_group_disease")
					.find(new BasicDBObject("groupId", groupId));
			Map<String, GroupDisease> gdMap = new HashMap<String, GroupDisease>();
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				Object aid = obj.get("articleId");
				GroupDisease gd = new GroupDisease();
				gd.setId(new ObjectId(MongodbUtil.getString(obj, "_id")));
				List<String> sList = new ArrayList<String>();
				if (aid != null) {
					List<Object> oList = (BasicDBList) aid;
					for (Object temp : oList) {
						sList.add(temp.toString());
					}
				}
				gd.setArticleId(sList);
				gd.setGroupId(groupId);
				gd.setDiseaseId(MongodbUtil.getString(obj, "diseaseId"));
				gdMap.put(MongodbUtil.getString(obj, "diseaseId"), gd);
			}
			cursor.close();

			long time = new Date().getTime();
			for (ArticleVO vo : doctorList) {
				if (!groupList.contains(vo.getId())) {
					// 更新集团收藏
					DBObject insertData = new BasicDBObject();
					insertData.put("articleId", vo.getId());
					insertData.put("collectorId", groupId);
					insertData.put("collectType",
							ArticleEnum.CollectType.collect.getIndex());
					insertData.put("collectorType",
							ArticleEnum.CollecterType.group.getIndex());
					insertData.put("collectTime", time);
					dsForRW.getDB().getCollection("i_article_collect")
							.save(insertData);// 更新集团收藏

					// 更新集团病种树,每篇文章病种不同，需要单独更新
					GroupDisease gdVO = gdMap.get(vo.getDiseaseId());
					if (gdVO != null) {
						List<String> gList = gdVO.getArticleId();
						if (!gList.contains(vo.getId())) {
							gList.add(vo.getId());
							query = new BasicDBObject();
							query.put("_id", gdVO.getId());
							DBObject update = new BasicDBObject();
							update.put("count", gList.size());
							update.put("articleId", gList);
							dsForRW.getDB()
									.getCollection("i_group_disease")
									.update(query,
											new BasicDBObject("$set", update),
											false, false);// 更新病种树
						}
					}
				}
			}
		}
	}

	/***
	 * 查询医生在没有创建自己的集团之前的创建的文章
	 * 
	 */
	@Override
	public List<ArticleVO> getArcticleListByCreatorId(String createrId) {
		List<ArticleVO> result = new ArrayList<ArticleVO>();
		BasicDBObject query = new BasicDBObject();
		query.put("createrId", createrId);
		query.put("groupId", "0");
		query.put("createType", ArticleEnum.CreaterType.doctor.getIndex());

		DBCursor cursor = dsForRW.getDB().getCollection("i_article")
				.find(query);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			ArticleVO vo = new ArticleVO();
			vo.setDiseaseId(MongodbUtil.getString(obj, "diseaseId"));
			vo.setId(MongodbUtil.getString(obj, "_id"));
			vo.setGroupId(MongodbUtil.getString(obj, "groupId"));
			result.add(vo);
		}
		cursor.close();
		return result;
	}

	/*** begin add by liwei 2016年1月18日   支持多集团********/

	@Override
	public Map<String, Object> getHotArcticleForMoreGroup(
			ArticleParam articleParam) {
		long s = System.currentTimeMillis();
		Map<String, Object> result = new HashMap<String, Object>();
		BasicDBList condList = new BasicDBList();

		DBObject share = new BasicDBObject();
		share.put("isShare", 1);
		share.put("enabled", "true");

		DBObject collect = new BasicDBObject();
		collect.put("enabled", "true");

		BasicDBList collectorId = new BasicDBList();
		List<String> groups = articleParam.getGroupIds();
		if (articleParam.getCreateType() == 0) {// 平台加集团
			collectorId.add(new BasicDBObject("collectorId", "system"));
			if (null != groups && groups.size() > 0) {
				collectorId.add(new BasicDBObject("collectorId",
						new BasicDBObject("$in", getGroupBasicList(groups))));
			}
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.group
				.getIndex()) {// 集团的
			collectorId.add(new BasicDBObject("collectorId", new BasicDBObject(
					"$in", getGroupBasicList(articleParam.getGroupIds()))));
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system
				.getIndex()) {
			collectorId.add(new BasicDBObject("collectorId", "system"));
		}
		/*** begin add by liwei 2016年2月15日 ********/
		else if (articleParam.getCreateType() == ArticleEnum.CreaterType.doctor
				.getIndex()) {// 集团的
			collectorId.add(new BasicDBObject("collectorId", new BasicDBObject(
					"$in", getGroupBasicList(articleParam.getGroupIds()))));
		}
		/*** end add by liwei 2016年2月15日 ********/
		else {
			result.put("count", 0);
			result.put("list", null);
			return result;
		}
		DBObject queryArt = new BasicDBObject();
		queryArt.put("$or", collectorId);

		// 在收藏表查出所有收藏文章ID

		DBCollection collection = null;
		collection = dsForRW.getDB().getCollection("i_article_collect");
		List<ObjectId> list = new ArrayList<ObjectId>();
		DBCursor cursor = collection.find(queryArt);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			String articleId = MongodbUtil.getString(obj, "articleId");
			list.add(new ObjectId(articleId));
		}
		cursor.close();
		collect.put("_id", new BasicDBObject("$in", list));

		Map<String, DiseaseTypeVO> disMap = getAllDisease(null);
		Map<String, ArticleCollect> collectMap = getCollectArticleByParam(new ArticleParam());
		Map<String, ArticleTop> topMap = getArticleTop(new ArticleParam());

		if (!StringUtil.isEmpty(articleParam.getDiseaseId())) {
			DiseaseTypeVO disease = disMap.get(articleParam.getDiseaseId()
					.toUpperCase());
			if (disease != null && disease.getParent().equals("0")) {
				// 父节点名称匹配
				Pattern pattern = Pattern.compile(
						"^" + articleParam.getDiseaseId() + ".*$",
						Pattern.CASE_INSENSITIVE);
				collect.put("diseaseId", pattern);
				share.put("diseaseId", pattern);
			} else {
				// 二级则全等匹配
				collect.put("diseaseId", articleParam.getDiseaseId());
				share.put("diseaseId", articleParam.getDiseaseId());
			}
		}

		if (articleParam.getCreatTime() != 0) {
			share.put("creatTime",
					new BasicDBObject("$gte", articleParam.getCreatTime()));
			collect.put("creatTime",
					new BasicDBObject("$gte", articleParam.getCreatTime()));
		}
		condList.add(collect);

		if (articleParam.getCreateType() == 0
				|| articleParam.getCreateType() == 1) {
			condList.add(share);
		}

		// 根据收藏ID以及病种查出所有文章
		collection = dsForRW.getDB().getCollection("i_article");

		Map<String, ArticleVO> maps = new HashMap<String, ArticleVO>();
		List<String> idList = new ArrayList<String>();
		List<Integer> userList = new ArrayList<Integer>();

		DBCursor cursorArt = collection
				.find(new BasicDBObject("$or", condList));
		while (cursorArt.hasNext()) {
			DBObject obj = cursorArt.next();
			
			if(MongodbUtil.getInteger(obj, "createType").intValue() != ArticleEnum.CreaterType.system.getIndex()){
//				userList.add(MongodbUtil.getInteger(obj, "author"));
				String author = MongodbUtil.getString(obj,"author");
				if(isNumeric(author)){
					userList.add(Integer.parseInt(author));
				}
			}
			
			ArticleVO vo = setField(obj, null, disMap, topMap);
			idList.add(vo.getId());
			articleParam.setArticleId(vo.getId());
			if (articleParam.getCreateType() == 1) {
				articleParam.setCreaterId("system");
			} else if (articleParam.getCreateType() == 2
					|| articleParam.getCreateType() == 4) {
				articleParam.setCreaterId(articleParam.getGroupId());
			}
			setCollectArticle(articleParam, collectMap, vo);
			maps.put(vo.getId(), vo);
		}
		cursorArt.close();
		// 按文章浏览量降序存放所有文章
		List<ArticleVO> vList = new ArrayList<ArticleVO>();
		Map<String, User> userMap = userList.size() == 0 ? new HashMap<String, User>():getAllUser((Integer[])userList.toArray(new Integer[userList.size()]));
		Map<String, Long> vMap = getVisitCount(idList, articleParam, true);
		Iterator<Entry<String, Long>> iterator = vMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Long> entry = iterator.next();
			String key = entry.getKey();
			Long value = entry.getValue();
			if (maps.containsKey(key)) {
				ArticleVO vo = maps.get(key);
				vo.setVisitCount(value);
				if (vo.getCreateType() == ArticleEnum.CreaterType.system.getIndex()) {
					vo.setAuthorName("玄关患教中心");
				} else {
					if(isNumeric(vo.getAuthor())){
						User user = userMap.get(vo.getAuthor());
						if (user != null) {
							vo.setDoctor(user.getDoctor());
							vo.setAuthorName(user.getName());
							vo.setGroupName(user.getGroupRemark());
						}
					}else{
						//集团ID
						DBObject gObj = getGroupById(vo.getAuthor());
						if(gObj != null){
							vo.setAuthorName(MongodbUtil.getString(gObj, "name"));
							vo.setGroupName(MongodbUtil.getString(gObj, "name"));
						}
					}
				}
				vList.add(vo);
			}
		}
		if (articleParam.getCreateType() == 1) {
			vList = setTop(vList);
		}
		result.put("count", vMap.get("count"));
		result.put("list", vList);

		long e = System.currentTimeMillis();
		System.out.println("Hot count is : " + (e - s));
		return result;
	}

	/**
	 * liwei 转换List对象
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
	public Map<String, Object> getNewArcticleForMoreGroup(
			ArticleParam articleParam) {

		Map<String, Object> result = new HashMap<String, Object>();

		DBObject queryArt = new BasicDBObject();

		BasicDBList collectorId = new BasicDBList();
		if (articleParam.getCreateType() == 0) {// 平台加集团
			collectorId.add(new BasicDBObject("collectorId", "system"));
			if (null!=articleParam.getGroupIds()&&articleParam.getGroupIds().size()>0 ) {
				collectorId.add(new BasicDBObject("collectorId", new BasicDBObject("$in",getGroupBasicList(articleParam
						.getGroupIds())) ));
			}
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.group
				.getIndex()) {// 集团的
			collectorId.add(new BasicDBObject("collectorId", new BasicDBObject("$in",getGroupBasicList(articleParam
					.getGroupIds())) ));
		} else if (articleParam.getCreateType() == ArticleEnum.CreaterType.system
				.getIndex()) {
			collectorId.add(new BasicDBObject("collectorId", "system"));
		} else {
			result.put("count", 0);
			result.put("list", null);
			return result;
		}
		// collectorId.add(new BasicDBObject("collectorId", "system"));
		// if(!StringUtil.isEmpty(articleParam.getGroupId())){
		// collectorId.add(new BasicDBObject("collectorId",
		// articleParam.getGroupId()));
		// }
		//
		queryArt.put("$or", collectorId);

		DBCollection collection = null;
		collection = dsForRW.getDB().getCollection("i_article_collect");
		List<ObjectId> list = new ArrayList<ObjectId>();
		DBCursor cursor = collection.find(queryArt);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			String articleId = MongodbUtil.getString(obj, "articleId");
			list.add(new ObjectId(articleId));
		}
		cursor.close();

		Map<String, DiseaseTypeVO> disMap = getAllDisease(null);
		Map<String, ArticleCollect> collectMap = getCollectArticleByParam(new ArticleParam());
		Map<String, ArticleTop> topMap = getArticleTop(new ArticleParam());
		DBObject isShare = new BasicDBObject();
		isShare.put("isShare", 1);
		isShare.put("enabled", "true");
		BasicDBObject collect = new BasicDBObject();
		collect.put("enabled", "true");
		if (!StringUtil.isEmpty(articleParam.getDiseaseId())) {
			DiseaseTypeVO disease = disMap.get(articleParam.getDiseaseId()
					.toUpperCase());
			if (disease != null && disease.getParent().equals("0")) {
				// 父节点名称匹配
				Pattern pattern = Pattern.compile(
						"^" + articleParam.getDiseaseId() + ".*$",
						Pattern.CASE_INSENSITIVE);
				collect.put("diseaseId", pattern);
				isShare.put("diseaseId", pattern);
			} else {
				// 二级则全等匹配
				collect.put("diseaseId", articleParam.getDiseaseId());
				isShare.put("diseaseId", articleParam.getDiseaseId());
			}
		}
		collect.put("_id", new BasicDBObject("$in", list));

		BasicDBList condList = new BasicDBList();
		condList.add(collect);
		if (articleParam.getCreateType() == 0
				|| articleParam.getCreateType() == 1) {
			condList.add(isShare);
		}
		int asc = -1;
		if (articleParam.getSortType().equals("asc")) {
			asc = 1;
		}
		collection = dsForRW.getDB().getCollection("i_article");
		DBCursor cursorArt = collection
				.find(new BasicDBObject("$or", condList))
				.sort(new BasicDBObject(articleParam.getSortBy(), asc))
				.skip((articleParam.getPageIndex() - 1)
						* articleParam.getPageSize())
				.limit(articleParam.getPageSize());
		List<ArticleVO> vList = new ArrayList<ArticleVO>();
		List<Integer> userList = new ArrayList<Integer>();
		while (cursorArt.hasNext()) {
			DBObject obj = cursorArt.next();
			if(MongodbUtil.getInteger(obj, "createType").intValue() != ArticleEnum.CreaterType.system.getIndex()){
				String author = MongodbUtil.getString(obj,"author");
				if(isNumeric(author)){
					userList.add(Integer.parseInt(author));
				}
			}
			
			ArticleVO vo = setField(obj, null, disMap, topMap);
			if (articleParam.getCreateType() == 1) {
				articleParam.setCreaterId("system");
			} else if (articleParam.getCreateType() == 2
					|| articleParam.getCreateType() == 4) {
				articleParam.setCreaterId(articleParam.getGroupId());
			}
			articleParam.setArticleId(vo.getId());
			setCollectArticle(articleParam, collectMap, vo);
			vList.add(vo);
		}
		cursorArt.close();
		if(vList.size() == 0){
			result.put("count", 0);
			result.put("list",new ArrayList<ArticleVO>());
			return result;
		}
		Map<String, User> userMap = userList.size() == 0 ? new HashMap<String, User>():getAllUser((Integer[])userList.toArray(new Integer[userList.size()]));
		for(ArticleVO vo : vList){
			if (vo.getCreateType() == ArticleEnum.CreaterType.system.getIndex()) {
				vo.setAuthorName("玄关患教中心");
			} else {
				if(isNumeric(vo.getAuthor())){
					User user = userMap.get(vo.getAuthor());
					if (user != null) {
						vo.setDoctor(user.getDoctor());
						vo.setAuthorName(user.getName());
						vo.setGroupName(user.getGroupRemark());
					}
				}else{
					//集团ID
					DBObject gObj = getGroupById(vo.getAuthor());
					if(gObj != null){
						vo.setAuthorName(MongodbUtil.getString(gObj, "name"));
						vo.setGroupName(MongodbUtil.getString(gObj, "name"));
					}
				}
			}
		}
		if (articleParam.getCreateType() == 1) {
			vList = setTop(vList);
		}
		result.put("count", collection.find(new BasicDBObject("$or", condList))
				.count());
		result.put("list", vList);
		return result;
	
	}
	/*** end add by liwei 2016年1月18日 ********/

	@Override
	public Map<String, Object> getArticleByDiseaseForMoreGroup(
			ArticleParam articleParam) {

		Map<String, Object> result = new HashMap<String, Object>();

		// 树那里带上分享和收藏，所以这里查询也带收藏和分享
		BasicDBObject collect = new BasicDBObject();
		collect.put("enabled", "true");

		DBObject isShare = new BasicDBObject();
		isShare.put("enabled", "true");
		// 收藏条件
		BasicDBList collectorId = new BasicDBList();
		if (articleParam.getCreateType() == 0) {
			// 平台加集团
			isShare.put("isShare", 1);
			collectorId.add(new BasicDBObject("collectorId", "system"));
			collectorId.add(new BasicDBObject("collectorId", new BasicDBObject("$in",getGroupBasicList(articleParam
					.getGroupIds())) ));
			// collectorId.add(new BasicDBObject("collectorId",
			// articleParam.getCreaterId()));
		} else if (articleParam.getCreateType() == 1
				|| articleParam.getCreateType() == 4
				|| articleParam.getCreateType() == 5) {
			// 1:平台查平台,4:集团查平台，5:医生查平台
			isShare.put("isShare", 1);
			collectorId.add(new BasicDBObject("collectorId", "system"));
		} else if (articleParam.getCreateType() == 2
				|| articleParam.getCreateType() == 6) {
			// 2：集团查集团，6：医生查集团
			collectorId.add(new BasicDBObject("collectorId", new BasicDBObject("$in",getGroupBasicList(articleParam
					.getGroupIds())) ));
			// collectorId.add(new BasicDBObject("collectorId",
			// articleParam.getCreaterId()));
		} else if (articleParam.getCreateType() == 3) {
			// 是医生
			collectorId.add(new BasicDBObject("collectorId",  new BasicDBObject("$in",getGroupBasicList(articleParam
					.getGroupIds())) ));
		}
		DBObject queryArt = new BasicDBObject();
		queryArt.put("$or", collectorId);

		DBCollection collection = null;
		collection = dsForRW.getDB().getCollection("i_article_collect");
		List<ObjectId> list = new ArrayList<ObjectId>();
		DBCursor cursor = collection.find(queryArt);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			String articleId = MongodbUtil.getString(obj, "articleId");
			list.add(new ObjectId(articleId));
		}
		cursor.close();
//		Map<String, User> userMap = getAllUser(null);
		Map<String, DiseaseTypeVO> disMap = getAllDisease(null);
		Map<String, ArticleTop> topMap = getArticleTop(new ArticleParam());
		Map<String, ArticleCollect> collectMap = getCollectArticleByParam(new ArticleParam());

		collect.put("_id", new BasicDBObject("$in", list));

		if (!StringUtil.isEmpty(articleParam.getDiseaseId())) {
			DiseaseTypeVO disease = disMap.get(articleParam.getDiseaseId()
					.toUpperCase());
			if (disease != null && disease.getParent().equals("0")) {
				// 父节点名称匹配
				Pattern pattern = Pattern.compile(
						"^" + articleParam.getDiseaseId() + ".*$",
						Pattern.CASE_INSENSITIVE);
				collect.put("diseaseId", pattern);
				isShare.put("diseaseId", pattern);
			} else {
				// 二级则全等匹配
				collect.put("diseaseId", articleParam.getDiseaseId());
				isShare.put("diseaseId", articleParam.getDiseaseId());
			}
		}

		BasicDBList condList = new BasicDBList();
		condList.add(collect);
		if (articleParam.getCreateType() == 0
				|| articleParam.getCreateType() == 1
				|| articleParam.getCreateType() == 4
				|| articleParam.getCreateType() == 5) {
			condList.add(isShare);
		}
		int asc = -1;
		if (articleParam.getSortType().equals("asc")) {
			asc = 1;
		}
		collection = dsForRW.getDB().getCollection("i_article");
		DBObject sort = new BasicDBObject();
		if (articleParam.getCreateType() == 1) {
			sort.put("isTop", asc);
		}
		sort.put(articleParam.getSortBy(), asc);
		List<ArticleVO> vList = new ArrayList<ArticleVO>();
		List<String> idlist = new ArrayList<String>();
		List<Integer> userList = new ArrayList<Integer>();
		
		DBCursor cursorArt = collection
				.find(new BasicDBObject("$or", condList))
				.sort(sort)
				.skip((articleParam.getPageIndex() - 1)
						* articleParam.getPageSize())
				.limit(articleParam.getPageSize());
		while (cursorArt.hasNext()) {
			DBObject obj = cursorArt.next();
			ArticleVO vo = setField(obj, null, disMap, topMap);
			if (vo != null) {
				idlist.add(vo.getId());
				if (articleParam.getCreateType() == 1) {
					articleParam.setCreaterId("system");
				} else if (articleParam.getCreateType() == 2
						|| articleParam.getCreateType() == 4) {
					articleParam.setCreaterId("moreGroup");
				}
				articleParam.setArticleId(vo.getId());
				setCollectArticleForMoreGroup(articleParam, collectMap, vo);
				vList.add(vo);
			}
		}
		cursorArt.close();
		if(vList.size() == 0){
			result.put("count", 0);
			result.put("list",new ArrayList<ArticleVO>());
			return result;
		}
		Map<String, User> userMap = userList.size() == 0 ? new HashMap<String, User>():getAllUser((Integer[])userList.toArray(new Integer[userList.size()]));
		Map<String, Long> map = getVisitCount(idlist, articleParam, false);
		for (ArticleVO vo : vList) {
			Long visit = map.get(vo.getId());
			if (visit == null) {
				visit = 0L;
			}
			vo.setVisitCount(visit);
			if (vo.getCreateType() == ArticleEnum.CreaterType.system
					.getIndex()) {
				vo.setAuthorName("玄关患教中心");
			} else {
				if(isNumeric(vo.getAuthor())){
					User user = userMap.get(vo.getAuthor());
					if (user != null) {
						vo.setDoctor(user.getDoctor());
						vo.setAuthorName(user.getName());
						vo.setGroupName(user.getGroupRemark());
					}
				}else{
					//集团ID
					DBObject gObj = getGroupById(vo.getAuthor());
					if(gObj != null){
						vo.setAuthorName(MongodbUtil.getString(gObj, "name"));
						vo.setGroupName(MongodbUtil.getString(gObj, "name"));
					}
				}
			}
		}
		result.put("count", collection.find(new BasicDBObject("$or", condList))
				.count());
		result.put("list", vList);
		return result;
	}
	
	private void setCollectArticleForMoreGroup(ArticleParam articleParam,
			Map<String, ArticleCollect> collectMap, ArticleVO vo) {
		if (collectMap == null || vo == null) {
			return;
		}
		String  createId = articleParam.getCreaterId();
		if("moreGroup".equals(createId))
		{	
			List<String> groupIds = articleParam.getGroupIds();
			if(null!=groupIds&&groupIds.size()>0)
			{	
				for(int i=0;i<groupIds.size();i++)
				{	
					createId =groupIds.get(i);
					int type = 0;
					String collectorId = "";
					String key = articleParam.getArticleId() + "-"
							+ createId;
					ArticleCollect collect = collectMap.get(key);
					if (collect != null) {
						type = collect.getCollectType();
						collectorId = collect.getCollectorId();
						vo.setCollect(type);
						vo.setCollectorId(collectorId);
					}
					
				}
			}
		}
		
	}
	private static void sortList(List<ArticleVO> vList){
		Collections.sort(vList, new Comparator<ArticleVO>() {
			public int compare(ArticleVO arg0, ArticleVO arg1) {
				if (arg1.getPriority() == null) {
					arg1.setPriority(0);
				}
				if (arg0.getPriority() == null) {
					arg0.setPriority(0);
				}
				return arg1.getPriority().compareTo(arg0.getPriority());
			}
		});
	}

	@Override
	public String saveVisitTimes(String articleId, Integer userId) {
		ArticleVisit vivit = new ArticleVisit();
		vivit.setArticleId(articleId);
		vivit.setVisitId(userId.toString());
		vivit.setVisitTime(System.currentTimeMillis());
		Object id = dsForRW.save(vivit).getId();
		return id.toString();
	}

	@Override
	public ArticleVisit isVisit(String articleId, Integer userId) {
		return dsForRW.createQuery(ArticleVisit.class).filter("articleId", articleId).filter("visitorId", userId.toString()).get();
	}

	@Override
	public List<ArticleTop> getAppTopArticle() {
		List<ArticleTop> list = new ArrayList<ArticleTop>();
		BasicDBObject sort = new BasicDBObject();
		sort.put("priority", -1);
		DBCursor cursor = dsForRW.getDB().getCollection("i_article_top").find()
				.sort(sort);
		ArticleTop top = null;
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			top = new ArticleTop();
			top.setArticleId(obj.get("articleId").toString());
			top.setPriority(Integer.parseInt(obj.get("priority").toString()));
			list.add(top);
		}
		cursor.close();
		return list;
	}

	@Override
	public List<String> getArticleIds(String diseaseId,String... groupId) {
		Set<String> ids = new HashSet<String>();
		if(groupId == null ){
			return new ArrayList<String>();
		}
		//parent或者diseaseId 等于 diseaseId
		DBObject cond = new BasicDBObject("groupId", new BasicDBObject("$in", Arrays.asList(groupId)));
		cond.put("count", new BasicDBObject("$gt", 0));
		
		if(!StringUtil.isEmpty(diseaseId)){
			BasicDBList list = new BasicDBList();
//			list.add(new BasicDBObject("parent",diseaseId));
//			list.add(new BasicDBObject("diseaseId",diseaseId));
//			cond.put("$or", list);
			Pattern pattern = Pattern.compile("^" + diseaseId + ".*$",Pattern.CASE_INSENSITIVE);
			list.add(new BasicDBObject("parent",pattern));
			list.add(new BasicDBObject("diseaseId",pattern));
			cond.put("$or", list);
		}
		DBObject project = new BasicDBObject();
		project.put("articleId", 1);
		
		DBCursor cursor = dsForRW.getDB().getCollection("i_group_disease").find(cond,project);
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			Object aid = obj.get("articleId");
			if (aid != null) {
				List<Object> oList = (BasicDBList) aid;
				for (Object temp : oList) {
					ids.add(temp.toString());
				}
			}
		}
		cursor.close();
		return new ArrayList<String>(ids);
	}

	@Override
	public Map<String, Object> getArticleByIds(List<String> ids, ArticleParam articleParam) {
		Map<String,Object> result = new HashMap<String,Object>();
		if(ids == null || ids.size() == 0){
			result.put("count", 0);
			result.put("list",new ArrayList<ArticleVO>());
			return result;
		}
		List<ObjectId> list = new ArrayList<ObjectId>();
		for(String id :ids){
			list.add(new ObjectId(id));
		}
		
		List<ArticleVO> vList = new ArrayList<ArticleVO>();
		List<Integer> userList = new ArrayList<Integer>();
		
		Map<String, DiseaseTypeVO> disMap = getAllDisease(null);
		Map<String, ArticleTop> topMap = getArticleTop(new ArticleParam());
		Map<String, ArticleCollect> collectMap = getCollectArticleByParam(new ArticleParam());
		
		int asc = -1;
		if (articleParam.getSortType().equals("asc")) {
			asc = 1;
		}
		DBObject sort = new BasicDBObject();
		if (articleParam.getCreateType() == 1) {
			sort.put("isTop", asc);
		}
		if (articleParam.getCreateType() ==2) {
			sort.put("creatTime", asc);
		}
		if(StringUtil.isNotBlank(articleParam.getSortBy())){
			sort.put(articleParam.getSortBy(), asc);
		}
		
		DBObject query = new BasicDBObject();
		query.put("_id", new BasicDBObject("$in", list));
		query.put("enabled", "true");
		if (articleParam.getCreatTime() != 0) {
			query.put("creatTime",
					new BasicDBObject("$gte", articleParam.getCreatTime()));
			query.put("creatTime",
					new BasicDBObject("$gte", articleParam.getCreatTime()));
		}
		DBCursor cursor = dsForRW.getDB().getCollection("i_article").find(query)
				.sort(sort)
				.skip((articleParam.getPageIndex() - 1)*articleParam.getPageSize())
				.limit(articleParam.getPageSize());
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			if(MongodbUtil.getInteger(obj, "createType").intValue() != ArticleEnum.CreaterType.system.getIndex()){
//				userList.add(MongodbUtil.getInteger(obj, "author"));
				String author = MongodbUtil.getString(obj,"author");
				if(isNumeric(author)){
					userList.add(Integer.parseInt(author));
				}
			}
			ArticleVO vo = setField(obj, null, disMap, topMap);
			if(vo != null){
				articleParam.setArticleId(vo.getId());
				setCollectArticle(articleParam, collectMap, vo);
				vList.add(vo);
			}
		}
		cursor.close();
		
		if(vList.size() == 0){
			result.put("count", 0);
			result.put("list",new ArrayList<ArticleVO>());
			return result;
		}
		Map<String, User> userMap = userList.size() == 0 ? new HashMap<String, User>():getAllUser((Integer[])userList.toArray(new Integer[userList.size()]));
		for (ArticleVO vo : vList) {
			if (vo.getCreateType() == ArticleEnum.CreaterType.system
					.getIndex()) {
				vo.setAuthorName("玄关患教中心");
			} else {
				if(isNumeric(vo.getAuthor())){
					User user = userMap.get(vo.getAuthor());
					if (user != null) {
						vo.setDoctor(user.getDoctor());
						vo.setAuthorName(user.getName());
						vo.setGroupName(user.getGroupRemark());
					}
				}else{
					//集团ID
					DBObject gObj = getGroupById(vo.getAuthor());
					if(gObj != null){
						vo.setAuthorName(MongodbUtil.getString(gObj, "name"));
						vo.setGroupName(MongodbUtil.getString(gObj, "name"));
					}
				}
			}
		}
		//只有平台才有置顶操作权限 所以排序的时候其余的可以按照发布时间来排序
		if(articleParam.getCreateType()==ArticleEnum.CreaterType.system.getIndex()){
			sortList(vList);
		}
		result.put("list",vList);
		result.put("count",  dsForRW.getDB().getCollection("i_article").find(query).count());
		return result;
	}

	@Override
	public void updateVisitNum() {
		
		DBObject matchFields = new BasicDBObject();
		// 时间查询条件
		DBObject match = new BasicDBObject("$match", matchFields);
		// 返回字段
		BasicDBObject projectFields = new BasicDBObject();
		projectFields.put("articleId", 1);
		DBObject project = new BasicDBObject("$project", projectFields);
		// 分组条件
		DBObject groupFields = new BasicDBObject("_id", "$articleId");
		groupFields.put("value", new BasicDBObject("$sum", 1));// 代表统计(1，count)不是累加(sum，则是对应的某个字段如"$articleId"),
		DBObject group = new BasicDBObject("$group", groupFields);
		
		List<DBObject> pipeline = new ArrayList<DBObject>();
		pipeline.add(match);
		pipeline.add(project);
		pipeline.add(group);

		// 按文章浏览量降序存放所有文章
		AggregationOutput output = dsForRW.getDB() .getCollection("i_article_visit").aggregate(pipeline);
		Iterator<DBObject> it = output.results().iterator();
		while (it.hasNext()) {
			DBObject obj = it.next();
			String key = MongodbUtil.getString(obj, "_id");
			long count = MongodbUtil.getLong(obj, "value");
			DBObject update = new BasicDBObject();
			update.put("visitCount", count);
			System.err.println(key+"===="+count);
			dsForRW.getDB().getCollection("i_article").update(new BasicDBObject("_id",new ObjectId(key)), 
					new BasicDBObject("$set", update), false, true);
		}
	}
	
}
