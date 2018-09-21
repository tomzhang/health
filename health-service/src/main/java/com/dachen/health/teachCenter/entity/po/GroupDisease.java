package com.dachen.health.teachCenter.entity.po;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.Property;

@Entity("i_group_disease")
public class GroupDisease {
	
	@Id
	private ObjectId id;

	private List<String> articleId;
	
    private String diseaseId;
	
	private String groupId;//如是集团则对应集团ID;平台则为system

    private String name;

    private String parent;
    
    private Integer count;
    
    private Integer weight;
    
    @NotSaved
    private List<GroupDisease> children;
	
    

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<String> getArticleId() {
		return articleId;
	}

	public void setArticleId(List<String> articleId) {
		this.articleId = articleId;
	}

	public String getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(String diseaseId) {
		this.diseaseId = diseaseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<GroupDisease> getChildren() {
		return children;
	}

	public void setChildren(List<GroupDisease> children) {
		this.children = children;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	
}
