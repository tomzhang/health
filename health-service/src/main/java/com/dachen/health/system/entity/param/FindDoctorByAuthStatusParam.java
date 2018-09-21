package com.dachen.health.system.entity.param;

import com.dachen.commons.page.PageVO;

/**
 * ProjectName： health-service<br>
 * ClassName： DoctorCheckParam<br>
 * Description：筛选医生的认证状态 <br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
public class FindDoctorByAuthStatusParam extends PageVO {

	// 医生的认证状态
	public int isAuthStatus;
	
	private String keyword;
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getIsAuthStatus() {
		return isAuthStatus;
	}


	public void setIsAuthStatus(int isAuthStatus) {
		this.isAuthStatus = isAuthStatus;
	}

}
