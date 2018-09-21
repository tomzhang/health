package com.dachen.health.user.entity.vo;

import com.dachen.health.user.entity.po.Change;

public class OperationRecordVO {
	
	private Long createTime;
	
	private String creatorName;
	
	private String content;
	
	private Change change;

	public Change getChange() {
		return change;
	}

	public void setChange(Change change) {
		this.change = change;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
