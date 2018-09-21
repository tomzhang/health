package com.dachen.health.knowledge.entity.vo;

import com.dachen.health.knowledge.entity.po.MedicalKnowledge;

public class MedicalKnowledgeVO extends MedicalKnowledge {
	
	private Integer priority;
	private String authorName;
	private String isTop;
	private String categoryId;
	
	private String categoryName;//分类名

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getIsTop() {
		return isTop;
	}

	public void setIsTop(String isTop) {
		this.isTop = isTop;
	}
	
	

}
