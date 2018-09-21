package com.dachen.health.document.dao.impl;

import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.document.constant.DocumentEnum;
import com.dachen.health.document.dao.IDocumentDao;
import com.dachen.health.document.entity.param.DocumentParam;
import com.dachen.health.document.entity.po.Document;
import com.dachen.health.document.entity.po.RecommendDetails;
import com.dachen.health.document.entity.po.VisitDetail;
import com.dachen.health.document.entity.vo.ContentTypeVO;
import com.dachen.health.document.entity.vo.DocumentVO;
import com.dachen.util.BeanUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository
public class DocumentDaoImpl extends NoSqlRepository implements IDocumentDao{

	@Autowired
	private UserRepository userDao;
	
	@Override
	public DocumentVO addDcoument(Document db) {
		Object id = dsForRW.insert(db).getId();
		DocumentVO vo = BeanUtil.copy(db, DocumentVO.class);
		vo.setId(id.toString());
		return vo;
	}

	@Override
	public boolean delDocumentById(DocumentParam param) {
		boolean result = false;
		DBObject query = new BasicDBObject("_id",new ObjectId(param.getId()));
		DBObject update = new BasicDBObject();
		update.put("enabled", DocumentEnum.DocumentEnabledStatus.disabled.getIndex());
		WriteResult writeResult = dsForRW.getDB().getCollection("t_document").update(query,new BasicDBObject("$set",update),false,false);
		if(writeResult.getN() > 0){
			result = true;
		}
		return result;
	}
	
	@Override
	public Document getById(String id) {
		Query<Document> query =dsForRW.createQuery(Document.class).field(Mapper.ID_KEY)
				.equal(new ObjectId(id));
		return query.get();
	}

    @Override
    public Document findByIdSimple(String id) {
        Query<Document> query =dsForRW.createQuery(Document.class)
                .field(Mapper.ID_KEY).equal(new ObjectId(id));
        query.retrievedFields(false, "content");
        return query.get();
    }

    @Override
    public List<Document> findByIdsSimple(List<String> idList) {
        Query<Document> query =dsForRW.createQuery(Document.class);

        Criteria[] idParams = new Criteria[idList.size()];
        for (int i = 0; i < idList.size(); i++) {
            idParams[i] = query.criteria(Mapper.ID_KEY).equal(new ObjectId(idList.get(i)));
        }
        query.or(idParams);

        query.retrievedFields(false, "content");
        return query.asList();
    }

	@Override
	public DocumentVO getDocumentDetail(DocumentParam param) {
		Query<Document> query =dsForRW.createQuery(Document.class)
										.field("_id").equal(new ObjectId(param.getId()))
										.field("enabled").equal(DocumentEnum.DocumentEnabledStatus.enabled.getIndex());
		Document dc = query.get();
		DocumentVO vo = null;
		if( dc !=null){
			vo = BeanUtil.copy(dc, DocumentVO.class);
			
			if(vo != null){
				param.setDocumentType(vo.getDocumentType());
				Map<String,ContentTypeVO> map =  getAllType(param);
				param.setDocumentType(vo.getDocumentType());
				if(vo.getDocumentType() == DocumentEnum.DocumentType.science.getIndex()){
					String name = map.get(vo.getContentType()).getName();
					vo.setTypeName(name);
				}
				vo.setId(dc.getId().toString());
			}
			
			if (vo.getRecommendDetails() != null) {
				if (vo.getRecommendDetails().getRecommendType() == DocumentEnum.RecommendType.group.getIndex() && StringUtil.isNotEmpty(vo.getRecommendDetails().getGroupId())) {
					DBObject groupQuery = new BasicDBObject();
					groupQuery.put("_id", new ObjectId(vo.getRecommendDetails().getGroupId()));
					DBObject obj = dsForRW.getDB().getCollection("c_group").findOne(groupQuery);
					if (obj != null) {
						vo.getRecommendDetails().setGroupName(MongodbUtil.getString(obj, "name"));
					}
				}else if (vo.getRecommendDetails().getRecommendType() == DocumentEnum.RecommendType.doctor.getIndex() && vo.getRecommendDetails().getDoctorId() != null) {
					User doctor = userDao.getUser(vo.getRecommendDetails().getDoctorId());
					if (doctor != null) {
						vo.getRecommendDetails().setDoctorName(doctor.getName());
					}
				}
			}
		}
		return vo;
	}
	
	@Override
	public void updateVisitCount(DocumentParam param) {
		//先更新document 表里的visitcount为1 ==》向明细表里添加一条纪录
		DBObject query = new BasicDBObject("_id",new ObjectId(param.getId()));
		WriteResult result = dsForRW.getDB().getCollection("t_document").update(query, new BasicDBObject("$inc",new BasicDBObject("visitCount",1)),false,false);
		if(result.getN() > 0){
			VisitDetail vd = new VisitDetail();
			vd.setDocumentId(param.getId());
			vd.setDocumentType(param.getDocumentType());
			vd.setVisitor(param.getVisitor());
			vd.setVisitTime(new Date().getTime());
			dsForRW.insert(vd);
		}
	}

	@Override
	public void updateDocument(DocumentParam param) {
		DBObject query = new BasicDBObject("_id",new ObjectId(param.getId()));
		DBObject update = new BasicDBObject();
		update.put("content", param.getContent());
		update.put("isShowImg", param.getIsShowImg());
		update.put("contentType", param.getContentType());
		update.put("copyPath", param.getCopyPath());
		update.put("description", param.getDescription());
		update.put("title", param.getTitle());
		update.put("url", param.getUrl());
		update.put("lastUpdateTime", new Date().getTime());
		update.put("isRecommend", param.getIsRecommend());
		if (Objects.nonNull(param.getExternalAd())) {
		    update.put("externalAd", param.getExternalAd());
        }
		if (param.getRecommendDetails() != null) {
			update.put("recommendDetails.recommendType", param.getRecommendDetails().getRecommendType());
			update.put("recommendDetails.groupId", param.getRecommendDetails().getGroupId());
			update.put("recommendDetails.doctorId", param.getRecommendDetails().getDoctorId());
		}
		
		if(param.getIsShow() != null){
			update.put("isShow", param.getIsShow());
		}
		dsForRW.getDB().getCollection("t_document").update(query,new BasicDBObject("$set",update),false,false);
	}

	@Override
	public Map<String,Object> getDocumentList(DocumentParam param) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		DBObject query = new BasicDBObject();
		query.put("enabled", DocumentEnum.DocumentEnabledStatus.enabled.getIndex());
		if(!StringUtil.isEmpty(param.getTitle())){
			Pattern pattern = Pattern.compile("^.*"+param.getTitle()+".*$", Pattern.CASE_INSENSITIVE);
			query.put("title", pattern);
		}
		query.put("documentType", param.getDocumentType());
		
		DBObject sort = new BasicDBObject();
		int limit = param.getPageSize();
		if(param.getDocumentType() == DocumentEnum.DocumentType.science.getIndex()){
			sort.put("isTop", 1);
			if(!StringUtil.isEmpty(param.getContentType())){
				query.put("contentType", param.getContentType());
			}
		}else if(param.getDocumentType() == DocumentEnum.DocumentType.adv.getIndex()){
//			limit = 5;//广告最多显示5条纪录
			sort.put("isShow", 1);
			if(param.getIsShow() != null){
				query.put("isShow", param.getIsShow());
			}
		}else{
			return map;
		}
		sort.put("weight", -1);
		sort.put("createTime",-1);
		int skip = (param.getPageIndex())*param.getPageSize();
		List<DocumentVO> list = new ArrayList<DocumentVO>();
		Map<String, ContentTypeVO> ctMap = getAllType(param);
		DBCollection collection = dsForRW.getDB().getCollection("t_document");
		
		DBCursor cursor = collection.find(query).sort(sort).skip(skip).limit(limit);
		DocumentVO vo = null;
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			vo = setField(obj);
			if(param.getDocumentType() == DocumentEnum.DocumentType.science.getIndex() && ctMap.get(vo.getContentType()) != null){
				vo.setTypeName(ctMap.get(vo.getContentType()).getName());
			}
			list.add(vo);
		}
		cursor.close();
		
		if (null != param.getIsRecommend()) {
			for(DocumentVO documentVO : list) {
				if (documentVO.getIsRecommend()!=null && documentVO.getIsRecommend() == 0) {
					documentVO.setRecommendDetails(null);
				}
			}
		}
		
		map.put("count", collection.find(query).count());
		map.put("list", list);
		return map;
	}
	
	@Override
	public PageVO getHealthSicenceDocumentPage(String contentType, String kw, int pageIndex, int pageSize) {
		DocumentEnum.DocumentType documentType = DocumentEnum.DocumentType.science;
		
		Query<Document> query = this.dsForRW.createQuery(Document.class)
				.field("enabled").equal(DocumentEnum.DocumentEnabledStatus.enabled.getIndex())
				.field("documentType").equal(documentType.getIndex());
		
		if(StringUtils.isNotBlank(kw)){
			Pattern pattern = Pattern.compile("^.*"+kw+".*$", Pattern.CASE_INSENSITIVE);
			query.filter("title", pattern);
		}
		
		if (StringUtils.isNotBlank(contentType)) {
			query.field("contentType").equal(contentType);
		}
		
		int skip = pageIndex*pageSize;
		
		query.order("-isTop, -weight, -createTime");
		query.offset(skip).limit(pageSize);
		
		List<Document> list = query.asList();
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		
		// wrap type name
		wrapTypeName(list, documentType);
		
		long count = query.countAll();
		
		PageVO pageVO = new PageVO(list, count);
//		pageVO.setPageData(documentList);
//		pageVO.setTotal(count);
		pageVO.setPageIndex(pageIndex);
		pageVO.setPageSize(pageSize);
		
		return pageVO;
	}
	
	private void wrapTypeName(List<Document> list, DocumentEnum.DocumentType documentType) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		
		List<com.dachen.health.document.entity.po.DocumentType> typeList = this.getAllType(documentType);
		if (CollectionUtils.isEmpty(typeList)) {
			return;
		}
		
		for (Document document:list) {
			if (null == document.getContentType()) {
				continue;
			}
			
			for (com.dachen.health.document.entity.po.DocumentType type:typeList) {
				if (type.getCode().equals(document.getContentType())) {
					document.setTypeName(type.getName());
					break;
				}
			}
		}
	}

	@Override
	public List<DocumentVO> getTopScienceList(DocumentParam param) {
		List<DocumentVO> list = new ArrayList<DocumentVO>();
		DBObject query = new BasicDBObject();
		query.put("enabled", DocumentEnum.DocumentEnabledStatus.enabled.getIndex());
		query.put("documentType", DocumentEnum.DocumentType.science.getIndex());
		query.put("isTop", DocumentEnum.DocumentTopStatus.top.getIndex());
		query.put("weight", new BasicDBObject("$gt",0));
		DBCursor cursor = dsForRW.getDB().getCollection("t_document").find(query).sort(new BasicDBObject("weight", -1)).limit(DocumentEnum.MAX_TOP);
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			DocumentVO vo = setField(obj);
			if(vo != null){
				list.add(vo);
			}
		}
		cursor.close();
		if(list.size() <  DocumentEnum.MAX_TOP){
			int limit = DocumentEnum.MAX_TOP - list.size();
			query = new BasicDBObject();
			query.put("enabled", DocumentEnum.DocumentEnabledStatus.enabled.getIndex());
			query.put("documentType", DocumentEnum.DocumentType.science.getIndex());
			query.put("isTop", DocumentEnum.DocumentTopStatus.unTop.getIndex());
			cursor = dsForRW.getDB().getCollection("t_document").find(query).sort(new BasicDBObject("visitCount", -1)).limit(limit);
			while(cursor.hasNext()){
				DBObject obj = cursor.next();
				DocumentVO vo = setField(obj);
				if(vo != null){
					list.add(vo);
				}
			}
			cursor.close();
		}
		return list;
	}
	
	private DocumentVO setField(DBObject obj){
		if(obj == null){
			return null;
		}
		DocumentVO vo = new DocumentVO();
		vo.setContent(MongodbUtil.getString(obj, "content"));
		vo.setContentType(MongodbUtil.getString(obj, "contentType"));
		vo.setCopyPath(MongodbUtil.getString(obj, "copyPath"));
		vo.setIsShowImg(MongodbUtil.getInteger(obj, "isShowImg"));
		vo.setCreateTime(MongodbUtil.getLong(obj, "createTime"));
		vo.setDescription(MongodbUtil.getString(obj, "description"));
		vo.setDocumentType(MongodbUtil.getInteger(obj, "documentType"));
		vo.setId(MongodbUtil.getString(obj, "_id"));
		vo.setIsShow(MongodbUtil.getInteger(obj, "isShow"));
		vo.setIsTop(MongodbUtil.getInteger(obj, "isTop"));
		vo.setLastUpdateTime(MongodbUtil.getLong(obj, "lastUpdateTime"));
		vo.setTitle(MongodbUtil.getString(obj, "title"));
		vo.setVisitCount(MongodbUtil.getInteger(obj, "visitCount"));
		vo.setWeight(MongodbUtil.getInteger(obj, "weight"));
		vo.setUrl(MongodbUtil.getString(obj, "url"));
		vo.setIsRecommend(MongodbUtil.getInteger(obj, "isRecommend"));
		Object  recommendDetails = obj.get("recommendDetails");
		if (recommendDetails != null) {
			RecommendDetails rd = new RecommendDetails();
			DBObject object = (DBObject) recommendDetails;
			rd.setGroupId(MongodbUtil.getString(object, "groupId"));
			rd.setRecommendType(MongodbUtil.getInteger(object, "recommendType"));
			rd.setDoctorId(MongodbUtil.getInteger(object, "doctorId"));
			vo.setRecommendDetails(rd);
		}
		
		return vo;
	}

	@Override
	public List<DocumentVO> getWeight(DocumentParam param) {
		List<DocumentVO> list = new ArrayList<DocumentVO>();
		DBObject query = new BasicDBObject();
		query.put("enabled", DocumentEnum.DocumentEnabledStatus.enabled.getIndex());
		query.put("documentType", param.getDocumentType());
		if(param.getDocumentType() == DocumentEnum.DocumentType.science.getIndex()){
			query.put("isTop", DocumentEnum.DocumentTopStatus.top.getIndex());
		}else if(param.getDocumentType() == DocumentEnum.DocumentType.adv.getIndex()){
			query.put("isShow", DocumentEnum.DocumentShowStatus.show.getIndex());
		}else{
			return list;
		}
		query.put("weight", new BasicDBObject("$gt",0));
		DocumentVO vo = null;
		DBCursor cursor = dsForRW.getDB().getCollection("t_document").find(query).sort(new BasicDBObject("weight",-1));
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			vo = setField(obj);
			if(vo != null){
				list.add(vo);
			}
		}
		cursor.close();
		return list;
	}

	@Override
	public boolean setShowOrTopStatus(DocumentParam param) {
		boolean result = false;
		DBObject query = new BasicDBObject();
		query.put("enabled", DocumentEnum.DocumentEnabledStatus.enabled.getIndex());
		query.put("_id", new ObjectId(param.getId()));
		DBObject update = new BasicDBObject();
		if(param.getDocumentType() == DocumentEnum.DocumentType.adv.getIndex()){
			update.put("isShow", param.getIsShow());
		}else if(param.getDocumentType() == DocumentEnum.DocumentType.science.getIndex()){
			update.put("isTop", param.getIsTop());
		}
		update.put("weight", param.getWeight());
		WriteResult wr = dsForRW.getDB().getCollection("t_document").update(query, new BasicDBObject("$set",update),false,false);
		if(wr.getN() > 0){
			result = true;
		}
		return result;
	}

	
	private Map<String ,ContentTypeVO> getAllType(DocumentParam param){
		Map<String,ContentTypeVO> map = new HashMap<String,ContentTypeVO>();
		DBObject query = new BasicDBObject("documentType",param.getDocumentType());
		DBCursor cursor = dsForRW.getDB().getCollection("t_document_type").find(query);
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			ContentTypeVO vo = new ContentTypeVO();
			vo.setCode(MongodbUtil.getString(obj, "code"));
			vo.setName(MongodbUtil.getString(obj, "name"));
			vo.setDocumentType(MongodbUtil.getInteger(obj, "documentType"));
			vo.setWeight(MongodbUtil.getInteger(obj, "weight"));
			vo.setCount(MongodbUtil.getInteger(obj, "count"));
			map.put(vo.getCode(), vo);
		}
		cursor.close();
		return map;
	}
	
	private List<com.dachen.health.document.entity.po.DocumentType> getAllType(DocumentEnum.DocumentType documentType){
		Query<com.dachen.health.document.entity.po.DocumentType> query = this.dsForRW.createQuery(com.dachen.health.document.entity.po.DocumentType.class)
					.field("documentType").equal(documentType.getIndex());
		List<com.dachen.health.document.entity.po.DocumentType> list = query.asList();
		return list;
	}

	@Override
	public void updateGroupIndexDesc(DocumentParam param) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(param.getGroupId()));
		DBObject update = new BasicDBObject();
		update.put("documentId", param.getId());
		dsForRW.getDB().getCollection("c_group").update(query, new BasicDBObject("$set",update),true,false);
	}
	
	@Override
	public String getGroupDcoIdByGId(String groupId) {
		String id = "";
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(groupId));
		DBObject object =  dsForRW.getDB().getCollection("c_group").findOne(query);
		if(object != null){
			 id = MongodbUtil.getString(object, "documentId");
		}
		return id;
	}

	@Override
	public boolean delDocumentByGDID(String doctorRecommendID) {
		DBObject query = new BasicDBObject("recommendDoctId",doctorRecommendID);
		query.put("documentType", DocumentEnum.DocumentType.doctorIndex.getIndex());
		dsForRW.getDB().getCollection("t_document").remove(query);
		return true;
	}

	@Override
	public Document getDocumentByRecommendId(String recommendId) {
		return dsForRW.createQuery(Document.class).filter("recommendDoctId", recommendId).filter("documentType", DocumentEnum.DocumentType.doctorIndex.getIndex()).get();
	}
	
	@Override
	public List<Document> getDocumentsByRecommendIds(List<String> recommendIds) {
		return dsForRW.createQuery(Document.class).filter("recommendDoctId in", recommendIds).filter("documentType", DocumentEnum.DocumentType.doctorIndex.getIndex()).asList();
	}
	
	public Map<String,String> getDoctorRecommendInfo(DocumentParam param){
		Map<String,String> map = new HashMap<String,String>();
		String groupDoctorId = "";
		DBObject query = new BasicDBObject("_id",new ObjectId(param.getRecommendDoctId()));
		DBObject projection = new BasicDBObject();
		projection.put("groupDocId", 1);
		projection.put("groupId", 1);
		projection.put("doctorId", 1);
		DBObject obj = dsForRW.getDB().getCollection("t_doctor_recommend").findOne(query, projection);
		if(obj != null){
			groupDoctorId = MongodbUtil.getString(obj, "groupDocId");
			
			//首页推荐医生，docId直接从t_doctor_recommend获取
			if (StringUtils.isEmpty(groupDoctorId)) {
				Integer doctorId = MongodbUtil.getInteger(obj, "doctorId");
				map.put("doctId", doctorId+"");
				User u = userDao.getUser(doctorId);
				if(u !=null){
					map.put("doctName", u.getName());
				}
				return map;
			}
			
			map.put("groupDocId", MongodbUtil.getString(obj, "groupDocId"));
			map.put("groupId", MongodbUtil.getString(obj, "groupId"));
			obj = dsForRW.getDB().getCollection("c_group_doctor").findOne(new BasicDBObject("_id",new ObjectId(groupDoctorId)));
			if(obj != null){
				int doctId = MongodbUtil.getInteger(obj, "doctorId");
				map.put("doctId", doctId+"");
				User u = userDao.getUser(doctId);
				if(u !=null){
					map.put("doctName", u.getName());
				}
			}
		}
		return map;
	}

}
