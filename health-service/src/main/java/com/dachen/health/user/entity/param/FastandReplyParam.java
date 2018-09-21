package com.dachen.health.user.entity.param;

public class FastandReplyParam {
	
	private Integer userId;
	
	private String replyId;
	
	private String replyContent;
	
	private Integer userType;
	
	private int is_system;//是否系统预制   默认为1--不是 0--是
	
	public int getIs_system() {
		return is_system;
	}

	public void setIs_system(int is_system) {
		this.is_system = is_system;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getReplyId() {
		return replyId;
	}

	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	
	

}
