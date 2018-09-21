package com.dachen.line.stat.dao;

import java.util.List;

import com.dachen.health.commons.vo.User;



/**
 * @author weilit
 * 2015 12 04 
 */
public interface INurseUserDao {
	

	
	/**
	 * 搜索符合医院条件的医生
	 * @param userId
	 * @return
	 */
	public List<User>  getUserList(List<String> hospital);
	
	public   boolean checkUserCertStatus(Integer userId);
	
	/**
	 * 记录是否进行了指引
	 * @param userId
	 * @param guideType
	 */
	public   User  updateUserGuide(Integer userId);
}
