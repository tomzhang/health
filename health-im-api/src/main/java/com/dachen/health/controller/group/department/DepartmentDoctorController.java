package com.dachen.health.controller.group.department;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.group.department.entity.param.DepartmentDoctorParam;
import com.dachen.health.group.department.entity.po.DepartmentDoctor;
import com.dachen.health.group.department.service.IDepartmentDoctorService;
import com.dachen.health.recommend.entity.po.DoctorRecommend;
import com.dachen.health.recommend.service.IDoctorRecommendService;

@RestController
@RequestMapping("/department/doctor")
public class DepartmentDoctorController extends AbstractController {

	@Autowired
	private IDepartmentDoctorService ddocService;
	
	@Autowired
	private IDoctorRecommendService dctorRecommendService;
	
	/**
     * @api {post} /department/doctor/saveDoctorIdBydepartIds 医生分配多个科室
     * @apiVersion 1.0.0
     * @apiName saveDoctorIdBydepartIds
     * @apiGroup 组织架构与医生
     * @apiDescription 医生分配多个科室（删除所有）
     *
     * @apiParam  {String}    		access_token        	token
     * @apiParam  {String}   		departmentIds       	组织Id
     * @apiParam  {String}   		doctorId          		医生Id
     * @apiParam  {String}   		groupId          		集团Id
     * 
     * @apiSuccess {Number} resultCode    返回状态吗
     * 
     * @apiAuthor  pijingwei
     * @date 2015年8月11日
	 */
	@RequestMapping("/saveDoctorIdBydepartIds")
	public JSONMessage saveDoctorIdBydepartIds(String[] departmentIds, Integer doctorId, String groupId) {
		ddocService.saveDoctorIdByDepartmentIds(departmentIds, doctorId, groupId);
		return JSONMessage.success("success");
	}
	
	/**
     * @api {post} /department/doctor/saveDepartIdByDoctorIds 科室添加多个医生
     * @apiVersion 1.0.0
     * @apiName saveDepartIdByDoctorIds
     * @apiGroup 组织架构与医生
     * @apiDescription 科室添加多个医生（删除所有）
     *
     * @apiParam {String}    		access_token       token
     * @apiParam {String}   		departmentId       组织Id
     * @apiParam {Integer[]}   		doctorIds          医生Id（多个医生Id的数组）
     * @apiParam {String}   		groupId            集团Id
     * 
     * @apiSuccess {Number} resultCode    返回状态吗
     * 
     * @apiAuthor  pijingwei
     * @date 2015年8月11日
	 */
	@RequestMapping("/saveDepartIdByDoctorIds")
	public JSONMessage saveDepartIdByDoctorIds(String departmentId, Integer[] doctorIds, String groupId) {
		ddocService.saveDepartmentIdByDoctorIds(departmentId, doctorIds, groupId);
		return JSONMessage.success("success");
	}
	
	/**
     * @api {post} /department/doctor/addDoctorByDepartmentId 科室添加单个医生（不删除）
     * @apiVersion 1.0.0
     * @apiName addDoctorByDepartmentId
     * @apiGroup 组织架构与医生
     * @apiDescription 科室添加单个医生（不删除）
     *
     * @apiParam {String}    		access_token        	token
     * @apiParam {String}   		departmentId       		组织Id
     * @apiParam {Integer[]}   		doctorId           		医生Id
     * @apiParam {Integer[]}   		groupId           		集团Id
     * 
     * @apiSuccess {Number} resultCode    返回状态吗
     * 
     * @apiAuthor  pijingwei
     * @date 2015年8月11日
	 */
	@RequestMapping("/addDoctorByDepartmentId")
	public JSONMessage addDoctorByDepartmentId(DepartmentDoctor ddoc) {
		ddocService.saveDepartmentDoctor(ddoc);
		return JSONMessage.success("success");
	}
	
	
	
	/**
     * @api {post} /department/doctor/searchByDeDoctor 查找组织架构的医生
     * @apiVersion 1.0.0
     * @apiName searchByDeDoctor
     * @apiGroup 组织架构与医生
     * @apiDescription 查找组织架构的医生
     *
     * @apiParam  {String}    	access_token        						token
     * @apiParam  {String}   	departmentId       							组织Id
     * @apiParam  {String}   	keyword       								搜索关键字（同时匹配医生名称和医生职称，条件是或者（or），不是并且（and））
     *
     * @apiSuccess {Object[]} 	ddoc              							公司用户
     * @apiSuccess {String}  	ddoc.id										记录Id
     * @apiSuccess {String}  	ddoc.departmentId							组织Id
     * @apiSuccess {Integer}  	ddoc.doctorId        						医生Id
     * @apiSuccess {Integer}  	ddoc.creator        						创建人
     * @apiSuccess {Long}  		ddoc.creatorDate       						创建时间
     * @apiSuccess {Integer}  	ddoc.updator        						更新人
     * @apiSuccess {Long}  		ddoc.updatorDate       						更新时间
     * @apiSuccess {Long}  		ddoc.remarks       							备注
     * @apiSuccess {Long}  		ddoc.contactWay       						备用联系方式
     * @apiSuccess {Integer}  	ddoc.applyStatus       						审核状态
     * @apiSuccess {String}  	ddoc.applyStatusName       					审核状态名称
     * @apiSuccess {Object[]}  	ddoc.doctor       							医生信息
     * @apiSuccess {String}  	ddoc.doctor.name       						医生名称
     * @apiSuccess {String}  	ddoc.doctor.position   						医生职称
     * @apiSuccess {Integer}  	ddoc.doctor.status   						医生审核状态
     * @apiSuccess {String}  	ddoc.doctor.statusName   					医生审核状态名称
     * @apiSuccess {String}  	ddoc.doctor.doctorNum  						医生号
     * @apiSuccess {String}  	ddoc.doctor.skill      						擅长领域
     * @apiSuccess {String}  	ddoc.doctor.introduction       				医生个人简介
     * @apiSuccess {Object[]}  	ddoc.invite       							谁邀请了我的邀请信息
     * @apiSuccess {Integer}  	ddoc.invite.inviterId       				邀请人Id
     * @apiSuccess {Integer}  	ddoc.invite.inviteeId       				被邀请人Id
     * @apiSuccess {String}  	ddoc.invite.headPicFileName     			头像名称
     * @apiSuccess {String}  	ddoc.invite.headPicFilePath     			头像地址
     * @apiSuccess {String}  	ddoc.invite.name       						名称
     * @apiSuccess {Long}  		ddoc.invite.inviteDate       				邀请日期
     * @apiSuccess {String}  	ddoc.invite.inviteMsg       				邀请信息
     * @apiSuccess {Object[]}  	ddoc.invite.myInvite       					我邀请的人的信息列表
     * @apiSuccess {Integer}  	ddoc.invite.myInvite.inviterId       		邀请人Id
     * @apiSuccess {Integer}  	ddoc.invite.myInvite.inviteeId       		被邀请人Id
     * @apiSuccess {String}  	ddoc.invite.myInvite.headPicFileName     	头像名称
     * @apiSuccess {String}  	ddoc.invite.myInvite.headPicFilePath     	头像地址
     * @apiSuccess {String}  	ddoc.invite.myInvite.name       			名称
     * @apiSuccess {Long}  		ddoc.invite.myInvite.inviteDate       		邀请日期
     * @apiSuccess {String}  	ddoc.invite.myInvite.inviteMsg       		邀请信息
     * 
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月11日
	 */
	@RequestMapping("/searchByDeDoctor")
	public JSONMessage searchByDeDoctor(DepartmentDoctorParam ddoc) {
		return JSONMessage.success(null, ddocService.searchDepartmentDoctor(ddoc));
	}
	
	/**
     * @api {post} /department/doctor/getDepartmentDoctor 按组织架构查找医生
     * @apiVersion 1.0.0
     * @apiName getDepartmentDoctor
     * @apiGroup 组织架构与医生
     * @apiDescription 按组织架构查找医生
     *
     * @apiParam  {String}      access_token                                token
     * @apiParam  {String}      groupId                                     集团Id
     * @apiParam  {String}      departmentId                                组织架构Id
     * @apiParam  {String}      consultationPackId                          当前会诊包id
     * @apiParam  {String[]}    status                                      状态： 正常（C）；待确认（I、J）； 已离职（S、O） ；已拒绝（N、M）,为空则查未分配的 
     * @apiParam  {Integer}     type                                        1：查询当前组织架构，2：查询当前及子组织架构
     *
     * @apiSuccess {Integer}    userId                                      医生id
     * @apiSuccess {String}     name                                        医生姓名
     * 
     *
     * @apiAuthor  fanp
     * @date 2015年9月21日
     */
	@RequestMapping("/getDepartmentDoctor")
    public JSONMessage searchByDeDoctor(String groupId,String departmentId,Integer type,String[] status ,String consultationPackId) {
        return JSONMessage.success(null, ddocService.getDepartmentDoctor(groupId,departmentId,type,status,consultationPackId));
    }
	
	/**
     * @api {post} /department/doctor/getDepartmentRecommendDoctor 集团名医推荐-按组织架构查找医生
     * @apiVersion 1.0.0
     * @apiName getDepartmentRecommendDoctor
     * @apiGroup 组织架构与医生
     * @apiDescription 集团名医推荐-按组织架构查找医生
     *
     * @apiParam  {String}      access_token                                token
     * @apiParam  {String}      groupId                                     集团Id
     * @apiParam  {String}      departmentId                                组织架构Id
     * @apiParam  {String}      consultationPackId                          当前会诊包id
     * @apiParam  {String[]}    status                                      状态： 正常（C）；待确认（I、J）； 已离职（S、O） ；已拒绝（N、M）,为空则查未分配的 
     * @apiParam  {Integer}     type                                        1：查询当前组织架构，2：查询当前及子组织架构
     *
     * @apiSuccess {Integer}    userId                                      医生id
     * @apiSuccess {String}     name                                        医生姓名
     * 
     *
     * @apiAuthor  fuyongde
     * @date 2016年8月5日
     */
	@RequestMapping("/getDepartmentRecommendDoctor")
    public JSONMessage getDepartmentRecommendDoctor(String groupId,String departmentId,Integer type,String[] status ,String consultationPackId) {
		
		//先获取该集团在t_doctor_recommend表中存在的记录
		List<Integer> recommendDoctorIds = dctorRecommendService.getDoctorIdsByGroup(groupId);
		
        return JSONMessage.success(null, ddocService.getDepartmentRecommendDoctor(groupId,departmentId,type,status,consultationPackId, recommendDoctorIds));
    }
	
	
	/**
     * @api {post} /department/doctor/deleteByDeDoctor 删除组织架构的医生
     * @apiVersion 1.0.0
     * @apiName deleteByDeDoctor
     * @apiGroup 组织架构与医生
     * @apiDescription 删除组织架构的医生
     *
     * @apiParam  {String}    		access_token        	token
     * @apiParam  {String}   		ids           			记录Id
     *
     * @apiSuccess {Number} resultCode    返回状态吗
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月11日
	 */
	@RequestMapping("/deleteByDeDoctor")
	public JSONMessage deleteGroup(String[] ids) {
		ddocService.deleteDepartmentDoctor(ids);
		return JSONMessage.success("success");
	}
	
	
	/**
     * @api {post} /department/doctor/getDepartmentById 查找医生的组织架构
     * @apiVersion 1.0.0
     * @apiName getDepartmentById
     * @apiGroup 组织架构与医生
     * @apiDescription 查找医生的组织架构
     *
     * @apiParam  {String}    	access_token        			token
     * @apiParam  {String}   	doctorId       					医生Id
     *
     * @apiSuccess {Object[]} 	department              		组织架构
     * @apiSuccess {String}  	department.id					组织Id
     * @apiSuccess {String}  	department.groupId				集团Id
     * @apiSuccess {String}  	department.parentId        		父节点Id
     * @apiSuccess {String}  	department.name        			组织名称
     * @apiSuccess {String}  	department.description        	组织描述
     * @apiSuccess {Integer}  	department.creator        		创建人
     * @apiSuccess {Long}  		department.creatorDate       	创建时间
     * @apiSuccess {Integer}  	department.updator        		更新人
     * @apiSuccess {Long}  		department.updatorDate       	更新时间
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月11日
	 */
	@RequestMapping("/getDepartmentById")
	public JSONMessage getDepartmentById(Integer doctorId) {
		return JSONMessage.success("success", ddocService.getDepartmentByDoctorId(doctorId));
	}
	
	
	/**
     * @api {post} /department/doctor/updateDepartment 修改医生的组织架构
     * @apiVersion 1.0.0
     * @apiName updateDepartment
     * @apiGroup 组织架构与医生
     * @apiDescription 修改医生的组织架构
     *
     * @apiParam  {String}    	access_token        			token
     * @apiParam  {String}   	groupId       					集团Id
     * @apiParam  {Integer}   	doctorId       					医生Id
     * @apiParam  {String}   	departmentId       				新组织Id
     *
     * @apiSuccess	{String}	groupId							集团Id
     * @apiSuccess	{String}	departmentId					门诊Id
     * @apiSuccess	{Integer}	doctorId						医生Id
     *
     * @apiAuthor  谢平
     * @date 2015年11月3日
	 */
	@RequestMapping("/updateDepartment")
	public JSONMessage updateDepartment(DepartmentDoctorParam param) {
		return JSONMessage.success("success", ddocService.updateDepartment(param));
	}
	
}
