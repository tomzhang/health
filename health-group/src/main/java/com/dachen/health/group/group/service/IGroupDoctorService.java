package com.dachen.health.group.group.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.api.client.group.entity.CGroup;
import com.dachen.health.base.entity.vo.DepartmentVO;
import com.dachen.health.group.department.entity.param.DepartmentDoctorParam;
import com.dachen.health.group.entity.param.GroupDoctorsParam;
import com.dachen.health.group.group.entity.param.GroupDoctorApplyParam;
import com.dachen.health.group.group.entity.param.GroupDoctorParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.vo.DoctorInfoDetailsVO;
import com.dachen.health.group.group.entity.vo.GroupDoctorApplyVO;
import com.dachen.health.group.group.entity.vo.GroupDoctorVO;
import com.dachen.health.group.group.entity.vo.GroupHospitalDoctorVO;
import com.dachen.health.group.group.entity.vo.GroupVO;
import com.dachen.health.group.group.entity.vo.OutpatientVO;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.tree.ExtTreeNode;

/**
 * 
 * @author pijingwei
 * @date 2015/8/11
 */
public interface IGroupDoctorService {

	/**
     * </p>添加医生</p>
     * @param GroupDoctorVO
     * @return Map<String, Object>
     * @author pijingwei
     * @date 2015年8月11日
     */
	Map<String, Object> saveGroupDoctor(GroupDoctor gdoc, Integer againInvite, String telephone) throws HttpApiException;
	
	public PageVO searchDoctorByKeyWord(DepartmentDoctorParam param);
	
	/**
	 * 批量邀请加入
	 * @param groupId
	 * @param ignore
	 * @param telephones
	 * @return
	 */
	Map<String, Object> saveBatchGroupDoctor(String groupId, String ignore, String ...telephonesOrdoctorNums) throws HttpApiException;
	
	
	/**
	 * </p>申请加入医生集团</p>
	 * @param gdoc
	 * @return
	 * @author fanp
	 * @date 2015年9月16日
	 */
//	Map<String, Object> saveByApply(GroupDoctor gdoc);
	
	
	
	/**
     * </p>更改医生在当前集团的状态</p>
     * @param GroupDoctorVO
     * @return 
     * @author pijingwei
     * @date 2015年8月11日
     */
//	void updateGroupDoctor(GroupDoctor gdoc);
	
	/**
     * </p>删除</p>
     * @param ids
     * @return boolean
     * @author pijingwei
     * @date 2015年8月11日
     */
	boolean deleteGroupDoctor(String ...ids);
	
//	/**
//     * </p>根据医生Id获取医生所在的集团及详细信息</p>
//     * @param ids
//     * @return boolean
//     * @author pijingwei
//     * @date 2015年8月11日
//     */
//	 Map<String, Object> getGroupDoctorByDoctorId(GroupDoctor gdoc);
	
	/**
     * </p>获取所有当前在集团下的医生列表</p>
     * @param GroupDoctorParam
     * @return List<GroupDoctor>
     * @author pijingwei
     * @date 2015年8月11日
     */
	PageVO searchGroupDoctor(GroupDoctorParam param);
	
	/**
     * </p>根据医生Id获取获取集团及科室</p>
     * @param param
     * @return Map<String, List<? extends Object>>
     * @author pijingwei
     * @date 2015年8月7日
     */
	Map<String, List<? extends Object>> getGroupAndDepartmentById(GroupDoctorParam param);

	/**
     * </p>根集团Id获取集团下的科室和科室的医生</p>
     * @param groupId
     * @return Map<String, List<? extends Object>>
     * @author pijingwei
     * @date 2015年8月7日
     */
	Map<String, List<? extends Object>> getDepartmentAndDoctorById(String groupId);

	/**
     * </p>根据医生Id获取所有集团列表及列表下所有科室列表及所有科室列表下的医生列表</p>
     * @param param
     * @return Map<String, List<? extends Object>>
     * @author pijingwei
     * @date 2015年8月17日
     */
	Map<String, Object> getGroupListAndAllSubListById(GroupDoctorParam gdoc);

	/**
     * </p>根据集团Id，获取未分配的医生列表</p>
     * @param param
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月18日
     */
	PageVO getUndistributedDoctorByGroupId(GroupDoctorParam gdoc);

	/**
     * </p>根据集团id,获取所有集团下的医生</p>
     * @param groupId
     * @return List<GroupDoctor>
     * @author pijingwei
     * @date 2015年8月20日
     */
	List<GroupDoctorVO> getGroupDoctorListByGroupId(String groupId);
	
	/**
     * </p>根据医生Id获取我邀请的人的列表</p>
     * @param doctorId
     * @param groupId
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月24日
     */
	PageVO getMyInviteRelationListByDoctorId(Integer doctorId, String groupId);

	/**
     * </p>查找登录用户所属集团管理员所有的邀请</p>
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年8月28日
     */
    List<ExtTreeNode> getInviteRelationTree(Integer doctorId,String groupId);
	
	/**
     * </p>根据医生Id和集团Id删除当前医生相关的数据并更新医生状态为S：离职状态</p>
     * @param doctorId
     * @return List<InviteRelation>
     * @author pijingwei
     * @date 2015年8月24日
     */
	void dimissionByCorrelation(GroupDoctor gdoc) throws HttpApiException;
	
	/**
     * </p>是否有权限邀请成员</p>
     * @param cuser
     * @return Map<String, Object>
     * @author pijingwei
     * @date 2015年8月29日
	 */
	Map<String, Object> HasPermissionByInvite(GroupDoctor cuser);

	/**
     * </p>发送短信邀请</p>
     * @param doctorId
     * @param groupId
     * @return Map<String, Object>
     * @author pijingwei
     * @date 2015年8月24日
     */
	Map<String, Object> sendNoteInviteBydoctorId(GroupDoctor gdoc) throws HttpApiException;
	
	/**
	 * 设置某个医生集团在线时间设置
	 * @param gdoc
	 */
	void setTaskTimeLong(GroupDoctor gdoc);
	
	/**
	 * 更新所有医生的值班时间为0
	 * @param gdocs
	 */
	void updateTaskDuration(String groupId);
	
	/**
	 * 上线（开始接单）
	 * @param gdoc
	 */
	boolean doctorOnline(GroupDoctor gdoc) throws HttpApiException;
	
	/**
	 * 下线（结束接单）
	 * @param gdoc
	 */
	public void doctorOffline(GroupDoctor gdoc, EventEnum event) throws HttpApiException;
	
	/**
	 * 设置某个医生集团的门诊价格
	 * @param gdoc
	 */
	public void setOutpatientPrice(String groupId,Integer outpatientPrice);
	
	GroupDoctor findOneByUserId(Integer userId);
	
	public GroupDoctor findOneByUserIdAndStatus(Integer userId,String status);
	
	void autoResetDuration();
	
	Object listGroupDoctorGroupByDept(GroupDoctorParam param);

	List<GroupDoctorVO> getHasSetPriceGroupDoctorListByGroupId(String groupId);

	GroupDoctor getOne(String groupId, Integer doctorId);
	
	GroupDoctor getOne(GroupDoctor gdoc);
	
	boolean updateContactWay(GroupDoctorParam param);
	
	public DoctorInfoDetailsVO getDoctorInfoDetails(String groupId, int doctorId);
	
	void setMainGroup(GroupDoctorParam param);
	
	List<GroupDoctorVO> getByDoctorId(Integer doctorId);
    List<GroupDoctor> getByDoctorId(Integer doctorId,String type);
	
	List<GroupVO> getGroups(Integer doctorId);
	
	List<GroupVO> getMyGroups(Integer doctorId);
	
	
	List<GroupVO> getGroupsOnDuty(Integer doctorId);
	
	/**
	 * 保存 医生申请加入医生集团
	 * @param GroupDoctor
	 * @return
	 *@author wangqiao
	 *@date 2015年12月21日
	 */
	public Map<String, Object> saveByDoctorApply(String groupId,String applyMsg,Integer doctorId) throws HttpApiException;
	

	public OutpatientVO getOutpatientInfo(Integer doctorId);
	
	/**
	 * 查询 加入医生集团申请
	 * @param param
	 * @return
	 *@author wangqiao
	 *@date 2015年12月21日
	 */
	public List<GroupDoctorApplyVO> getDoctorApplyByGroupId(GroupDoctorApplyParam param);
	
	/**
	 * 初始化主集团（如果当前医生没有主集团，则选择加入时间早的集团作为主集团）
	 * @param doctorId
	 *@author wangqiao
	 *@date 2016年1月7日
	 */
	public void initMainGroup(Integer doctorId);
	
	/**
	 * 根据集团id，医生id，和状态 查找医生与集团的关系记录
	 * @param doctorId 医生id
	 * @param groupId  集团id
	 * @param status  医生在集团中的状态
	 * @return
	 *@author wangqiao
	 *@date 2016年1月18日
	 */
	public List<GroupDoctor> findGroupDoctor (Integer doctorId,String groupId,String status);
	
	/**
	 * 通过申请id查询，医生加入集团申请的相关信息
	 * @param id  申请id
	 * @return
	 *@author wangqiao
	 *@date 2016年1月19日
	 */
	public GroupDoctorApplyVO getDoctorApplyByApplyId(String id);
	
	/**
	 * 根据医生id查询该医生所属的集团（主集团有限排序，加入时间早的优先排序）
	 * @param doctorId
	 * @return
	 *@author wangqiao
	 *@date 2016年2月18日
	 */
	public List<String> getGroupListByDoctorId(Integer doctorId);
	
	/**
	 * 查询 多个医生在 某集团中 的相关信息
	 * @param groupId
	 * @param doctorIds
	 * @return
	 *@author wangqiao
	 *@date 2016年2月18日
	 */
	public List<GroupDoctorVO> getByIds(String groupId, List<Integer> doctorIds);
	
	/**
	 * 通过医生id，读取医生相关的医院以及医生在医院中的相关信息
	 * @author wangqiao
	 * @date 2016年3月29日
	 * @param doctorId
	 * @return
	 */
	public GroupHospitalDoctorVO getGroupHospitalDoctorByDoctorId(Integer doctorId);
	
	/**
	 * 通过手机号(或邀请id)，查询医生相关的医院信息
	 * @author wangqiao
	 * @date 2016年3月29日
	 * @param telephone
	 * @param inviteId
	 * @return
	 */
	public GroupHospitalDoctorVO getGroupHospitalDoctorByTelephone(String telephone,String inviteId);

	void activeGroupByMemberNum(String groupId) throws HttpApiException;
	
	/**
	 * 根据集团id、医生id 类型 删除集团医生
	 * 
	 * @param id(GroupDoctor的id)
	 */
	void leaveHospitalRoot(String id,Integer userId);
	
	/**
	 * 根据医生id和集团id，查询医生与集团的关系记录
	 * @author wangqiao
	 * @date 2016年4月20日
	 * @param doctorId
	 * @param groupId
	 * @return
	 */
	public GroupDoctor getGroupDoctor(Integer doctorId,String groupId);
	
	/**
	 * 修复 groupDoctor中的type错误数据
	 * @author wangqiao
	 * @date 2016年4月27日
	 * @return
	 */
	public int repairTypeForHospitalGroup();

	void activeAllUserGroup(Integer userId) throws HttpApiException;
	
	/**
	 * 根据集团Id获取对应医生ID
	 * @return
	 */
	List<Integer> getDocIdsByGroupID(String groupId,String... status);
	
	/**
	 *  集团下成员数
	 * @param groupId 集团ID
	 * @return
	 * @author tan.yf
	 * @date 2016年6月2日
	 */
	public long findDoctorsCountByGroupId(String groupId);
	
	/**
	 *  集团下成员列表
	 * @param groupId 集团ID
	 * @return
	 * @author tan.yf
	 * @date 2016年6月2日
	 */
	public PageVO findDoctorsListByGroupDoctorsParam(GroupDoctorsParam param);
	
	/**
	 * 更新集团医生
	 * @param groupDoctor
	 * @author tan.yf
	 * @date 2016年6月6日
	 */
	public void updateGroupDoctor(GroupDoctor groupDoctor);
	
	/**
	 * 是否允许某医生加入到某集团
	 * @param doctorId 医生id
	 * @param groupId  集团id
	 * @author fuyongde
	 * @date 2016-6-6
	 * @return true可以申请，false不能申请
	 */
	boolean allowdeJoinGroup(Integer doctorId, String groupId);
	
	Map<String, Object> isInBdjl();
	
	/**
	 * 获取医生的集团
	 * @param doc
	 * @return
	 * @author tan.yf
	 * @date 2016年6月15日
	 */
	public List<GroupVO> getMyNormalGroups(Integer doctorId) ;

	/**
	 * 获取集团通讯录
	 * @param groupId
	 * @param areaCode
	 * @param deptId
	 */
	PageVO getGroupAddrBook(String groupId, Integer areaCode, String deptId, Integer pageIndex, Integer pageSize);
	
	List<String> getGroupIsMain(Integer doctorId);
	List<String> getGroup(Integer doctorId, String type);

	
	/**
	 * 根据医生ids查询该医生所属的集团
	 * @param doctorId
	 * @return
	 *@author liwei
	 *@date 2016年8月6日
	 */
	public List<String> getGroupListByDoctorIds(List<Integer> doctorId);
	
	List<String> getActiveGroupIdListByDoctor(Integer doctorId);

	List<Group> getActiveGroupListByDoctor(Integer doctorId);

    List<CGroup> getActiveCGroupListByDoctor(Integer doctorId);

	void sendNotifyForApplyJoin(Integer doctorId,String groupName,String groupDoctorId,String groupId) throws HttpApiException;

    List<Integer> getDoctorByStatus(String groupId,String[] statuses);

    List<Integer> getNotInCurrentPackDoctorIds(String groupId, String consultationPackId);

    GroupDoctor getById(GroupDoctor gdoc);
}
