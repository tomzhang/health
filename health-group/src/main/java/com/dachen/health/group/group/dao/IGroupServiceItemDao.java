package com.dachen.health.group.group.dao;

import java.util.List;

import com.dachen.health.group.group.entity.po.GroupServiceItem;

public interface IGroupServiceItemDao {

	List<GroupServiceItem> getByFilter(String groupId, String hospitalId);
	
	GroupServiceItem getByFilter(String groupId, String hospitalId, String serviceItemId);
}
