package com.dachen.health.group.group.entity.vo;

import java.util.List;

public class GroupServiceItemVO {

	private String id;
	
	private String groupId;
	
	private String serviceItemId;
	
	private String serviceItemName;
	
	private List<GroupServiceItemVO> children;

	private Integer price;//集团制定的价格
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getServiceItemId() {
		return serviceItemId;
	}

	public void setServiceItemId(String serviceItemId) {
		this.serviceItemId = serviceItemId;
	}

	public String getServiceItemName() {
		return serviceItemName;
	}

	public void setServiceItemName(String serviceItemName) {
		this.serviceItemName = serviceItemName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public List<GroupServiceItemVO> getChildren() {
		return children;
	}

	public void setChildren(List<GroupServiceItemVO> children) {
		this.children = children;
	}

}
