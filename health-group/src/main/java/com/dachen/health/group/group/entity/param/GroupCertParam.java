package com.dachen.health.group.group.entity.param;

import com.dachen.commons.page.PageVO;

public class GroupCertParam extends PageVO {

	/* 集团id */
    private String groupId;
    
	/* 搜索关键字 */
    private String keyword;
    
    /* 认证状态 */
    private String status;
    
    
    public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    
    
}
