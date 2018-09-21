package com.dachen.line.stat.dao;

import java.util.List;

import com.dachen.line.stat.entity.vo.UserLineService;

/**
 * 获取系统产品列表
 * 
 * @author weilit
 *
 */
public interface IUserLineServiceDao {
	
	/**
	 * 获取用户的服务器列表
	 * @param userId
	 * @return
	 */
	public List<UserLineService> getUserLineService(Integer userId);

	/**
	 * 更新用户服务设置
	 * @param userId
	 * @param serviceId
	 * @param status
	 */
	public void updateUserLineService(Integer userId, String serviceId,
			Integer status);
	
	/**
	 * 批量插入用户服务数据
	 * @param userId
	 * @return
	 */
	public void  insertUserLineServiceList(List<UserLineService> userList);
	
	/**
	 * 批量插入用户服务数据
	 * @param userId
	 * @return
	 */
	public void  insertUserLineService(UserLineService userList);
}
