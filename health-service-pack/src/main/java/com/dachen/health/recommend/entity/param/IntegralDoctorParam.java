package com.dachen.health.recommend.entity.param;

public class IntegralDoctorParam {
	private Integer pageIndex;
	private Integer pageSize;
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
	public Integer getStart() {
		return start = (pageIndex * pageSize);
	}
	public void setStart(Integer start) {
		this.start = start;
	}

}
