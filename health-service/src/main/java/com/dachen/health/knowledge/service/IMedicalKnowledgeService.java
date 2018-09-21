package com.dachen.health.knowledge.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.knowledge.entity.param.MedicalKnowledgeParam;
import com.dachen.health.knowledge.entity.po.KnowledgeCategory;
import com.dachen.health.knowledge.entity.po.MedicalKnowledge;
import com.dachen.health.knowledge.entity.vo.MedicalKnowledgeVO;

public interface IMedicalKnowledgeService {
	
	public Map<String,Object> updateKnowledg(MedicalKnowledgeParam param);
	
	public Map<String ,Object> delCategoryById(String id);
	
	public Map<String ,Object> addCategoryById(KnowledgeCategory category);
	
	public Map<String, Object> renameCategory(String id, String name);
	
	public List<KnowledgeCategory> getCategoryList(String groupId);
	
	public PageVO getMedicalKnowledgeListByCategoryId(MedicalKnowledgeParam param);
	
	public Map<String,Object> delKnowledgeById(String id,String groupId);
	
	public PageVO searchKnowledgeListByKeys(MedicalKnowledgeParam param);
	
	public Map<String, Object> addShareCount(String bizId,Integer bizType);
	/**
	 * 获取集团就医知识列表
	 * @param groupId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public PageVO getGroupMedicalKnowledgeList(MedicalKnowledgeParam param);
	
	/**
	 * 获取医生就医知识列表
	 * @param groupId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public PageVO getDoctorMedicalKnowledgeList(MedicalKnowledgeParam param);
	
	/**
	 * 访问就医知识
	 * @param id
	 * @return
	 */
	public Map<String, Object> getUrlById(String id);
	
	/**
	 * 查询就医知识详情
	 * @param id
	 * @return
	 */
	public MedicalKnowledgeVO getDetailById(MedicalKnowledgeParam param);
	MedicalKnowledge getById(String id);
    MedicalKnowledge findByIdSimple(String id);
	List<MedicalKnowledge> getByIds(List<String> ids);

    List<MedicalKnowledge> findByIdsSimple(List<String> ids);

    /**
	 * 设置就医知识置顶
	 * @param id
	 * @return
	 */
	public Map<String, Object> setTop(String id,String bizId);
	
	/**
	 * 取消就医知识置顶
	 * @param id
	 * @return
	 */
	public Map<String, Object> cancelTop(String id,String bizId);
	
	public Map<String, Object> upKnowledge(String id,String bizId);
	
	public Map<String, Object> addKnowledge(MedicalKnowledgeParam param);
	
	public boolean addDoctorToGroup(String source,String to);
}

