package com.dachen.health.disease.vo;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.dachen.health.disease.entity.DiseaseType;
import com.google.common.collect.Lists;

public class DiseaseTypeVo {
	private String id;
	//名称
	private String name;
	
	//科室
	private String department;
	
	/**医生数量*/
	private Integer docNum=0;
	
	private Integer weights=0;
	
	private List<String> alias;

	//科室对应的图片路径
	private String imgPath;
	
	/**疾病简介**/
	private String introduction;
	/**注意事项**/
	private String attention;
	/**疾病**/
	private List<String> remark;
	//常见疾病显示名称
	private String recommendName;
	
	public String getRecommendName() {
		return recommendName;
	}

	public void setRecommendName(String recommendName) {
		this.recommendName = recommendName;
	}

	public List<String> getAlias() {
		return alias;
	}

	public void setAlias(List<String> alias) {
		this.alias = alias;
	}

	public List<String> getRemark() {
		return remark;
	}

	public void setRemark(List<String> remark) {
		this.remark = remark;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getAttention() {
		return attention;
	}

	public void setAttention(String attention) {
		this.attention = attention;
	}

	public Integer getWeights() {
		return weights;
	}

	public void setWeights(Integer weights) {
		this.weights = weights;
	}

	public Integer getDocNum() {
		return docNum;
	}

	public void setDocNum(Integer docNum) {
		this.docNum = docNum;
	}

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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	
	public static DiseaseTypeVo parseFromDiseaseType(DiseaseType diseaseType) {
		DiseaseTypeVo diseaseTypeVo = new DiseaseTypeVo();
		diseaseTypeVo.setId(diseaseType.getId());
		diseaseTypeVo.setName(diseaseType.getName());
		diseaseTypeVo.setDepartment(diseaseType.getDepartment());
		diseaseTypeVo.setDocNum(diseaseType.getDocNum());
		diseaseTypeVo.setWeights(diseaseType.getWeights());
		String alias = diseaseType.getAlias();
		List<String> alia = Lists.newArrayList();
		if(StringUtils.isNotEmpty(alias)) {
			alias = alias.replaceAll(",", "，");
			String[] tempAlias = alias.split("，");
			Collections.addAll(alia, tempAlias);
		}
		diseaseTypeVo.setAlias(alia);
		
		diseaseTypeVo.setImgPath(diseaseType.getImgPath());
		diseaseTypeVo.setIntroduction(diseaseType.getIntroduction());
		diseaseTypeVo.setAttention(diseaseType.getAttention());
		String remarks = diseaseType.getRemark();
		List<String> remark = Lists.newArrayList();
		if (StringUtils.isNotEmpty(remarks)) {
			remarks = remarks.replaceAll(",", "，");
			String[] tempRemark = remarks.split("，");
			Collections.addAll(remark, tempRemark);
		}
		diseaseTypeVo.setRemark(remark);			
		
		return diseaseTypeVo;
	}
}
