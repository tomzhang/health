package com.dachen.health.group.group.dao;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.department.entity.param.DepartmentDoctorParam;
import com.dachen.health.group.department.entity.po.Department;
import com.dachen.health.group.department.entity.vo.DepartmentMobileVO;
import com.dachen.health.group.department.entity.vo.DepartmentVO;
import com.dachen.health.group.group.entity.param.GroupDoctorApplyParam;
import com.dachen.health.group.group.entity.param.GroupDoctorParam;
import com.dachen.health.group.group.entity.param.GroupSearchParam;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.vo.GroupDoctorApplyVO;
import com.dachen.health.group.group.entity.vo.GroupDoctorInfoVO;
import com.dachen.health.group.group.entity.vo.GroupDoctorVO;
import com.dachen.health.group.group.entity.vo.GroupVO;
import com.dachen.health.group.group.entity.vo.InviteRelation;
import com.dachen.util.tree.ExtTreeNode;

/**
 * 
 * @author pijingwei
 * @date 2015/8/11
 */
public interface IGroupDoctorDao {

	/**
     * </p>添加医生</p>
     * @param GroupDoctorVO
     * @return GroupDoctor
     * @author pijingwei
     * @date 2015年8月11日
     */
	GroupDoctor save(GroupDoctor gdoc);
	
	/**
     * </p>更改医生在当前集团的状态</p>
     * @param GroupDoctorVO
     * @return GroupDoctor
     * @author pijingwei
     * @date 2015年8月11日
     */
	GroupDoctor update(GroupDoctor gdoc);
	
	/**
     * </p>删除</p>
     * @param ids
     * @return boolean
     * @author pijingwei
     * @date 2015年8月11日
     */
	boolean delete(String ...ids);
	
	/**
     * </p>获取所有当前在集团下的医生列表</p>
     * @param GroupDoctorParam
     * @return List<GroupDoctor>
     * @author pijingwei
     * @date 2015年8月11日
     */
	PageVO search(GroupDoctorParam param);

	/**
     * </p>根据Id获取的当前集团成员</p>
     * @param GroupDoctorParam
     * @return List<GroupDoctor>
     * @author pijingwei
     * @date 2015年8月11日
     */
	GroupDoctor getById(GroupDoctor gdoc);
	
	GroupDoctor getById(GroupDoctor gdoc, String status);
	
	
	/**
     * </p>根据医生Id和Status获取的当前集团成员</p>
     * @param GroupDoctorParam
     * @return List<GroupDoctor>
     * @author zhangyin
     * @date 2015年10月14日
     */
	GroupDoctor getByDoctorIdAndStatus(GroupDoctor gdoc);

	/**
     * </p>根据医生Id获取当前所有处于正常状态的集团列表</p>
     * @param param
     * @return List<GroupDoctor>
     * @author pijingwei
     * @date 2015年8月15日
     */
	List<GroupVO> findAllCompleteStatusByDoctorId(GroupDoctorParam param);
	
	
	/**
     * </p>根据公司Id获取当前所有处于正常状态的集团列表</p>
     * @param companyId
     * @return List<GroupDoctor>
     * @author pijingwei
     * @date 2015年8月15日
     */
	List<GroupVO> findAllCompleteStatusByCompanyId(String companyId);
	
	/**
     * </p>根据集团Id获取当前所有处于正常状态的集团列表</p>
     * @param companyId
     * @return List<GroupDoctor>
     * @author pijingwei
     * @date 2015年8月15日
     */
	List<GroupVO> findAllCompleteStatusByGroupId(String groupId);

	/**
     * </p>获取当前用户所在集团下的所在科室列表</p>
	 * @param gList 
	 * @param doctorId 
     * @return List<Department>
     * @author pijingwei
     * @date 2015年8月15日
     */
	List<Department> getDepartmentById(List<GroupVO> gList, Integer doctorId);

	/**
     * </p>根据当前科室Id的列表获取当前科室下所有的医生</p>
	 * @param dList 
	 * @param groupId 
     * @return List<GroupDoctorVO>
     * @author pijingwei
     * @date 2015年8月16日
     */
	List<GroupDoctorVO> findListByDepartmentIds(List<DepartmentVO> dList, String groupId);

	/**
     * </p>获取当前集团下所有科室列表</p>
	 * @param groupVO
	 * @param doctorId
     * @return List<DepartmentMobileVO>
     * @author pijingwei
     * @date 2015年8月16日
     */
	List<DepartmentMobileVO> getAllDepartmentListById(GroupVO groupVO);
	
	/**
     * </p>获取当前集团下所有科室列表</p>
	 * @param groupVO
	 * @param doctorId
     * @return List<DepartmentMobileVO>
     * @author pijingwei
     * @date 2015年8月16日
     */
	List<DepartmentMobileVO> getMyDepartmentListById(List<GroupVO> gList, Integer doctorId);

	/**
     * </p>根据集团Id，获取未分配的医生列表</p>
     * @param param
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月18日
     */
	PageVO findUndistributedListById(GroupDoctorParam gdoc);

	/**
     * </p>根据集团id,获取所有集团下的医生</p>
     * @param groupId
     * @return List<GroupDoctor>
     * @author pijingwei
     * @date 2015年8月18日
     */
	List<GroupDoctorVO> findGroupDoctorListByGroupId(String groupId);

	/**
     * </p>根据groupId查询当前集团下的医生count</p>
     * @param doctorId
     * @return List<InviteRelation>
     * @author pijingwei
     * @date 2015年8月24日
     */
	int getCountByGroupIds(String... ids);

	/**
     * </p>根据id获取所有数据</p>
     * @param doctorId
     * @return List<InviteRelation>
     * @author pijingwei
     * @date 2015年8月24日
     */
	List<GroupDoctorVO> findListByIds(String[] ids);

	/**
     * </p>根据医生Id个集团Id更新医生帐号状态</p>
     * @param doctorId
     * @return List<InviteRelation>
     * @author pijingwei
     * @date 2015年8月24日
     */
	boolean updateStatus(GroupDoctor gdoc);
	
	
	/**
	 * 更新全集团的所有医生免费值班时间为0
	 * @param groupId
	 * @return
	 */
	boolean updateTaskDuration(String groupId);

	/**
	 * </p>根据doctorId获取所有数据</p>
	 * @param groupId
	 * @param doctorIds
	 * @return
	 * @author fanp
	 * @date 2015年9月6日
	 */
	List<GroupDoctorVO> getByIds(String groupId,List<Integer> doctorIds);
	
	/**
	 * </p>通过状态查找集团医生</p>
	 * @param groupId
	 * @param statuses
	 * @return
	 * @author fanp
	 * @date 2015年9月23日
	 */
	List<Integer> getDoctorByStatus(String groupId,String[] statuses);
	
	/**
	 * </p>根据集团医生预约ID查询</p>
	 * @param groupId
	 * @param statuses
	 * @return
	 * @author xiepei
	 * @date 2015年9月23日
	 */
	PageVO findDoctorByGroup(GroupSearchParam param);
	
	PageVO findProDoctorByGroupId(GroupSearchParam param);
	
	
	/**
     * </p>根据集团id,获取所有集团下的医生</p>
     * @param groupId
     * @return List<GroupDoctor>
     * @author pijingwei
     * @date 2015年8月18日
     */
	List<GroupDoctor> findGroupDoctorsByGroupId(String groupId);

	/**
	 * 根据医生Id获取主医生集团
	 * @param userId
	 * @param status
	 * @return
	 */
	GroupDoctor findOneByUserId(Integer userId,String status);
	
	public List<GroupDoctorInfoVO> findUsersBydocId(List<Integer> docIds,PageVO pageVo);
	
	public List<GroupDoctorInfoVO> findUsersAndStateBydocId(List<Integer> docIds,Map<Integer,String> onLineStateMap);

	Object listGroupDoctorGroupByDept(GroupDoctorParam param);

	void clearDuration();

	List<GroupDoctorVO> getHasSetPriceGroupDoctorListByGroupId(String groupId);
	
	public PageVO searchDoctorByKeyWord(DepartmentDoctorParam param);
	
	boolean updateContactWay(GroupDoctorParam param);
	public boolean updateOutpatientPrice(String groupId,Integer outpatientPrice);
	
	
	List<GroupDoctor> getGroupDoctor(GroupDoctorParam param);
	
	void updateMainGroup(Integer doctorId, String groupId);
	
	List<GroupDoctor> getByDoctorIds(List<Integer> doctorIds,String type);
	
	List<GroupDoctor> getByDoctorId(Integer doctorId);
	
	List<GroupDoctor> getByDoctorId(Integer doctorId,String type);
	
	<T> List<T> getByDoctorId(Integer doctorId,String type, Class<T> clazz);
	
	
	
	/**
	 * 根据groupId，doctorId，status查询 医生与集团的关系 
	 * @param doctorId  医生id
	 * @param groupId  医生集团id
	 * @param status  医生在集团中的状态
	 * @return
	 *@author wangqiao
	 *@date 2015年12月22日
	 */
	public List<GroupDoctor> findGroupDoctor(Integer doctorId,String groupId,String status);
	
	/**
	 * 查询 某个医生所属的主集团
	 * @param doctorId
	 * @return
	 *@author wangqiao
	 *@date 2015年12月23日
	 */
	public List<GroupDoctor> findMainGroupByDoctorId(Integer doctorId);
	
	/**
	 * 通过id 获取 groupDoctor
	 * @param id
	 * @return
	 *@author wangqiao
	 *@date 2015年12月23日
	 */
	public 	GroupDoctor getById(String  id);
	
	/**
	 * 通过groupId读取 医生加入集团申请
	 * @param groupId
	 * @param status
	 * @return
	 *@author wangqiao
	 *@date 2015年12月23日
	 */
	public List<GroupDoctorApplyVO> getDoctorApplyByGroupId(GroupDoctorApplyParam param,String status);
	
	/**
	 * 通过groupId和doctorId 读取医生与集团的关系记录
	 * @param doctorId
	 * @param groupId
	 * @return
	 *@author wangqiao
	 *@date 2015年12月25日
	 */
	public GroupDoctor getByDoctorIdAndGroupId(Integer doctorId,String groupId);
	
	/**
	 * 通过id读取group信息
	 * @param groupId
	 * @return
	 *@author wangqiao
	 *@date 2015年12月29日
	 */
	public GroupVO findGroupById(String groupId);
	
	/**
	 * 查找 该医生最早加入的集团
	 * @param doctorId  医生id
	 * @return
	 *@author wangqiao
	 *@date 2016年1月7日
	 */
	public GroupDoctor findFirstJoinGroupByDoctorId(Integer doctorId);
	
	/**
	 * 通过医生id，读取医生与医院的关系信息
	 * @author wangqiao
	 * @date 2016年3月29日
	 * @param doctorId
	 * @return
	 */
	public GroupDoctor getGroupHospitalDoctorByDoctorId(Integer doctorId);

	GroupUser findGroupDoctorByGroupIdAndDoctorId(String groupId, Integer inviteUserId);

	GroupUser findByRootAndDoctorId(Integer applyUserId);

	void updateGroupUser(GroupUser transferGu);

//	GroupUser findGroupUser(Integer confirmUserId, String groupId, String status);

	List<Integer> findAllDoctorIdsInGroupIds(List<String> groupIds);
	
//	List<GroupUser> getGroupUserByGroupId(String groupId);

	List<Integer> getNotInCurrentPackDoctorIds(String groupId, String consultationPackId);

	List<GroupDoctor> findDoctorsByGroupId(String groupId);

	GroupUser findGroupRootAdmin(String groupId);

	/**
	 * 根据集团id删除
	 * @param objectId
	 */
	void removeByObjectId(String objectId);
	
	/**
	 * 根据集团id、医生id 类型 删除集团医生
	 * @param doctorId
	 * @param groupId
	 * @param type
	 */
	void delectGroupDoctor(Integer doctorId, String groupId,String type);

	void updateCreateDate(String groupId, Integer doctorId);
	
	/**
     * </p>根据医生Id和集团获取我邀请的人的列表</p>
     * @param doctorId
     * @param groupId
     * @return List<InviteRelation>
     * @author pijingwei
     * @date 2015年8月24日
     */
	PageVO findInviteListById(Integer doctorId, String groupId);
	
	/**
     * </p>根据医生Id和集团获取我邀请的人的列表（邀请人状态为正常的）</p>
     * @param doctorId
     * @param groupId
     * @return List<InviteRelation>
     * @author pijingwei
     * @date 2015年8月24日
     */
	PageVO findInviteListByIdWithNormalStatus(Integer doctorId, String groupId);
	
	/**
     * </p>根据医生Id和集团Id获取谁邀请我进来的信息</p>
     * @param doctorId
     * @param groupId
     * @return List<InviteRelation>
     * @author pijingwei
     * @date 2015年8月24日
     */
	InviteRelation getInviteInfo(Integer doctorId, String groupId);
	
	/**
     * </p>集团创建者邀请的人的树</p>
     * @param doctorId
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年8月28日
     */
    List<ExtTreeNode> getMyInviteRelaions(Integer doctorId, String groupId);
    
	/**
	 * 根据多个groupId和type，查询 gruopDoctor 数量
	 * @author wangqiao
	 * @date 2016年4月27日
	 * @param groupIdList
	 * @param type
	 * @return
	 */
	public int getGroupDoctorCountByGroupIdsAndType(List<String> groupIdList , String type);
	
	/**
	 * 根据多个groupId和type，批量更新groupDoctor的type类型
	 * @author wangqiao
	 * @date 2016年4月27日
	 * @param groupIdList
	 * @param type
	 * @param newType
	 */
	public void updateGroupDoctorTypeByGroupIdsAndType(List<String> groupIdList , String type,String newType);
	
	
	public List<User> getDoctorByDiseaseId(GroupSearchParam param);
	
	/**
	 * 获取一个集团在线的医生
	 * 
	 * @param groupId
	 * @return
	 */
	List<GroupDoctor> getOnlineDoctorsByGroup(String groupId);

	List<String> findAllGroupIdByDoctorId(Integer doctorId);
	
	/**
	 * 根据用户id，集团id和状态来获取用户
	 * @param userId
	 * @param groupId
	 * @param status
	 * @return
	 */
	GroupDoctor findOneByUserIdAndGroupId(Integer userId, String groupId, String status);

	/**
	 *  集团下成员数
	 * @param groupId 集团ID
	 * @return
	 * @author tan.yf
	 * @date 2016年6月2日
	 */
	public long findDoctorsCountByGroupId(String groupId);
	
	/**
	 * 集团下成员列表
	 * @param groupId 集团ID
	 * @return List<GroupDoctor>
	 * @author tan.yf
	 * @date 2016年6月2日
	 */
	public List<GroupDoctor> findDoctorsListByGroupId(String groupId);

	List<Integer> filterByGroupId(List<Integer> doctorList, String groupId);
	
	/**
	 * 获取第一次加入集团的ID过滤掉 屏蔽的集团
	 * @param doctorId
	 * @return 集团ID
	 * @author tan.yf
	 * @date 2016年6月6日
	 */
	public String findFirstJoinGroupIdByDoctorId(Integer doctorId) ;
	
	/**
	 * 更新集团医生
	 * @param groupDoctor
	 * @author tan.yf
	 * @date 2016年6月6日
	 */
	void updateGroupDoctor(GroupDoctor groupDoctor);

	List<GroupDoctor> findDoctorsByGroupIdAllStatus(String groupId);
	
	/**
	 * 根据groupId来获取集团正常医生的Id
	 * @param groupId
	 * @return
	 */
	List<Integer> getDoctorIdByGroupId(String groupId);
	
	List<String> getGroupNameByDoctorId(Integer doctorId);
	
	Map<Integer, List<String>> getGroupNameByDoctorIds(List<Integer> doctorId);
	
	/**
	 * 获取集团下面并且设置为主集团的医生的id
	 * @author liming
	 */
	List<String> findGroupMainList(String groupId);
	
	List<String> getActiveGroupIdListByDoctor(Integer doctorId);

    List<GroupDoctor> getActiveListByDoctor(Integer doctorId);

    GroupDoctor findByDoctorIdAndType(Integer doctorId, String index);
}
