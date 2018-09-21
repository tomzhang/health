package com.dachen.health.controller.pack.patient;

import java.util.Date;

import javax.annotation.Resource;

import com.dachen.health.pack.order.entity.po.PendingOrderStatus;
import com.dachen.health.pack.order.service.IPendingOrderStatusService;
import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.service.IOrderSessionService;

/**
 * 
 * ProjectName： health-im-api<br>
 * ClassName： DiseaseController<br>
 * Description： <br>
 * @author 李淼淼
 * @createTime 2015年8月12日
 * @version 1.0.0
 */
@RestController
@RequestMapping(value="/orderSession")
public class OrderSessionController extends BaseController<OrderSession, Integer> {
	
	@Resource
	private IOrderSessionService service;
	
	@Resource
	private IOrderService orderSerice;

	@Autowired
	private IPendingOrderStatusService pendingOrderStatusService;


	public IOrderService getOrderSerice(){
		return orderSerice;
	}
	
	public IOrderSessionService getService() {
		return service;
	}

	/**
	 * 
	 * @api {[get,post]} /orderSession/create  订单-会话创建
	 * @apiVersion 1.0.0
	 * @apiName create
	 * @apiGroup 订单-会话
	 * @apiDescription 订单-会话创建
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			patientId 				患者id
	 * @apiParam {String} 			needHelp 				需要帮助 1是 2否
	 * @apiParam {String} 			diseaseInfo 			病情
	 * @apiParam {String} 			telephone 				手机号
	 * 
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("create")
	public JSONMessage create(OrderSession intance) throws HttpApiException {
		intance.setCreateTime(new Date().getTime());
		return super.create(intance);
		
	}

	/**
	 * 
	 * @api {[get,post]} /orderSession/update  订单-会话修改
	 * @apiVersion 1.0.0
	 * @apiName update
	 * @apiGroup  订单-会话
	 * @apiDescription  订单-会话修改
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			patientId 				患者id
	 * @apiParam {String} 			needHelp 				需要帮助 1是 2否
	 * @apiParam {String} 			diseaseInfo 			病情
	 * @apiParam {String} 			id 						id
	 * @apiParam {String} 			telephone 				手机号
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("update")
	public JSONMessage update(OrderSession intance) throws HttpApiException {
		return super.update(intance);
		
	}

	/**
	 * 
	 * @api {[get,post]} /orderSession/deleteByPk 根据主键删除
	 * @apiVersion 1.0.0
	 * @apiName deleteByPk
	 * @apiGroup  订单-会话
	 * @apiDescription 根据主键删除 订单-会话
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			id 						id
	 * 
	 *
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping(value="deleteByPk")
	public JSONMessage deleteByPk(Integer id) {
		return super.deleteByPk(id);
		
	}
	
	/**
	 * 
	 * @api {[get,post]} /orderSession/findById 根据主键查找
	 * @apiVersion 1.0.0
	 * @apiName findById
	 * @apiGroup 订单-会话
	 * @apiDescription 根据主键查找
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			id 						id
	 * 
	 * 
	 * @apiSuccess {String} 			patientId 				患者id
	 * @apiSuccess {String} 			needHelp 				需要帮助 1是 2否
	 * @apiSuccess {String} 			diseaseInfo 			病情
	 * @apiSuccess {String} 			id 						id
	 * @apiSuccess {String} 			createdTime 			创建时间
	 * @apiSuccess {String} 			createUserId 			创建人
	 * @apiSuccess {String} 			telephone 				手机号

	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping(value="findById")
	public JSONMessage findById(Integer id) throws HttpApiException {
		return super.findById(id);
	}
	
	/**
	 * 
	 * @api {[get,post]} /orderSession/updateSplitAndFinishService 更新会诊订单分医生分成比例并结束订单
	 * @apiVersion 1.0.0
	 * @apiName updateSplitAndFinishService
	 * @apiGroup  订单-会话
	 * @apiDescription  更新会诊订单分医生分成比例并结束订单
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			orderId 				订单id
	 * @apiParam {String} 			splitJson 				所有医生分成比例     ：  "{\"doctor1\":percent1,\"doctor2\":percent2}";
	 * 
	 * @author zhy
	 * @date 2017年2月28日
	 */
	@RequestMapping(value="updateSplitAndFinishService")
	public JSONMessage  updateSplitAndFinishService(String splitJson,int orderId) throws HttpApiException {
		service.updateSplit(splitJson, orderId, true);
		return JSONMessage.success();
	}
	/**
	 * 
	 * @api {[get,post]} /orderSession/finishService 医生结束服务
	 * @apiVersion 1.0.0
	 * @apiName finishService
	 * @apiGroup  订单-会话
	 * @apiDescription  结束服务
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			orderId 				订单id
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月13日
	 */
	@RequestMapping(value="finishService")
	public JSONMessage finishService(Integer orderId) throws HttpApiException {
		if(orderId==null){
			throw new ServiceException(30003,"parameter [orderId] is null!");
		}
		service.finishService(orderId,2);

		//删除订单待处理状态，by qinyuan.chen
		pendingOrderStatusService.deleteByOrderId(orderId);

		return JSONMessage.success(null, "结束服务成功");
	}
	
	/**
	 * 
	 * @api {post} /orderSession/finishServiceByPatient 患者结束服务
	 * @apiVersion 1.0.0
	 * @apiName finishServiceByPatient
	 * @apiGroup  订单-会话
	 * @apiDescription  结束服务
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			orderId 				订单id
	 * @apiParam {String} 			patientId 				患者id
	 * 
	 * @apiAuthor 肖伟
	 * @date 2016年10月18日
	 */
	@RequestMapping(value="finishServiceByPatient", method=RequestMethod.POST)
	public JSONMessage finishServiceByPatient(Integer orderId, Integer patientId) throws HttpApiException {
		if(null == orderId || null == patientId){
			throw new ServiceException(30003,"parameter [orderId or patientId] is null!");
		}
		service.finishServiceByPatient(orderId, patientId);
		
		//删除订单待处理状态，by qinyuan.chen
		pendingOrderStatusService.deleteByOrderId(orderId);
				
		return JSONMessage.success(null, "结束服务成功");
	}
	
	/**
	 * 
	 * @api {[get,post]} /orderSession/beginService 开始服务
	 * @apiVersion 1.0.0
	 * @apiName beginService
	 * @apiGroup  订单-会话
	 * @apiDescription 开始服务
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			orderId 				订单id
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月13日
	 */
	@RequestMapping(value="beginService")
	public JSONMessage beginService(Integer orderId) throws HttpApiException {
		if(orderId==null){
			throw new ServiceException(30003,"parameter [orderId] is null!");
		}
		service.beginService(orderId);
		return JSONMessage.success(null, "开启服务成功");
				
	}
	
	/**
	 * @api {[get,post]} /orderSession/beginService4Plan 开始服务（健康關懷）
	 * @apiVersion 1.0.0
	 * @apiName beginService4Plan
	 * @apiGroup  订单-会话
	 * @apiDescription 开始服务（健康關懷）
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			orderId 				订单id
	 * @apiParam {String} 			startDate 				开始时间
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月13日
	 */
	@RequestMapping(value="beginService4Plan")
	public JSONMessage beginService4Plan(Integer orderId, String startDate) throws HttpApiException {
		if(orderId==null){
			throw new ServiceException(30003,"parameter [orderId] is null!");
		}
		service.beginService4Plan(orderId, startDate);
		return JSONMessage.success(null, "开启服务成功");
				
	}
	
	/**
	 * 
	 * @api {[get,post]} /orderSession/abandonAdvisory 患者放弃咨询
	 * @apiVersion 1.0.0
	 * @apiName abandonAdvisory
	 * @apiGroup 诊疗记录
	 * @apiDescription  患者放弃咨询医生
	 * @apiParam {String} 				access_token 			token
	 * @apiParam {String} 				orderId 				订单id
	 * 
	 * 
	 * @apiAuthor 张垠
	 * @author 张垠
	 * @date 2015年9月29日
	 */
	@RequestMapping(value="abandonAdvisory")
	public JSONMessage abandonAdvisory(Integer orderId) throws HttpApiException {
		if(orderId==null){
			throw new ServiceException(30002, "parameter :[orderId] is null");
		}
		service.abandonService(orderId);
		return JSONMessage.success(null, "已放弃本次咨询");
	}
	
	
	/**
	 * 
	 * @api {[get,post]} /orderSession/appointTime 预约时间
	 * @apiVersion 1.0.0
	 * @apiName appointTime
	 * @apiGroup  订单-会话
	 * @apiDescription 预约时间
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			orderId 				订单id
	 * @apiParam {Long} 		appointTime 			预约时间13位long 类型时间戳
	 * @apiParam {String} 		    hospitalId 			    针对预约套餐需要关联医院id
	 * 

	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月13日
	 */
	@RequestMapping(value="appointTime")
	public JSONMessage appointTime(Integer orderId,Long appointTime,String hospitalId) throws HttpApiException {
		if(orderId==null){
			throw new ServiceException(30003,"parameter [orderId] is null!");
		}
		if(appointTime==null){
			throw new ServiceException(30004,"parameter [appointTime] is null!");
		}
		service.appointServiceTime(orderId, appointTime,true,hospitalId);
		return JSONMessage.success(null, "预约成功");
				
	}
	
	/**
	 * 
	 * @api {[get,post]} /orderSession/agreeAppointmentOrder 医生同意患者的预约订单
	 * @apiVersion 1.0.0
	 * @apiName agreeAppointmentOrder 医生同意患者的预约订单
	 * @apiGroup  订单-会话
	 * @apiDescription 预约时间
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			orderId 				订单id
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @author wangl
	 * @date 2016年6月16日13:15:58
	 */
	@RequestMapping(value="agreeAppointmentOrder")
	public JSONMessage agreeAppointmentOrder(@RequestParam(required=true)Integer orderId) throws HttpApiException {
		service.agreeAppointmentOrder(orderId);
		return JSONMessage.success();
	}
	
	
	/**
	* 
	 * @api {[get,post]} /orderSession/prepareTreat 门诊接单开始，
	 * @apiVersion 1.0.0
	 * @apiName prepareTreat
	 * @apiGroup  订单-会话
	 * @apiDescription 门诊接单
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			orderId 				订单id
	 * 
	 * @apiAuthor 张垠
	 * @author 张垠
	 * @date 2015年10月09日
	 */
	@RequestMapping(value="prepareTreat")
	public JSONMessage prepareTreat(Integer orderId) throws HttpApiException {
		if(orderId==null){
			throw new ServiceException(30003,"parameter [orderId] is null!");
		}
		 service.prepareService(orderId);
		return JSONMessage.success(null, "开启门诊接单成功");
	}
	
	
	/**
	 * 
	 * @api {[get,post]} /orderSession/goPatientCustomerSession 患者联系客服
	 * @apiVersion 1.0.0
	 * @apiName goPatientCustomerSession
	 * @apiGroup  订单-会话
	 * @apiDescription 患者联系客服
	 * @apiParam {String} 			access_token 			token
	 * 
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {Integer} patientUserId	患者用户id
	 * @apiSuccess {Integer} msgGroupId	  组id
	 * 
	 * @apiAuthor wangl
	 * @date 2016年7月25日
	 */
	@RequestMapping(value="goPatientCustomerSession")
	public JSONMessage goPatientCustomerSession(){
		return JSONMessage.success(service.getOrCreatePatientCustomerSession());
	}
	
	
	/**
	 * 
	 * @api {[get,post]} /orderSession/cacheBeServicedUser 缓存待服务的患者
	 * @apiVersion 1.0.0
	 * @apiName cacheBeServicedUser
	 * @apiGroup  订单-会话
	 * @apiDescription 缓存待服务的患者
	 * @apiParam {String} 	access_token 			token
	 * 
	 * @apiSuccess {String} resultCode	1 成功
	 * 
	 * @apiAuthor wangl
	 * @date 2016年7月25日
	 */
	@RequestMapping(value="cacheBeServicedUser")
	public JSONMessage cacheBeServicedUser(){
		service.cacheBeServicedUser();
		return JSONMessage.success();
	}
	
	/**
	 * 
	 * @api {[get,post]} /orderSession/cacheSendMessageRecord 图文咨询记录消息发送次数
	 * @apiVersion 1.0.0
	 * @apiName cacheSendMessageRecord
	 * @apiGroup  订单-会话
	 * @apiDescription 图文咨询记录消息发送次数
	 * @apiParam {String} 	access_token 			token
	 * @apiParam {String} 	messageGroupId 			消息会话id
	 * 
	 * @apiSuccess {String} resultCode	1 成功
	 * 
	 * @apiAuthor wangl
	 * @date 2016年9月23日17:37:51
	 */
	@RequestMapping(value="cacheSendMessageRecord")
	public JSONMessage cacheSendMessageRecord(@RequestParam(required=true) String messageGroupId){
		service.cacheSendMessageRecord(messageGroupId);
		return JSONMessage.success();
	}
	
	/**
	 * 
	 * @api {[get,post]} /orderSession/addFreeReplyCount 赠送给患者免费的咨询次数
	 * @apiVersion 1.0.0
	 * @apiName addFreeReplyCount
	 * @apiGroup  订单-会话
	 * @apiDescription 赠送给患者免费的咨询次数
	 * @apiParam {String} 	access_token 			token
	 * @apiParam {String} 	messageGroupId 			消息会话id（必传）
	 * @apiParam {Integer} 	count 					回复条数
	 * 
	 * @apiSuccess {String} resultCode	1 成功
	 * 
	 * @apiAuthor wangl
	 * @date 2016年9月26日10:43:55
	 */
	@RequestMapping(value="addFreeReplyCount")
	public JSONMessage addFreeReplyCount(@RequestParam(required=true) String messageGroupId , 
										 Integer count) throws HttpApiException {
		service.addFreeReplyCount(messageGroupId,count);
		return JSONMessage.success();
	}
	
	
	
	/**
	 * 
	 * @api {[get,post]} /orderSession/cacheSessionMessageRecord 图文咨询记录消息发送次数
	 * @apiVersion 1.0.0
	 * @apiName cacheSessionMessageRecord
	 * @apiGroup  订单-会话
	 * @apiDescription 图文咨询记录消息发送次数
	 * @apiParam {String} 	access_token 			token
	 * @apiParam {String} 	messageGroupId 			消息会话id
	 * @apiParam {String} 	messageId 			消息id
	 * 
	 * @apiSuccess {String} resultCode	1 成功
	 * 
	 * @apiAuthor wangl
	 * @date 2016年12月7日19:51:43
	 */
	@RequestMapping(value="cacheSessionMessageRecord")
	public JSONMessage cacheSessionMessageRecord(@RequestParam(required=true) String messageGroupId , String messageId) throws HttpApiException {
		service.cacheSessionMessageRecord(messageGroupId,messageId);
		return JSONMessage.success();
	}
	
	/**
	 * 
	 * @api {[get,post]} /orderSession/addFreeMessageCount 赠送给患者免费的咨询次数
	 * @apiVersion 1.0.0
	 * @apiName addFreeMessageCount
	 * @apiGroup  订单-会话
	 * @apiDescription 赠送给患者免费的咨询次数
	 * @apiParam {String} 	access_token 			token
	 * @apiParam {String} 	messageGroupId 			消息会话id（必传）
	 * @apiParam {Integer} 	count 					回复条数
	 * 
	 * @apiSuccess {String} resultCode	1 成功
	 * 
	 * @apiAuthor wangl
	 * @date 2016年12月7日19:52:32
	 */
	@RequestMapping(value="addFreeMessageCount")
	public JSONMessage addFreeMessageCount(@RequestParam(required=true) String messageGroupId ,Integer count) throws HttpApiException {
		service.addFreeMessageCount(messageGroupId,count);
		return JSONMessage.success();
	}

	/**
	 *
	 * @api {[get,post]} /orderSession/processPaySuccess 客户端支付成功之后调用服务端获取gid
	 * @apiVersion 1.0.0
	 * @apiName addFreeMessageCount
	 * @apiGroup  订单-会话
	 * @apiDescription 客户端支付成功之后调用服务端获取gid
	 * @apiParam {String} 	access_token 			token
	 * @apiParam {Integer} 	orderId 				订单id
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {String} orderId 订单ID
	 * @apiSuccess {Integer} status 订单状态
	 * @apiSuccess {String} msgGroupId 订单会话ID
	 *
	 * @apiAuthor wangl
	 * @date 2016年12月7日19:52:32
	 */
	@RequestMapping(value="processPaySuccess")
	public JSONMessage processPaySuccess(@RequestParam(required=true) Integer orderId){
		return JSONMessage.success(service.getOrderSessionWhenPaysuccess(orderId));
	}


	@RequestMapping(value="repairOldCareOrderSession")
	public JSONMessage repairOldCareOrderSession(){
		service.repairOldCareOrderSession();
		return JSONMessage.success();
	}

}
