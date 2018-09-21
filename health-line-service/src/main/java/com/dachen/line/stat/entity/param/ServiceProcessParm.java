package com.dachen.line.stat.entity.param;

import com.dachen.commons.page.PageVO;

public class ServiceProcessParm extends PageVO {

	private Integer userId;
	private Integer[] statusList;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer[] getStatusList() {
		return statusList;
	}

	public void setStatusList(Integer[] statusList) {
		this.statusList = statusList;
	}

}
