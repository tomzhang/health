package com.dachen.health.controller.group.group;

import java.util.HashMap;
import java.util.Map;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.commons.example.UserExample;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.group.IGroupFacadeService;
import com.dachen.health.group.group.entity.param.GroupParam;
import com.dachen.health.group.group.entity.vo.GroupHospitalDoctorVO;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.util.ReqUtil;

 
@RestController
@RequestMapping("/group/hospital")
public class GroupHospitalController extends AbstractController{
	
	

	@Autowired
	private IGroupFacadeService groupFacadeService;
	
	@Autowired
	private IGroupService groupService;
	
	@Autowired
	private IGroupDoctorService groupDoctorService;
	
	@Autowired
	private UserManager userManager;
	
	
	/**
	 * @api {get} /group/hospital/groupHospitalList 激活医院列表
	 * @apiVersion 1.0.0
	 * @apiName groupHospitalList
	 * @apiGroup 医院平台
	 * @apiDescription 激活医院列表
	 * @apiParam {String} access_token token
	 * @apiParam {String} keyWord 关键字
	 * @apiParam {Integer} pageIndex 第几页 默认从0 页开始
	 * @apiParam {Integer} pageSize 每页的大小
	 * 
	 * @apiSuccess {Integer} pageCount 总页数
	 * @apiSuccess {Integer} pageIndex 页数
	 * @apiSuccess {Integer} pageSize 每页的大小
	 * @apiSuccess {Integer} total 总记录数
	 * @apiSuccess {Object[]} pageData 数据集合
	 * @apiAuthor 姜宏杰
	 * @date 2016年3月28日13:38:54
	 */ 
	@RequestMapping("/groupHospitalList")
	public JSONMessage groupHospitalList(String keyWord,Integer pageIndex,Integer pageSize){
		GroupParam param = new GroupParam();
		param.setKeyWord(keyWord);
		param.setPageIndex(pageIndex);
		param.setPageSize(pageSize);
		return JSONMessage.success("success",groupFacadeService.groupHospitalList(param));
	}
	
	/**
	 * @api {get} /group/hospital/createGroupHospital 创建医院
	 * @apiVersion 1.0.0
	 * @apiName createGroupHospital
	 * @apiGroup 医院平台
	 * @apiDescription 创建医院
	 * @apiParam {String} access_token token
	 * @apiParam {String} hospitalId 医院id
	 * @apiParam {Integer} doctorId  医生id
	 * 
	 * @apiSuccess {String} ResultCode
	 * @apiAuthor 姜宏杰
	 * @date 2016年3月28日13:38:54
	 */ 
	@RequestMapping("/createGroupHospital")
	public JSONMessage createGroupHospital(String hospitalId,Integer doctorId) throws HttpApiException {
		//操作人id
		Integer operationUserId = ReqUtil.instance.getUserId();
		
		return JSONMessage.success("success",groupFacadeService.createHospital(hospitalId,doctorId,operationUserId));
	}
	
	/**
	 * @api {get} /group/hospital/getGroupHospitalByDoctorId 读取我的医院信息
	 * @apiVersion 1.0.0
	 * @apiName getGroupHospitalByDoctorId
	 * @apiGroup 医院平台
	 * @apiDescription 通过用户id，读取该用户相关的医院信息
	 * 
	 * @apiParam {String} access_token     token
	 * @apiParam {Integer} doctorId       医生id
	 * 
	 * @apiSuccess {String} groupHospitalId   激活医院id
	 * @apiSuccess {String} hospitalName       医院名称
	 * @apiSuccess {String} departmentId          医生所在科室id
	 * @apiSuccess {String} departmentName          医生所在科室名称
	 * @apiSuccess {String} title                      医生职称
	 * @apiSuccess {boolean} memberInvite                      是否允许成员邀请
	 *   
	 * 
	 * @apiAuthor wangqiao
	 * @date 2016年3月28日
	 */ 
	@RequestMapping("/getGroupHospitalByDoctorId")
	public JSONMessage getGroupHospitalByDoctorId(Integer doctorId) {
		
		GroupHospitalDoctorVO  groupHospitalDoctor = groupDoctorService.getGroupHospitalDoctorByDoctorId(doctorId);
		
		return JSONMessage.success("success",groupHospitalDoctor);
	}
	 
	
	/**
     * @api {post} /group/hospital/confirmByJoin 已有账号确认加入医院
     * @apiVersion 1.0.0
     * @apiName confirmByJoin
     * @apiGroup 医院平台
     * @apiDescription 已有账号确认加入医院（管理员，医生确认加入都可以）
     *
     * @apiParam  {String}   	id       			            邀请记录Id
     * 
     * @apiParam  {Integer}   	type				类型：2 = 医院管理员，3 = 医院成员
     * @apiParam  {String}   	status				状态：C，同意，N，拒绝
     *
     * @apiSuccess {Number} 	resultCode    		返回状态吗
     *
     * @apiAuthor  wangqiao
     * @date 2016年3月29日
     * H5页码调用 不需要校验access_token
	 */
	@RequestMapping("/confirmByJoin")
	public JSONMessage confirmByJoin(String id  , Integer type,String status) throws HttpApiException {
		
		Integer operationUserId = ReqUtil.instance.getUserId();
		
		String retString = groupFacadeService.confirmByJoinGroupHospital(  id  ,  type, status,operationUserId);
		
		return JSONMessage.success("success",retString);
	}

	
	
	/**
     * @api {post} /group/hospital/completeJoinHospital  已有账号直接加入医院
     * @apiVersion 1.0.0
     * @apiName completeJoinHospital
     * @apiGroup 医院平台
     * @apiDescription 通过短信转发的已有账号直接加入医院
     *
     * @apiParam {Integer}  inviteDoctorId       邀请医生Id
     * @apiParam {Integer} doctorId                医生id
     * @apiParam {String} groupHospitalId       确认加入的医院id  telephone
     * @apiParam {String} telephone                医生手机号
     * @apiParam  {String}   	status				状态：C，同意，N，拒绝
     *
     * @apiSuccess {Number} 	resultCode    		返回状态吗
     *
     * @apiAuthor  wangqiao
     * @date 2016年3月30日
     * H5页码调用 不需要校验access_token
	 */
	@RequestMapping("/completeJoinHospital")
	public JSONMessage completeJoinHospital(String groupHospitalId  , Integer inviteDoctorId,Integer doctorId , String telephone, String status) throws HttpApiException {
		
		if("C".equals(status)){
			//先离职其它医院
			groupFacadeService.doctorLeaveOtherHospital(doctorId);
			//再加入新医院
			Map<String, Object> retMap = groupFacadeService.saveCompleteGroupDoctor(   groupHospitalId, doctorId,  telephone,  inviteDoctorId);
			
			return JSONMessage.success("success",retMap);
		}else{
			//拒绝加入时，暂时不更新邀请状态
			
		}
		
		
		return  JSONMessage.success("success");

	}
	
	/**
     * @api {post} /group/hospital/confirmByRegisterJoin 新注册账号确认加入医院
     * @apiVersion 1.0.0
     * @apiName confirmByRegisterJoin
     * @apiGroup 医院平台
     * @apiDescription 新注册账号确认加入医院
     *
     * 
     * @apiParam  {Integer}   	inviteDoctorId       		邀请人Id
     * @apiParam  {String}   	groupHospitalId       加入的医院id
     * 
     * @apiParam  {String}   	telephone       被邀请的医生手机号
     * @apiParam  {String}   	password                  账号密码
     * @apiParam  {String}   	name                  医生姓名
     * @apiParam  {String}   	departments       科室名称
     * @apiParam  {String}   	deptId               科室id
     * @apiParam  {String}   	title                           职称
     *
     * @apiSuccess {Number} 	resultCode    		返回状态吗
     *
     * @apiAuthor  wangqiao
     * @date 2016年3月29日
     * H5页码调用 不需要校验access_token
	 */
	@RequestMapping("/confirmByRegisterJoin")
	public JSONMessage confirmByRegisterJoin(  Integer inviteDoctorId,String groupHospitalId,String telephone,String password,
			String name,String departments,String deptId,String title) throws HttpApiException {
		
		String retString = groupFacadeService.confirmByRegisterJoinGroupHospital( inviteDoctorId, groupHospitalId, telephone, password,
				 name,deptId, departments, title);

		return JSONMessage.success("success",retString);
	}
	
	/**
     * @api {post} /group/hospital/getGroupHospitalByTelephone 根据手机号读取医生加入医院信息
     * @apiVersion 1.0.0
     * @apiName getGroupHospitalByTelephone
     * @apiGroup 医院平台
     * @apiDescription 根据手机号读取医生加入医院信息
     *
     * 
     * @apiParam  {String}   	telephone      	  用户手机号（手机号和邀请id不能同时为空）
     * @apiParam  {String}   	inviteId      	      邀请id(优先使用邀请id进行匹配)
     *
     * @apiSuccess {Number} 	resultCode    		返回状态吗
	 * @apiSuccess {Integer} doctorId                医生id
	 * @apiSuccess {String} groupHospitalId     医生已经加入的医院id
	 * @apiSuccess {String} hospitalName         医生已经加入的医院名称
	 * @apiSuccess {String} hospitalStatus         unregistered=未注册，noUserName=已注册没有用户名，register=已注册没加入医院，hospitalDoctor=已加入医院，root=已加入医院并且是超级管理员，onlyManage=已加入医院并且是唯一管理员
	 * @apiSuccess {String} inviteGroupHospitalId                 邀请加入的医院id
	 * @apiSuccess {String} telephone                 医生手机号
     *
     * @apiAuthor  wangqiao
     * @date 2016年3月29日
     * H5页码调用 不需要校验access_token
	 */
	@RequestMapping("/getGroupHospitalByTelephone")
	public JSONMessage getGroupHospitalByTelephone(String telephone,String inviteId ) {
		
		GroupHospitalDoctorVO  groupHospitalDoctor = groupDoctorService.getGroupHospitalDoctorByTelephone(telephone,inviteId);
		
		return JSONMessage.success("success",groupHospitalDoctor);
	}
	
	/**
     * @api {post} /group/hospital/updateUnauthUserMessage 补充未注册账号的个人信息
     * @apiVersion 1.0.0
     * @apiName updateUnauthUserMessage
     * @apiGroup 医院平台
     * @apiDescription 医生加入医院时，补充未注册账号的个人信息
     *
     * 
     * @apiParam  {Integer}   	doctorId      	  医生id
     * 
     * @apiParam  {String}   	name      	  医生名称
     * @apiParam  {String}   	deptId      	  科室id
     * @apiParam  {String}   	departments      	  科室名称
     * @apiParam  {String}   	title      	  职称
     * 
     *
     * @apiSuccess {Number} 	resultCode    		返回状态码
     *
     * @apiAuthor  wangqiao
     * @date 2016年3月29日
     * H5页码调用 不需要校验access_token
	 */
	@RequestMapping("/updateUnauthUserMessage")
	public JSONMessage updateUnauthUserMessage(Integer doctorId,String name,String deptId, String departments,String title ) throws HttpApiException {
		//参数校验
		
		UserExample userExample = new  UserExample();
		Doctor doctor = new Doctor();
		doctor.setDepartments(departments);
		doctor.setDeptId(deptId);
		doctor.setTitle(title);
		userExample.setDoctor(doctor);
		userExample.setName(name);
		userManager.updateUser(doctorId,userExample);
		
		
		return JSONMessage.success("success",null);
	}
	
	/**
     * @api {post} /group/hospital/getDetailByGroupId 根据集团id查询创建的医院详情
     * @apiVersion 1.0.0
     * @apiName getDetailByGroupId
     * @apiGroup 医院平台
     * @apiDescription 根据集团id查询创建的医院详情
     *
     * @apiParam  {String}   	id      	  集团id
     *
     * @apiSuccess {String} 	id    		集团 id
	 * @apiSuccess {Integer} hospitalId               医院id
	 * @apiSuccess {String} adminName     管理员姓名
	 * @apiSuccess {String} telephone     管理员电话
	 * @apiSuccess {String} province      省
	 * @apiSuccess {String} city          市
	 * @apiSuccess {String} country       区
	 * @apiSuccess {Long} creatorDate     创建日期
	 * @apiSuccess {String} HospitalName  医院名称
     *
     *
     * @apiAuthor  姜宏杰
     * @date 2016年4月13日10:33:32
	 */
	@RequestMapping("/getDetailByGroupId")
	public JSONMessage getDetailByGroupId(String id) {
		return JSONMessage.success("success",groupFacadeService.getDetailByGroupId(id));
	}
	
	/**
	 * @api {post} /group/hospital/updateHospitalRoot 更新并替换医院集团的超级管理员
	 * @apiVersion 1.0.0
	 * @apiName updateHospitalRoot
	 * @apiGroup 医院平台
	 * @apiDescription 更新并替换医院集团的超级管理员
	 *
	 * @apiParam  {String}   	hospitalId   医院id
	 * @apiParam  {Integer}   	doctorId     替换后的超级管理员医生id
	 *
	 *
	 * @apiAuthor  姜宏杰
	 * @date 2016年4月13日10:33:32
	 */
	@RequestMapping("/updateHospitalRoot")
	public JSONMessage updateHospitalRoot(String hospitalId,Integer doctorId) throws HttpApiException {
		//操作人
		Integer userId = ReqUtil.instance.getUserId();
		groupFacadeService.updateHospitalRoot(hospitalId, doctorId,userId);
		return JSONMessage.success("success");
	}
	
	/**
     * @api {post} /group/hospital/repairTypeForHospitalGroup 清理加入医院的医生中，部分数据在c_gruop_doctor表中的类型错误
     * @apiVersion 1.0.0
     * @apiName updateUnauthUserMessage
     * @apiGroup 医院平台
     * @apiDescription 清理加入医院的医生中，部分数据在c_gruop_doctor表中的类型错误
     *
	 * @apiParam {String} access_token token
     *
     * @apiSuccess {Number} 	resultCode    		返回状态码
     *
     * @apiAuthor  wangqiao
     * @date 2016年4月27日
     * 手工调用
	 */
	@RequestMapping("/repairTypeForHospitalGroup")
	public JSONMessage repairTypeForHospitalGroup() {
		
		int repairCount = groupDoctorService.repairTypeForHospitalGroup();
		Map<String,Object> retMap = new HashMap<String,Object>();
		retMap.put("repairCount", repairCount);
		
		return JSONMessage.success("success",retMap);
	}
	
}
