package com.dachen.health.group.company.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.company.entity.param.GroupUserParam;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.company.entity.vo.GroupUserVO;
import com.dachen.sdk.exception.HttpApiException;


/**
 * 集团管理员相关业务 接口定义
 * @author wangqiao 重构
 * @date 2016年4月26日
 */
public interface ICompanyUserService {

	
	/**
	 *   直接新增一个集团管理员
	 * @param groupId  集团id
	 * @param doctorId  医生id
	 * @param operationUserId  操作人id
	 * @return
	 *@author wangqiao
	 *@date 2016年1月7日
	 */
	public GroupUser addGroupManage(String groupId,Integer doctorId , Integer operationUserId);
	
	/**
	 * 新增一个公司的管理员
	 * @author wangqiao
	 * @date 2016年4月18日
	 * @param companyId
	 * @param doctorId
	 * @param operationUserId
	 * @return
	 */
	public GroupUser addCompanyManage(String companyId,Integer doctorId , Integer operationUserId);
	
	/**
	 * 直接新增一个集团的 超级管理员
	 * @author wangqiao
	 * @date 2016年3月5日
	 * @param groupId
	 * @param doctorId
	 * @param operationUserId  操作人id
	 * @return
	 */
	public GroupUser addRootGroupManage(String groupId,Integer doctorId,Integer operationUserId);
	
	
	/**
	 * 获取 集团所有管理员的 doctorId
	 * @param groupId  集团id
	 * @return
	 *@author wangqiao
	 *@date 2016年1月18日
	 */
	public List<Integer> getGroupAdminByGroupId(String groupId);
	

	
	/**
	 * 邀请成为集团/医院  普通管理员
	 * @author wangqiao
	 * @date 2016年4月20日
	 * @param doctorId 被邀请医生id
	 * @param groupId  集团id
	 * @param operationUserId 邀请操作人id
	 * @param againInvite  是否再次邀请  1=再次邀请
	 * @return
	 */
	public Map<String, Object> inviteJoinGroupManage(Integer doctorId,String groupId,Integer operationUserId,Integer againInvite) throws HttpApiException;

	
	/**
     * </p>添加（注册）公司管理员</p>
     * @param companyUser
     * @return boolean
     * @author pijingwei
     * @date 2015年8月12日
     */
	@Deprecated
	Map<String, Object> saveCompanyUser(GroupUserParam cuser) throws HttpApiException;
	
	

	

	/**
	 * 根据id删除 GroupUser信息（支持多个删除，超级管理员不能删除）
	 * @author wangqiao 重构
	 * @date 2016年4月19日
	 * @param ids
	 */
	public void deleteGroupUserById(String[] ids);
	
	/**
	 * 医生离职时，删除groupUser信息（不需要判断是否超级管理员）
	 * @author wangqiao 重构
	 * @date 2016年4月19日
	 * @param groupId
	 * @param doctorId
	 */
	public void deleteGroupUserByLeaveGroup(String groupId,Integer doctorId);
	

	/**
	 * 根据条件 搜索groupUser信息
	 * @author wangqiao 重构
	 * @date 2016年4月19日
	 * @param param
	 * @return
	 */
	public PageVO searchCompanyUser(GroupUserParam param);
	
	/**
     * </p>验证用户名</p>
     * @param cuser
     * @return CompanyUser
     * @author pijingwei
     * @date 2015年8月12日
	 */
//	GroupUser validationUser(GroupUser cuser);

	
	/**
     * </p>公司用户登录入口</p>
	 * @param telephone
	 * @param password
     * @return Map<String, Object>
     * @author pijingwei
     * @date 2015年9月11日
	 */
	@Deprecated
	Map<String, Object> companyLogin(String telephone, String password);
	
	/**
     * </p>验证公司帐号登录</p>
     * @param cuser
     * @return Map<String, Object>
     * @author pijingwei
     * @date 2015年8月12日
	 */
	@Deprecated
	Map<String, Object> getLoginByCompanyUser(GroupUserVO cuser);
	

	/**
	 * 医生登录验证
	 * @author wangqiao 重构
	 * @date 2016年4月20日
	 * @param doctorId
	 * @param groupId
	 * @return
	 */
	public Map<String, Object> getLoginByGroupUser(Integer doctorId,String groupId);
	
	

	
	/**
	 * 获取 加入邀请的 状态
	 * @param id      邀请记录的id
	 * @param type    1，公司管理员，2，集团管理员，3，医生集团成员
	 * @return
	 *@author wangqiao
	 *@date 2016年1月8日
	 */
	public Map<String, Object> getInviteStatus(String id,Integer type);

//	/**
//	 * 添加医院超级 管理员
//	 * @param groupId
//	 * @param doctorId
//	 * @return
//	 */
//	public GroupUser addRootHospitalManage(String groupId,Integer doctorId);
	

	/**
	 * 获取集团的超级管理员
	 * @author wangqiao 重构
	 * @date 2016年4月20日
	 * @param groupId
	 * @return
	 */
	public GroupUser getRootGroupManage(String groupId);
	
	/**
	 * 通过集团id，读取所有在职的集团管理员列表信息
	 * @author wangqiao
	 * @date 2016年4月18日
	 * @param groupId
	 * @return
	 */
	public List<GroupUser> getGroupUserByGroupId(String groupId);
	
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
	 * 根据记录id 更新GroupUser状态
	 * @author wangqiao
	 * @date 2016年4月19日
	 * @param id
	 * @param status
	 * @param updateUserId
	 */
	public void updateStatusById(String id,String status,Integer updateUserId);
	
	/**
	 * 判断用户是否为某一集团的管理员
	 * @return
	 */
	boolean isAdminOfGroup();
	
}
