package com.dachen.health.base.entity.vo;

import java.util.List;

public class DiseaseLaberVO {
	
	private String diseaseId;
	
	private String diseaseName;
	
	private Integer followed;
	
	private List<DiseaseLaberVO> childs;

	public String getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(String diseaseId) {
		this.diseaseId = diseaseId;
	}

	public String getDiseaseName() {
		return diseaseName;
	}

	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}

	public Integer getFollowed() {
		return followed;
	}

	public void setFollowed(Integer followed) {
		this.followed = followed;
	}

	public List<DiseaseLaberVO> getChilds() {
		return childs;
	}

	public void setChilds(List<DiseaseLaberVO> childs) {
		this.childs = childs;
	}
}
