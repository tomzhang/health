package com.dachen.health.friend.entity.param;

public class FriendReqQuery {
	
	/**
	 * 要查询的用户id
	 */
	private Integer userId;
	
	/**
	 * 会话组Id，默认为GROUP_0001
	 */
	private String groupId;
	
	/**
	 * 是否为医药代表 1：是、2：不是
	 */
	private Integer userReqType;
	
	/**
	 * 查询的页码，从0开始
	 */
	private Integer pageIndex;
	/**
	 * 每页大小
	 */
	private Integer pageSize=15;
	
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
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public Integer getUserReqType() {
		return userReqType;
	}
	public void setUserReqType(Integer userReqType) {
		this.userReqType = userReqType;
	}
	


}
