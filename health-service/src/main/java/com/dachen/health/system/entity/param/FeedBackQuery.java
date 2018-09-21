package com.dachen.health.system.entity.param;

public class FeedBackQuery {
	
	/**
	 * 要查询的用户id
	 */
	private Integer userId;
	/**
	 * 查询的页码，从0开始
	 */
	private Integer pageIndex;
	/**
	 * 每页大小
	 */
	private Integer pageSize=15;
	/**
	 * 意见反馈开始时间
	 */
	private long startTime;
	/**
	 * 意见反馈结束时间
	 */
	private long endTime;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	


}
