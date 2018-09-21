package com.dachen.health.controller.pack.guide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dachen.health.pack.guide.entity.vo.HelpVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.controller.pack.expand.OrderExpandController;
import com.dachen.health.group.group.entity.param.GroupConfigAndFeeParam;
import com.dachen.health.group.group.entity.param.GroupParam;
import com.dachen.health.group.group.entity.po.GroupConfig;
import com.dachen.health.msg.entity.param.BusinessParam;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.guide.dao.IGuideDAO;
import com.dachen.health.pack.guide.entity.OrderCache;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO;
import com.dachen.health.pack.guide.entity.vo.OrderDiseaseVO;
import com.dachen.health.pack.guide.service.IGuideMsgService;
import com.dachen.health.pack.guide.service.IGuideService;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.vo.AppointmentOrderWebParams;
import com.dachen.health.pack.order.entity.vo.OrderDetailVO;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.user.service.IDoctorService;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.JSONUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;

@RestController
@RequestMapping("/guide")
public class GuideController {
	@Autowired
	private IGuideService guideService;

	@Autowired
	private IGuideMsgService guideMsgService;
	
	@Autowired
	IOrderService orderService;

	@Autowired
	private IBusinessServiceMsg businessServiceMsg;

	@Autowired
	private UserManager userManager;

	@Autowired
	private IGuideDAO iGuideDAO;

	@Autowired
	private IDoctorService doctorService;
	
	/**
	 * @api {get} /guide/exist 患者下单时判断是否存在尚未结束的电话咨询订单（选择电话咨询的时候判断）
	 * @apiVersion 1.0.0
	 * @apiName exist
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 患者下单时判断是否存在尚未结束的电话咨询订单（选择电话咨询的时候判断）
	 *
	 * @apiParam {String} access_token token
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 */
	@RequestMapping(value = "/exist")
	public JSONMessage exist() {
		UserSession session = ReqUtil.instance.getUser();
		Object obj = guideService.existServingOrder(session);
		if (obj == null) {
			return JSONMessage.success();
		} else {
			return new JSONMessage(0, "您有尚未结束的电话咨询订单。", obj);
		}
	}

	/**
	 * @api {get} /guide/receive 导医接单
	 * @apiVersion 1.0.0
	 * @apiName receive
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 生成咨询订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {String} id 咨询订单Id
	 * @apiSuccess {Object} GroupInfo 结构详见：
	 *             http://192.168.3.7:8091/apidoc/#api-消息-groupInfo
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 */
	@RequestMapping(value = "/receive")
	public JSONMessage receivingOrder(@RequestParam String id) throws HttpApiException {
		UserSession session = ReqUtil.instance.getUser();
		return JSONMessage.success(null,
				guideService.receivingOrder(id, session));
	}

	/**
	 * @api {get} /guide/groupList 获取服务中的会话（导医端）
	 * @apiVersion 1.0.0
	 * @apiName groupList
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 获取服务中的会话（导医端）
	 * @apiParam {String} access_token token
	 * @apiParam {String} userId 用户id
	 * @apiParam {long} ts 时间戳，上次更新时间，第一次传0
	 * @apiParam {int} cnt 更新个数
	 *
	 * @apiSuccess {Object} msgGroupVO
	 *             返回会话列表对象(详细结构见http://192.168.3.7:8091/apidoc
	 *             /#api-消息-getBusiness)
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 */
	@RequestMapping(value = "/groupList")
	public JSONMessage groupList(BusinessParam buisnessParam) {
		if (buisnessParam.getUserId() == null) {
			UserSession session = ReqUtil.instance.getUser();
			buisnessParam.setUserId(String.valueOf(session.getUserId()));
		}
		// 1、导医获取服务中的会话
		return JSONMessage.success(null,
				guideMsgService.groupList(buisnessParam));
	}

	/**
	 * @api {get} /guide/waitOrderList 获取等待接单列表（导医端）
	 * @apiVersion 1.0.0
	 * @apiName waitOrderList
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 获取等待接单列表（导医端）
	 * @apiParam {String} access_token token
	 * @apiParam {int} count 每次获取订单的数量（默认20）
	 * @apiParam {long} startScore 开始时间（如果不空，则查询订单的创建时间大于此时间，默认为0）
	 * @apiParam {long} endScore 截至时间（如果不空， 则订单的创建时间小于此时间，默认为0，表示不加此限制条件）
	 *
	 * @apiSuccess {List} consultOrderVO 咨询订单详情
	 * @apiSuccess {String} consultOrderVO.id 咨询订单Id
	 * @apiSuccess {int} consultOrderVO.userId 用户Id（下单人）
	 * @apiSuccess {String} consultOrderVO.name 用户姓名（下单人）
	 * @apiSuccess {String} consultOrderVO.headImg 用户头像（下单人）
	 * @apiSuccess {String} consultOrderVO.patientName 患者姓名（病人）
	 * @apiSuccess {int} consultOrderVO.sex 患者性别（病人）（1男，2女 3 保密）
	 * @apiSuccess {int} consultOrderVO.age 患者年龄（病人）
	 * @apiSuccess {long} consultOrderVO.createTime 订单创建时间
	 * @apiSuccess {long} consultOrderVO.appointTime 预约开始时间
	 * @apiSuccess {long} consultOrderVO.endTime 预约结束时间
	 * @apiSuccess {String} consultOrderVO.diseaseDesc 病人病情描述
	 * @apiSuccess {String} consultOrderVO.telephone 病人联系方式
	 * @apiSuccess {String} consultOrderVO.talkState 通话状态（0：未开始；1：通话中；2、已完成；）
	 * @apiSuccess {int} consultOrderVO.orderId 该服务订单对应的真正的订单的Id
	 * @apiSuccess {String} consultOrderVO.doctor 医生信息
	 * @apiSuccess {String} consultOrderVO.doctor.id 医生用户Id
	 * @apiSuccess {String} consultOrderVO.doctor.name 医生姓名
	 * @apiSuccess {String} consultOrderVO.doctor.tel 医生电话
	 * @apiSuccess {String} consultOrderVO.doctor.headImg 医生头像
	 * @apiSuccess {String} consultOrderVO.doctor.troubleFree 免打扰（1：正常，2：免打扰）
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 */
	@RequestMapping(value = "/waitOrderList")
	public JSONMessage waitOrderList(Integer count, Long startScore,
			Long endScore) {
		if (count == null) {
			count = 20;
		}
		if (startScore == null) {
			startScore = 0L;
		}
		if (endScore == null) {
			endScore = 0L;
		}
		return JSONMessage.success(null,
				guideService.waitOrderList(count, startScore, endScore));
	}

	/**
	 * @api {get} /guide/orderList 导医接单记录（导医端）
	 * @apiVersion 1.0.0
	 * @apiName orderList
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 历史接单记录（导医端）
	 * @apiParam {String} access_token token
	 * @apiParam {long} startTime
	 *           时间戳，服务开始时间小于此时间（获取最新数据传0或者空，获取历史数据传入最后那条记录的startTime）
	 * @apiParam {int} count 一次取出数据的条数，默认20条
	 *
	 * @apiSuccess {List} data 返回数据List
	 * @apiSuccess {String} data.day 接单日期（格式如：2015-10-15）
	 * @apiSuccess {int} data.count 当天接单数量
	 * @apiSuccess {List} data.orderList 明细数据List
	 * @apiSuccess {int} data.orderList.userId 用户Id（下单人）
	 * @apiSuccess {String} data.orderList.headImg 用户头像（下单人）
	 * @apiSuccess {String} data.orderList.name 用户姓名（下单人）
	 * @apiSuccess {int} data.orderList.age 患者年龄（病人）
	 * @apiSuccess {int} data.orderList.sex 患者性别（病人）
	 * @apiSuccess {String} data.orderList.patientName 患者姓名（病人）
	 * @apiSuccess {String} data.orderList.telephone 病人电话
	 * @apiSuccess {String} data.orderList.diseaseDesc 病情描述
	 * @apiSuccess {String} data.orderList.createTime 订单创建时间
	 * @apiSuccess {String} data.orderList.startTime 导医接单时间（开始服务时间）
	 * @apiSuccess {String} data.orderList.appointTime 预约开始时间
	 * @apiSuccess {String} data.orderList.endTime 预约结束时间
	 * @apiSuccess {String} data.orderList.groupId 会话Id
	 * @apiSuccess {String} data.orderList.id 服务咨询订单Id
	 * @apiSuccess {String} data.orderList.msgId 该订单对应最开始的消息Id
	 * @apiSuccess {int} data.orderList.orderId 对应的订单的Id
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 */
	@RequestMapping(value = "/orderList")
	public JSONMessage finishOrderList(Integer userId, Long startTime,
			Integer count) {
		// 1、导医获取未开始服务状态的订单
		if (userId == null) {
			userId = ReqUtil.instance.getUserId();
		}
		return JSONMessage.success(null,
				guideService.orderList(userId, startTime, count));
	}

	/**
	 * @api {get} /guide/endService 结束服务
	 * @apiVersion 1.0.0
	 * @apiName endService
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 结束服务
	 * @apiParam {String} access_token token
	 * @apiParam {String} gid 会话Id
	 *
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 */
	@RequestMapping(value = "/endService")
	public JSONMessage endService(@RequestParam String gid) throws HttpApiException {
		// 1、结束服务
		String orderId = guideService.getOrderIdByGid(gid);
		return JSONMessage.success(null, guideService.endService(orderId));
	}

	/**
	 * @api {get} /guide/appointTime 发送预约时间
	 * @apiVersion 1.0.0
	 * @apiName appointTime
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 发送预约时间
	 * @apiParam {String} access_token token
	 * @apiParam {String} gid 会话Id
	 * @apiParam {String} type 卡片类型
	 * @apiParam {Integer} packId 套餐Id（可空）
	 * @apiParam {Long} startTime 预约开始时间
	 * @apiParam {Long} endTime 预约结束时间(可空)
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 */
	@RequestMapping(value = "/appointTime")
	public JSONMessage sendAppointTime(@RequestParam String gid,
			Integer packId, Long startTime, Long endTime, String type) throws HttpApiException {
		// 1、导医向患者发送图文消息
		// 2、创建订单（Order）----待支付
		/*if (endTime == null && startTime != null) {
			endTime = startTime + 30 * 60 * 1000L;
		}*/
		/**
		 * version 1 修改之前的接口
		 */
		/*return JSONMessage
				.success(null, guideService.appointTime(gid, packId, startTime,endTime, type));*/
		/**
		 * version 2 修改之后的接口 2016年8月1日14:56:10
		 */
		guideService.sendDoctorCard2Patient(gid, packId);
		return JSONMessage.success();
	}

	/**
	 * @api {get} /guide/doctorInfo 查看医生信息（预约时间）
	 * @apiVersion 1.0.0
	 * @apiName doctorInfo
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 查看医生信息（预约时间） ---DoctorTimeVO （修复了免打扰字段troubleFree错误的问题）
	 * @apiParam {String} access_token token
	 * @apiParam {String} gid 会话Id（如果医生Id为空则根据会话查找当前订单对应的医生）
	 * @apiParam {Integer} doctorId 医生Id（如果医生Id不为空，则优先查找doctorId）
	 * 
	 * @apiSuccess {String} userId 医生id
	 * @apiSuccess {String} doctorNum 医生号
	 * @apiSuccess {String} name 医生姓名
	 * @apiSuccess {String} telephone 医生手机
	 * @apiSuccess {Number} sex 医生性别
	 * @apiSuccess {String} headImg 医生头像
	 * @apiSuccess {String} hospital 医生所在医院
	 * @apiSuccess {String} departments 医生所在科室
	 * @apiSuccess {String} title 医生职称
	 * @apiSuccess {String} troubleFree 医生设置：免打扰（1：正常，2：免打扰）
	 * @apiSuccess {List} packList 套餐(只包含电话咨询套餐)
	 * @apiSuccess {String} packList.pack.id 套餐Id
	 * @apiSuccess {String} packList.pack.name 套餐名称
	 * @apiSuccess {Long} packList.pack.price 套餐价格（单位：元）
	 * @apiSuccess {Integer} packList.pack.timeLimit 套餐服务时长（单位：分钟）
	 * @apiSuccess {List} timeList 可预约时间
	 * @apiSuccess {Long} timeList.start 可预约时间:开始时间
	 * @apiSuccess {Long} timeList.end 可预约时间:截至时间
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 */
	@RequestMapping(value = "/doctorInfo")
	public JSONMessage doctorInfo(String gid, Integer doctorId) {
		return JSONMessage
				.success(null, guideService.doctorInfo(gid, doctorId));
	}

	/**
	 * @api {get} /guide/addDocTime 添加医生可预约时间
	 * @apiVersion 1.0.0
	 * @apiName addDocTime
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 添加医生可预约时间
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生Id（）
	 * @apiParam {Long} startTime 预约开始时间
	 * @apiParam {Long} endTime 预约结束时间（可空）
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 */
	@RequestMapping(value = "/addDocTime")
	public JSONMessage addDocTime(@RequestParam Integer doctorId,
			Long startTime, Long endTime) {
		return JSONMessage.success(null,
				guideService.addDocTime(doctorId, startTime, endTime));
	}

	/**
	 * @api {get} /guide/addDocRemark 添加医生备注信息
	 * @apiVersion 1.0.0
	 * @apiName addDocTime
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 添加医生可预约时间
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生Id（）
	 * @apiParam {String} guideId 导医id
	 * @apiParam {String} guideName 导医名称
	 * @apiParam {String} remark 备注信息
	 * @apiAuthor 姜宏杰
	 * @date 2016年1月20日09:46:18
	 */
	@RequestMapping(value = "/addDocRemark")
	public JSONMessage addDocRemark(@RequestParam Integer doctorId,
			String remark, String guideId, String guideName) {
		return JSONMessage
				.success(null, guideService.addDocRemark(doctorId, remark,
						guideId, guideName));
	}

	/**
	 * @api {get} /guide/removeDocTime 删除医生可预约时间
	 * @apiVersion 1.0.0
	 * @apiName removeDocTime
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 删除医生可预约时间
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生Id
	 * @apiParam {Long} startTime 预约开始时间
	 * @apiParam {Long} endTime 预约结束时间（可空）
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 */
	@RequestMapping(value = "/removeDocTime")
	public JSONMessage removeDocTime(@RequestParam Integer doctorId,
			Long startTime, Long endTime) {
		return JSONMessage.success(null,
				guideService.removeDocTime(doctorId, startTime, endTime));
	}

	/**
	 * @api {get} /guide/mySchedule 我的日程（已废弃）
	 * @apiVersion 1.0.0
	 * @apiName mySchedule
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 删除医生可预约时间（已废弃，请使用/pack/orderExpand/getSchedule）
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} userId 导医（用户）Id
	 * @apiParam {Long} date 查询日期
	 * @apiSuccess {List} consultOrderVO
	 *             返回会话列表对象(详细结构见http://192.168.3.7:8091/apidoc
	 *             /#api-咨询订单（导医）-waitOrderList)
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 * @deprecated
	 * @see OrderExpandController#getDocSchedule(com.dachen.health.pack.schedule.entity.vo.ScheduleParam)
	 */
	@RequestMapping(value = "/mySchedule")
	public JSONMessage myScheduleList(@RequestParam Integer userId, Long date) {
		if (date == null || date.longValue() == 0) {
			date = System.currentTimeMillis();
		}
		// return JSONMessage.success(null,guideService.myScheduleList(new
		// Date(date), userId));
		return JSONMessage.failure("此接口已废弃，请使用/pack/orderExpand/getSchedule");
	}

	/**
	 * @api {get} /guide/orderDisease 导医会话---查看病情详情
	 * @apiVersion 1.0.0
	 * @apiName orderDisease
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 查看病情详情--OrderDiseaseVO
	 * @apiParam {String} access_token token
	 * @apiParam {String} gid 会话(导医和患者)Id
	 * @apiParam {String} orderId
	 *           电话咨询服务订单Id(orderId和gid不能同时为空。都不为空的时候以orderId为准)
	 * 
	 * @apiSuccess {String} orderId 电话咨询服务订单Id
	 * @apiSuccess {String} diseaseDesc 病情描述
	 * @apiSuccess {String} seeDoctorMsg 就诊情况
	 * @apiSuccess {String} isSeeDoctor "true" 或者 "false"
	 * @apiSuccess {List} imgStringPath 病情图
	 * @apiSuccess {Object} orderVo 病人基本信息详情
	 * @apiSuccess {Integer} orderVo.userId 用户Id
	 * @apiSuccess {Integer} orderVo.patientId 病人Id
	 * @apiSuccess {Integer} orderVo.sex 病人性别
	 * @apiSuccess {Integer} orderVo.relation 与用户关系
	 * @apiSuccess {Integer} orderVo.age 病人年龄
	 * @apiSuccess {String} orderVo.patientName 病人姓名
	 * @apiSuccess {String} orderVo.telephone 病人手机号码
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 */
	@RequestMapping(value = "/orderDisease")
	public JSONMessage findOrderDisease(String gid, String orderId) {
		if (orderId == null || orderId.trim().length() == 0) {
			if (gid == null || gid.trim().length() == 0) {
				throw new ServiceException("参数错误,gid和orderId不能同时为空。");
			}
			orderId = guideService.getOrderIdByGid(gid);
		}
		return JSONMessage
				.success(null, guideService.findOrderDisease(orderId));
	}

	/**
	 * @api {get} /guide/findOrderDiseaseAndRemark 导医会话---查看病情详情（将患者备注与患者信息合二为一）
	 * @apiVersion 1.0.1
	 * @apiName findOrderDiseaseAndRemark
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 查看病情详情--OrderDiseaseVO
	 * @apiParam {String} access_token token
	 * @apiParam {String} gId 会话(导医和患者)Id
	 * @apiParam {String} orderId
	 *           电话咨询服务订单Id(orderId和gid不能同时为空。都不为空的时候以orderId为准)
	 * @apiParam {Integer} userId 患者用户id
	 * 
	 * @apiSuccess {String} orderId 电话咨询服务订单Id
	 * @apiSuccess {String} diseaseDesc 病情描述
	 * @apiSuccess {List} imgStringPath 病情图
	 * @apiSuccess {Object} orderVo 病人基本信息详情
	 * @apiSuccess {Integer} orderVo.userId 用户Id
	 * @apiSuccess {Integer} orderVo.patientId 病人Id
	 * @apiSuccess {Integer} orderVo.sex 病人性别
	 * @apiSuccess {Integer} orderVo.relation 与用户关系
	 * @apiSuccess {Integer} orderVo.age 病人年龄
	 * @apiSuccess {String} orderVo.patientName 病人姓名
	 * @apiSuccess {String} orderVo.telephone 病人手机号码
	 * @apiSuccess {String} orderVo.remark 患者信息备注
	 * @apiAuthor 姜宏杰
	 * @date 2016年1月13日10:38:57
	 */
	@RequestMapping(value = "/findOrderDiseaseAndRemark")
	public JSONMessage findOrderDiseaseAndRemark(Integer userId, String gId,
			String orderId) throws HttpApiException {
		if (orderId == null || orderId.equals("0")) {
			if (gId == null || gId.trim().length() == 0) {
				if (userId == null) {
					throw new ServiceException(
							"参数错误,userId、gId和orderId不能同时为空。请检查输入参数是否正确！！！");
				}
			}
			orderId = guideService.getOrderIdByGid(gId);
			return JSONMessage.success(null,
					guideService.findOrderDiseaseAndRemarks(orderId, userId));
		} else {
			OrderParam orderParam = new OrderParam();
			orderParam.setOrderId(Integer.valueOf(orderId));
			OrderDetailVO orderDetail = orderService.detail(orderParam,
					ReqUtil.instance.getUser());
			User user = userManager.getRemindVoice(userId);
			orderDetail.setUser(user);
			return JSONMessage.success(null, orderDetail);
		}
	}

	/**
	 * @api {get} /guide/updateOrderDisease 导医会话---修改病情详情
	 * @apiVersion 1.0.0
	 * @apiName updateOrderDisease
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 修改病情详情
	 * @apiParam {String} access_token token
	 * @apiParam {String} orderId 服务订单Id
	 * @apiParam {String} diseaseDesc 病情描述
	 * @apiParam {List} imgStringPath 病情图
	 * @apiParam {String} seeDoctorMsg 就诊情况
	 * @apiParam {String} isSeeDoctor 是否就诊 "false" 没有 "true" 有 注意 这里的取值必须是对应的字段串
	 * 
	 * @apiAuthor 成伟
	 * @date 2015年10月8日
	 */
	@RequestMapping(value = "/updateOrderDisease")
	public Object updateOrderDisease(OrderDiseaseVO param) throws HttpApiException {
		return JSONMessage
				.success(null, guideService.updateOrderDisease(param));
	}

	/*
	 * 测试使用
	 */
	// @RequestMapping(value = "/endPay")
	// public JSONMessage endPay(@RequestParam Integer orderId)
	// {
	// return JSONMessage.success(null,guideService.endPay(orderId));
	// }

	/**
	 * @api {get} /guide/scheduleTime 我的日程记录（已废弃）
	 * @apiVersion 1.0.0
	 * @apiName scheduleTime
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 
	 *                 根据查询日期查询当月的每天的是否存在日程安排（已废弃，请使用/pack/orderExpand/scheduleTime
	 *                 ）
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {String} searchDate 查询日期 yyyy-MM-dd
	 * @apiSuccess {List} data 日程记录
	 * @apiSuccess {Integer} data.dayNum 日期（天）
	 * @apiSuccess {Integer} data.isTrue 是否 0:否，1：有
	 * 
	 * @apiAuthor 成伟
	 * @date 2015年10月21日
	 * @deprecated
	 * @see OrderExpandController#getScheduleTime(com.dachen.health.pack.schedule.entity.vo.ScheduleParam)
	 */
	@RequestMapping(value = "/scheduleTime")
	public JSONMessage scheduleTime(@RequestParam String searchDate) {
		UserSession session = ReqUtil.instance.getUser();
		// return
		// JSONMessage.success(null,guideService.getScheduleTime(searchDate,
		// session));
		return JSONMessage.failure("此接口已废弃，请使用/pack/orderExpand/scheduleTime");
	}

	/**
	 * @api {[get,post]} /guide/getOrders 我的订单
	 * @apiVersion 1.0.0
	 * @apiName getOrders
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 我的订单（导医）
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} userId 导医ID
	 * @apiParam {Integer} recordStatus 咨询记录状态（0全部、1未填写、2待确定、3已确定）
	 * @apiParam {Integer} pageIndex 查询页，从0开始
	 * @apiParam {Integer} pageSize 每页显示条数，不传默认15条
	 * 
	 * @apiSuccess {Integer} orderId 订单id
	 * @apiSuccess {Long} appointTime 预约时间
	 * @apiSuccess {String} topPath 医生头像
	 * @apiSuccess {String} doctorName 医生名字
	 * @apiSuccess {String} telephone 医生电话
	 * @apiSuccess {Integer} recordStatus 1未填写、2待确定、3已确定
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年11月24日
	 */
	@RequestMapping("/getOrders")
	public JSONMessage getOrders(OrderParam param) {
		return JSONMessage.success(null, guideService.getOrders(param));
	}

	/**
	 * @api {[get,post]} /guide/confirm 确定
	 * @apiVersion 1.0.0
	 * @apiName confirm
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 确定
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单ID
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年11月24日
	 */
	@RequestMapping("/confirm")
	public JSONMessage confirm(Integer orderId) throws HttpApiException {
		guideService.handleConfirm(orderId);
		return JSONMessage.success(null);
	}

	@RequestMapping("/upgrade")
	public JSONMessage upgrade() {
		guideService.dataUpgrade();
		return JSONMessage.success(null);
	}

	/**
	 * @api {[get,post]} /guide/getConsultOrderDoctorList 查询患者的医生团队
	 * @apiVersion 1.0.0
	 * @apiName getConsultOrderDoctorList
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 查询患者的医生团队
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生ID
	 * @apiParam {String} gid 会话ID
	 * @apiSuccess {int} state 8未预约 2 待支付
	 * @apiSuccess {Long} appointTime 预约时间
	 * @apiSuccess {String} topPath 医生头像
	 * @apiSuccess {String} doctorName 医生名字
	 * @apiSuccess {String} telephone 医生电话
	 * @apiSuccess {Integer} recordStatus 1未填写、2待确定、3已确定
	 * 
	 * @apiAuthor 姜宏杰
	 * @date 2016年1月20日14:28:47
	 */
	@RequestMapping("/getConsultOrderDoctorList")
	public JSONMessage getConsultOrderDoctorList(String gid) {
		// 通过咨询订单id去查询患者信息
		OrderCache orderCache = iGuideDAO.getOrderCacheByGroup(gid);
		String id = orderCache.getId();
		ConsultOrderPO consultOrderPO = iGuideDAO.getConsultOrderPO(id);
		if (null != consultOrderPO && consultOrderPO.getUserId() == null) {
			throw new ServiceException("患者信息不存在！！！");
		}
		return JSONMessage.success(null, guideService.getConsultOrderDoctor(
				consultOrderPO.getDoctorId(), id, consultOrderPO.getUserId()));
	}

	/**
	 * @api {get} /guide/sendOnWaiterMsg 约不到医生，继续等待接口
	 * @apiVersion 1.0.0
	 * @apiName sendOnWaiterMsg
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 约不到医生，继续等待接口
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {String} id 咨询订单Id
	 * @apiParam {String} groupId 会话组id
	 * @apiSuccess {Integer} resultCode 1 成功
	 * @apiAuthor liwei
	 * @date 2016年1月19日
	 */
	@RequestMapping(value = "/sendOnWaiterMsg")
	public JSONMessage sendOnWaiterMsg(String groupId, String orderId) throws HttpApiException {
		guideService.sendOnWaiterMsg(groupId, orderId);
		return JSONMessage.success();
	}

	/**
	 * @api {get} /guide/sendCommendMsg 请导医帮忙推荐接口
	 * @apiVersion 1.0.0
	 * @apiName sendOnWaiterMsg
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 请导医帮忙推荐接口
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {String} groupId 会话组id
	 * @apiParam {String} id 咨询订单Id
	 * @apiSuccess {Integer} resultCode 1 成功
	 * @apiAuthor liwei
	 * @date 2016年1月19日
	 */
	@RequestMapping(value = "/sendCommendMsg")
	public JSONMessage sendCommendMsg(String groupId, String orderId) throws HttpApiException {
		guideService.sendCommendMsg(groupId, orderId);
		return JSONMessage.success();
	}

	/**
	 * @api {[get,post]} /guide/sendCardEvent .预约不到医生
	 * @apiVersion 1.0.0
	 * @apiName sendCardEvent
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 预约不到医生！！
	 * @apiParam {String} access_token token
	 * @apiParam {String} userId 用户ID
	 * @apiParam {String} recommendDoctorId 医生id
	 * @apiParam {String} type
	 *           指令类型-----type=1:联系不上医生；type=2：医生没时间；type=3：通知已有推荐医生
	 * @apiParam {String} guideOrderId 咨询订单ID
	 * @apiAuthor 姜宏杰
	 * @date 2016年1月20日14:28:47
	 */
	@RequestMapping("/sendCardEvent")
	public JSONMessage sendCardEvent(String guideOrderId, String userId,Integer recommendDoctorId,
			String type) throws HttpApiException {
		// 通过咨询订单id去查询患者信息
		Map<String, Object> mp = new HashMap<String, Object>();
		mp = doctorService.getIntro(recommendDoctorId);
		OrderCache orderCache = iGuideDAO.getOrderCacheByGroup(guideOrderId);
		String id = orderCache.getId();
		ConsultOrderPO consultOrderPO = iGuideDAO.getConsultOrderPO(id);
		if (null != consultOrderPO && consultOrderPO.getUserId() == null) {
			throw new ServiceException("患者信息不存在！！！");
		}
		businessServiceMsg.sendEventForGuide(id,
				String.valueOf(consultOrderPO.getUserId()), type,String.valueOf(mp.get("doctorName")));
		// 点击之后在给导医发送提醒信息
		if("1".equals(type)){
			businessServiceMsg.sendNotifyMsgToUser(String.valueOf(consultOrderPO.getGuideId()),guideOrderId, mp.get("doctorName")+"医生联系不上，等待患者答复是否继续等待");
		}else if("2".equals(type)){
			businessServiceMsg.sendNotifyMsgToUser(String.valueOf(consultOrderPO.getGuideId()),guideOrderId, mp.get("doctorName")+"医生不接受预约，等待患者答复是否找其他医生");
		}
		return JSONMessage.success();
	}

	/**
	 * @api {get} /guide/findDoctors 查找医生
	 * @apiVersion 1.0.0
	 * @apiName findDoctors
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 请导医帮忙推荐接口
	 * @apiParam {String} access_token token
	 * @apiParam {String} groupId 会话组id
	 * @apiParam {String} id 咨询订单Id
	 * @apiSuccess {Integer} resultCode 1 成功
	 * @apiAuthor liwei
	 * @date 2016年1月19日
	 */

	/**
	 * @api {get} /guide/findDoctors 查找医生(APP端)
	 * @apiVersion 1.0.0
	 * @apiName findDoctors
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 请导医帮忙推荐接口(APP端)
	 * @apiParam {String} access_token token
	 * @apiParam {String} orderId 导医订单id
	 * @apiParam {String} isCity "true" 按照城市查询 "false" 不按照城市查询
	 * @apiParam {String} isHospital "true" 按照医院查询 "false" 不按照医院查询
	 * @apiParam {String} isTitle "true" 按照职称查询 "false" 不按照职称查询
	 * @apiParam {Integer} pageIndex 第几页 默认从0 页开始
	 * @apiParam {Integer} pageSize 每页的大小
	 * 
	 * @apiSuccess {Integer} pageCount 总页数
	 * @apiSuccess {Integer} pageIndex 页数
	 * @apiSuccess {Integer} pageSize 每页的大小
	 * @apiSuccess {Integer} total 总记录数
	 * @apiSuccess {Object[]} pageData 数据集合
	 * @apiSuccess {String} pageData.doctorId 医生id
	 * @apiSuccess {String} pageData.name 医生名字
	 * @apiSuccess {String} pageData.hospital 医院名称
	 * @apiSuccess {String} pageData.skill 擅长
	 * @apiSuccess {String} pageData.departments 医生所属科室
	 * @apiSuccess {String} pageData.headPicFileName 医生头像
	 * 
	 * @apiAuthor liwei
	 * @date 2016年1月19日
	 */
	@RequestMapping(value = "/findDoctors")
	public JSONMessage findDoctors(String orderId, String isCity,
			String isHospital, String isTitle, Integer pageIndex,
			Integer pageSize) {
		PageVO page = guideService.fingDoctors(orderId,
				Boolean.valueOf(isCity), Boolean.valueOf(isHospital),
				Boolean.valueOf(isTitle), pageIndex, pageSize);
		return JSONMessage.success(page);
	}

	/**
	 * @api {get} /guide/sendDoctorCard 请导医预约接口
	 * @apiVersion 1.0.0
	 * @apiName sendDoctorCard
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription sendDoctorCard
	 * @apiParam {String} access_token token
	 * @apiParam {String} groupId 会话组id
	 * @apiParam {String} doctorId 新的医生id
	 * 
	 * @apiParam {String} id 咨询订单Id
	 * @apiSuccess {Integer} resultCode 1 成功
	 * @apiAuthor liwei
	 * @date 2016年1月19日
	 */
	@RequestMapping(value = "/sendDoctorCard")
	public JSONMessage sendDoctorCard(String groupId, String orderId,
			Integer doctorId) throws HttpApiException {
		guideService.sendDoctorCard(groupId, orderId, doctorId);

		return JSONMessage.success();
	}

	/**
	 * @api {get} /guide/findDoctorsForWeb 查找医生(Web端)
	 * @apiVersion 1.0.0
	 * @apiName findDoctorsForWeb
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 请导医帮忙推荐接口(Web端)
	 * @apiParam {String} access_token token
	 * @apiParam {String} guideOrderId 会话id
	 * @apiParam {String} isCity "true" 按照城市查询 "false" 不按照城市查询
	 * @apiParam {String} isHospital "true" 按照医院查询 "false" 不按照医院查询
	 * @apiParam {String} isTitle "true" 按照职称查询 "false" 不按照职称查询
	 * @apiParam {Integer} pageIndex 第几页 默认从0 页开始
	 * @apiParam {Integer} pageSize 每页的大小
	 * 
	 * @apiSuccess {Integer} pageCount 总页数
	 * @apiSuccess {Integer} pageIndex 页数
	 * @apiSuccess {Integer} pageSize 每页的大小
	 * @apiSuccess {Integer} total 总记录数
	 * @apiSuccess {Object[]} pageData 数据集合
	 * @apiSuccess {String} pageData.doctorId 医生id
	 * @apiSuccess {String} pageData.name 医生名字
	 * @apiSuccess {String} pageData.hospital 医院名称
	 * @apiSuccess {String} pageData.skill 擅长
	 * @apiSuccess {String} pageData.departments 医生所属科室
	 * @apiSuccess {String} pageData.headPicFileName 医生头像
	 * @apiSuccess {String} pageData.cureNum 就诊量
	 * 
	 * @apiAuthor liwei
	 * @date 2016年1月19日
	 */
	@RequestMapping(value = "/findDoctorsForWeb")
	public JSONMessage findDoctorsForWeb(String guideOrderId, String isCity,
			String isHospital, String isTitle, Integer pageIndex,
			Integer pageSize) {
		if (StringUtils.isEmpty(guideOrderId)) {
			throw new ServiceException("订单id不能够为空！");
		}
		OrderCache orderCache = iGuideDAO.getOrderCacheByGroup(guideOrderId);
		String orderId = orderCache.getId();

		PageVO page = guideService.fingDoctors(orderId,
				Boolean.valueOf(isCity), Boolean.valueOf(isHospital),
				Boolean.valueOf(isTitle), pageIndex, pageSize);
		return JSONMessage.success(page);
	}
	
	/**
	 * @api {get} /guide/findDoctorsFromKeyWord 查找医生(Web端)  根据搜索框输入的参数值查询整个平台的医生信息
	 * @apiVersion 1.0.0
	 * @apiName findDoctorsFromKeyWord
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 请导医帮忙推荐接口(Web端)--输入框传入关键字去查找医生
	 * @apiParam {String} access_token token
	 * @apiParam {String} keyWord 关键字（医生姓名、医院、职称、擅长、科室查找医生信息）
	 * @apiParam {Integer} queryType 查询类型（1:预约类型 ， null ： 电话类型）
	 * @apiParam {Integer} pageIndex 第几页 默认从0 页开始
	 * @apiParam {Integer} pageSize 每页的大小
	 * 
	 * @apiSuccess {Integer} pageCount 总页数
	 * @apiSuccess {Integer} pageIndex 页数
	 * @apiSuccess {Integer} pageSize 每页的大小
	 * @apiSuccess {Integer} total 总记录数
	 * @apiSuccess {Object[]} pageData 数据集合
	 * @apiSuccess {String} pageData.doctorId 医生id
	 * @apiSuccess {String} pageData.name 医生名字
	 * @apiSuccess {String} pageData.hospital 医院名称
	 * @apiSuccess {String} pageData.skill 擅长
	 * @apiSuccess {String} pageData.departments 医生所属科室
	 * @apiSuccess {String} pageData.headPicFileName 医生头像
	 * @apiSuccess {String} pageData.cureNum 就诊量
	 * @apiSuccess {String} pageData.appointmentPrice 预约套餐价格
	 * @apiAuthor 姜宏杰 2016年2月18日15:39:25
	 * @date 2016年1月19日
	 */
	@RequestMapping(value = "/findDoctorsFromKeyWord")
	public JSONMessage findDoctorsFromKeyWord(String keyWord,Integer queryType ,Integer pageIndex,Integer pageSize) {
		keyWord = StringUtils.trim(keyWord);
		PageVO page = guideService.findDoctorByKeyWord(keyWord,queryType, pageIndex, pageSize);
		return JSONMessage.success(page);
	}
	
	/**
	 * @api {get} /guide/addDialogueImg 患者与导医在会话的过程当中将或者发送的图片记录下来
	 * @apiVersion 1.0.0
	 * @apiName addDialogueImg
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 患者与导医在会话的过程当中将或者发送的图片记录下来
	 * @apiParam {String} access_token token
	 * @apiParam {String} guideId 会话id
	 * @apiParam {Object[]} imgs 图片地址
	 * @apiParam {String} orderId 咨询订单id
	 * @apiAuthor 姜宏杰
	 * @date 2016年2月24日11:47:20
	 */
	@RequestMapping(value = "/addDialogueImg")
	public JSONMessage addDialogueImg(@RequestParam String guideId,String[] imgs, String orderId) {
		if("".equals(orderId)||null==orderId){
			throw new ServiceException("订单id不能为空！");
		}
		if (null==imgs) {
			throw new ServiceException("图片为空不可以保存！");
		}
		guideService.addDialogueImg(guideId, imgs, orderId);
		return JSONMessage.success(null);
	}
	/**
	 * @api {get} /guide/getDialogueImg 获取患者发送的IM图片
	 * @apiVersion 1.0.0
	 * @apiName getDialogueImg
	 * @apiGroup 咨询订单（导医）
	 * @apiDescription 获取患者发送的IM图片
	 * @apiParam {String} access_token token
	 * @apiParam {String} guideId 会话id
	 * @apiAuthor 姜宏杰
	 * @date 2016年2月24日11:47:20
	 */
	@RequestMapping(value = "/getDialogueImg")
	public JSONMessage getDialogueImg(@RequestParam String guideId) {
		OrderCache orderCache = iGuideDAO.getOrderCacheByGroup(guideId);
		String orderId = orderCache.getId();
		return JSONMessage.success(null,guideService.getDialogueImg(orderId));
	}
	/**
	 * @api {get} /guide/getGuideNoServiceOrder 获取导医待处理订单
	 * @apiVersion 1.0.0
	 * @apiName getGuideNoServiceOrder
	 * @apiGroup 导医平台
	 * @apiDescription 获取导医待处理订单
	 * @apiParam {String} access_token token
	 * @apiParam {String} guideId 会话id
	 * @apiParam {Integer} pageIndex 第几页 默认从0 页开始
	 * @apiParam {Integer} pageSize 每页的大小
	 * @apiParam {String} recordStatus 咨询记录的状态
	 * 
	 * @apiSuccess {Integer} pageCount 总页数
	 * @apiSuccess {Integer} pageIndex 页数
	 * @apiSuccess {Integer} pageSize 每页的大小
	 * @apiSuccess {Integer} total 总记录数
	 * @apiSuccess {Object[]} pageData 数据集合
	 * @apiAuthor 姜宏杰
	 * @date 2016年2月24日11:47:20
	 */
	@RequestMapping(value = "/getGuideNoServiceOrder")
	public JSONMessage getGuideNoServiceOrder(OrderParam param) {
		param.setUserId(ReqUtil.instance.getUserId());
		return JSONMessage.success(guideService.get8HourOrder(param));
	}
	
	/**
	 * @api {get} /guide/getGuideAlreadyServicedOrder 获取导医已经的处理订单
	 * @apiVersion 1.0.0
	 * @apiName getGuideAlreadyServicedOrder
	 * @apiGroup 导医平台
	 * @apiDescription 获取导医待处理订单
	 * @apiParam {String} access_token token
	 * @apiParam {String} guideId 会话id
	 * @apiParam {Integer} pageIndex 第几页 默认从0 页开始
	 * @apiParam {Integer} pageSize 每页的大小
	 * @apiParam {String} recordStatus 咨询记录的状态
	 * 
	 * @apiSuccess {Integer} pageCount 总页数
	 * @apiSuccess {Integer} pageIndex 页数
	 * @apiSuccess {Integer} pageSize 每页的大小
	 * @apiSuccess {Integer} total 总记录数
	 * @apiSuccess {Object[]} pageData 数据集合
	 * @apiAuthor 姜宏杰
	 * @date 2016年2月24日11:47:20
	 */
	@RequestMapping(value = "/getGuideAlreadyServicedOrder")
	public JSONMessage getGuideAlreadyServicedOrder(OrderParam param) {
		//（注意在创建咨询订单的时候Integer userId = ReqUtil.instance.getUserId();intance.setDoctorId(userId);是将操作对象存储在了doctorId里的）
		Integer userId = ReqUtil.instance.getUserId();
		param.setUserId(userId);
		return JSONMessage.success(guideService.getGuideAlreadyServicedOrder(param));
	}
	
	//*****************************关怀计划订单**************************************
	/**
	 * @api {get} /guide/heathWaitOrderList 获取等待接单列表（导医端）
	 * @apiVersion 1.0.0
	 * @apiName heathWaitOrderList
	 * @apiGroup 关怀订单（导医）
	 * @apiDescription 获取等待接单列表（导医端）
	 * @apiParam {String} access_token token
	 *
	 * @apiSuccess {String} careName 关怀名称
	 * @apiSuccess {patient} patient 患者信息
	 * @apiSuccess {String} careType 关怀类型
	 * @apiSuccess {Long} createTime 关怀创建时间
	 * @apiSuccess {Integer} orderId 订单id
	 * @apiSuccess {String} careTemplateId 关怀计划id
	 * @apiAuthor 姜宏杰
	 * @date 2016年3月11日18:59:59
	 */
	@RequestMapping(value = "/heathWaitOrderList")
	public JSONMessage heathWaitOrderList() throws HttpApiException {
		return JSONMessage.success(null,guideService.heathWaitOrderList());
	}
	
	/**
	 * @api {get} /guide/receiveCareOrder 导医接关怀计划订单（导医端）
	 * @apiVersion 1.0.0
	 * @apiName receiveCareOrder
	 * @apiGroup 关怀订单（导医）
	 * @apiDescription 导医接关怀计划订单（导医端）
	 * @apiParam {String}         access_token token
	 * @apiParam {String}         fromId       告警或者是求助id
	 * @apiParam {Integer}        orderId      订单id
	 * @apiParam {String}         careType     关怀类型
	 * @apiParam {String}         careTemplateId     关怀模板id
	 *
	 * @apiSuccess {String} careName 关怀名称
	 * @apiSuccess {patient} patient 患者信息
	 * @apiSuccess {String} careType 关怀类型
	 * @apiSuccess {Long} createTime 关怀创建时间
	 * @apiSuccess {Integer} orderId 订单id
	 * @apiSuccess {String} careTemplateId 关怀计划id
	 * @apiAuthor 姜宏杰
	 * @date 2016年3月11日18:59:59
	 */
	@RequestMapping(value = "/receiveCareOrder")
	public JSONMessage receiveCareOrder(HelpVO vo) {
		if("".equals(vo.getFromId())||null==vo.getFromId()){
			throw new ServiceException("帮助或者告警订单id为空");
		}
		if("".equals(vo.getOrderId())||null==vo.getOrderId()){
			throw new ServiceException("订单id不能为空");
		}
		if("".equals(vo.getCareType())||null==vo.getCareType()){
			throw new ServiceException("关怀类型不能为空");
		}
		if("".equals(vo.getCareTemplateId())||null==vo.getCareTemplateId()){
			throw new ServiceException("关怀模板不能为空");
		}
		return JSONMessage.success(null,guideService.receiveCareOrder(vo));
	}
	
	/**
	 * @api {get} /guide/getDoctorTeam 获取医生团队
	 * @apiVersion 1.0.0
	 * @apiName getDoctorTeam
	 * @apiGroup 关怀订单（导医）
	 * @apiDescription 获取医生团队
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 *
	 * @apiSuccess {User} user 人员信息
	 * @apiAuthor 姜宏杰
	 * @date 2016年3月11日18:59:59
	 */
	@RequestMapping(value = "/getDoctorTeam")
	public JSONMessage getDoctorTeam(Integer orderId) {
		if(orderId==0){
			throw new ServiceException("订单id不能为空");
		}
		return JSONMessage.success(null,guideService.getDoctorTeamByOrderId(orderId));
	}
	
	/**
	 * @api {get} /guide/getCareOrderDetail 关怀订单详情
	 * @apiVersion 1.0.0
	 * @apiName getCareOrderDetail
	 * @apiGroup 关怀订单（导医）
	 * @apiDescription 获取医生团队
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {String} careType 关怀订单类型 告警：warning 求助：help
	 * @apiParam {String} fromId 告警或者求助id
	 *
	 * @apiSuccess {String} helpMsg 求助内容
	 * @apiSuccess {String} careName 关怀名称
	 * @apiAuthor 姜宏杰
	 * @date 2016年3月11日18:59:59
	 */
	@RequestMapping(value = "/getCareOrderDetail")
	public JSONMessage getCareOrderDetail(Integer orderId,String fromId,String careType) throws HttpApiException {
		if(orderId==0&&careType==null){
			throw new ServiceException("订单id不能为空");
		}
		return JSONMessage.success(null,guideService.getCareOrderDetail(orderId, careType,fromId));
	}
	
	
	/**
	 * @api {get} /guide/updateCareOrder 标记求助或告警处理完成
	 * @apiVersion 1.0.0
	 * @apiName updateCareOrder
	 * @apiGroup 关怀订单（导医）
	 * @apiDescription 标记求助或告警处理完成
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {String} careType 关怀订单类型 告警：warning 求助：help
	 * @apiParam {String} id       接单记录id
	 *
	 * @apiSuccess {String} careName 关怀名称
	 * @apiAuthor 姜宏杰
	 * @date 2016年3月11日18:59:59
	 */
	@RequestMapping(value = "/updateCareOrder")
	public JSONMessage updateCareOrder(Integer orderId,String careType,String id) throws HttpApiException {
		if(orderId==0&&careType==null){
			throw new ServiceException("订单id不能为空");
		}
		if(StringUtil.isEmpty(id)){
			throw new ServiceException("接单记录id不能为空");
		}
		return JSONMessage.success(null,guideService.updateCareOrder(orderId, careType,id));
	}
	
	/**
	 * @api {get} /guide/getHandleCareOrder 查询正在处理中的订单
	 * @apiVersion 1.0.0
	 * @apiName getHandleCareOrder
	 * @apiGroup 关怀订单（导医）
	 * @apiDescription 查询正在处理中的订单
	 * @apiParam {String} access_token token
	 *
	 * @apiSuccess {String} careName 关怀名称
	 * @apiAuthor 姜宏杰
	 * @date 2016年3月11日18:59:59
	 */
	@RequestMapping(value = "/getHandleCareOrder")
	public JSONMessage getHandleCareOrder() throws HttpApiException {
		Integer userId = ReqUtil.instance.getUserId();
		return JSONMessage.success(null,guideService.getHandleCareOrder(userId));
	}
	//*************************导医咨询2.0版本**********************//
	
	
	/*博德嘉联导医API start*/
	
	/**
	 * @api {get} /guide/getAppointmentOrders 查询待处理的预约订单
	 * @apiVersion 1.0.0
	 * @apiName getAppointmentOrders
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 查询待处理的预约订单
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} status  订单状态可以不传
	 * @apiParam {Integer} pageIndex 页码数
	 * @apiParam {Integer} pageSize 条数
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {String} orderId	    订单id
	 * @apiSuccess {String} orderTime	下单时间
	 * @apiSuccess {String} doctorName	医生名
	 * @apiSuccess {String} title	职称
	 * @apiSuccess {String} departments	科室
	 * @apiSuccess {String} patientName	患者名称
	 * 
	 * @apiAuthor wangl
	 * @date 2016年4月27日16:40:22
	 */
	@RequestMapping(value = "/getAppointmentOrders")
	public JSONMessage getAppointmentOrders(Integer status ,Integer pageIndex,Integer pageSize){
		return JSONMessage.success(guideService.getAppointmentOrders(status,pageIndex,pageSize));
	}
	
	
	/**
	 * @api {get} /guide/getAppointmentDetail 查看预约订单详情
	 * @apiVersion 1.0.0
	 * @apiName getAppointmentDetail
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 查看预约订单详情
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id必传
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {String} orderId	   订单id
	 * @apiSuccess {String} orderNo	 订单号
	 * @apiSuccess {String} doctorId	医生id
	 * @apiSuccess {String} doctorName	以生命
	 * @apiSuccess {String} title	职称
	 * @apiSuccess {String} departments	科室
	 * @apiSuccess {String} doctorTel	医生手机号码
	 * @apiSuccess {String} headPicFileName	头像
	 * @apiSuccess {String} patientId	患者id
	 * @apiSuccess {String} patientName	患者名称
	 * @apiSuccess {String} patientTel	患者号码
	 * @apiSuccess {object} remark	   标签
	 * @apiSuccess {object} disease		疾病对象
	 * @apiSuccess {String[]} disease.diseaseImgs		疾病对象
	 * @apiSuccess {String} disease		疾病对象
	 * @apiSuccess {String} disease.diseaseDesc		疾病对象
	 * @apiSuccess {String} disease.diseaseInfo_now		现病史
	 * @apiSuccess {String} disease.diseaseInfo_old		既往史
	 * @apiSuccess {String} disease.familydiseaseInfo		家族史
	 * @apiSuccess {String} disease.menstruationdiseaseInfo		月经史
	 * @apiSuccess {String} disease.cureSituation		诊治情况
	 * @apiSuccess {String} disease.isSeeDoctor		是否就诊  false  没有  true  有
	 * 
	 * @apiAuthor wangl
	 * @date 2016年4月27日16:40:22
	 */
	@RequestMapping(value = "/getAppointmentDetail")
	public JSONMessage getAppointmentDetail(@RequestParam(required = true)Integer orderId) throws HttpApiException {
		return JSONMessage.success(guideService.getAppointmentDetail(orderId));
	}
	
	/**
	 * @api {get} /guide/submitAppointmentOrder 提交订单
	 * @apiVersion 1.0.0
	 * @apiName submitAppointmentOrder
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 预约订单提交
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id必传
	 * @apiParam {String} hospitalId 订单id必传
	 * @apiParam {Long} appointTime 时间
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * 
	 * @apiAuthor wangl
	 * @date 2016年4月27日16:40:22
	 */
	@RequestMapping(value = "/submitAppointmentOrder")
	public JSONMessage submitAppointmentOrder(@RequestParam(required = true)Integer orderId , 
											   String hospitalId ,
											   Long appointTime) throws HttpApiException {
		guideService.submitAppointmentOrder(orderId,hospitalId,appointTime);
		return JSONMessage.success();
	}
	
	/**
	 * @api {get} /guide/getGroupHospital 获取医院列表
	 * @apiVersion 1.0.0
	 * @apiName getGroupHospital
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 获取医院列表
	 * @apiParam {String} access_token token
	 * @apiParam {String} groupId  集团id 必传
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {String} id	          医院id
	 * @apiSuccess {String} name	医院名称
	 * @apiSuccess {String} lat		医院经度
	 * @apiSuccess {String} lng		医院维度
	 * 
	 * @apiAuthor wangl
	 * @date 2016年4月27日16:40:22
	 */
	@RequestMapping(value = "/getGroupHospital")
	public JSONMessage getGroupHospital(@RequestParam(required = true)String groupId){
		return JSONMessage.success(guideService.getGroupHospital(groupId));
	}
	
	
	/**
	 * @api {get} /guide/setGroupHospital 设置医院列表
	 * @apiVersion 1.0.0
	 * @apiName setGroupHospital
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 设置医院列表
	 * @apiExample {javascript} Example usage:
     *  传递一个参数data，以json格式，如下：
     *  String data="{'hospitalInfo':[{'id':'1','name':'aaa','lat':'85','lng':'1'},
     *                                {'id':'2','name':'bbb','lat':'15','lng':'2'}]}";
     *  
     * json格式各个字段的含义如下
	 * 
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {String} id  集团id 必传
	 * @apiParam {String} id  医院id(data参数)
	 * @apiParam {String} name 医院名称（data参数）
	 * @apiParam {String} lat  医院经度（data参数）
	 * @apiParam {String} lng  医院维度（data参数）
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * 
	 * @apiAuthor liming
	 * @date 2016年5月19日10:28:18
	 */
	
	
	@RequestMapping(value = "/setGroupHospital")
	public JSONMessage setGroupHospital(GroupParam param,String data) throws HttpApiException {
		GroupConfig group=JSONUtil.parseObject(GroupConfig.class, data);
		param.setConfig(group);
		guideService.setGroupHospital(param);
		return JSONMessage.success();
	}
	
	
	
	/**
	 * @throws HttpApiException 
	 * @api {get} /guide/setAppointmentInfo 集团开通或关闭预约套餐
	 * @apiVersion 1.0.0
	 * @apiName setAppointmentInfo
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 集团开通或关闭预约套餐
	 * @apiParam {String} access_token token
	 * @apiParam {String} groupId  集团id 必传
	 * @apiParam {Boolean} openAppointment  线下预约开关 必传
	 * @apiParam {Boolean} appointmentGroupProfit  集团抽成
	 * @apiParam {Boolean} appointmentParentProfit  上级抽成
	 * @apiParam {Boolean} appointmentMin  价格 范围小
	 * @apiParam {Boolean} appointmentMax  价格 范围大
	 * @apiParam {Boolean} appointmentDefault  价格默认值
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * 
	 * @apiAuthor wangl
	 * @date 2016年4月27日16:40:22
	 */
	@RequestMapping(value = "/setAppointmentInfo")
	public JSONMessage setAppointmentInfo(@RequestParam(required = true)String groupId,
										  @RequestParam(required = true)Boolean openAppointment,
										  Integer appointmentGroupProfit,
										  Integer appointmentParentProfit,
										  Integer appointmentMin,
										  Integer appointmentMax,
										  Integer appointmentDefault
										  ) throws HttpApiException{
		guideService.setAppointmentInfo(groupId,openAppointment,appointmentGroupProfit,appointmentParentProfit,appointmentMin,appointmentMax,appointmentDefault);
		return JSONMessage.success();
	}
	
	
	/**
	 * @api {get} /guide/getAppointmentInfo 获取集团的线下预约信息
	 * @apiVersion 1.0.0
	 * @apiName getAppointmentInfo
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 获取集团的线下预约信息
	 * @apiParam {String} access_token token
	 * @apiParam {String} groupId  集团id 必传
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {Integer} appointmentMin		预约价格范围 小 
	 * @apiSuccess {Integer} appointmentMax		预约价格范围 大
	 * @apiSuccess {Integer} appointmentDefault	预约价格默认值
	 * @apiSuccess {Boolean} openAppointment	是否开通现在预约
	 * @apiSuccess {Integer} appointmentGroupProfit	  集团抽成
	 * @apiSuccess {Integer} appointmentParentProfit 上级抽成
	 * 
	 * @apiAuthor wangl
	 * @date 2016年4月27日16:40:22
	 */
	@RequestMapping(value = "/getAppointmentInfo")
	public JSONMessage getAppointmentInfo(@RequestParam(required = true)String groupId){
		return JSONMessage.success(guideService.getAppointmentInfo(groupId));
	}
	

	/**
	 * @api {get} /guide/getHaveAppointmentListByDate 获取当前时间段每一天是否有预约的结果列表
	 * @apiVersion 1.0.0
	 * @apiName getHaveAppointmentListByDate
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 获取当前时间段每一天是否有预约的结果列表
	 * @apiParam {String} access_token token
	 * @apiParam {String} hospitalId  医院id 必传
	 * @apiParam {Long} date  起始日期时间戳 必传
	 * @apiParam {Integer}  doctorId  医生id,可选
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {Integer} dayNum	当月第几天
	 * @apiSuccess {Integer} week	周几
	 * @apiSuccess {Integer} isTrue	  是否有预约
	 * 
	 * @apiAuthor CQY
	 */
	@RequestMapping(value = "/getHaveAppointmentListByDate")
	public JSONMessage getHaveAppointmentListByDate(@RequestParam(required = true)String hospitalId, @RequestParam(required = true)Long date,Integer doctorId){
		OrderParam param=new OrderParam();
		List<Integer> orderStatusList=new ArrayList<Integer>();
		orderStatusList.add(3);
		orderStatusList.add(4);
		orderStatusList.add(6);
		param.setOrderStatusList(orderStatusList);
		//param.setOrderStatus(3);
		param.setOppointTime(date);
		param.setHospitalId(hospitalId);
		if(doctorId!=null){
			param.setDoctorId(doctorId);
		}
		param.setOrderType(9);
		return JSONMessage.success(guideService.getHaveAppointmentListByDate(param));
	}
	
	/**
	 * @api {get} /guide/getAppointmentListByCondition 按照条件多条件查询搜索订单
	 * @apiVersion 1.0.0
	 * @apiName getAppointmentListByCondition
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 按照条件多条件查询搜索订单
	 * @apiParam {String} access_token token
	 * @apiParam {String} hospitalId  医院id 必传
	 * @apiParam {String} doctorId  医生id 必传
	 * @apiParam {Long} oppointTime  预约日期时间戳 必传
	 * @apiParam {Integer} period 1早上；2下午；3晚上
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {Integer} count 预约个数
	 * @apiSuccess {List} orderList 订单集合
	 * @apiSuccess {Integer} orderList.orderId 订单ID
	 * @apiSuccess {String} orderList.offlineItemId 预约id
	 * @apiSuccess {Long} orderList.price  预约价格
	 * @apiSuccess {Integer} orderList.orderStatus 订单状态 （1：待预约；2：待支付；3：已支付；4：已完成；5：已取消）
	 * @apiSuccess {String} orderList.patientName 患者姓名
	 * @apiSuccess {Long} orderList.appointTime 预约时间
	 * @apiSuccess {String} orderList.telephone 患者电话
	 * @apiSuccess {String} orderList.topPath 患者头像
	 * @apiSuccess {Long} orderList.appointTime  预约时间
	 * @apiSuccess {Long} orderList.serviceBeginTime  实际服务开始时间
	 * @apiSuccess {String} orderList.remarks  备注
	 * 
	 * 
	 * @apiAuthor CQY
	 * 
	 */
	@RequestMapping(value = "/getAppointmentListByCondition")
	public JSONMessage getAppointmentListByCondition(OrderParam param){
		List<Integer> orderStatusList=new ArrayList<Integer>();
		orderStatusList.add(3);
		orderStatusList.add(4);
		orderStatusList.add(6);
		param.setOrderStatusList(orderStatusList);
		//param.setOrderStatus(3);
		param.setOrderType(9);
		return JSONMessage.success(guideService.getAppointmentListByCondition(param));
	}

	/**
	 * @api {get} /guide/getAppointmentPaidOrders 获取某一天的有支付预的约订单医生列表
	 * @apiVersion 1.0.0
	 * @apiName getAppointmentPaidOrders
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 获取某一天的有支付预的约订单医生列表
	 * @apiParam {String} access_token token
	 * @apiParam {String} hospitalId  医院id 必传
	 * @apiParam {String} groupId  集团id 必传
	 * @apiParam {Long} date  日期时间戳 必传
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {Integer} doctorId	医生id
	 * @apiSuccess {String} name	医生名
	 * @apiSuccess {String} headPicFileName	  头像
	 * @apiSuccess {Integer} count 预约个数
	 * @apiSuccess {String} departments 部门
	 * @apiSuccess {String} title 级别
	 * 
	 * @apiAuthor wangl
	 * @date 2016年6月6日15:29:34
	 */
	@RequestMapping(value = "/getAppointmentPaidOrders")
	public JSONMessage getAppointmentPaidOrders(@RequestParam(required = true)String hospitalId,
												@RequestParam(required = true)String groupId,
											    @RequestParam(required = true)Long date){
		return JSONMessage.success(guideService.getAppointmentPaidOrders(groupId,hospitalId,date));
	}
	

	
	/**
	 * @api {get} /guide/doctorOfflinesByDate 获取某一天的有支付预的约订单医生列表
	 * @apiVersion 1.0.0
	 * @apiName doctorOfflinesByDate
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 获取某一天的有支付预的约订单医生列表
	 * @apiParam {String} access_token token
	 * @apiParam {String} hospitalId  医院id 必传
	 * @apiParam {String} groupId  集团id 必传
	 * @apiParam {Long} date  日期时间戳 必传
	 * @apiParam {Integer}    period  1：上午、2：下午、3：晚上
	 * @apiParam {Integer}    pageSize   条数
	 * @apiParam {Integer}    pageIndex  页数
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {Integer} doctorId	医生id
	 * @apiSuccess {String} name	医生名
	 * @apiSuccess {String} headPicFileName	  头像
	 * @apiSuccess {Integer} totalCount 预约总数
	 * @apiSuccess {Integer} realCount 预约总数
	 * @apiSuccess {String} departments 部门
	 * @apiSuccess {String} title 级别
	 * @apiSuccess {List}   timeSegment 时间段列表
	 * @apiSuccess {Long}   timeSegment.beginTime 开始时间
	 * @apiSuccess {Long}   timeSegment.overTime  结束时间
	 * 
	 * @apiAuthor wangl
	 * @date 2016年6月6日15:29:34
	 */
	@RequestMapping(value = "/doctorOfflinesByDate")
	public JSONMessage doctorOfflinesByDate(@RequestParam(required = true)String hospitalId,
												@RequestParam(required = true)String groupId,
											    @RequestParam(required = true)Long date ,
											    @RequestParam(required = true)Integer period ,
											    Integer pageSize ,
												Integer pageIndex){
		return JSONMessage.success(guideService.doctorOfflinesByDate(groupId,hospitalId,date,period,pageSize,pageIndex));
	}
	
	/**
	 * @api {get} /guide/searchAppointmentOrderByKeyword 根据keyword搜索预约订单列表
	 * @apiVersion 1.0.0
	 * @apiName searchAppointmentOrderByKeyword
	 * @apiGroup 预约订单（导医）
	 * @apiDescription  根据keyword搜索预约订单列表
	 * @apiParam {String} access_token token
	 * @apiParam {String} keyword  关键字
	 * @apiParam {Integer} pageIndex 页码数
	 * @apiParam {Integer} pageSize 条数
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {Long} appointmentStart	预约开始时间
	 * @apiSuccess {Long} appointmentEnd	预约结束时间
	 * @apiSuccess {Long} serviceBeginTime	服务开始时间
	 * @apiSuccess {String} patientName	 患者姓名
	 * @apiSuccess {String} doctorName 医生姓名
	 * @apiSuccess {Integer} orderType 订单类型
	 * @apiSuccess {Integer} orderId 订单id
	 * 
	 * @apiAuthor wangl
	 * @date 2016年6月6日15:29:34
	 */
	@RequestMapping(value = "/searchAppointmentOrderByKeyword")
	public JSONMessage searchAppointmentOrderByKeyword( @RequestParam(required = true)String keyword ,
														Integer pageIndex,
														Integer pageSize){
		return JSONMessage.success(guideService.searchAppointmentOrderByKeyword(keyword,pageIndex,pageSize));
	}
	
	
	/**
	 * @api {get} /guide/getPatientAppointmentByCondition 查询医生某一天值班的被患者预约情况 按照早中晚分组
	 * @apiVersion 1.0.0
	 * @apiName getPatientAppointmentByCondition
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 查询医生某一天值班的被患者预约情况
	 * @apiParam {String} access_token token
	 * @apiParam {String} hospitalId  医院id 必传
	 * @apiParam {String} doctorId  医生id 必传
	 * @apiParam {Long} oppointTime  医生值班日期时间戳 必传
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {String}  1：上午、2：下午、3：晚上
	 * @apiSuccess {List} offlineItemList 订单集合
	 * @apiSuccess {String} offlineItemList.id  预约ID
	 * @apiSuccess {Integer} offlineItemList.doctorId 医生ID
	 * @apiSuccess {String} offlineItemList.hospitalId 医院ID
	 * @apiSuccess {Integer} offlineItemList.patientId 患者ID
	 * @apiSuccess {Integer} offlineItemList.orderId 订单ID
	 * @apiSuccess {Integer} offlineItemList.period 早中晚
	 * @apiSuccess {Integer} offlineItemList.week 周几
	 * @apiSuccess {Long} offlineItemList.startTime  开始时间
	 * @apiSuccess {Long} offlineItemList.endTime  结束时间
	 * @apiSuccess {Integer} offlineItemList.status  预约状态
	 * 
	 * @apiAuthor CQY
	 * 
	 */
	@RequestMapping(value = "/getPatientAppointmentByCondition")
	public JSONMessage getPatientAppointmentByCondition(OrderParam param){
		return JSONMessage.success(guideService.getPatientAppointmentByCondition(param));
	}

	
	/**
	 * @api {get} /guide/changeAppointmentTime 导医修改预约时间
	 * @apiVersion 1.0.0
	 * @apiName changeAppointmentTime 导医修改预约时间
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 导医修改预约时间
	 * @apiParam {String} access_token token
	 * @apiParam {String} offlineItemId  预约时间条目id 必传
	 * @apiParam {String} orderId        订单id 必传
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiAuthor wangl
	 * @date 2016年6月16日14:57:28
	 */
	@RequestMapping(value = "/changeAppointmentTime")
	public JSONMessage changeAppointmentTime(@RequestParam(required=true)String offlineItemId,
											 @RequestParam(required=true)Integer orderId) throws HttpApiException {
		guideService.changeAppointmentTime(offlineItemId,orderId);
		return JSONMessage.success();
	}
	
	
	/*------------------ 博德改版 -------------------- */
	
	/**
	 * @api {get} /guide/getDoctorOneDayOffline 获取医生某一天的排班列表
	 * @apiVersion 1.0.0
	 * @apiName getDoctorOneDayOffline
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 获取医生某一天的排班列表
	 * @apiParam {String} access_token token
	 * @apiParam {String} hospitalId  医院id 必传
	 * @apiParam {Long} date  日期时间戳
	 * @apiParam {Integer}  doctorId  医生id 必传
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {Map}     data	1 成功
	 * @apiSuccess {Integer} period	 1:早，2:午，3:晚
	 * @apiSuccess {Long} startTime	起始时间
	 * @apiSuccess {Long} endTime	结束时间
	 * @apiSuccess {String}  id	          结束时间
	 * @apiSuccess {String}  status	预约状态
	 * @apiExample {javascript} Example usage:
	 *          {   
	 *          	1=[{startTime=1234567890,endTime=1234567890},{startTime=1234567890,endTime=1234567890}] 
	 *              2=[{startTime=1234567890,endTime=1234567890},{startTime=1234567890,endTime=1234567890}] 
	 *              3=[{startTime=1234567890,endTime=1234567890},{startTime=1234567890,endTime=1234567890}] 
	 *          }
	 * 
	 * @apiAuthor wangl
	 * @date 2016年6月22日18:02:40
	 */
	@RequestMapping(value = "/getDoctorOneDayOffline")
	public JSONMessage getDoctorOneDayOffline(@RequestParam(required=true)String hospitalId,
											  @RequestParam(required=true)Long date,
											  @RequestParam(required=true)Integer doctorId){
		return JSONMessage.success(guideService.getDoctorOneDayOffline(hospitalId,date,doctorId));
	}
	
	
	
	/**
	 * @api {get} /guide/isTimeToAppointment 获取医生在某一时间段是否可预约
	 * @apiVersion 1.0.0
	 * @apiName isTimeToAppointment
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 获取医生在某一时间段是否可预约
	 * @apiParam {String} access_token token
	 * @apiParam {Long} startTime  预约的起始时间戳 必传
	 * @apiParam {Integer}  doctorId  医生id 必传
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {Map}     data	1 ：可预约 ， 2：不可预约
	 * 
	 * @apiAuthor wangl
	 * @date 2016年6月22日18:02:40
	 */
	@RequestMapping(value = "/isTimeToAppointment")
	public JSONMessage isTimeToAppointment(  @RequestParam(required=true)Long startTime,
											 @RequestParam(required=true)Integer doctorId){
		return JSONMessage.success(guideService.isTimeToAppointment(startTime,doctorId));
	}
	
	
	/**
	 * @api {get} /guide/takeAppointmentOrder 医生助手帮患者预约名医面对面订单
	 * @apiVersion 1.0.0
	 * @apiName takeAppointmentOrder
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 医生助手帮患者预约名医面对面订单
	 * @apiParam {String}   access_token token
	 * @apiParam {Object}   offlineItem     医生排班信息
	 * @apiParam {String}   offlineItem.id  医生预约排班id （可为空）
	 * @apiParam {Integer}  offlineItem.doctorId  医生id 
	 * @apiParam {String}   offlineItem.hospitalId  医院id
	 * @apiParam {Integer}  offlineItem.startTime  		开始时间
	 * @apiParam {Integer}  offlineItem.endTime  		结束时间
	 * 
	 * @apiParam {Object}   patient  	患者信息
	 * @apiParam {Integer}  patient.id  	患者id
	 * @apiParam {String}  patient.telephone  	电话
	 * @apiParam {String}   patient.userName  	患者姓名
	 * @apiParam {Integer}  patient.sex  		性别
	 * @apiParam {Long}     patient.birthday  	生日
	 * @apiParam {String}  patient.relation  	关系
	 * @apiParam {String}   patient.area  	所在区域
	 * @apiParam {String}  patient.idcard  	证件号码
	 * @apiParam {String}  patient.idtype  	类型
	 * @apiParam {String}  patient.height  	身高
	 * @apiParam {String}  patient.weight  	体重
	 * @apiParam {String}  patient.marriage  	婚姻
	 * @apiParam {String}  patient.professional  职业
	 * @apiParam {String}  patient.topPath  	头像
	 * 
	 * @apiParam {Object}  orderParam  			       病情信息
	 * @apiParam {String}  orderParam.diseaseDesc  病情
	 * @apiParam {String}  orderParam.isSee  是否就诊 "false" 没有   "true" 有
	 * @apiParam {String}  orderParam.seeDoctorMsg  诊治情况
	 * @apiParam {Long}    orderParam.price       价格
	 * @apiParam {String[]} orderParam.imagePaths 病情图
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * @apiSuccess {Map}     data	1 ：可预约 ， 2：不可预约
	 * 
	 * @apiAuthor wangl
	 * @date 2016年6月22日18:02:40
	 */
	@RequestMapping(value = "/takeAppointmentOrder")
	public JSONMessage takeAppointmentOrder(@RequestParam(required=true) String jsonString) throws HttpApiException {
		AppointmentOrderWebParams webParams = JSONUtil.parseObject(AppointmentOrderWebParams.class, jsonString);
		return JSONMessage.success(guideService.takeAppointmentOrder(webParams));
	}
	/*博德嘉联导医API end*/
	
	/**
	 * @throws HttpApiException 
	 * @api {POST} /guide/updateGroupConfigAndFee 集团财务设置
	 * @apiVersion 1.0.0
	 * @apiName setAppointmentInfo
	 * @apiGroup 预约订单（导医）
	 * @apiDescription 集团财务设置
	 * @apiParam  {String} access_token token
	 * @apiParam  {String} groupId  集团id 必传
	 * @apiParam  {Integer} type   必传 ,（修改|保存类型）类型 1、 名医面对面 2 、 图文咨询 3 、电话咨询 4、健康关怀 5、收费项  6、在线门诊 7、会诊
	 * @apiParam  {Boolean} openAppointment  线下预约开关 必传
	 * @apiParam  {Integer} appointmentGroupProfit  名医面对面集团抽成
	 * @apiParam  {Integer} appointmentParentProfit 名医面对面上级抽成
	 * @apiParam  {Integer}  appointmentMin  		名医面对面最低价
	 * @apiParam  {Integer}  appointmentMax  		名医面对面最高价
	 * @apiParam  {Integer}  appointmentDefault  	价格默认值
	 * 
	 * @apiParam  {Integer} textGroupProfit  		图文咨询 集团抽成比例
     * @apiParam  {Integer}  textParentProfit 		 图文咨询 上级抽成比例
     * @apiParam  {Integer}     textMin                     图文咨询最低价
     * @apiParam  {Integer}     textMax                     图文咨询最高价
     * 
     * @apiParam  {Integer}  phoneGroupProfit  			电话咨询 集团抽成比例
     * @apiParam  {Integer}  phoneParentProfit  电话咨询 上级抽成比例
     * @apiParam  {Integer}     phoneMin                    电话咨询最低价
     * @apiParam  {Integer}     phoneMax                    电话咨询最高价
     * 
     * @apiParam  {Integer}  carePlanGroupProfit  关怀计划 集团抽成比例
     * @apiParam  {Integer}  carePlanParentProfit 	关怀计划 上级抽成比例
     * @apiParam  {Integer}     carePlanMin                 计划关怀最低价
     * @apiParam  {Integer}     carePlanMax                 计划关怀最高价 
     * 
     * @apiParam  {Integer}     chargeItemGroupProfit     收费项 集团抽成比例
     * @apiParam  {Integer}     chargeItemParentProfit    收费项 上级抽成比例 
     * 
     * @apiParam  {String}   	clinicGroupProfit  门诊 集团抽成比例
     * @apiParam  {String}   	clinicParentProfit  门诊 上级抽成比例
     * 
     * @apiParam  {String}   	consultationGroupProfit  会诊 集团抽成比例
     * @apiParam  {String}   	consultationParentProfit  会诊 上级抽成比例
     * 
	 *
	 * @apiSuccess {String} resultCode	1 成功
	 * 
	 * @apiAuthor tanyf
	 * @date 2016年6月24日
	 */
	@RequestMapping(value = "/updateGroupConfigAndFee")
	public JSONMessage updateGroupConfigAndFee(GroupConfigAndFeeParam param) throws HttpApiException{
		// type   （修改）类型 1、 名医面对面 2 、 图文咨询 3 、电话咨询 4、健康关怀 5、收费项
		guideService.updateGroupConfigAndFee(param);
		return JSONMessage.success();
	}
	
	
	
	/**
	 * @api {get} /guide/getBeServicedPatients 导医客服获取需要服务的患者列表
	 * @apiVersion 1.0.0
	 * @apiName getBeServicedPatients
	 * @apiGroup 导医客服
	 * @apiDescription 导医客服获取需要服务的患者列表
	 * @apiParam {String}   access_token token
	 * @apiParam {Integer} 			pageIndex 				页码数
	 * @apiParam {Integer} 			pageSize 				分页条数
	 *
	 * @apiSuccess {String} 	resultCode	1 成功
	 * @apiSuccess {Integer}     pageCount	总页数
	 * @apiSuccess {Integer}        pageData.userId               患者的用户id
	 * @apiSuccess {Integer}        pageData.msgGroupId           客服会话id
	 * @apiSuccess {String}         pageData.patientName				     患者姓名
	 * @apiSuccess {String}         pageData.userName			  患者用户名
	 * @apiSuccess {String}         pageData.headPicFileName      患者头像   
	 * @apiSuccess {Integer}         pageData.sex                  性别
	 * @apiSuccess {String}         pageData.ageStr             年龄
	 * 
	 * @apiAuthor wangl
	 * @date 2016年7月25日17:43:41
	 */
	@RequestMapping(value = "/getBeServicedPatients")
	public JSONMessage getBeServicedPatients(Integer pageIndex , Integer pageSize){
		return JSONMessage.success(guideService.getBeServicedPatients(pageIndex,pageSize));
	}
	
	
	/**
	 * @api {get} /guide/replyPatientMessage 导医回复患者消息
	 * @apiVersion 1.0.0
	 * @apiName replyPatientMessage
	 * @apiGroup 导医客服
	 * @apiDescription 导医回复患者消息
	 * @apiParam {String}   access_token token
	 * @apiParam {Integer} 			userId 				患者id
	 * @apiParam {Integer} 			msgGroupId 			会话id
	 *
	 * @apiSuccess {String} 	resultCode	1 成功
	 * 
	 * @apiAuthor wangl
	 * @date 2016年7月25日17:43:41
	 */
	@RequestMapping(value = "/replyPatientMessage")
	public JSONMessage replyPatientMessage(@RequestParam(required=true) Integer userId){
		guideService.replyPatientMessage(userId);
		return JSONMessage.success();
	}
	
	/**
	 * @api {get} /guide/finish 结束服务
	 * @apiVersion 1.0.0
	 * @apiName finish
	 * @apiGroup 导医客服
	 * @apiDescription 结束服务
	 * @apiParam {String}   access_token token
	 * @apiParam {Integer} 			msgGroupId 			会话id
	 *
	 * @apiSuccess {String} 	resultCode	1 成功
	 * 
	 * @apiAuthor wangl
	 * @date 2016年7月25日17:43:41
	 */
	@RequestMapping(value = "/finish")
	public JSONMessage finish(@RequestParam(required=true) String msgGroupId) throws HttpApiException {
		guideService.finish(msgGroupId);
		return JSONMessage.success();
	}
	
	
	
	/**
	 * @api {get} /guide/getCustomerWorkDate 结束服务
	 * @apiVersion 1.0.0
	 * @apiName finish
	 * @apiGroup 导医客服
	 * @apiDescription 获取一月内的记录数
	 * @apiParam {String}   access_token token
	 * @apiParam {Long}   dateTime (null：获取第一页)
	 * @apiParam {Integer}   pageIndex 页数
	 * @apiParam {Integer}   pageSize  记录数
	 *
	 * @apiSuccess {String} 	resultCode	1 成功
	 * @apiSuccess {Integer} 	dayCount	统计数字
	 * @apiSuccess {Long} 	dateTime	日期时间戳
	 * 
	 * @apiAuthor wangl
	 * @date 2016年7月25日17:43:41
	 */
	@RequestMapping(value = "/getCustomerWorkDate")
	public JSONMessage getCustomerWorkDate(Long dateTime , Integer pageIndex , Integer pageSize){
		return JSONMessage.success(guideService.getCustomerWorkDate(dateTime,pageIndex,pageSize));
	}
	
	
	/**
	 * @api {get} /guide/getDayRecords 获取某一天的记录列表
	 * @apiVersion 1.0.0
	 * @apiName finish
	 * @apiGroup 导医客服
	 * @apiDescription 获取某一天的记录列表
	 * @apiParam {String}   access_token token
	 * @apiParam {Long}   dateTime 当天时间
	 *
	 * @apiSuccess {String} 	resultCode	1 成功
	 * @apiSuccess {long} 	total       总记录数
	 * @apiSuccess {long} 	pageCount       总页数
	 * @apiSuccess {Integer} 	patientUserId       总记录数
	 * @apiSuccess {String} 		name	日期时间戳
	 * @apiSuccess {String} 		headPicFileName	头像
	 * @apiSuccess {String} 		startTime	时间
	 * @apiSuccess {String} 		gid	会话id
	 * 
	 * @apiAuthor wangl
	 * @date 2016年7月25日17:43:41
	 */
	@RequestMapping(value = "/getDayRecords")
	public JSONMessage getDayRecords(@RequestParam(required=true) Long dateTime,
										Integer pageIndex , Integer pageSize){
		return JSONMessage.success(guideService.getDayRecords(dateTime,pageIndex,pageSize));
	}
	
	/**
	 * @api {get} /guide/getUserInfo 导医获取患者信息
	 * @apiVersion 1.0.0
	 * @apiName getUserInfo
	 * @apiGroup 导医客服
	 * @apiDescription 导医获取患者信息
	 * @apiParam {String}   access_token token
	 * @apiParam {Integer}   userId 用户id
	 *
	 * @apiSuccess {String} 	resultCode	1 成功
	 * @apiSuccess {String} 	name       姓名
	 * @apiSuccess {String} 	headPicFileName      头像url
	 * @apiSuccess {String} 	remarks    备注
	 * @apiSuccess {Integer} 		sex	性别
	 * @apiSuccess {Long} 		birthday	生日时间戳
	 * @apiSuccess {String} 		ageStr	年龄
	 * @apiSuccess {Integer} 		area	地址
	 * 
	 * @apiAuthor wangl
	 * @date 2016年8月10日19:28:31
	 */
	@RequestMapping(value = "/getUserInfo")
	public JSONMessage getUserInfo(@RequestParam(required=true) Integer userId){
		return JSONMessage.success(guideService.getUserInfo(userId));
	}
	
	@RequestMapping(value = "/clearAllGuideSession")
	public JSONMessage clearAllGuideSession(){
		return JSONMessage.success(guideService.clearAllGuideSession());
	}
}
