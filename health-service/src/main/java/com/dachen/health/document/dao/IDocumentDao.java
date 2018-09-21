package com.dachen.health.document.dao;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.document.entity.param.DocumentParam;
import com.dachen.health.document.entity.po.Document;
import com.dachen.health.document.entity.vo.DocumentVO;

public interface IDocumentDao {
	
	DocumentVO addDcoument(Document db);
	
	boolean delDocumentById(DocumentParam param);

    Document findByIdSimple(String id);

    List<Document> findByIdsSimple(List<String> idList);

    DocumentVO getDocumentDetail(DocumentParam param);
	Document getById(String id);
	
	void updateVisitCount(DocumentParam param);
	
	void updateDocument(DocumentParam param);
	
	Map<String,Object> getDocumentList(DocumentParam param);
	PageVO getHealthSicenceDocumentPage(String contentType, String kw, int pageIndex, int pageSize);
	
	List<DocumentVO> getTopScienceList(DocumentParam param);
	
	List<DocumentVO> getWeight(DocumentParam param);
	
	boolean setShowOrTopStatus(DocumentParam param);
	
	void updateGroupIndexDesc(DocumentParam param);
	
	String  getGroupDcoIdByGId(String groupId);
	
	/**
	 * 根据doctorRecommendID删除对应的文档
	 * @param groupDoctorID
	 * @return
	 */
	boolean delDocumentByGDID(String doctorRecommendID);
	
	Document getDocumentByRecommendId(String recommendId);
	
	List<Document> getDocumentsByRecommendIds(List<String> recommendIds);

	Map<String,String> getDoctorRecommendInfo(DocumentParam param);
}
