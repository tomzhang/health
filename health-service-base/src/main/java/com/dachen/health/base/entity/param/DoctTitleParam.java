package com.dachen.health.base.entity.param;

import com.dachen.health.base.entity.vo.DoctorTitleVO;

public class DoctTitleParam extends DoctorTitleVO {
	
	private Integer pageIndex=0;
	private Integer pageSize=200;
	private Integer start;
	
	public Integer getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public int getStart() {
		return start = (pageIndex * pageSize);
	}
}
