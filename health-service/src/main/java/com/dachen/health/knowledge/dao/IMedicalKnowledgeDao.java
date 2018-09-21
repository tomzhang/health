package com.dachen.health.knowledge.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.dachen.health.knowledge.entity.param.MedicalKnowledgeParam;
import com.dachen.health.knowledge.entity.po.KnowledgeCategory;
import com.dachen.health.knowledge.entity.po.MedicalKnowledge;
import com.dachen.health.knowledge.entity.po.MedicalKnowledgeTop;
import com.mongodb.DBObject;

public interface IMedicalKnowledgeDao {

	public MedicalKnowledge insertMedicalKnowledge(MedicalKnowledge medicalKnowledge);
	
	public MedicalKnowledge selectMedicalKnowledgeById(String id);

    MedicalKnowledge findByIdSimple(String id);

    List<MedicalKnowledge> findByIdsSimple(List<String> idList);

    public boolean updateMedicalKnowledgeById(MedicalKnowledge medicalKnowledge);
	
	public boolean delMedicalKnowledge(String id);
	/**
	 * 物理删除就医知识
	 * @param id
	 * @return
	 */
	public boolean delMedicalKnowledgePhysics(String id);
	
	public boolean upMedicalKnowledge(MedicalKnowledgeTop top);
	
	public List<MedicalKnowledgeTop> selectMedicalKnowledgeTopListByBizId(String bizId);
	
	public List<KnowledgeCategory> selectMedicalKnowledgeListByGroupId(MedicalKnowledgeParam param);
	
	/**
	 * 查询置顶就医知识总数
	 */
	public long getMedicalKnowledgeTopCount(String bizId);
	
	/**
	 * 分页查询置顶就医知识id集合
	 */
	public List<MedicalKnowledgeTop> getMedicalKnowledgeTopIds(List<String> knowledgeIds,String bizId,Integer offset,Integer limit);
	public List<MedicalKnowledgeTop> getMedicalKnowledgeTopIds(List<String> knowledgeIds,String bizId);
	/**
	 * 根据id集合查询就医知识，不排序
	 */
	public List<MedicalKnowledge> selectMedicalKnowledgeListByIds(List<ObjectId> ids,boolean content);
	/**
	 * 根据id集合查询就医知识，排序
	 */
	public List<MedicalKnowledge> selectMedicalKnowledgeListByIds(List<ObjectId> ids,boolean content,Integer offset,Integer limit);
//	/**
//	 *根据医生id查询就医知识
//	 */
//	public List<MedicalKnowledge> selectMedicalKnowledgeListByDoctorIds(List<Integer> doctorIds,Integer offset,Integer limit);
	/**
	 *根据医生id查询就医知识
	 */
	public List<MedicalKnowledge> selectMedicalKnowledgeListByDoctorId(List<String> idList);
	
	public boolean isAdminToGroup(Integer doctorId,String groupId);
	
	//查询就医知识priority
	public Integer getMaxPriority(String bizId);
	//增加置顶就医知识
	public MedicalKnowledgeTop insertMedicalKnowledgeTop(MedicalKnowledgeTop medicalKnowledgeTop);
	
	public boolean delMedicalKnowledgeTop(String id,String bizId);
	
	public boolean updateMedicalKnowledgeTopById(MedicalKnowledgeTop medicalKnowledgeTop);
	
	public MedicalKnowledgeTop selectMedicalKnowledgeTopById(String id);
	
	public boolean delKnowledgeTopByKnowledgeId(String knowledgeId);
	
	public boolean delCategoryByKnowledgeId(String knowledgeId);
	
	public Query<MedicalKnowledge> selectMedicalByCondition(List<ObjectId> idList,String title,String... author);
	
	public MedicalKnowledgeTop selectMedicalKnowledgeTopBiggerThanPriority(String bizId,Integer priority);
	
	public MedicalKnowledgeTop selectMedicalKnowledgeTopByKnowledgeId(String knowledgeId,String bizId);
	
	public DBObject getGroupInfoByGId(String id);
	
	public List<String> getGroupIdByDoctorId(Integer doctorId);
	
	public List<MedicalKnowledge> getKnowledgeListByCreaterId(String createrId,boolean content );
}
