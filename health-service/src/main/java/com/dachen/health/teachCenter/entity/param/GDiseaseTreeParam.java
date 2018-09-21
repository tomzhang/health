package com.dachen.health.teachCenter.entity.param;

import java.util.List;

import com.dachen.health.teachCenter.entity.po.GroupDisease;

public class GDiseaseTreeParam {

	private String diseaseId;

	private String groupId;// 如是集团则对应集团ID;平台则为system

	private String name;

	private String parent;

	private Integer count;

	private List<GroupDisease> children;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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

}
