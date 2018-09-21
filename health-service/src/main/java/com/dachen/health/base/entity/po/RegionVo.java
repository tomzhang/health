package com.dachen.health.base.entity.po;

import java.util.List;

/**
 * 地区的VO对象
 * @author fuyongde
 *
 */
public class RegionVo {
	private String id;
	private String name;
	private String parentId;
	private List<RegionVo> subList;
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
	public List<RegionVo> getSubList() {
		return subList;
	}
	public void setSubList(List<RegionVo> subList) {
		this.subList = subList;
	}
	
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RegionVo) {
			RegionVo target = (RegionVo) obj;
			return this.hashCode() == target.hashCode();
		} else {
			return false;
		}
	}
}
