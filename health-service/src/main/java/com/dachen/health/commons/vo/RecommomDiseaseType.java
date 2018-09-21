package com.dachen.health.commons.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value="t_disease_recommend", noClassnameStored = true)
public class RecommomDiseaseType {

	@Id
	private String diseaseId;
	
	private String name;
	
	private Long weight;//排序权重

	public String getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(String id) {
		this.diseaseId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}
	
}
