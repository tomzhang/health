package com.dachen.health.base.entity.vo;

import java.util.List;

/**
 * 服务类型的VO对象
 * @author liangcs
 *
 */

public class ServiceTypeVO {
	
	private Integer id;
	
	private String name;
	
	private String parentId;
	
	private List<ServiceTypeVO> subList;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public List<ServiceTypeVO> getSubList() {
		return subList;
	}

	public void setSubList(List<ServiceTypeVO> subList) {
		this.subList = subList;
	}
	
}
