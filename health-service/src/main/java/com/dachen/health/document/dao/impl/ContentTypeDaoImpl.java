package com.dachen.health.document.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.document.constant.DocumentEnum;
import com.dachen.health.document.dao.IContentTypeDao;
import com.dachen.health.document.entity.param.DocumentParam;
import com.dachen.health.document.entity.po.Document;
import com.dachen.health.document.entity.po.DocumentContentType;
import com.dachen.health.document.entity.po.DocumentType;
import com.dachen.health.document.entity.vo.ContentTypeVO;
import com.dachen.util.MongodbUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository
public class ContentTypeDaoImpl extends NoSqlRepository implements IContentTypeDao {
	

	@Override
	public boolean updateContentTypeCountByDocumentType(DocumentParam param,String type) {
		boolean result = false;
		DBObject query = new BasicDBObject();
		query.put("code",param.getContentType());
		query.put("documentType", param.getDocumentType());
		
		DBObject update = new BasicDBObject();
		if(type.equals("+")){
			update.put("$inc",new BasicDBObject("count",1));
		}else if(type.equals("-")){
			update.put("$inc",new BasicDBObject("count",-1));
		}else{
			return false;
//			update.put("$inc",new BasicDBObject("count",0));
		}
		WriteResult writeResult = dsForRW.getDB().getCollection("t_document_type").update(query, update, false, false);
		if(writeResult.getN()>0){
			result = true;
		}
		return result;
	}

	@Override
	public Map<String,Object> getAllTypeByDocumentType(DocumentParam param) {
		Map<String,Object> map = new HashMap<String, Object>();
		DBObject query = new BasicDBObject("documentType",param.getDocumentType());
		DBCursor cursor = dsForRW.getDB().getCollection("t_document_type").find(query).sort(new BasicDBObject("weight",-1));
		List<ContentTypeVO> list = new ArrayList<ContentTypeVO>();
		ContentTypeVO vo = null;
		int count = 0;
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			vo = new ContentTypeVO();
			vo.setCode(MongodbUtil.getString(obj, "code"));
			vo.setName(MongodbUtil.getString(obj, "name"));
			vo.setDocumentType(MongodbUtil.getInteger(obj, "documentType"));
			vo.setWeight(MongodbUtil.getInteger(obj, "weight"));
			vo.setCount(MongodbUtil.getInteger(obj, "count"));
			count += vo.getCount();
			list.add(vo);
		}
		cursor.close();
		map.put("count", count);
		map.put("list", list);
		return map;
	}
	
	@Override
	public List<DocumentType> getAllTypeByHealthSicenceDocument() {
		Query<DocumentType> query = this.dsForRW.createQuery(DocumentType.class)
				.field("documentType").equal(DocumentEnum.DocumentType.science.getIndex())
				.field("count").greaterThan(0)
				.retrievedFields(false, Mapper.ID_KEY)
				.order("-weight");
		
		List<DocumentType> typeList = query.asList();
		return typeList;
	}

}
