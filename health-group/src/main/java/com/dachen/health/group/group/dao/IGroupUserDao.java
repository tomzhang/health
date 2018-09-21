package com.dachen.health.group.group.dao;

import java.util.List;

import com.dachen.health.group.company.entity.po.GroupUser;

/**
 * 
 * @author fuyongde
 * @date 2016/5/23
 */
public interface IGroupUserDao {
	
	/**
	 * 获取集团的所有者
	 * @param groupId
	 * @return
	 */
	GroupUser findGroupRootAdmin(String groupId);


	List<GroupUser> findGroupRootAdmins(List<String> groupId);

	/**
	 * 查找一个用户是否为管理员
	 * @param userId
	 * @return
	 */
	boolean findUserIsAdmin(Integer userId);

	boolean isAdmin(Integer userId, String groupId);
	
	List<GroupUser> findGroupRoots(String groupId);
	
	List<GroupUser> findGroupAdminWithOutRoot(String groupId);

    List<GroupUser> findByDoctorId(Integer doctorId);
}
