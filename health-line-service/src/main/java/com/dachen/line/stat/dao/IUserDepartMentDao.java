package com.dachen.line.stat.dao;

import java.util.List;

import org.bson.types.ObjectId;

import com.dachen.line.stat.entity.vo.UserDepartment;



/**
 * 用户科室对象
 * @author weilit
 * 2015 12 04 
 */
public interface IUserDepartMentDao {
	
	/**
	 * 查询制定字段条件下的下面的科室
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<UserDepartment> getUserDepartmentList(String column,Object sourceId);
	/**
	 * 查询制定字段条件下的下面的科室
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<ObjectId> getUserDepartmentStringList(String column,Object sourceId);
	
	/**
	 * 更新用户服务设置
	 * @param userId
	 * @param serviceId
	 * @param status
	 */
	public void updateUserDepartment(String  id, String hostitalId);
	
	/**
	 * 批量插入用户服务数据
	 * @param userId
	 * @return
	 */
	public void  insertBatchUserDepartment(List<UserDepartment> hospital);
	
	/**
	 * 批量插入用户服务数据
	 * @param userId
	 * @return
	 */
	public void  insertUserDepartment(UserDepartment hospital);
}
