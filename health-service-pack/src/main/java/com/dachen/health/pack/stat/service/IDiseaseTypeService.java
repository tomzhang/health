package com.dachen.health.pack.stat.service;

import java.util.List;
import java.util.Set;

import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.tree.ExtTreeNode;

public interface IDiseaseTypeService {

	List<DiseaseTypeVO> getDiseaseTypeTree4Plan(String groupId, Integer tmpType);
	
	List<DiseaseTypeVO> getNewDiseaseType(String groupId, Integer tmpType, boolean includePlatform) throws HttpApiException;
	
	List<DiseaseTypeVO> getDiseaseTypeList(Set<String> ids, boolean onlyShowParentNode);
	
	/**
	 * 获取病种列表所在的一级病种
	 * @param ids
	 * @return
	 */
	List<DiseaseTypeVO> getLevel1DiseaseTypeList(Set<String> ids);
	List<ExtTreeNode> getLevel1DiseaseTypeTree(Set<String> ids);

	Set<String> findCarePlanDiseaseTypeIdSet(String groupId, Integer tmpType);
	
}
