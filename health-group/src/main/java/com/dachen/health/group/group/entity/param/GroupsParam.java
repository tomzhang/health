package com.dachen.health.group.group.entity.param;

import java.util.List;

import com.dachen.health.group.group.entity.po.Group;

/**
 * 集团查询参数
 * @author tan.yf
 * @date  20160606
 */
public class GroupsParam extends Group {
	private List<String> groupIds;

	public List<String> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
	}
}
