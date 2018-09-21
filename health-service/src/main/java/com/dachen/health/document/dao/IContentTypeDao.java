package com.dachen.health.document.dao;

import java.util.List;
import java.util.Map;

import com.dachen.health.document.entity.param.DocumentParam;
import com.dachen.health.document.entity.po.DocumentType;

public interface IContentTypeDao {
	
	boolean updateContentTypeCountByDocumentType(DocumentParam param,String type);
	
	Map<String,Object> getAllTypeByDocumentType(DocumentParam param);
	
	List<DocumentType> getAllTypeByHealthSicenceDocument();

}
