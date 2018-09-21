package com.dachen.health.pack.stat.service.impl;

import com.dachen.careplan.api.client.CarePlanApiClientProxy;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.pack.stat.service.IDiseaseTypeService;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.StringUtil;
import com.dachen.util.tree.ExtTreeNode;
import com.dachen.util.tree.ExtTreeUtil;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class DiseaseTypeServiceImpl implements IDiseaseTypeService {

	@Resource(name = "dsForRW")
	private AdvancedDatastore dsForRW;

	@Resource
	protected CarePlanApiClientProxy carePlanApiClientProxy;
	
	private Map<String, DiseaseTypeVO> voMap = new HashMap<String, DiseaseTypeVO>();
	private void initDiseaseType() {
		if (!voMap.isEmpty()) return;
		List<DiseaseTypeVO> voList = dsForRW.createQuery("b_disease_type",DiseaseTypeVO.class).asList();
		for (DiseaseTypeVO vo : voList) {
			voMap.put(vo.getId(), vo);
		}
	}
	
	@Override
	public List<DiseaseTypeVO> getDiseaseTypeTree4Plan(String groupId, Integer tmpType) {
		groupId = initGroupId(groupId);
		
		List<DiseaseTypeVO> vos = getDiseaseTypeList(findCarePlanDiseaseTypeIdSet(groupId, tmpType), false);
		for (Iterator<DiseaseTypeVO> it = vos.iterator(); it.hasNext();) {
			if (!"0".equals(it.next().getParent())) {
				it.remove();
			}
		}
		return vos;
	}
	
	@Override
	public List<DiseaseTypeVO> getNewDiseaseType(String groupId, Integer tmpType, boolean includePlatform) throws HttpApiException {
		List<String> groupIds = new ArrayList<String>();
		if (includePlatform) {
			groupIds.addAll(addPlatformId(groupId));
		} else {
			groupIds.add(initGroupId(groupId));
		}
		return getDiseaseTypeList(selectNewRecord(groupIds, tmpType), true);
	}

	private String initGroupId(String groupId) {
		if(StringUtil.isBlank(groupId)) {
			groupId = GroupUtil.PLATFORM_ID;
		}
		return groupId;
	}
	
	private List<String> addPlatformId(String groupId) {
		if(StringUtil.isBlank(groupId)) {
			return Arrays.asList(GroupUtil.PLATFORM_ID);
		}
		return Arrays.asList(groupId, GroupUtil.PLATFORM_ID);
	}
	
	public List<DiseaseTypeVO> getDiseaseTypeList(Set<String> ids, boolean onlyShowParentNode) {
		if (ids.isEmpty()) return new ArrayList<DiseaseTypeVO>();
		
		initDiseaseType();
		Query<DiseaseTypeVO> q = dsForRW.createQuery("b_disease_type",DiseaseTypeVO.class).filter("_id in", ids);
		if (onlyShowParentNode) {
			q.filter("parent", "0");
		}
		List<DiseaseTypeVO> voList = q.asList();
		return addParentNode(voList);
	}
	
	@Override
	public List<DiseaseTypeVO> getLevel1DiseaseTypeList(Set<String> ids) {
		
		List<DiseaseTypeVO> list = this.getDiseaseTypeList(ids, false);
		
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		
		for (Iterator<DiseaseTypeVO> it = list.iterator(); it.hasNext();) {
			DiseaseTypeVO vo = it.next();
			if (!"0".equals(vo.getParent())) {
				it.remove();
			}
		}
		
		return list;
	}
	
	@Override
	public List<ExtTreeNode> getLevel1DiseaseTypeTree(Set<String> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}
		
		List<DiseaseTypeVO> voList = this
				.getLevel1DiseaseTypeList(ids);

		if (CollectionUtils.isEmpty(voList)) {
			return null;
		}
		
		List<ExtTreeNode> nodeList = new ArrayList<ExtTreeNode>();
        for (DiseaseTypeVO vo : voList) {
            ExtTreeNode node = new ExtTreeNode();
            node.setId(vo.getId());
            node.setName(vo.getName());
            node.setParentId(vo.getParent());

            nodeList.add(node);
        }

        return ExtTreeUtil.buildTree(nodeList);
	}
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Set<String> findCarePlanDiseaseTypeIdSet(String groupId, Integer tmpType) {
		Set<String> ids = new HashSet<String>();
		
		List<String> diseaseTypeIdList;
		try {
			diseaseTypeIdList = carePlanApiClientProxy.getDiseaseTypeIdListByQuery(groupId, null, tmpType);
			if (!CollectionUtils.isEmpty(diseaseTypeIdList)) {
				ids.addAll(diseaseTypeIdList);
			}
		} catch (HttpApiException e) {
			logger.error(e.getMessage(), e);
		}
		
		return ids;
	} 

	private Set<String> selectNewRecord(List<String> groupIds, Integer tmpType) throws HttpApiException {
		
		Set<String> ids = new HashSet<String>();
		
		switch (tmpType.intValue()) {
		case 1:
		case 2:
		case 3://病情跟踪
		case 4://日程
		case 5://生活量表
			List<String> tmpIds = carePlanApiClientProxy.getLifeScaleDiseaseTypeIdList(groupIds);
			if (!CollectionUtils.isEmpty(tmpIds)) {
				ids.addAll(tmpIds);
			}
			break;
		case 6://调查表
			tmpIds = carePlanApiClientProxy.getSurveyDiseaseTypeIdList(groupIds);
			if (!CollectionUtils.isEmpty(tmpIds)) {
				ids.addAll(tmpIds);
			}
			break;
		default:
			throw new IllegalArgumentException();
		}
		return ids;
	}
	
	/**
	 * 添加父节点
	 * @param voList
	 * @return
	 */
	private List<DiseaseTypeVO> addParentNode(List<DiseaseTypeVO> voList) {
		List<DiseaseTypeVO> list = new ArrayList<DiseaseTypeVO>();
		Set<DiseaseTypeVO> voSet = new HashSet<DiseaseTypeVO>();
		for (DiseaseTypeVO vo : voList) {
			addNode(voSet, vo);
		}
		list.addAll(voSet);
		return list;
	}
	
	/**
	 * 递归添加节点
	 * @param voSet
	 * @param vo
	 */
	private void addNode(Set<DiseaseTypeVO> voSet, DiseaseTypeVO vo) {
		voSet.add(vo);
		if (vo.getParent() == null || "0".equals(vo.getParent()) || voSet.contains(voMap.get(vo.getParent())))
			return;
		if (voMap.containsKey(vo.getParent())) {
			addNode(voSet, voMap.get(vo.getParent())); 
		}
	}

}
