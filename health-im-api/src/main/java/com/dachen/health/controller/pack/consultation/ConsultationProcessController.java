package com.dachen.health.controller.pack.consultation;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.pack.consult.Service.ConsultationFriendService;
import com.dachen.health.pack.consult.Service.ConsultationPackService;
import com.dachen.health.pack.order.service.IOrderService;

@RestController
@RequestMapping("/consultation/process")
public class ConsultationProcessController {

	
	@Autowired
	ConsultationPackService  consultationPackService;
	
	@Autowired
	IGroupDoctorService groupDoctorService;
	
	@Autowired
	UserManager userManager;
	
	@Autowired
	ConsultationFriendService consultationFriendService;
	
	@Autowired
	IOrderService orderService;
	
	/**
	 * 
	 * @api {get/post} /consultation/process/getGroupList 获取大医生的集团列表
	 * @apiVersion 1.0.0
	 * @apiName getGroupList
	 * @apiGroup 会诊订单过程处理
	 * @apiDescription 使用场景：接受订单之前选择订单所属群组
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			doctorId 	                                    医生id(会诊医生)
	 * @apiParam {Integer} 			orderId 	                                    订单id
	 * 
	 * @apiSuccess {List}           data                    集团列表对象     
	 * @apiSuccess {String}         data.groupId            集团Id 
	 * @apiSuccess {String}         data.groupName          集团名称     
	 * @apiSuccess {String}         data.isMain             是否是主集团     
	 *
     * @apiAuthor  wangl
     * @date 2016年1月20日
	 */
	@RequestMapping(value = "getGroupList")
	public JSONMessage getGroupList(@RequestParam(required = true) Integer doctorId,
									@RequestParam(required = true) Integer orderId){
		return JSONMessage.success(consultationPackService.getGroupListByDoctorId(doctorId,orderId));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/process/getConsultationMember 会诊组成员
	 * @apiVersion 1.0.0
	 * @apiName getConsultationMember
	 * @apiGroup 会诊订单过程处理
	 * @apiDescription 使用场景：获取会诊组成员 item[0]:患者，item[1]:助手，item[2]:专家
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			orderId 	                                    订单Id(必填)
	 * 
	 * @apiSuccess {List}           data                       集团列表对象     
	 * @apiSuccess {Integer}        data.userId                患者对应的用户id 
	 * @apiSuccess {Integer}        data.patientId             患者Id
	 * @apiSuccess {String}         data.name                  姓名     
	 * @apiSuccess {String}         data.title                 职称     
	 * @apiSuccess {String}         data.hospital              医院名称     
	 * @apiSuccess {String}         data.doctorGroupName       集团
	 * @apiSuccess {String}         data.headPicFileName       图片URL     
	 * @apiSuccess {String}         data.introduction          医生介绍 
	 * @apiSuccess {String}         data.departments           医生部门 
	 * @apiSuccess {String}         data.cureNum               问诊人数
	 * @apiSuccess {String}         data.skill                 擅长
	 * @apiSuccess {String}         data.sex                   性别
	 * @apiSuccess {String}         data.age                   年龄
	 * @apiSuccess {String}         data.roleType              1:患者，3：医生
	 *
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "getConsultationMember")
	public JSONMessage getConsultationMember(@RequestParam(required = true) Integer orderId){
		return JSONMessage.success(consultationFriendService.getConsultationMember(orderId));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/process/notifySpecialist 发送会诊通知大医生
	 * @apiVersion 1.0.0
	 * @apiName notifySpecialist
	 * @apiGroup 会诊订单过程处理
	 * @apiDescription 使用场景：发送会诊的时候短信通知大医生
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			orderId 	                                    订单Id
	 * 
	 * @apiSuccess {Integer}           data                    1成功    
	 *
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "notifySpecialist")
	public JSONMessage notifySpecialist(@RequestParam(required = true) Integer orderId) throws HttpApiException {
		consultationFriendService.notifySpecialist(orderId);
		return JSONMessage.success();
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/process/sendDirective 发送指令通知大医生 @Deprecated
	 * @apiVersion 1.0.0
	 * @apiName sendDirective
	 * @apiGroup 会诊订单过程处理
	 * @apiDescription 使用场景：发送指令通知大医生
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			orderId 	                                    订单Id
	 * 
	 * @apiSuccess {Integer}           data                 房间号
	 *
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "sendDirective")
	@Deprecated
	public JSONMessage sendDirective(@RequestParam(required = true) Integer orderId) throws HttpApiException {
		consultationFriendService.sendDirective(orderId);
		return JSONMessage.success(orderService.getConsultationRoomNumber(orderId));
	}
	
	
	
	/**
	 * 
	 * @api {get/post} /consultation/process/getRoomNumber 获取房间号
	 * @apiVersion 1.0.0
	 * @apiName getRoomNumber
	 * @apiGroup 会诊订单过程处理
	 * @apiDescription 使用场景：获取房间号
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			orderId 	                                    订单Id（必传）
	 * 
	 * @apiSuccess {Integer}           data                 房间号    
	 *
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "getRoomNumber")
	public JSONMessage getRoomNumber(@RequestParam(required = true) Integer orderId){
		return JSONMessage.success(orderService.getConsultationRoomNumber(orderId));
	}
	
}
