package com.dachen.health.pack.income.entity.param;

import com.dachen.commons.page.PageVO;

/**
 * 医生收入相关的 参数VO
 *@author wangqiao
 *@date 2016年1月25日
 *
 */
public class DoctorIncomeParam extends PageVO{

	/**
	 * 集团id
	 */
	private String groupId;

	private Integer userType;
	
	
	
	
	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	
	
}

