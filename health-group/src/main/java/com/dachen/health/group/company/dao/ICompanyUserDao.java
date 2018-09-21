package com.dachen.health.group.company.dao;

import java.util.List;

import org.mongodb.morphia.query.Query;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.company.entity.param.GroupUserParam;
import com.dachen.health.group.company.entity.po.GroupUser;


/**
 * 集团管理员（c_group_user）相关的Dao层接口定义
 * @author wangqiao 重构
 * @date 2016年4月19日
 */
public interface ICompanyUserDao {


	/**
	 * 新增 groupUser（集团管理员） 记录
	 * @author wangqiao 重构
	 * @date 2016年4月18日
	 * @param cuser
	 * @return
	 */
	public GroupUser add(GroupUser cuser);
	
	/**
     * </p>更新（修改）公司信息</p>
     * @param companyUser
     * @return boolean
     * @author pijingwei
     * @date 2015年8月3日
     */
	@Deprecated
	void update(GroupUser cuser);
	
	/**
	 * 根据记录id 更新GroupUser状态
	 * @author wangqiao
	 * @date 2016年4月19日
	 * @param id
	 * @param status
	 * @param updateUserId
	 */
	public void updateStatusById(String id,String status,Integer updateUserId);
	
	/**
     * </p>根据groupId和doctorId删除集团管理员，把状态设置为：S：离职状态</p>
	 * @param groupId
	 * @param doctorId
     * @return
     * @author pijingwei
     * @date 2015年8月3日
     */
	@Deprecated
	public void deleteByGroupUser(String groupId, Integer doctorId);
	
	/**
	 * 根据集团id和医生id删除 groupUser记录
	 * @author wangqiao
	 * @date 2016年4月19日
	 * @param groupId
	 * @param doctorId
	 */
	public void deleteByGroupIdAndDoctorId(String groupId, Integer doctorId);

	/**
	 * 根据记录id 删除groupUser记录
	 * @author wangqiao 重构
	 * @date 2016年4月19日
	 * @param ids
	 */
	public void deleteById(String[] ids);
	

	/**
	 * 根据条件 搜索groupUser信息
	 * @author wangqiao 重构
	 * @date 2016年4月19日
	 * @param param
	 * @return
	 */
	public PageVO search(GroupUserParam param);

//	/**
//     * </p>验证用户登录</p>
//     * @param cuser
//     * @return CompanyUser
//     * @author pijingwei
//     * @date 2015年8月12日
//	 */
//	GroupUser getLogin(GroupUser cuser);

	/**
     * </p>根据条件 获取单个用户信息</p>
     * @param cuser
     * @return GroupUser
     * @author pijingwei
     * @date 2015年8月12日
	 */
	@Deprecated
	GroupUser getGroupUserById(GroupUser cuser);

	/**
     * </p>根据groupId集合获取当前集团下管理员的人数</p>
     * @param ids
     * @return int
     * @author pijingwei
     * @date 2015年8月28日
	 */
//	Query<GroupUser> getQueryByGroupIds(String... groupIds);
	
	/**
	 * 通过id读取 groupUser信息
	 * @param id
	 * @return
	 *@author wangqiao
	 *@date 2015年12月26日
	 */
//	public GroupUser getGroupUserById(String id);
	
	/**
	 * 通过doctorid查询 是否有管理员权限
	 * @param doctorId
	 * @param type
	 * @param status
	 * @return
	 *@author wangqiao
	 *@date 2015年12月28日
	 */
//	public List<GroupUser> getGroupUserListByIdAndStatus(Integer doctorId,Integer type,String status) ;
	
	/**
	 * 通过 doctorId，groupId，type和status查询 groupUser信息
	 * @param doctorId
	 * @param type
	 * @param status
	 * @param groupId
	 * @return
	 *@author wangqiao
	 *@date 2015年12月29日
	 */
//	public List<GroupUser> getGroupUserListByIdAndStatus(Integer doctorId,Integer type,String status,String groupId) ;
	
	/**
	 * 根据医生id和集团id查询 集团管理员信息（两个id参数都不能为空）
	 * @author wangqiao
	 * @date 2016年4月18日
	 * @param doctorId  医生id
	 * @param groupId   集团id
	 * @param status      状态
	 * @return
	 */
//	public GroupUser getGroupUserByIdAndStatus(Integer doctorId,String groupId,String status);
	
	/**
	 * 根据记录id，医生id，集团id，type和status 读取groupUser信息
	 * @author wangqiao
	 * @date 2016年4月19日
	 * @param id  记录id
	 * @param doctorId 医生id
	 * @param groupId 集团id
	 * @param type      类型：集团/公司
	 * @param status   状态
	 * @return
	 */
	public GroupUser getGroupUserByIdAndStatus(String id,Integer doctorId,String groupId,Integer type,String status);
	
	/**
	 * 根据记录id，医生id，集团id，type和status 读取groupUser列表
	 * @author wangqiao
	 * @date 2016年4月19日
	 * @param id  记录id
	 * @param doctorId 医生id
	 * @param groupId 集团id
	 * @param type      类型：集团/公司
	 * @param status   状态
	 * @return
	 */
	public List<GroupUser> getGroupUserListByIdAndStatus(String id,Integer doctorId,String groupId,Integer type,String status);
	
	/**
	 * 读取某groupId的超级管理员
	 * @author wangqiao
	 * @date 2016年4月19日
	 * @param groupId
	 * @return
	 */
	public GroupUser getRootAdminByGroupId(String groupId);

    GroupUser getRootGroupManage(String groupId);
}
