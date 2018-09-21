package com.dachen.health.knowledge.dao;

import java.util.List;

import com.dachen.health.knowledge.entity.po.KnowledgeCategory;

public interface IKnowledgeCategoryDao {

    public KnowledgeCategory insertKnowledgeCategory(KnowledgeCategory category);
	
	public KnowledgeCategory selectKnowledgeCategoryById(String Id);
	
	public boolean delKnowledgeCategoryById(String id);
	
	public KnowledgeCategory selectDefaultKnowledgeCategoryById(String groupId,String name);
	
	public KnowledgeCategory updateKnowledgeCategoryById(KnowledgeCategory category);
	
	public List<KnowledgeCategory> selectKnowledgeCategoryListByGroupId(String groupId,boolean knowledgeIds);
	
	public boolean insertKnowledgeintoCategory(String categoryId,String knowledgeId);
	
	public List<KnowledgeCategory> searchCategory(List<String> groupIds,String keyword);
	
}


