package com.dachen.health.group.common.entity.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

//集团推荐
@Entity(value="t_group_recommend", noClassnameStored = true)
public class RecommendGroupVO {

	@Id
	private String id;
	
	private String name;
	
	private String manager;
	
	private String managerTitle;
	
	private Long memberNumber;//成员数
	
	private String logoUrl;
	
	private Long weight;//排序权重
	
	private Long groupCureNum;//就诊量
	
	private String introduction;//集团介绍
	
	private String skill;//集团擅长
	
	private Integer isSelect;//是否已在推荐列表
	
	private String certStatus;//认证状态

	public Integer getIsSelect() {
		return isSelect;
	}

	public void setIsSelect(Integer isSelect) {
		this.isSelect = isSelect;
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

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getManagerTitle() {
		return managerTitle;
	}

	public void setManagerTitle(String managerTitle) {
		this.managerTitle = managerTitle;
	}

	public Long getMemberNumber() {
		return memberNumber;
	}

	public void setMemberNumber(Long memberNumber) {
		this.memberNumber = memberNumber;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}

	public Long getGroupCureNum() {
		return groupCureNum;
	}

	public void setGroupCureNum(Long groupCureNum) {
		this.groupCureNum = groupCureNum;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}
	
	public String getCertStatus() {
		return certStatus;
	}

	public void setCertStatus(String certStatus) {
		this.certStatus = certStatus;
	}
	
}
