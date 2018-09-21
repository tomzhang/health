package com.dachen.line.stat.dao;

import java.util.List;

import com.dachen.line.stat.entity.vo.NurseDutyTime;

/**
 * 获取系统产品列表
 * 
 * @author weilit
 *
 */
public interface IUserServiceTimeDao {
	
	/**
	 * 获取用户的服务时间列表
	 * @param userId
	 * @return
	 */
	public List<NurseDutyTime> getUserServiceTimeList(Integer userId);
	
	/**
	 * 查询用户大于当前时间的服务时间列表
	 * @param userId
	 * @return
	 */
	public List<NurseDutyTime> getUserServiceTime(Integer userId);
	
	/**
	 * 更新用户服务时间设置
	 */
	public void updateUserServiceTime(Integer userId,String time,Integer status);
	
	/**
	 * 插入用户服务时间
	 * @param userId
	 * @return
	 */
	public void  insertUserServiceTime(NurseDutyTime nurseDutyTime);
	
	/**
	 * 批量插入用户服务数据
	 * @param userId
	 * @return
	 */
	public void  deleteUserServiceTime(Integer userId, String time);
	
	public  NurseDutyTime  getDutyTime(Integer userId, String time);
}
