package com.dachen.health.base.entity.po;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author fuyongde
 *
 */
public class ExistRegionVo implements Serializable {
	private String name;
	private Integer code;
	private Integer isHot;
	private Integer parentId;
	private List<ExistRegionVo> children;
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public Integer getIsHot() {
		return isHot;
	}
	public void setIsHot(Integer isHot) {
		this.isHot = isHot;
	}
	public List<ExistRegionVo> getChildren() {
		return children;
	}
	public void setChildren(List<ExistRegionVo> children) {
		this.children = children;
	}
	
	@Override
	public int hashCode() {
		return this.code.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExistRegionVo) {
			ExistRegionVo target = (ExistRegionVo) obj;
			return this.hashCode() == target.hashCode();
		} else {
			return false;
		}
	}
}
