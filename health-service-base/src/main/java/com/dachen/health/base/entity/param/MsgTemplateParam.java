package com.dachen.health.base.entity.param;

import com.dachen.health.base.entity.po.MsgTemplate;

public class MsgTemplateParam extends MsgTemplate {
	
	public int pageIndex = 0; // 页码，从0开始，即0为第一页
	
	public int pageSize = 20; // 每页的数量

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
}
