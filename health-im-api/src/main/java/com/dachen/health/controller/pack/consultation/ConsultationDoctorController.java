package com.dachen.health.controller.pack.consultation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.friend.service.FriendsManager;
import com.dachen.health.pack.consult.Service.ConsultationFriendService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.util.ReqUtil;

/**
 * @version 2
 * @see 版本2 会诊好友关系使用之前的朋友圈建立好友
 * @author wangl
 * @date 2016年2月27日18:09:02
 *
 */
@RestController
@RequestMapping("/consultation/doctor")
public class ConsultationDoctorController {
	
	
	@Autowired
	FriendsManager   friendsManagerImpl;
	
	@Autowired
	IOrderService  ordreService;
	
	@Autowired
	ConsultationFriendService consultationFriendService;

	/**
	 * @api {get/post} /consultation/doctor/getFriendsNum 获取会诊医生好友总数
	 * @apiVersion 1.0.0
	 * @apiName getFriendsNum
	 * @apiGroup 医生会诊
	 * @apiDescription 使用场景：会诊大厅获取会诊医生好友总数
	 * @apiParam {String} 			access_token 			token
	 * 
	 * @apiSuccess {String}         resultCode              状态码
	 * 
	 * @apiSuccess {String}          data                   会诊好友个数
     * @apiAuthor  wangl
     * @date 2016年2月27日
	 */
	@RequestMapping(value = "getFriendsNum")
	public JSONMessage getFriendsNum(){
		return JSONMessage.success(consultationFriendService.getFriendsNum(ReqUtil.instance.getUserId()));
	}
	
	
	/**
	 * @api {get/post} /consultation/doctor/getConsultationRecordList 会诊记录列表
	 * @apiVersion 1.0.0
	 * @apiName getConsultationRecordList
	 * @apiGroup 医生会诊
	 * @apiDescription 使用场景：会诊记录列表页面
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			enterType 			（大厅："hall",全部"all",通过医生："byDoctorId"）
	 * @apiParam {String} 			doctorId 				大医生id(在会诊好友列表页面进入)
	 * @apiParam {Integer} 			pageIndex 				页码数
	 * @apiParam {Integer} 			pageSize 				分页条数
	 * 
	 * @apiSuccess {String}         resultCode              状态码
	 * @apiSuccess {Object[]}         data                  数据对象
	 * 
	 * @apiSuccess {Integer}        pageCount              总页数
	 * @apiSuccess {Integer}        pageData.doctorId               医生id
	 * @apiSuccess {Integer}        pageData.msgGroupId             订单会话组id
	 * @apiSuccess {Integer}        pageData.orderId                订单id
	 * @apiSuccess {String}         pageData.illCaseInfoId           病历Id
	 * @apiSuccess {String}         pageData.name				            医生姓名
	 * @apiSuccess {String}         pageData.headPicFileName         图片URL     
	 * @apiSuccess {String}         pageData.departments             科室
	 * @apiSuccess {String}         pageData.hospital              	医院名称
	 * @apiSuccess {String}         pageData.patientName             患者名称
	 * @apiSuccess {String}         pageData.orderStatus             会诊记录状态（1：待预约，2：待支付，3：已支付，4：已完成，5:已取消,6：进行中，7：待完善，10：预约成功）
	 * @apiSuccess {String}         pageData.roleType                操作者角色（1：我发起的会诊，2：我接受的会诊）
	 * 
     * @apiAuthor  wangl
     * @date 2016年2月27日
	 */
	@RequestMapping(value = "getConsultationRecordList")
	public JSONMessage getConsultationRecordList(@RequestParam(required=true)String enterType , 
												 Integer doctorId,
												 Integer pageIndex,
												 Integer pageSize){
		return JSONMessage.success(ordreService.getConsultationRecordList(enterType,doctorId,pageIndex,pageSize));
	}
	
	
	/**
	 * @api {get/post} /consultation/doctor/getFriendList 获取会诊好友列表
	 * @apiVersion 1.0.0
	 * @apiName getFriendList
	 * @apiGroup 医生会诊
	 * @apiDescription 使用场景：获取会诊好友列表
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			pageIndex 				页码数
	 * @apiParam {Integer} 			pageSize 				分页条数
	 * 
	 * @apiSuccess {String}         resultCode              状态码
	 * @apiSuccess {Object[]}         data                  数据对象
	 * @apiSuccess {Integer}        pageCount              总页数
	 * @apiSuccess {Integer}        pageData.userId               医生id
	 * @apiSuccess {String}         pageData.name               	医生姓名
	 * @apiSuccess {String}         pageData.headPicFileName         图片URL     
	 * @apiSuccess {String}         pageData.departments             科室
	 * @apiSuccess {String}         pageData.title            		职称
	 * @apiSuccess {String}         pageData.doctorGroupName         集团名称
	 * @apiSuccess {String}         pageData.hospital              	医院名称
	 * @apiSuccess {Integer}        pageData.consultationPrice       会诊价格
	 * @apiSuccess {String}         pageData.consultationRequired    会诊要求
	 * @apiSuccess {Integer}        pageData.consultationCount      会诊次数
	 * 
     * @apiAuthor  wangl
     * @date 2016年2月27日
	 */
	@RequestMapping(value = "getFriendList")
	public JSONMessage getFriendList(Integer pageIndex,Integer pageSize){
		return JSONMessage.success(consultationFriendService.getFriendList(pageIndex,pageSize));
	}
	
	
	
	
	/**
	 * 
	 * @api {get/post} /consultation/doctor/searchConsultationDoctors 搜索会诊医生列表
	 * @apiVersion 1.0.0
	 * @apiName searchConsultationDoctors
	 * @apiGroup 医生会诊
	 * @apiDescription 使用场景：搜索会诊医生列表
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			areaCode 			            区域编号（可不传）
	 * @apiParam {String} 			deptId 			                        部门编号（可不传）
	 * @apiParam {String} 			name 			                        医生姓名（可不传）
	 * @apiParam {Integer} 			pageIndex 			            页数（可不传）
	 * @apiParam {Integer} 			pageSize 			             条数（可不传）
	 * 
	 * @apiSuccess {String}         resultCode              状态码
	 * 
	 * @apiSuccess {Integer}         pageCount              总页数
	 * @apiSuccess {List}            pageData               对象列表
	 * @apiSuccess {String}          pageData.userId      	医生id
	 * @apiSuccess {String}          pageData.name          医生姓名
	 * @apiSuccess {String}          pageData.title         职称
	 * @apiSuccess {String}          pageData.departments   科室
     * @apiSuccess {String}          pageData.hospital      医生所属医院名称
     * @apiSuccess {String}          pageData.doctorGroupName         集团名称
     * @apiSuccess {String}          pageData.headPicFileName         医生职称名称
     * @apiSuccess {String}          pageData.consultationPrice 会诊费用
     * @apiSuccess {String}          pageData.consultationRequired 会诊要求
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "searchConsultationDoctors")
	public JSONMessage searchConsultationDoctors(Integer areaCode,
												 String  name,
												 String  deptId,
												 Integer pageIndex,
												 Integer pageSize){
		return JSONMessage.success(consultationFriendService.searchDoctors(areaCode,name,deptId,pageIndex,pageSize));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/doctor/getPatientIllcaseList 发起会诊选择患者的用户资料页面
	 * @apiVersion 1.0.0
	 * @apiName getPatientIllcaseList
	 * @apiGroup 医生会诊
	 * @apiDescription 使用场景： 发起会诊选择患者的用户资料页面
	 * @apiParam {String} 			access_token 			token
	 * 
	 * @apiSuccess {String}         resultCode              状态码
	 * 
	 * @apiSuccess {Integer}         pageCount              总页数
	 * @apiSuccess {List}            pageData               对象列表
	 * @apiSuccess {String}          pageData.userId								用户id
	 * @apiSuccess {String}          pageData.userName							    用户名
	 * @apiSuccess {String}          pageData.sex         性别
	 * @apiSuccess {String}          pageData.ageStr     年龄
	 * @apiSuccess {String}          pageData.area        地区
	 * @apiSuccess {String}          pageData.groupName      分组
	 * @apiSuccess {String}          pageData.patientCaseInfoList    患者对象列表
	 * @apiSuccess {String}          pageData.patientCaseInfoList.patientId    患者id
	 * @apiSuccess {String}          pageData.patientCaseInfoList.name         患者名
	 * @apiSuccess {String}          pageData.patientCaseInfoList.sex          患者性别
	 * @apiSuccess {String}          pageData.patientCaseInfoList.ageStr       患者年龄
	 * @apiSuccess {String}          pageData.patientCaseInfoList.illcaseInfoId   电子病历id
	 * @apiSuccess {String}          pageData.patientCaseInfoList.mainCase        主诉
	 * @apiSuccess {String}          pageData.patientCaseInfoList.type            就诊类型
	 * @apiSuccess {String}          pageData.patientCaseInfoList.updateTime      最近更新时间

     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@Deprecated
	@RequestMapping(value = "getPatientIllcaseList")
	public JSONMessage getPatientIllcaseList(@RequestParam(required = true) Integer userId){
		return JSONMessage.success();
	}
	
	
}
