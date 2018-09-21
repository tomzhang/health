package com.dachen.line.stat.dao;

import java.util.List;


/**
 * 护士订单服务
 * @author weilit
 * 2015 12 04 
 */
public interface INurseOrderConditionDao {
	
	/**
	 * 获取护士护士所在医院
	 * resultCode 
	 * data 
	 * @param userId
	 * @return
	 */
	public  List<String> getNurseBelongHospital(Integer  userId);
	
	
	/**
	 * 获取护士护士所在科室
	 * resultCode 
	 * data 
	 * @param userId
	 * @return
	 */
	public  List<String> getNurseBelongDepartment(Integer  userId);
	
	
}
