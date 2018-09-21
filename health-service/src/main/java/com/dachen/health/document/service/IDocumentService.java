package com.dachen.health.document.service;

import java.util.List;
import java.util.Map;
import com.dachen.commons.page.PageVO;
import com.dachen.health.document.entity.param.DocumentParam;
import com.dachen.health.document.entity.po.Document;
import com.dachen.health.document.entity.po.DocumentType;
import com.dachen.health.document.entity.vo.DocumentVO;

public interface IDocumentService {
	DocumentVO addDcoument(DocumentParam param);
	
	Map<String ,Object> delDocument(DocumentParam param);
	
	Map<String ,Object> updateDocument(DocumentParam param);
	
	DocumentVO getDocumentDetail(DocumentParam param);
	Document getById(String id);
    Document findByIdSimple(String id);
	List<Document> getByIds(List<String> ids);

    List<Document> findByIdsSimple(List<String> ids);

    DocumentVO getDocumentByGid(String groupId);
	
	Map<String ,Object> viewDocument(DocumentParam param);
	
	PageVO getDocumentList(DocumentParam param);
	PageVO getHealthSicenceDocumentList(String contentType, String kw, int pageIndex, int pageSize);
	
	Map<String,Object> getAllTypeByDocumentType(DocumentParam param);
	List<DocumentType> getAllTypeByHealthSicenceDocument();
	
	Map<String,Object> getTopScienceList(DocumentParam param);
	
	Map<String,Object> setADVShowStatus(DocumentParam param);
	
	Map<String , Object> setTopScience(DocumentParam param);
	
	Map<String , Object> changeWeight(DocumentParam param,String type);
	
	DocumentVO addGoupIndexDoc(DocumentParam param);
	
	Map<String ,Object> updateRecommendDocument(DocumentParam param);
}
