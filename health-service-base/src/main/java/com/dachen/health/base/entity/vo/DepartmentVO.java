package com.dachen.health.base.entity.vo;

import java.util.List;

import org.mongodb.morphia.annotations.Property;

public class DepartmentVO {
    @Property("_id")
    private String id;
    private String name;
    private String parentId;
    private Integer isLeaf;
    private Integer enableStatus;
    private List<DepartmentVO> subList;
	private Integer weight;
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
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public Integer getIsLeaf() {
		return isLeaf;
	}
	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}
	public Integer getEnableStatus() {
		return enableStatus;
	}
	public void setEnableStatus(Integer enableStatus) {
		this.enableStatus = enableStatus;
	}
	public List<DepartmentVO> getSubList() {
		return subList;
	}
	public void setSubList(List<DepartmentVO> subList) {
		this.subList = subList;
	}
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DepartmentVO) {
			DepartmentVO target = (DepartmentVO) obj;
			return this.hashCode() == target.hashCode();
		} else {
			return false;
		}
	}
}
