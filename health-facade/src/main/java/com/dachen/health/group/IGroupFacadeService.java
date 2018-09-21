package com.dachen.health.group;

import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.group.entity.param.GroupParam;
import com.dachen.health.group.group.entity.po.GroupApply;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.sdk.exception.HttpApiException;

public interface IGroupFacadeService {


	/**
	 * 是否接受 加入集团/管理员邀请
	 * @param id    邀请id
	 * @param type   类型：1 = 公司管理员，2 = 集团管理员，3 = 医生集团成员
	 * @param status 状态：C，同意，N，拒绝
	 * @param operationUserId 操作人id
	 * @return
	 *@author wangqiao
	 *@date 2016年1月7日
	 */
	String confirmByInvite(String id,Integer type,String status,Integer operationUserId) throws HttpApiException;
	
	/**
	 * 同意集团邀请并设置为主集团
	 * @param groupId 集团id
	 * @return
	 */
	void confirmByInviteAndSetMain(String groupId) throws HttpApiException;

	/**
	 * </p>
	 * 将医生直接加入到集团 
	 * </p>
	 * 
	 * @param GroupDoctorVO
	 * @return Map<String, Object>
	 * @author pijingwei
	 * @date 2015年8月11日
	 */
	Map<String, Object> saveCompleteGroupDoctor(String  groupId,Integer doctorId, String telephone, Integer inviteId) throws HttpApiException;

	/**
	 * </p>
	 * 将医生直接加入到集团 
	 * </p>
	 * 
	 * @param GroupDoctorVO
	 * @return Map<String, Object>
	 * @author fuyongde
	 * @date 2016年6月2日
	 */
	Map<String, Object> saveCompleteGroupDoctorByRegister(String  groupId,Integer doctorId, String telephone, Integer inviteId) throws HttpApiException;
	
	/**
	 * 更新 医生与集团的关系信息
	 * 
	 * @param gdoc
	 * @author wangqiao
	 * @date 2015年12月26日
	 */
	public void updateGroupDoctor(GroupDoctor gdoc) throws HttpApiException;
	
	/**
	 * 更新 医生与集团的关系状态
	 * @param groupId
	 * @param doctorId
	 * @param status
	 *@author wangqiao
	 *@date 2016年1月7日
	 */
	public void updateGroupDoctorStatus(String groupId,Integer doctorId,String status) throws HttpApiException;

	/**
	 * 医生集团删除医生
	 * @param ids
	 * @return
	 *@author wangqiao
	 *@date 2015年12月26日
	 */
//	public boolean deleteGroupDoctor(String... ids);
	
	
	/**
	 * 审核 医生申请加入医生集团
	 * @param gdoc
	 * @param approve
	 * @return
	 *@author wangqiao
	 *@date 2015年12月26日
	 */
	public Map<String, Object> confirmByDoctorApply(GroupDoctor gdoc,boolean approve) throws HttpApiException;
	
	/**
	 * 返回值班信息
	 * @param userId
	 * @return
	 */
	Map<String, Object> getDutyInfo(Integer userId);
	
	
	/**
	 * 在创建集团之后，更新医生创建集团之前创建的文章
	 * @param groupId 集团id 
	 * @param creatorId 创建id
	 * @return
	 */
	public void  updateDoctorDiseaseBeforeCreateGroup(String  groupId ,Integer creatorId);
	
	/**
	 * 申请创建集团
	 * @author wangqiao
	 * @date 2016年3月5日
	 * @param groupApply
	 * @return
	 */
	public GroupApply groupApply(GroupApply groupApply) throws HttpApiException;

	/**
	 * 创建医院
	 * author：姜宏杰
	 * @param hospitalId
	 * @return
	 */
	public Object createHospital(String hospitalId,Integer doctorId,Integer operationUserId) throws HttpApiException;
	
	/**
	 * 激活医院列表（可根据【集团】医院名称进行模糊查询）
	 * @param group
	 * @return
	 */
	PageVO groupHospitalList(GroupParam group);
	
	/**
	 * 注册用户并加入医院
	 * @author wangqiao
	 * @date 2016年3月29日
	 * @param inviteDoctorId  邀请人id
	 * @param groupHospitalId 激活医院id
	 * @param telephone 注册手机号
	 * @param password 账号密码
	 * @param name  姓名
	 * @param deptId 科室id
	 * @param departments 科室名称
	 * @param title 职称
	 * @return
	 */
	public String confirmByRegisterJoinGroupHospital(Integer inviteDoctorId,String groupHospitalId,
			String telephone,String password,String name,String deptId,String departments,String title) throws HttpApiException;
	
	/**
	 * 确认是否加入医院
	 * @author wangqiao
	 * @date 2016年3月29日
	 * @param id    邀请id
	 * @param type   类型：2 = 医院管理员，3 = 医院医生
	 * @param status 状态：C，同意，N，拒绝
	 * @param operationUserId 操作人id
	 * @return
	 */
	public  String confirmByJoinGroupHospital(String id,Integer type, String status,Integer operationUserId) throws HttpApiException;
	
	/**
	 * 医生离职其它医院
	 * @author wangqiao
	 * @date 2016年3月30日
	 * @param doctorId
	 */
	public void doctorLeaveOtherHospital(Integer doctorId) throws HttpApiException;
	
	
	/**
	 * 根据集团id查询创建的医院详情
	 * @param id
	 * @return
	 */
	public Map<String, Object> getDetailByGroupId(String id);
	
	/**
	 * 更新并替换医院集团的超级管理员
	 * @param hospitalId
	 * @param doctorId(新的集团管理员)
	 * @param operationUserId  操作人id
	 * @return
	 */
	String updateHospitalRoot(String hospitalId, Integer newdoctorId,Integer operationUserId) throws HttpApiException;

	/**
	 * 某医生是否在某集团
	 * @param doctorId
	 * @param groupId
	 * @return
	 */
	boolean isInGroup(Integer doctorId, String groupId);

	/**
	 * 加入一个集团
	 * @param groupId
	 * @param userId
	 * @param telephone
	 * @param currentUserId
	 * @param platform
	 * @return
	 */
	Map<String, Object> joinGroup(String groupId, Integer userId, String telephone, Integer currentUserId, Integer platform) throws HttpApiException;
	
	/**
	 * 根据二维码加入一个集团
	 * @param groupId
	 * @param userId
	 * @param telephone
	 * @param inviterId
	 * @return
	 */
	Map<String, Object> joinGroupByQRCode(String groupId, Integer userId, String telephone, Integer inviterId, String applyMsg) throws HttpApiException;
	
	/**
	 * 运营平台将医生移除集团
	 * @param groupId
	 * @param doctorIds
	 */
	void removeDoctors(String groupId, Integer[] doctorIds) throws HttpApiException;
	
	/**
	 * 运营平台将医生批量加入集团
	 * @param groupId
	 * @param doctorIds
	 */
	void inviterJoinGroup(String groupId, Integer[] doctorIds) throws HttpApiException;
}
