package com.dachen.health.controller.nurse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.health.user.entity.param.NurseParam;
import com.dachen.health.user.entity.vo.NurseVO;
import com.dachen.health.user.service.INurseService;
import com.dachen.line.stat.comon.constant.OrderStatusEnum;
import com.dachen.line.stat.comon.constant.VServiceProcessStatusEnum;
import com.dachen.line.stat.entity.param.CheckResultsParm;
import com.dachen.line.stat.entity.param.PalceOrderParam;
import com.dachen.line.stat.entity.vo.CheckResults;
import com.dachen.line.stat.entity.vo.LineServiceProduct;
import com.dachen.line.stat.entity.vo.NurseDutyTime;
import com.dachen.line.stat.entity.vo.UserLineService;
import com.dachen.line.stat.service.ICheckResultsService;
import com.dachen.line.stat.service.ILineServiceProductService;
import com.dachen.line.stat.service.IMessageService;
import com.dachen.line.stat.service.IOrderService;
import com.dachen.line.stat.service.IServiceStatistic;
import com.dachen.line.stat.service.IUserLineService;
import com.dachen.line.stat.service.IUserServiceTimeService;
import com.dachen.line.stat.service.IVSPTrackingService;
import com.dachen.line.stat.service.IVServiceProcessService;
import com.dachen.line.stat.util.Helper;
import com.dachen.util.ReqUtil;

/**
 * 
 * @author liwei
 *
 */
@RestController
@RequestMapping("/nurse")
public class NurseController extends AbstractController {

	@Autowired
	private INurseService nurseService;
	@Autowired
	private IServiceStatistic serviceStatistic;
	@Autowired
	private ILineServiceProductService lineServiceProductService;

	@Autowired
	private IOrderService orderService;

	@Autowired
	private IUserLineService userLineService;

	@Autowired
	private IUserServiceTimeService userServiceTimeService;

	@Autowired
	private IVSPTrackingService vspTrackingService;

	@Autowired
	private IVServiceProcessService vServiceProcessService;

	@Autowired
	private IMessageService messageService;// 短信列表

	@Autowired
	private ICheckResultsService checkService;

	/**
	 * @api {post} /nurse/createCheckInfo 提交护士认证信息
	 * @apiVersion 1.0.0
	 * @apiName createCheckInfo
	 * @apiGroup 护士
	 * @apiDescription 提交护士认证信息
	 * @apiParam {String} access_token token
	 * @apiParam {String} name 姓名
	 * @apiParam {String} idCard 身份证号码
	 * @apiParam {String} hospital 医院
	 * @apiParam {String} hospitalId 医院Id 如果没有医院选择，则hospitalId传""
	 * @apiParam {String} departments 科室
	 * @apiParam {String} title 职称
	 * @apiParam {String} images 多个图片已逗号分隔
	 * @apiSuccess {Number=1} resultCode 返回状态吗
	 * @apiAuthor 李伟
	 * @date 2015年12月2日
	 */
	@RequestMapping("/createCheckInfo")
	public JSONMessage createCheckInfo(NurseParam param) {
		JSONMessage result = null;
		param.setUserId(ReqUtil.instance.getUserId());
		User user = nurseService.createCheckInfo(param);
		if (null != user) {
			result = JSONMessage.success();
		} else {
			result = JSONMessage.failure("提交认证信息失败！");
		}
		return result;
	}

	/**
	 * @api {post} /nurse/updateNurseCheckInfo 修改护士认证信息
	 * @apiVersion 1.0.0
	 * @apiName updateNurseCheckInfo
	 * @apiGroup 护士
	 * @apiDescription 修改护士认证信息
	 * @apiParam {String} access_token token
	 * @apiParam {String} name 姓名
	 * @apiParam {String} idCard 身份证号码
	 * @apiParam {String} hospital 医院
	 * @apiParam {String} hospitalId 医院Id（如果没有医院选择，则传""）
	 * @apiParam {String} departments 科室
	 * @apiParam {String} title 职称
	 * @apiParam {String} images 多个图片已逗号分隔
	 * @apiSuccess {Number=1} resultCode 返回状态吗
	 * @apiAuthor 李伟
	 * @date 2015年12月2日
	 */
	@RequestMapping("/updateNurseCheckInfo")
	public JSONMessage updateCheckInfo(NurseParam param) {
		param.setUserId(ReqUtil.instance.getUserId());
		param.setAccess_token(ReqUtil.instance.getToken());
		nurseService.updateCheckInfo(param);
		return JSONMessage.success();
	}

	/**
	 * @api {get} /nurse/getNurseCheckInfo 获取护士认证信息
	 * @apiVersion 1.0.0
	 * @apiName getNurseCheckInfo
	 * @apiGroup 护士
	 * @apiDescription 获取护士认证信息
	 * @apiParam {String} access_token token
	 * @apiSuccess {String} name 姓名
	 * @apiSuccess {String} idCard 身份证号码
	 * @apiSuccess {String} status 状态（1.未认证，2.待审核，3.不通过，9.已通过）
	 * @apiSuccess {String} hospital 医院
	 * @apiSuccess {String} departments 科室
	 * @apiSuccess {String} title 职称
	 * @apiSuccess {String} remark 审核不通过时备注
	 * @apiSuccess {Object[]} images 图片集合
	 * @apiSuccess {String} images.imageId 图片id
	 * @apiSuccess {String} images.imageType 图片类型
	 * @apiSuccess {String} images.order 图片顺序
	 * 
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/getNurseCheckInfo")
	public JSONMessage getCheckInfo(NurseParam param) {
		param.setUserId(ReqUtil.instance.getUserId());
		NurseVO nurseVo = nurseService.getCheckInfo(param);
		return JSONMessage.success(null, nurseVo);
	}

	/**
	 * @api {get} /nurse/getServiceStatisticService 获取护士认证通过前订单页面显示信息
	 * @apiVersion 1.0.0
	 * @apiName getServiceStatisticService
	 * @apiGroup 护士
	 * @apiDescription 获取护士认证前订单页面显示信息
	 * @apiParam {String} access_token token
	 * @apiSuccess {String} todayAddNum 今天加入人数
	 * @apiSuccess {String} totalReceptionNum 共接待人数
	 * @apiSuccess {String[]} content 护士订单页面显示信息
	 * @apiSuccess {String} content.image 用户头像
	 * @apiSuccess {String} content.setContent 服务滚动信息 20条
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/getServiceStatisticService")
	public JSONMessage getServiceStatisticService() {
		return JSONMessage.success(null,
				serviceStatistic.getServiceStatisticService());
	}

	/**
	 * @api {get} /nurse/getSystemLineServiceProduct 获取系统产品列表
	 * @apiVersion 1.0.0
	 * @apiName getSystemLineServiceProduct
	 * @apiGroup 护士
	 * @apiDescription 获取系统产品列表
	 * @apiParam {String} access_token token
	 * @apiSuccess {String} title 产品标题
	 * @apiSuccess {String} content 内容
	 * @apiSuccess {String} price 价格
	 * @apiSuccess {String} infoURL html的地址
	 * @apiSuccess {String[]} picIds 背景图片
	 * @apiSuccess {String} type 产品类型 1
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/getSystemLineServiceProduct")
	public JSONMessage getSystemLineServiceProduct() {
		List<LineServiceProduct> lineServiceProductList = lineServiceProductService
				.getSystemLineServiceProduct();
		// String response = JSON.toJSONString(lineServiceProductList);
		List<Map<String, String>> mapList = Helper
				.getValueMapList(lineServiceProductList);
		return JSONMessage.success(null, mapList);
	}

	/**
	 * @api {get} /nurse/getNurseAvialOrder 查询护士可以接的订单列表
	 * @apiVersion 1.0.0
	 * @apiName getNurseAvialOrder
	 * @apiGroup 护士
	 * @apiDescription 查询护士可以接的订单列表
	 * @apiParam {String} access_token token
	 * @apiSuccess {String} orderId 订单id
	 * @apiSuccess {String} productTitle 产品标题
	 * @apiSuccess {String} doctorName 医生名称
	 * @apiSuccess {String} doctorDepart 医生科室
	 * @apiSuccess {String} checkItem 检查项目
	 * @apiSuccess {String} patientTel 患者电话
	 * @apiSuccess {String} patientName 患者名称
	 * @apiSuccess {String} appointmentTime 预约时间
	 * @apiSuccess {double} price 产品套餐价格
	 * @apiSuccess {String} hospital 医院名称
	 * @apiSuccess {String} patientHeadPicFileName 患者头像
	 * @apiSuccess {Objecgt} showTime 产品标题
	 * @apiSuccess {String} showTime.timeAgo 产品标题
	 * @apiSuccess {String} showTime.orderCreateTime 订单创建时间
	 * @apiSuccess {String} showTime.orderDate 时间
	 * @apiSuccess {String} showTime.orderHours 小时
	 * @apiSuccess {Number=1} resultCode 返回状态吗 103：暂时没有订单
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/getNurseAvialOrder")
	public JSONMessage getNurseAvialOrder() {
		int resultCode = 1;
		String message = null;
		Map<String, Object> resultMap = orderService.getNurseOrder(
				ReqUtil.instance.getUserId(), 1, 10);
		resultCode = Integer.parseInt(resultMap.get("resultCode").toString());
		message = resultMap.get("message").toString();
		JSONMessage result = new JSONMessage(resultCode, message,
				resultMap.get("data"));
		return result;
	}

	/**
	 * @api {get} /nurse/getUserLineService 获取护士服务设置列表
	 * @apiVersion 1.0.0
	 * @apiName getUserLineService
	 * @apiGroup 护士
	 * @apiDescription 获取护士服务设置列表
	 * @apiParam {String} access_token token
	 * @apiSuccess {Integer} userId 用户id
	 * @apiSuccess {Integer} status 用户服务设置状态 0 未设置 1 打开 2 关闭
	 * @apiSuccess {Object} lineService 服务对象
	 * @apiSuccess {String} lineService.id 服务id
	 * @apiSuccess {String} lineService.title 标题
	 * @apiSuccess {String} lineService.content 内容
	 * @apiSuccess {String} lineService.price 价格
	 * @apiSuccess {String} lineService.infoURL html的地址
	 * @apiSuccess {String[]} lineService.picIds 背景图片
	 * @apiSuccess {String } lineService.remarks 产品备注
	 * @apiSuccess {String} lineService.type 产品类型 1
	 * @apiSuccess {String[]} timeList 时间列表
	 * @apiSuccess {String} timeList.week 星期几
	 * @apiSuccess {String} timeList.time 时间 格式 yyyy-MM-dd
	 * @apiSuccess {String} timeList.day 时间 格式 MM-dd
	 * @apiSuccess {String} timeList.status 服务时间状态 0 不可以接单 1 可以接单
	 * @apiSuccess {Number=1} resultCode 返回状态码
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/getUserLineService")
	public JSONMessage getUserLineService() {
		Integer userId = ReqUtil.instance.getUserId();
		List<UserLineService> lineServiceProductList = userLineService
				.getUserLineService(userId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("lineService", lineServiceProductList);
		map.put("timeList", Helper
				.getServiceTimeResultList(userServiceTimeService
						.getUserServiceTimeList(userId)));
		JSONMessage result = JSONMessage.success("获取用户服务设置列表成功！", map);
		return result;
	}

	/**
	 * @api {post} /nurse/updateUserLineService 设置护士服务状态
	 * @apiVersion 1.0.0
	 * @apiName updateUserLineService
	 * @apiGroup 护士
	 * @apiDescription 设置护士服务状态
	 * @apiParam {String} access_token token
	 * @apiParam {String} id 服务id
	 * @apiParam {Integer} status 服务状态 0 未设置 1 打开 2 关闭
	 * @apiSuccess {Number=1} resultCode 返回状态吗
	 * @apiAuthor 李伟
	 * @date 2015年12月9日
	 */
	@RequestMapping("/updateUserLineService")
	public JSONMessage updateUserLineService(UserLineService param) {
		userLineService.updateUserLineService(ReqUtil.instance.getUserId(),
				param.getId(), param.getStatus());
		return JSONMessage.success();
	}

	/**
	 * @api {post} /nurse/updateUserServiceTime 设置护士服务时间
	 * @apiVersion 1.0.0
	 * @apiName updateUserServiceTime
	 * @apiGroup 护士
	 * @apiDescription 设置护士服务时间
	 * @apiParam {String} access_token token
	 * @apiParam {String} time 服务时间 格式 yyyy-MM-dd 精确到天
	 * @apiParam {Integer} status 服务状态 0 不可以接单 1 可以接单
	 * @apiSuccess {Number=1} resultCode 返回状态吗
	 * @apiAuthor 李伟
	 * @date 2015年12月9日
	 */
	@RequestMapping("/updateUserServiceTime")
	public JSONMessage updateUserServiceTime(NurseDutyTime param) {
		userServiceTimeService.updateUserServiceTime(ReqUtil.instance.getUserId(),
				param.getTime(), param.getStatus());
		return JSONMessage.success();
	}

	/**
	 * @api {get} /nurse/getTheOrder 护士抢单
	 * @apiVersion 1.0.0
	 * @apiName getTheOrder
	 * @apiGroup 护士
	 * @apiDescription 护士抢单
	 * @apiParam {String} access_token token
	 * @apiParam {String} orderId
	 * @apiSuccess {Number=1} resultCode 返回状态吗 0：没有抢到订单 1 抢单成功
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/getTheOrder")
	public JSONMessage getTheOrder(String orderId) {
		int resultCode = 1;
		String message = null;
		Map<String, Object> resultMap = orderService.getTheOrder(
				ReqUtil.instance.getUserId(), orderId);
		resultCode = Integer.parseInt(resultMap.get("resultCode").toString());
		message = resultMap.get("message").toString();
		JSONMessage result = new JSONMessage(resultCode, message,
				resultMap.get("isFirst"));
		return result;
	}

	/**
	 * 
	 * @api {[get,post]} /nurse/callByTel 护士打电话
	 * @apiVersion 1.0.0
	 * @apiName callByTel
	 * @apiGroup 护士
	 * @apiDescription 护士打电话
	 * @apiParam {String} access_token 凭证
	 * @apiParam {String} toTel 电话号码（不能为空，拨给哪个）
	 * @apiParam {String} vspId 服务id
	 * @apiSuccess {Number=1} resultCode
	 * @apiAuthor 李伟
	 * @date 2015年12月12日
	 */
	@RequestMapping("/callByTel")
	public JSONMessage callByTel(String toTel, String vspId) {
		String message = null;
		Integer nurseId = ReqUtil.instance.getUserId();
		Map<String, Object> resultMap = vspTrackingService.callByTel(nurseId,
				vspId);
		int resultCode = Integer.parseInt(resultMap.get("resultCode")
				.toString());
		message = resultMap.get("message").toString();
		JSONMessage result = new JSONMessage(resultCode, message);

		return result;
	}

	/**
	 * 
	 * @api {[get,post]} /nurse/sendMessage 护士发送短信
	 * @apiVersion 1.0.0
	 * @apiName sendMessage
	 * @apiGroup 护士
	 * @apiDescription 护士发送短信
	 * @apiParam {String} access_token 凭证
	 * @apiParam {String} toTel 电话号码（不能为空，发送的短信的对象）
	 * @apiParam {String} messageId 短信id
	 * @apiParam {String} content 短信内容
	 * @apiParam {String} vspId 服务id
	 * @apiSuccess {Number=1} resultCode 返回状态吗
	 * @apiAuthor 李伟
	 * @date 2015年12月12日
	 */
	@RequestMapping("/sendMessage")
	public JSONMessage sendMessage(String toTel, String messageId,
			String content, String vspId) {
		String message = null;
		Map<String, Object> resultMap = vspTrackingService.sendMessage(
				messageId, content, vspId, ReqUtil.instance.getUserId());
		int resultCode = Integer.parseInt(resultMap.get("resultCode")
				.toString());
		message = resultMap.get("message").toString();
		JSONMessage result = new JSONMessage(resultCode, message);

		return result;
	}

	/**
	 * 
	 * @api {[get,post]} /nurse/updateNurseServiceStatus 更新护士流程服务状态
	 * @apiVersion 1.0.0
	 * @apiName updateNurseServiceStatus
	 * @apiGroup 护士
	 * @apiDescription 更新护士流程服务状态
	 * @apiParam {String} access_token 凭证
	 * @apiParam {String} serviceId 服务id;
	 * @apiParam {Integer} status 1 开始服务 2 等待上传检查结果 3.结束 4.申请关闭 5关闭
	 * @return
	 * @apiAuthor 李伟
	 * @date 2015年12月12日
	 */
	@RequestMapping("/updateNurseServiceStatus")
	public JSONMessage updateNurseServiceStatus(String serviceId) {
		vServiceProcessService.updateVServiceProcess(serviceId,
				VServiceProcessStatusEnum.toUploadResult.getIndex());// 点击开始服务的时候
																		// 状态的转过来的是
																		// 2
																		// 上传结果
		return JSONMessage.success();
	}

	/**
	 * @api {get} /nurse/getVServiceProcessList 获取护士流程服务列表
	 * @apiVersion 1.0.0
	 * @apiName getVServiceProcessList
	 * @apiGroup 护士
	 * @apiDescription 获取护士流程服务列表
	 * @apiParam {String} access_token token
	 * @apiParam {String} serviceId 服务id
	 * @apiSuccess {String} productTitle 产品标题
	 * @apiSuccess {String} doctorName 医生名称
	 * @apiSuccess {String} doctorDepart 医生科室
	 * @apiSuccess {String} patientName 患者名称
	 * @apiSuccess {String} patientTel 患者电话
	 * @apiSuccess {String} nurseTel 护士电话
	 * @apiSuccess {String} appointmentTime 预约时间
	 * @apiSuccess {double} price 产品套餐价格
	 * @apiSuccess {String} hospital 医院名称
	 * @apiSuccess {String} patientHeadPicFileName 患者头像
	 * @apiSuccess {String} serviceStatus 服务状态
	 * @apiSuccess {Integer} orderStatus 订单状态 4 已完成,5 已取消,6 进行中,10 预约成功
	 * @apiSuccess {Object} showTime 产品标题
	 * @apiSuccess {String} showTime.timeAgo 产品标题
	 * @apiSuccess {String} showTime.orderCreateTime 订单创建时间
	 * @apiSuccess {String} showTime.orderDate 时间
	 * @apiSuccess {String} showTime.orderHours 小时
	 * @apiSuccess {Object[]} checkItem 产品标题
	 * @apiSuccess {String} checkItem.id 线下服务id
	 * @apiSuccess {String} checkItem.type 线下服务类型 0是加号服务 1是检查类型 2是住院类型
	 * @apiSuccess {String} checkItem.title 线下服务标题
	 * 
	 * @apiSuccess {Number=1} resultCode 返回状态吗 0：没有抢到订单 1 抢单成功
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/getVServiceProcessList")
	public JSONMessage getVServiceProcessList() {
		int resultCode = 1;
		List<Map<String, Object>> resultMap = vServiceProcessService
				.getVServiceProcessList(ReqUtil.instance.getUserId());
		// resultCode =
		// Integer.parseInt(resultMap.get("resultCode").toString());
		JSONMessage result = new JSONMessage(resultCode, null, resultMap);
		return result;
	}

	/**
	 * @api {get} /nurse/getHistoryVServiceProcessList 获取护士历史流程服务列表
	 * @apiVersion 1.0.0
	 * @apiName getHistoryVServiceProcessList
	 * @apiGroup 护士
	 * @apiDescription 获取护士历史流程服务列表
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} pageIndex 第几页 从0 开始 
	 * @apiParam {Integer} pageSize 每页的大小
	 * @apiParam {String}  serviceId 服务id
	 * @apiSuccess {String} productTitle 产品标题
	 * @apiSuccess {String} doctorName 医生名称
	 * @apiSuccess {String} doctorDepart 医生科室
	 * @apiSuccess {String} patientName 患者名称
	 * @apiSuccess {String} patientTel 患者电话
	 * @apiSuccess {String} nurseTel 护士电话
	 * @apiSuccess {String} appointmentTime 预约时间
	 * @apiSuccess {double} price 产品套餐价格
	 * @apiSuccess {String} hospital 医院名称
	 * @apiSuccess {String} patientHeadPicFileName 患者头像
	 * @apiSuccess {String} serviceStatus 服务状态
	 * @apiSuccess {Integer} orderStatus 订单状态 4 已完成,5 已取消,6 进行中,10 预约成功
	 * @apiSuccess {Object} showTime 产品标题
	 * @apiSuccess {String} showTime.timeAgo 产品标题
	 * @apiSuccess {String} showTime.orderCreateTime 订单创建时间
	 * @apiSuccess {String} showTime.orderDate 时间
	 * @apiSuccess {String} showTime.orderHours 小时
	 * @apiSuccess {Object[]} checkItem 产品标题
	 * @apiSuccess {String} checkItem.id 线下服务id
	 * @apiSuccess {String} checkItem.type 线下服务类型 0是加号服务 1是检查类型 2是住院类型
	 * @apiSuccess {String} checkItem.title 服务标题
	 * @apiSuccess {Number=1} resultCode 返回状态吗 0：没有抢到订单 1 抢单成功
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/getHistoryVServiceProcessList")
	public JSONMessage getHistoryVServiceProcessList() {
		int resultCode = 1;
		List<Map<String, Object>> resultMap = vServiceProcessService
				.getHistoryVServiceProcessList(ReqUtil.instance.getUserId());
		JSONMessage result = new JSONMessage(resultCode, null, resultMap);
		return result;
	}
//	@RequestMapping("/getHistoryVServiceProcessListForPage")
//	public JSONMessage getHistoryVServiceProcessList(Integer pageIndex,Integer pageSize) {
//		int resultCode = 1;
//		Map<String, Object> resultMap = vServiceProcessService.getHistoryVServiceProcessList(ReqUtil.instance.getUserId(), pageIndex, pageSize);
//		JSONMessage result = new JSONMessage(resultCode, null, resultMap);
//		return result;
//	}
//	

	/**
	 * @api {get} /nurse/getUserMessageList 获取护士短信列表
	 * @apiVersion 1.0.0
	 * @apiName getUserMessageList
	 * @apiGroup 护士
	 * @apiDescription 获取护士短信列表
	 * @apiParam {String} access_token token
	 * @apiSuccess {String} messageId 短信id
	 * @apiSuccess {String} content 短信内容
	 * @apiSuccess {Number=1} resultCode 返回状态吗 0
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/getUserMessageList")
	public JSONMessage getUserMessageList() {
		int resultCode = 1;
		JSONMessage result = new JSONMessage(resultCode, null,
				messageService.getMessageJsonList(ReqUtil.instance.getUserId()));
		return result;
	}

	/**
	 * @api {get} /nurse/insertUserMessage 新增护士短信
	 * @apiVersion 1.0.0
	 * @apiName insertUserMessage
	 * @apiGroup 护士
	 * @apiDescription 新增护士短信
	 * @apiParam {String} access_token token
	 * @apiParam {String} content 短信内容
	 * @apiSuccess {Number=1} resultCode 返回状态吗 0
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/insertUserMessage")
	public JSONMessage insertUserMessage(String content) {
		messageService.insertUserMessage(ReqUtil.instance.getUserId(), content);
		return JSONMessage.success();
	}

	/**
	 * @api {get} /nurse/deleteUserMessage 删除护士短信
	 * @apiVersion 1.0.0
	 * @apiName deleteUserMessage
	 * @apiGroup 护士
	 * @apiDescription 删除护士短信
	 * @apiParam {String} access_token token
	 * @apiParam {String} messageId 短信id
	 * @apiSuccess {Number=1} resultCode 返回状态吗 0
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/deleteUserMessage")
	public JSONMessage deleteUserMessage(String messageId) {
		messageService.deleteUserMessage(messageId);
		;
		return JSONMessage.success();
	}

	/**
	 * @api {get} /nurse/getNurseList 查询护士列表
	 * @apiVersion 1.0.0
	 * @param param
	 * @apiGroup 护士
	 * @apiDescription 查询护士列表
	 * @apiAuthor jianghj
	 * @return
	 */
	@RequestMapping("/getNurseList")
	public JSONMessage getNurseList(NurseParam param) {
		return JSONMessage.success(null, nurseService.getNurseList(param));
	}

	/**
	 * @api {get} /nurse/getCheckItemListById 【患者】患者下单页面-获取查询检查项目
	 * @apiVersion 1.0.0
	 * @apiName getCheckItemListById
	 * @apiGroup 护士
	 * @apiDescription 【患者】患者下单页面-获取查询检查项目
	 * @apiParam {String} access_token token
	 * @apiParam {String} id id
	 * @apiParam {int} type 0 产品id 1 检查单id
	 * @apiSuccess {String} id 服务项目id
	 * @apiSuccess {String} title 检查项标题
	 * @apiSuccess {int} type 线下服务类型 0是加号服务 1是检查类型 2是住院类型
	 * @apiSuccess {Number=1} resultCode 返回状态吗 0
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/getCheckItemListById")
	public JSONMessage getCheckItemListById(String id, int type) {
		int resultCode = 1;
		JSONMessage result = new JSONMessage(resultCode, null,
				lineServiceProductService.getCheckItemListById(id, type));
		return result;
	}

	/**
	 * @api {get} /nurse/insertCheckResults 上传检查结果
	 * @apiVersion 1.0.0
	 * @apiName insertCheckResults
	 * @apiGroup 护士
	 * @apiDescription 上传检查结果
	 * @apiParam {String} access_token token
	 * @apiParam {String} serviceId 服务id
	 * @apiParam {Integer} from 来源 0是护士1是患者 2是客服 3是系统对接
	 * @apiParam {Object[]} checkItemList 检查项
	 * @apiParam {String} checkItemList.lsIds 线下服务id
	 * @apiParam {String} checkItemList.results 检查结果说明
	 * @apiParam {String} checkItemList.imageList 多个图片逗号 分割
	 * @apiSuccess {Number=1} resultCode 返回状态吗 0
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/insertCheckResults")
	public JSONMessage insertCheckResults(CheckResultsParm param) {
		checkService.insertCheckResults(param);
		return JSONMessage.success();
	}

	/**
	 * @api {get} /nurse/getCheckResults 获取订单检查结果列表
	 * @apiVersion 1.0.0
	 * @apiName getCheckResults
	 * @apiGroup 护士
	 * @apiDescription 获取订单检查列表
	 * @apiParam {String} access_token token
	 * @apiParam {String} orderId 短信id
	 * @apiSuccess {Number=1} resultCode 返回状态吗 0
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/getCheckResults")
	public JSONMessage getCheckResults(String orderId) {
		List<CheckResults> results = checkService
				.getCheckResultsServiceList(orderId);
		return JSONMessage.success("获取订单检查项成功", results);
	}

	/**
	 * @api {get} /nurse/getCertHospitalList 【患者】获取护士认证通过的医院
	 * @apiVersion 1.0.0
	 * @apiName getCertHospitalList
	 * @apiGroup 护士
	 * @apiDescription 【患者】获取护士认证通过的医院
	 * @apiParam {String} access_token token
	 * @apiSuccess {Number=1} resultCode 返回状态吗 0
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/getCertHospitalList")
	public JSONMessage getCertHospitalList() {
		List<Map<String, Object>> resultMap = lineServiceProductService
				.getCertificatedHospitalList(UserEnum.UserStatus.normal
						.getIndex());
		return JSONMessage.success("获取认证医院列表成功", resultMap);
	}

	/**
	 * @api {get} /nurse/checkCancleUserOrder 【患者】取消线下服务订单确认接口
	 * @apiVersion 1.0.0
	 * @apiName checkCancleUserOrder
	 * @apiGroup 护士
	 * @apiDescription 【患者】取消线下服务订单
	 * @apiParam {String} access_token token
	 * @apiParam {String} orderId 服务订单的id
	 * @apiSuccess {Number=1} resultCode 返回状态吗 1 可以取消 2.可以取消 不扣钱 3.可以取消 但是会扣50%
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/checkCancleUserOrder")
	public JSONMessage checkCancleUserOrder(String orderId) {
		Map<String, Object> result = orderService.checkCancleUserOrder(orderId);
		return JSONMessage.success(result);
	}

	/**
	 * @api {get} /nurse/cancleUserOrder 【患者】取消线下服务订单
	 * @apiVersion 1.0.0
	 * @apiName cancleUserOrder
	 * @apiGroup 护士
	 * @apiDescription 【患者】取消线下服务订单
	 * @apiParam {String} access_token token
	 * @apiParam {String} orderId 服务订单的id
	 * @apiSuccess {Number=1} resultCode 状态码
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/cancleUserOrder")
	public JSONMessage cancleUserOrder(String orderId) {
		orderService.cancleUserOrder(orderId, ReqUtil.instance.getUserId());
		return JSONMessage.success();
	}

	/**
	 * @api {get} /nurse/callBackBasicOrder 【患者】基础订单支付结果的回调接口
	 * @apiVersion 1.0.0
	 * @apiName callBackBasicOrder
	 * @apiGroup 护士
	 * @apiDescription 【患者】基础订单支付结果的回调接口
	 * @apiParam {String} access_token token
	 * @apiParam {String} basicOrderId 基础订单id
	 * @apiSuccess {Number=1} resultCode 状态码 1 成功 10012 更新线下服务基础订单状态失败
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/callBackBasicOrder")
	public JSONMessage callBackBasicOrder(String basicOrderId) {
		orderService.callBackBasicOrder(basicOrderId);
		return JSONMessage.success();
	}

	/**
	 * @api {get} /nurse/endAppraise 【患者】确认结束线下服务订单
	 * @apiVersion 1.0.0
	 * @apiName endAppraise
	 * @apiGroup 护士
	 * @apiDescription 【患者】确认结束线下服务订单
	 * @apiParam {String} access_token token
	 * @apiParam {String} orderId 订单id
	 * @apiSuccess {Number=1} resultCode 返回状态吗 0
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/endAppraise")
	public JSONMessage endAppraise(String orderId) {
		orderService.endAppraise(orderId, OrderStatusEnum.close.getIndex());
		return JSONMessage.success();
	}

	/**
	 * @api {post} /nurse/insertUserOrder 【患者】患者下线下服务的订单
	 * @apiVersion 1.0.0
	 * @apiName insertUserOrder
	 * @apiGroup 护士
	 * @apiDescription 【患者】患者下线下服务的订单
	 * @apiParam {String} access_token token
	 * @apiParam {String} productId 产品Id 备注可以为空，下单来源ViP直通车产品
	 * @apiParam {String} checkId 检查单Id 可以为空，下单来源检查单
	 * @apiParam {Integer} patientId 就诊人id
	 * @apiParam {String} patientTel 就诊人的电话 默认为患者的手机号
	 * @apiParam {String} doctorName 预约加号的医生 可以由患者用户修改
	 * @apiParam {Integer} doctorId 预约加号的医生的id 可以为空
	 * @apiParam {String} appointmentTime 预约时间 格式 yyyy-MM-dd HH:mm
	 * @apiParam {String[]} hospitals 选择的医院id集合，备注预约加号医生不可多选，检查项目可以多选
	 * @apiParam {String[]} departments 选择的科室集合，可以为空
	 * @apiParam {String[]} checkItems 检查项目的id组合
	 * @apiParam {String} remark 留言
	 * @apiSuccess {Object} data 返回状态吗
	 * @apiSuccess {String} data.productTitle 套餐名称
	 * @apiSuccess {String} data.price 套餐价格
	 * @apiSuccess {String} data.id 订单id
	 * @apiSuccess {Number=1} resultCode 返回状态吗
	 * @apiAuthor 李伟
	 * @date 2015年12月2日
	 */
	@RequestMapping("/insertUserOrder")
	public JSONMessage insertUserOrder(PalceOrderParam param) {
		Map<String, Object> basicId = orderService.insertUserOrder(param);
		return JSONMessage.success(basicId);
	}

	/**
	 * @api {post} /nurse/pushMessage 推送测试接口
	 * @apiVersion 1.0.0
	 * @apiName pushMessage
	 * @apiGroup 护士
	 * @apiDescription 推送测试接口
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} userId推送用户id
	 * @apiParam {String} content 推送内容
	 * @apiSuccess {Number=1} resultCode 返回状态吗
	 * @apiAuthor 李伟
	 * @date 2015年12月2日
	 */
	@RequestMapping("/pushMessage")
	public JSONMessage pushMessage(Integer userId, String content) {
		List<String> userIds = new ArrayList<String>();
		userIds.add(String.valueOf(userId));
		JSON resultJson = null;
		try {
			resultJson = Helper.push(content, userIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONMessage.success(resultJson);
	}

	/**
	 * 
	 * @param orderId
	 * @return
	 * @RequestMapping("/getOrderListByStatus") public JSONMessage
	 *                                          getOrderListByStatus(Integer
	 *                                          status) { return
	 *                                          OutServiceHelper
	 *                                          .getOrderListByStatus
	 *                                          (ReqUtil.instance.getToken(), status); }
	 * @RequestMapping("/updateOrderStatus") public JSONMessage
	 *                                       UpdateOrderStatus(String
	 *                                       orderId,Integer status) { return
	 *                                       OutServiceHelper
	 *                                       .updateOrderStatus(ReqUtil
	 *                                       .getToken(),orderId, status); }
	 **/
	/**
	 * @api {get} /nurse/getOrderDetail 获取订单详情
	 * @apiVersion 1.0.0
	 * @apiName getOrderDetail 获取订单详情
	 * @apiGroup 护士
	 * @apiDescription 【患者】获取订单详情
	 * @apiParam {String} access_token token
	 * @apiParam {String} orderId 订单id
	 * @apiSuccess {String} orderNo 订单好号
	 * @apiSuccess {String} serviceOrderId 线下服务订单id
	 * @apiSuccess {String} productTitle 套餐名称
	 * @apiSuccess {String} productPrice 套餐价格
	 * @apiSuccess {String} productType  套餐类型  5就医直通车、6专家直通车、7检查直通车
	 * @apiSuccess {String} orderTime 预约时间
	 * @apiSuccess {String} doctorName 医生名称
	 * @apiSuccess {String[]} hospitalList
	 * @apiSuccess {String} orderStatus 订单状态 3,"已支付" 4,"已完成" 5,"已取消" 6,"进行中" 10,
	 *             "预约成功" 2 待支付
	 * @apiSuccess {String} preOrderStatus 上一个订单状态 3,"已支付" 4,"已完成" 5,"已取消"
	 *             6,"进行中" 10, "预约成功" 2 待支付
	 * @apiSuccess {String} remark 留言
	 * @apiSuccess {String[]} departList 科室
	 * @apiSuccess {Object[]} result 科室
	 * @apiSuccess {String} result.title 检查项名称
	 * @apiSuccess {String} result.results 检查结果
	 * @apiSuccess {String[]} result.imageList 图片集合
	 * @apiSuccess {String} name 姓名
	 * @apiSuccess {Integer} sex 性别 1男，2女
	 * @apiSuccess {Integer} age 年龄
	 * @apiSuccess {String} checkItem 检查项
	 * @apiSuccess {Number=1} resultCode 返回状态吗 0
	 * @apiAuthor liwei
	 * @date 2015年12月2日
	 */
	@RequestMapping("/getOrderDetail")
	public JSONMessage getOrderDetail(String orderId) {
		Map<String, Object> map = orderService.getOrderDetail(orderId);
		return JSONMessage.success(map);
	}

	/**
	 * 
	 * @param checkId
	 * @return
	 */
	@RequestMapping("/getCheckBillService")
	public JSONMessage getCheckBillService(String checkId,
			Integer checkBillStatus) {
		Map<String, Object> map = vServiceProcessService.getCheckBillService(
				checkId, checkBillStatus);
		return JSONMessage.success(map);
	}

	/**
	 * @api {post} /nurse/updateUserGuide 更新用户指引状态
	 * @apiVersion 1.0.0
	 * @apiName updateUserGuide
	 * @apiGroup 护士
	 * @apiDescription 更新用户指引状态
	 * @apiParam {String} access_token token
	 * @apiSuccess {Number=1} resultCode 返回状态吗
	 * @apiAuthor 李伟
	 * @date 2016年1月5日
	 */
	@RequestMapping("/updateUserGuide")
	public JSONMessage updateUserGuide() {
		vServiceProcessService.updateUserGuide(ReqUtil.instance.getUserId());
		return JSONMessage.success();
	}

	/**
	 * 
	 * @api {[get,post]} /nurse/confirmNurseService 护士确认结束服务流程接口
	 * @apiVersion 1.0.0
	 * @apiName confirmNurseService
	 * @apiGroup 护士
	 * @apiDescription 护士确认结束服务流程接口
	 * @apiParam {String} access_token 凭证
	 * @apiParam {String} serviceId 服务id
	 * @return
	 * @apiAuthor 李伟
	 * @date 2016年1月5日
	 */
	@RequestMapping("/confirmNurseService")
	public JSONMessage endNurseService(String serviceId) {
		vServiceProcessService.endNurseServiceProcess(serviceId);// 我知道了
		return JSONMessage.success();
	}
}
