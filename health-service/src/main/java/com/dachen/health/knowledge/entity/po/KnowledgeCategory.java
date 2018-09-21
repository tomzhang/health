package com.dachen.health.knowledge.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "i_knowledge_category",noClassnameStored=true)
public class KnowledgeCategory {

	@Id
	private String id;
	private String name;
	private String groupId;
	private Integer weight;
	private String[] knowledgeIds;
	private Integer count;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	public String[] getKnowledgeIds() {
		return knowledgeIds;
	}
	public void setKnowledgeIds(String[] knowledgeIds) {
		this.knowledgeIds = knowledgeIds;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
	
	
}
