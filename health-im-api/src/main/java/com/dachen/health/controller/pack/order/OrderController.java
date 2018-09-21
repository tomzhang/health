package com.dachen.health.controller.pack.order;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderType;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.vo.CarePlanDoctorVO;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.pack.guide.entity.param.ConsultOrderParam;
import com.dachen.health.pack.guide.service.IGuideService;
import com.dachen.health.pack.order.entity.param.CareOrderParam;
import com.dachen.health.pack.order.entity.param.CheckOrderParam;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.vo.OrderDetailVO;
import com.dachen.health.pack.order.entity.vo.OrderVO;
import com.dachen.health.pack.order.entity.vo.PreOrderVO;
import com.dachen.health.pack.order.service.IOrderDoctorService;
import com.dachen.health.pack.order.service.IOrderRefundService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.patient.service.IDiseaseService;
import com.dachen.health.pack.pay.entity.PaymentVO;
import com.dachen.health.pack.pay.service.IPayHandleBusiness;
import com.dachen.im.server.data.request.CreateGroupRequestMessage;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.manager.ISMSManager;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/pack/order")
public class OrderController {
	@Autowired
	private IGuideService guideService;

	@Autowired
	private IOrderService orderService;

	@Autowired
	private IOrderRefundService orderRefundService;

	@Autowired
	private IPackService packService;

	@Autowired
	private IPayHandleBusiness handpayBusiness;

	@Autowired
	com.dachen.line.stat.service.IOrderService nurseOrderServiceImpl;

	@Resource
	private IDiseaseService service;

	@Autowired
	private IGroupService groupService;

	@Autowired
	IBaseDataService baseDataService;

	@Autowired
	private ISMSManager smsManager;
	
	 @Autowired
    private IOrderDoctorService orderDoctorService;

	/**
	 * @api {get} /pack/order/takeOrder 订单生成
	 * @apiVersion 1.0.0
	 * @apiName takeOrder
	 * @apiGroup 订单
	 * @apiDescription 生成初始订单和充值信息 (before 2016年7月22日17:43:54)
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生id
	 * @apiParam {Integer} packId 套餐id
	 * @apiParam {Integer} patientId 病人id
	 * @apiParam {Integer} diseaseDesc 病情描述
	 * @apiParam {String} voice 病情语音
	 * @apiParam {String} telephone 电话号码
	 * @apiParam {String[]} imagePaths 病情图
	 * @apiParam {Integer} orderType 订单类型
	 *           （1：套餐订单；2：报到；3：门诊订单；4：关怀计划套餐订单；5：随访套餐订单）空值则为套餐订单
	 * @apiParam {String} isSee 是否就诊 "false" 没有 "true" 有
	 * @apiParam {String} seeDoctorMsg 患者 在医院诊治情况
	 * @apiParam {String} offlineItemId 名医面对面订单会传递预约条目id
	 * @apiParam {String} illCaseInfoId 病情ID
	 * @apiParam {Boolean} isIllCaseCommit 是否使用旧电子病历提交（true：使用，false | null：
	 *           不使用|新建）
	 * @apiParam {String} expectAppointmentId 期望预约时间id
	 * 
	 * @apiSuccess {String} orderId 订单ID
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * @apiSuccess {String} gid 订单会话ID
	 * 
	 * @apiAuthor 谢佩
	 * @date 2015年7月2日
	 */
	@Deprecated
	@RequestMapping("/takeOrder")
	public JSONMessage takeOrder(OrderParam param) throws HttpApiException {
		param.setUserId(ReqUtil.instance.getUserId());
		UserSession session = ReqUtil.instance.getUser();
		Pack pack = null;
		if (StringUtils.isBlank(param.getDiseaseDesc())) {
			throw new ServiceException("请填写病情描述");
		}
		if ((param.getOrderType() == null) || (param.getOrderType() == OrderEnum.OrderType.order.getIndex())) {
			// 只有套餐类开型订单才需判断套餐Id不能为空
			if (param.getPackId() == null) {
				throw new ServiceException("请正确选择套餐");
			}
			// 查询套餐信息，判断该套餐是否属于该医生
			pack = packService.getPack(param.getPackId());
			if (pack == null) {
				throw new ServiceException("请正确选择套餐");
			}
			if (pack.getDoctorId().intValue() != param.getDoctorId().intValue()) {
				throw new ServiceException("套餐有误，请正确选择套餐");
			}
			if (pack.getPackType() == PackEnum.PackType.phone.getIndex()) {
				ConsultOrderParam param1 = BeanUtil.copy(param, ConsultOrderParam.class);
				GroupInfo groupInfo = guideService.createConsultOrder(param1, session);
				return JSONMessage.success(null, groupInfo);
			}
		}
		return JSONMessage.success(null, orderService.add(param, session));
	}

	/**
	 * @api {get} /pack/order/createOrder 患者生成订单
	 * @apiVersion 1.0.0
	 * @apiName createOrder
	 * @apiGroup order
	 * @apiDescription 生成初始订单和所有订单会话(兼容 2016-12-08之前的版本，并让修改了一部分的健康关怀正常调用)
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生id
	 * @apiParam {Integer} packId 套餐id
	 * @apiParam {Integer} patientId 病人id
	 * @apiParam {String} voice 病情语音
	 * @apiParam {String} telephone 电话号码
	 * @apiParam {String[]} imagePaths 病情图
	 * @apiParam {Integer} orderType
	 *           订单类型（1：咨询；3：门诊订单；4：关怀计划套餐订单；5：随访套餐订单）空值则为套餐订单
	 * @apiParam {String} isSee 是否就诊 "false" 没有 "true" 有
	 * @apiParam {String} seeDoctorMsg 患者 在医院诊治情况
	 * @apiParam {String} illCaseInfoId 病情ID
	 * @apiParam {Boolean} isIllCaseCommit 是否使用旧电子病历提交（true：使用，false | null：
	 *           不使用|新建）
	 * @apiParam {String} expectAppointmentIds 期望预约时间id
	 * @apiParam {Integer} transferDoctorId 转诊医生id (转诊使用)
	 * @apiParam {Long} transferTime 转诊时间 (转诊使用)
	 * @apiParam {Integer} preOrderId 原订单id (转诊使用)
	 * 
	 * @apiParam {String} diseaseDuration 病症时长
	 * @apiParam {String} diseaseDesc 病情描述
	 * @apiParam {String} isSeeDoctor 是否就诊
	 * @apiParam {String} treatCase 诊治情况
	 * @apiParam {String} drugCase 用药情况
	 * @apiParam {String} drugInfos （Json格式的字符串：[ { "drugId" :
	 *           "577e178ab522257e681a0d15", "drugCount":1, "drugName": "云南白药",
	 *           "specification": "10g", "packSpecification": "1瓶",
	 *           "drugImageUrl": "http://xxx.xxx.com/ynby.jpg", "manufacturer":
	 *           "云南制药厂" }, { "drugId" : "577e178ab522257e681a0d15",
	 *           "drugCount":1, "drugName": "云南黑药", "specification": "10g",
	 *           "packSpecification": "1瓶", "drugImageUrl":
	 *           "http://xxx.xxx.com/ynhy.jpg", "manufacturer": "云南制药厂" } ]）
	 * @apiParam {String[]} drugPicUrls 用药图片资料
	 * @apiParam {String} hopeHelp 希望获得的帮助
	 * @apiParam {String[]} picUrls 图片资料数组
	 * @apiParam {String} illHistoryInfoId 新版电子病历id
	 *
	 * @apiSuccess {String} orderId 订单ID
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * @apiSuccess {String} gid 订单会话ID
	 * @apiSuccess {Boolean} ifNewOrder 是否是新订单，如否，表示有正在进行的健康关怀患者订单
	 * 
	 * @apiAuthor wangl
	 * @author wangl
	 * @date 2016年7月22日
	 */
	@RequestMapping("/createOrder")
	public JSONMessage createOrder(OrderParam param) throws HttpApiException {
		PreOrderVO vo = orderService.processCreateOrder(param);

		// 免费的关怀计划订单创建就开始服务（因为端会立刻跳转到IM，需要及时处理，不能异步）
		if (null != vo.getIfNewOrder() && vo.getIfNewOrder()) {
			this.orderService.beginService4FreePlanImmediately(vo.getOrderId());
		}
		return JSONMessage.success(vo);
	}

	/**
	 * @apiIgnore deprecated
	 * @api {post} /pack/order/sendPayOrderByNotice2 医生发送关怀订单卡片给患者支付
	 * @apiVersion 1.0.0
	 * @apiName sendPayOrderByNotice2
	 * @apiGroup order
	 * @apiDescription 医生发送关怀订单卡片给患者支付
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} packId packId
	 * @apiParam {Integer[]} patientIds 患者数组
	 * @apiParam {String} groupId 集团ID
	 * 
	 * 
	 * @apiAuthor 肖伟
	 * @date 2015年12月19日
	 */
	// @RequestMapping(value="sendPayOrderByNotice2", method=RequestMethod.POST)
	// public JSONMessage sendPayOrderByNotice2(CarePlanParam param) {
	// param.setUserType(ReqUtil.instance.getUser().getUserType());
	// param.setDoctorsId(ReqUtil.instance.getUserId());
	// List<Integer> patientIdList = param.getPatientIds();
	// for (Integer patientId : patientIdList) {
	// orderService.saveSendPayOrderSingleByNotice2(param, patientId);
	// }
	// return JSONMessage.success();
	// }

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @api {post} /pack/order/sendPayOrderByNotice3 医生发送关怀订单卡片给患者支付
	 * @apiVersion 1.0.0
	 * @apiName sendPayOrderByNotice3
	 * @apiGroup order
	 * @apiDescription 医生发送关怀订单卡片给患者支付
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} packId packId
	 * @apiParam {Integer[]} patientIds 患者数组
	 * @apiParam {String} groupId 集团ID
	 * 
	 * @apiSuccess {List} list 每个患者的结果集
	 * @apiSuccess {String} list.orderId 订单ID
	 * @apiSuccess {Integer} list.orderStatus 订单状态
	 * @apiSuccess {String} list.gid 订单会话ID
	 * @apiSuccess {Boolean} list.ifNewOrder 是否是新订单，如否，表示有正在进行的健康关怀患者订单
	 * @apiSuccess {Patient} list.patient 患者信息
	 * @apiAuthor 肖伟
	 * @date 2017年01月06日
	 * 
	 *       此接口目前只有在关怀计划套餐里使用到
	 */
	@RequestMapping(value = "sendPayOrderByNotice3", method = RequestMethod.POST)
	public JSONMessage sendPayOrderByNotice3(@RequestParam Integer packId, @RequestParam List<Integer> patientIds) throws HttpApiException {
		String tag = "sendPayOrderByNotice3";
		List<PreOrderVO> list = new ArrayList<PreOrderVO>();
		for (Integer patientId : patientIds) {
			PreOrderVO vo = orderService.saveSendPayOrderSingleByNotice3(ReqUtil.instance.getUser().getUserType(), ReqUtil.instance.getUserId(),
					patientId, packId);
			list.add(vo);
		}

		logger.info("{}. list={}", tag, JSONUtil.toJSONString(list));
		if (CollectionUtils.isEmpty(list)) {
			return JSONMessage.success();
		}

		orderService.wrapPatients(list);

		return JSONMessage.success(null, list);
	}

	/**
	 * @api {get} /pack/order/recommendCarePack 推荐患者关怀计划
	 * @apiVersion 1.0.0
	 * @apiName recommendCarePack
	 * @apiGroup order
	 * @apiDescription 推荐患者关怀计划
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单Id
	 * @apiParam {Integer} followPackId 推荐关怀套餐Id
	 * 
	 * @apiAuthor 谢平
	 * @date 2016年4月8日
	 */
	@RequestMapping("recommendCarePack")
	public JSONMessage recommendCarePack(Integer orderId, Integer followPackId) {
		orderService.saveRecommendCarePack(orderId, followPackId);
		return JSONMessage.success();

	}

	/**
	 * @api {get} /pack/order/newOrder 患者生成订单（仅此 图文/电话 ）
	 * @apiVersion 1.0.0
	 * @apiName newOrder
	 * @apiGroup order
	 * @apiDescription 生成初始 图文/电话 订单和订单会话
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生id
	 * @apiParam {Integer} packId 套餐id
	 * @apiParam {Integer} patientId 病人id
	 * @apiParam {String} diseaseID 病情描述 多个使用","隔开
	 * @apiParam {String} diseaseDuration 病症时长
	 * @apiParam {String} diseaseDesc 病情描述
	 * @apiParam {String} isSeeDoctor 是否就诊
	 * @apiParam {String} treatCase 诊治情况
	 * @apiParam {String} drugCase 用药情况
	 * @apiParam {String} drugInfos （Json格式的字符串：[ { "drugId" :
	 *           "577e178ab522257e681a0d15", "drugCount":1, "drugName": "云南白药",
	 *           "specification": "10g", "packSpecification": "1瓶",
	 *           "drugImageUrl": "http://xxx.xxx.com/ynby.jpg", "manufacturer":
	 *           "云南制药厂" }, { "drugId" : "577e178ab522257e681a0d15",
	 *           "drugCount":1, "drugName": "云南黑药", "specification": "10g",
	 *           "packSpecification": "1瓶", "drugImageUrl":
	 *           "http://xxx.xxx.com/ynhy.jpg", "manufacturer": "云南制药厂" } ]）
	 * @apiParam {String[]} drugPicUrls 用药图片资料
	 * @apiParam {String} hopeHelp 希望获得的帮助
	 * @apiParam {String[]} picUrls 图片资料数组
	 * @apiParam {Integer} careOrderId 健康关怀订单id （用于区分在什么入口下订单）
	 * 
	 * @apiParam {Integer} orderType 订单类型（1:咨询套餐）
	 * @apiParam {String} expectAppointmentIds 期望预约时间id
	 * @apiParam {String} illHistoryInfoId 电子病历id
	 *
	 * @apiSuccess {String} orderId 订单ID
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * @apiSuccess {String} gid 订单会话ID
	 * 
	 * @apiAuthor wangl
	 * @author wangl
	 * @date 2016年12月8日11:50:57
	 */
	@RequestMapping("/newOrder")
	public JSONMessage newOrder(OrderParam param) {
		PreOrderVO vo = orderService.processNewOrder(param);
		return JSONMessage.success(vo);
	}

	/**
	 * @api {get} /pack/order/updateNewOrder 患者生成订单（仅此 图文/电话 ）
	 * @apiVersion 1.0.0
	 * @apiName newOrder
	 * @apiGroup order
	 * @apiDescription 生成初始 图文/电话 订单和订单会话
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {String} drugCase 用药情况
	 * @apiParam {String} expectAppointmentIds 期望预约时间id
	 * @apiParam {String} drugInfos （Json格式的字符串：[ { "drugId" :
	 *           "577e178ab522257e681a0d15", "drugCount":1, "drugName": "云南白药",
	 *           "specification": "10g", "packSpecification": "1瓶",
	 *           "drugImageUrl": "http://xxx.xxx.com/ynby.jpg", "manufacturer":
	 *           "云南制药厂" }, { "drugId" : "577e178ab522257e681a0d15",
	 *           "drugCount":1, "drugName": "云南黑药", "specification": "10g",
	 *           "packSpecification": "1瓶", "drugImageUrl":
	 *           "http://xxx.xxx.com/ynhy.jpg", "manufacturer": "云南制药厂" } ]）
	 * @apiParam {String[]} drugPicUrls 用药图片资料
	 * @apiParam {String} hopeHelp 希望获得的帮助
	 * @apiParam {String[]} picUrls 图片资料数组
	 *
	 *
	 * @apiSuccess {String} orderId 订单ID
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * @apiSuccess {String} gid 订单会话ID
	 *
	 * @apiAuthor wangl
	 * @author wangl
	 * @date 2016年12月8日11:50:57
	 */
	@RequestMapping("/updateNewOrder")
	public JSONMessage updateNewOrder(OrderParam param) throws HttpApiException {
		PreOrderVO vo = orderService.updateNewOrder(param);
		return JSONMessage.success(vo);
	}

	/**
	 * @api {get} /pack/order/getOldSession 图文/电话/健康关怀 下单判断
	 * @apiVersion 1.0.0
	 * @apiName getOldSession
	 * @apiGroup order
	 * @apiDescription 创建订单之前调用供客户端判断时候是初诊还是复诊
	 *
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生id 必需
	 * @apiParam {Integer} patientId 套餐id患者id 必需
	 * @apiParam {Integer} packId 套餐id 必需
	 * @apiParam {Integer} packType 套餐类型
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {Integer} careOrderId 健康关怀订单id （非必须）
	 *
	 * @apiSuccess {Integer} status 病历状态(1-无任何病历 2-和当前医生有一个病历
	 *             3-和当前医生没有病历or有两个以上的病历)
	 * @apiSuccess {String} illHistoryInfoId 病历id（status状态为2的时候，返回病历id）
	 * @apiSuccess {Integer} hasSession 初诊状态(2-复诊 3-初诊)
	 * @apiSuccess {Integer} newIllHistory 是否可以新建病历(0-不能 1-可以)
	 * @apiSuccess {String} msgGroupId 正在进行的会话消息id
	 * @apiSuccess {Integer} packType 正在进行的订单套餐类型
	 *
	 * @apiAuthor wangl
	 * @author wangl
	 * @date 2016-12-08 14:02:50
	 */
	@RequestMapping("/getOldSession")
	public JSONMessage getOldSession(@RequestParam(required = true) Integer doctorId,
			@RequestParam(required = true) Integer patientId, @RequestParam(required = true) Integer packId,
			Integer packType, Integer careOrderId, Integer orderId) {
		return JSONMessage.success(orderService.getOldSession(doctorId, ReqUtil.instance.getUserId(), patientId, packId,
				careOrderId, packType, orderId));
	}

	/**
	 * @api {get} /pack/order/getFirstSession 图文/电话/健康关怀 判断是否为初诊
	 * @apiVersion 1.0.0
	 * @apiName getFirstSession
	 * @apiGroup order
	 * @apiDescription 选择患者之后判断医生时候有病例，返回true和false
	 *
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生id 必需
	 * @apiParam {Integer} patientId 套餐id患者id 必需
	 * @apiParam {Integer} packId 套餐id 必需
	 * @apiParam {Integer} careOrderId 健康关怀订单id （非必须）
	 *
	 * @apiSuccess {Boolean} hasOrder 是否是初诊
	 * @apiSuccess {String} msgGroupId 正在进行的会话消息id
	 * @apiSuccess {Integer} packType 正在进行的订单套餐类型
	 *
	 * @apiAuthor wangl
	 * @author wangl
	 * @date 2016-12-08 14:02:50
	 */
	@RequestMapping("getFirstOrder")
	public JSONMessage getFirstOrder(@RequestParam(required = true) Integer doctorId,
			@RequestParam(required = true) Integer patientId) {

		return JSONMessage.success(false);
	}

	/**
	 * @apiIgnore deprecated 与createOrder接口合并
	 * @api {get} /pack/order/getOngoingCareOrderByPatient 获取患者当前正在进行的关怀计划订单
	 * @apiVersion 1.0.0
	 * @apiName getOngoingCareOrderByPatient
	 * @apiGroup order
	 * @apiDescription 获取患者当前正在进行的关怀计划订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生id
	 * @apiParam {Integer} packId 套餐id
	 * @apiParam {Integer} patientId 病人id
	 * @apiParam {Integer} orderType 订单类型
	 *           （1：咨询；2：报到；3：门诊订单；4：关怀计划套餐订单；5：随访套餐订单）空值则为套餐订单
	 * 
	 * @apiSuccess {String} orderId 订单ID
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * @apiSuccess {String} gid 订单会话ID
	 * 
	 * @apiAuthor 肖伟
	 * @author 肖伟
	 * @date 2016年10月18日
	 */
	// @RequestMapping(value="getOngoingCareOrderByPatient",
	// method=RequestMethod.GET)
	// @Deprecated
	// public JSONMessage getOngoingCareOrderByPatient(OrderParam param) {
	// return
	// JSONMessage.success(orderService.getOngoingCareOrderByPatient(param));
	// }

	/**
	 * @api {get} /pack/order/packDoctorList 查询订单医生的列表（含套餐）
	 * @apiVersion 1.0.0
	 * @apiName doctorPackList
	 * @apiGroup 根据订单id查询订单医生的列表（含套餐）
	 * @apiDescription 根据订单id查询订单医生的列表（含套餐）
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * 
	 * @apiSuccess {List} list 医生列表
	 * @apiSuccess {Integer} list.doctorId 医生id
	 * @apiSuccess {String} list.name 医生姓名
	 * @apiSuccess {String} list.title 医生职称
	 * @apiSuccess {String} list.departments 医生科室
	 * @apiSuccess {String} list.hospital 医生所在的医院
	 * @apiSuccess {String} list.headPicFileName 医生头像
	 * @apiSuccess {List} list.packList
	 *             返回在线门诊、图文咨询、电话咨询的集合，每个pack的详细结构可参看：/pack/pack/get返回的结构
	 * @apiAuthor 肖伟
	 * @date 2016年11月28日
	 */
	@RequestMapping(value = "packDoctorList", method = RequestMethod.GET)
	public JSONMessage packDoctorList(Integer orderId) {
		return JSONMessage.success(orderService.getPackDoctorListByOrder(orderId));
	}

	/**
	 * @api {get} /pack/order/takeTransferOrder 患者接受转诊订单
	 * @apiVersion 1.0.0
	 * @apiName takeTransferOrder
	 * @apiGroup 订单
	 * @apiDescription 患者接受转诊订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生id
	 * @apiParam {Integer} transferDoctorId 转诊医生id
	 * @apiParam {Long} transferTime 转诊时间
	 * @apiParam {Integer} preOrderId 原订单id
	 * @apiParam {Integer} packId 套餐id
	 * @apiParam {Integer} orderType 订单类型 （1：套餐订单；3：门诊订单；4：关怀计划套餐订单；）空值则为套餐订单
	 * 
	 * @apiSuccess {String} orderId 订单ID
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * @apiSuccess {String} gid 订单会话ID
	 * 
	 * @apiAuthor wangl
	 * @date 2016年5月12日
	 */
	@RequestMapping("/takeTransferOrder")
	public JSONMessage takeTransferOrder(OrderParam param) throws HttpApiException {
		param.setUserId(ReqUtil.instance.getUserId());
		UserSession session = ReqUtil.instance.getUser();
		if (isPhoneOrder(param)) {
			ConsultOrderParam param1 = BeanUtil.copy(param, ConsultOrderParam.class);
			return JSONMessage.success(guideService.createConsultOrder(param1, session));
		}
		return JSONMessage.success(orderService.add(param, session));
	}

	/**
	 * @api {get} /pack/order/takeOrderByIllCase 根据病诊纪录生成订单
	 * @apiVersion 1.0.0
	 * @apiName takeOrderByIllCase
	 * @apiGroup 订单
	 * @apiDescription 根据病诊纪录生成订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生id
	 * @apiParam {String} illCaseInfoId 病情ID
	 * @apiParam {Integer} packId 套餐id
	 * @apiParam {Integer} orderType
	 *           订单类型（1：套餐订单；2：报到；3：门诊订单；4：关怀计划套餐订单；5：随访套餐订单）空值则为套餐订单
	 * @apiParam {String} offlineItemId 名医面对面订单会传递预约条目id
	 * 
	 * 
	 * @apiSuccess {String} orderId 订单ID
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * @apiSuccess {String} gid 订单会话ID
	 * 
	 * @apiAuthor 张垠
	 * @date 2016年6月22日
	 */
	@RequestMapping("/takeOrderByIllCase")
	public JSONMessage takeOrderByIllCase(OrderParam param) throws HttpApiException {
		UserSession session = ReqUtil.instance.getUser();
		param.setUserId(ReqUtil.instance.getUserId());
		if (isPhoneOrder(param)) {
			ConsultOrderParam param1 = BeanUtil.copy(param, ConsultOrderParam.class);
			param1.setIllCaseInfoId(param.getIllCaseInfoId());
			param1.setIsIllCaseCommit(true);
			return JSONMessage.success(guideService.createConsultOrder(param1, session));
		}
		// 根据illCaseInfo的ID可找到订单，根据订单找到之前的患者，疾病ID
		if (StringUtil.isEmpty(param.getIllCaseInfoId())) {
			throw new ServiceException("illCaseInfoId 不能为空");
		}
		param.setIsIllCaseCommit(true);
		return JSONMessage.success(orderService.add(param, session));
	}

	/**
	 * 判断是否是电话订单
	 * 
	 * @param param
	 * @return
	 */
	private boolean isPhoneOrder(OrderParam param) {
		if ((param.getOrderType() == null) || (param.getOrderType() == OrderEnum.OrderType.order.getIndex())) {
			if (param.getPackId() == null) {
				throw new ServiceException("请正确选择套餐");
			}
			// 查询套餐信息，判断该套餐是否属于该医生
			Pack pack = packService.getPack(param.getPackId());
			if (pack == null) {
				throw new ServiceException("请正确选择套餐");
			}
			if (pack.getDoctorId().intValue() != param.getDoctorId().intValue()) {
				throw new ServiceException("套餐有误，请正确选择套餐");
			}
			return pack.getPackType() == PackEnum.PackType.phone.getIndex();
		}
		return false;
	}

	/**
	 * @api {get} /pack/order/updateCareCorder 健康关怀订单填写病情资料
	 * @apiVersion 1.0.0
	 * @apiName updateCareCorder
	 * @apiGroup 订单
	 * @apiDescription 健康关怀订单填写病情资料
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {Integer} patientId 病人id
	 * @apiParam {Integer} diseaseDesc 病情描述
	 * @apiParam {String} telephone 电话号码
	 * @apiParam {String[]} imagePaths 病情图
	 * @apiParam {String} isSee 是否就诊 "false" 没有 "true" 有
	 * @apiParam {String} seeDoctorMsg 患者 在医院诊治情况
	 * 
	 * @apiSuccess {String} resultCode 返回码
	 * 
	 * @apiAuthor wangl
	 * @date 2016年3月12日
	 */
	@RequestMapping("/updateCareCorder")
	public JSONMessage updateCareCorder(OrderParam param) {
		orderService.updateCareCorder(param);
		return JSONMessage.success();
	}

	/**
	 * @api {get} /pack/order/hasIllCase 订单是否有电子病历
	 * @apiVersion 1.0.0
	 * @apiName hasIllCase
	 * @apiGroup 订单
	 * @apiDescription 订单是否有电子病历
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * 
	 * @apiSuccess {Integer} data 1:有，0：没有
	 * 
	 * @apiAuthor wangl
	 * @date 2016年3月12日
	 */
	@RequestMapping("/hasIllCase")
	public JSONMessage hasIllCase(@RequestParam(required = true) Integer orderId) {
		return JSONMessage.success(orderService.hasIllCase(orderId));
	}

	/**
	 * @api {get} /pack/order/getIllCase 订单是否有电子病历
	 * @apiVersion 1.0.0
	 * @apiName getIllCase
	 * @apiGroup 订单
	 * @apiDescription 订单是否有电子病历，有则返回病情id
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * 
	 * @apiSuccess {Integer} hasIllCase 1:有，0：没有
	 * @apiSuccess {String} IllCaseInfoId 病情id
	 * 
	 * @apiAuthor 傅永德
	 * @date 2016年9月22日
	 */
	@RequestMapping("/getIllCase")
	public JSONMessage getIllCase(@RequestParam(required = true) Integer orderId) {
		return JSONMessage.success(orderService.getIllCase(orderId));
	}

	/**
	 * @api {get} /pack/order/modifyOrder PC端订单信息修改
	 * @apiVersion 1.0.0
	 * @apiName modifyOrder
	 * @apiGroup 订单
	 * @apiDescription 生成初始订单和充值信息
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生id
	 * @apiParam {Integer} packId 套餐id
	 * @apiParam {Integer} patientId 病人id
	 * @apiParam {Integer} diseaseDesc 病情描述
	 * @apiParam {String} voice 病情语音
	 * @apiParam {String} telephone 电话号码
	 * @apiParam {String[]} imagePaths 病情图
	 * @apiParam {Integer} orderType 订单类型
	 *           （1：套餐订单；2：报到；3：门诊订单；4：关怀计划套餐订单；5：随访套餐订单）空值则为套餐订单
	 * 
	 * @apiSuccess {String} orderId 订单ID
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * @apiSuccess {String} gid 订单会话ID
	 * 
	 * @apiAuthor 姜宏杰
	 * @date 2016年1月19日15:18:57
	 */
	@RequestMapping("/modifyOrder")
	public JSONMessage modifyOrder(OrderParam param) {
		param.setUserId(ReqUtil.instance.getUserId());
		if (org.apache.commons.lang3.StringUtils.isBlank(param.getDiseaseDesc())) {
			throw new ServiceException("病情描述不能为空");
		}
		/*
		 * UserSession session = ReqUtil.instance.getUser(); Pack pack = null; if
		 * (param.getOrderType() == null || param.getOrderType() ==
		 * OrderEnum.OrderType.order.getIndex()) { // 只有套餐类开型订单才需判断套餐Id不能为空 if
		 * (param.getPackId() == null) { throw new ServiceException("请正确选择套餐");
		 * } // 查询套餐信息，判断该套餐是否属于该医生 pack =
		 * packService.getPack(param.getPackId()); if (pack == null) { throw new
		 * ServiceException("请正确选择套餐"); } if (pack.getDoctorId().intValue() !=
		 * param.getDoctorId().intValue()) { throw new
		 * ServiceException("套餐有误，请正确选择套餐"); } if (pack.getPackType() ==
		 * PackEnum.PackType.phone.getIndex()) { ConsultOrderParam param1 =
		 * BeanUtil.copy(param, ConsultOrderParam.class); GroupInfo groupInfo =
		 * guideService.createConsultOrder(param1, session); return
		 * JSONMessage.success(null, groupInfo); } }
		 */
		return JSONMessage.success(null, orderService.modifyOrder(param));
	}

	/**
	 * @api {get} /pack/order/throughTrainOrder 生成直通车订单
	 * @apiVersion 1.0.0
	 * @apiName throughTrainOrder
	 * @apiGroup 订单
	 * @apiDescription 生成直通车订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} patientId 病人id
	 * @apiParam {Integer} doctorId 医生id
	 * @apiParam {Integer} packType 套餐类型 （5就医直通车、6专家直通车、7检查直通车）
	 * @apiParam {Long} price 价格，以分为单位
	 * @apiParam {String} remarks 医院（备注字段暂存线下医院）
	 * @apiParam {Integer} diseaseDesc 病情描述
	 * @apiParam {String} voice 病情语音
	 * @apiParam {String} telephone 电话号码
	 * @apiParam {String[]} imagePaths 病情图
	 * 
	 * @apiSuccess {com.dachen.health.pack.order.entity.po.Order} order 订单信息
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年12月23日
	 */
	@RequestMapping("/throughTrainOrder")
	public JSONMessage throughTrainOrder(OrderParam param) {
		param.setUserId(ReqUtil.instance.getUserId());
		return JSONMessage.success("success", orderService.addThroughTrainOrder(param));
	}

	/**
	 * @api {get} /pack/order/takeConsultation 生成会诊订单
	 * @apiVersion 1.0.0
	 * @apiName takeConsultation
	 * @apiGroup 订单
	 * @apiDescription 生成会诊订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {String} illCaseId 病历id
	 * @apiParam {Integer} conDoctorId 会诊医生id
	 * 
	 * @apiSuccess {com.dachen.health.pack.order.entity.po.Order} order 订单信息
	 * 
	 * @apiAuthor 谢平
	 * @date 2016年1月20日
	 */
	@RequestMapping("/takeConsultation")
	public JSONMessage takeConsultation(String illCaseId, Integer conDoctorId) throws HttpApiException {
		return JSONMessage.success(orderService.addConsultation(illCaseId, conDoctorId));
	}
	
	
	/**
	 * @api {get} /pack/order/takeNewConsultation 新的生成会诊订单
	 * @apiVersion 1.0.0
	 * @apiName takeNewConsultation
	 * @apiGroup 订单
	 * @apiDescription 新的生成会诊订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {String} illHistoryInfoId 病历id
	 * @apiParam {Integer} conDoctorId 会诊医生id
	 *
	 * @apiSuccess {com.dachen.health.pack.order.entity.po.Order} order 订单信息
	 *
	 * @apiAuthor 李明
	 * @date 2016年12月28日11:33:34
	 */
	@RequestMapping("takeNewConsultation")
	public JSONMessage takeNewConsultation(String illHistoryInfoId, Integer conDoctorId) throws HttpApiException {
		return JSONMessage.success(orderService.addNewConsultation(illHistoryInfoId, conDoctorId));

	}
	
	
	/**
	 * @api {get} /pack/order/takeConsultationWithPack 生成会诊订单
	 * @apiVersion 1.0.0
	 * @apiName takeConsultationWithPack
	 * @apiGroup 订单
	 * @apiDescription 通过会诊包生成会诊订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {String} illHistoryInfoId 病历id
	 * @apiParam {String} consultationId 会诊包ID
	 *
	 * @apiSuccess {com.dachen.health.pack.order.entity.po.Order} order 订单信息
	 *
	 * @apiAuthor 张垠
	 * @date 2017年2月23日
	 */
	@RequestMapping("takeConsultationWithPack")
	public JSONMessage takeConsultationWithPack(String illHistoryInfoId, String  consultationId) throws HttpApiException {
		return JSONMessage.success(orderService.addNewConsultation(illHistoryInfoId, consultationId));

	}
	/**
	 * @api {get} /pack/order/commitTreatAdvise 提交或更新诊疗建议
	 * @apiVersion 1.0.0
	 * @apiName commitTreatAdvise
	 * @apiGroup 订单
	 * @apiDescription 提交或更新诊疗建议
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {String} treatAdvise 诊疗建议
	 *
	 *
	 * @apiAuthor 张垠
	 * @date 2017年2月23日
	 */
	@RequestMapping("commitTreatAdvise")
	public JSONMessage commitTreatAdvise(Integer orderId,String treatAdvise ) throws HttpApiException {
		orderService.updateTreatAdvise(orderId, treatAdvise);
		return JSONMessage.success();
	}
	/**
	 * @api {get} /pack/order/getTreatAdvise 会诊订单当前医生诊疗建议
	 * @apiVersion 1.0.0
	 * @apiName getTreatAdvise
	 * @apiGroup 订单
	 * @apiDescription 会诊订单当前医生诊疗建议
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 *
	 * @apiSuccess {String} resultMsg 当前医生诊疗建议
	 */
	@RequestMapping("getTreatAdvise")
	public JSONMessage getTreatAdvise(Integer orderId){
		return JSONMessage.success(orderService.getTreatAdvise(orderId));
	}
	/**
	 * @api {get} /pack/order/getTreatAdviseList 会诊订单参与医生诊疗建议
	 * @apiVersion 1.0.0
	 * @apiName getTreatAdviseList
	 * @apiGroup 订单
	 * @apiDescription 会诊订单参与医生诊疗建议
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 *
	 * @apiSuccess {com.dachen.health.pack.order.entity.po.OrderExt} order 会诊订单参与医生诊疗建议
	 * @apiSuccess {String} name 医生名字
	 * @apiSuccess {String} title 医生职称
	 * @apiSuccess {String} department 医生部门
	 * @apiSuccess {String} hospital 医生医院
	 * @apiSuccess {String} pic 医生头像
	 *
	 * @date 2017年2月23日
	 */
	@RequestMapping("getTreatAdviseList")
	public JSONMessage getTreatAdviseList(Integer orderId){
		return JSONMessage.success(orderService.getTreatAdviseAndDocInfoList(orderId));
	}
	
	/**
	 * @api {get} /pack/order/getDoctorSplit 获取订单相关医生分成比例
	 * @apiVersion 1.0.0
	 * @apiName getDoctorSplit
	 * @apiGroup 订单
	 * @apiDescription 获取订单相关医生分成比例
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 *
	 * @apiSuccess {com.dachen.health.pack.order.entity.po.OrderDoctor} order 订单参与医生分成比例
	 *
	 * @date 2017年2月23日
	 */
	@RequestMapping("getDoctorSplit")
	public JSONMessage getDoctorSplit(Integer orderId){
		return JSONMessage.success(orderDoctorService.findOrderDoctors(orderId));
	}
	
	/**
	 * @api {get} /pack/order/getGroupNameByOrderId
	 * @apiVersion 1.0.0
	 * @apiName getGroupNameByOrderId
	 * @apiGroup 订单
	 * @apiDescription 获取会诊订单所属的群组名称
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * 
	 * @apiSuccess resultCode 返回码
	 * @apiSuccess data 集团名称
	 * 
	 * @apiAuthor wangl
	 * @date 2016年3月15日
	 */
	public JSONMessage getGroupNameByOrderId(@RequestParam(required = true) Integer orderId) {
		return JSONMessage.success(orderService.getGroupNameByOrderId(orderId));
	}

	/**
	 * @api {get} /pack/order/resubmitConsultation 重新提交会诊订单
	 * @apiVersion 1.0.0
	 * @apiName resubmitConsultation
	 * @apiGroup 订单
	 * @apiDescription 完善病历后重新提交会诊订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * 
	 * @apiSuccess {Number} resultCode 标志
	 * 
	 * @apiAuthor 谢平
	 * @date 2016年1月20日
	 */
	@RequestMapping("/resubmitConsultation")
	public JSONMessage resubmitConsultation(Integer orderId) throws HttpApiException {
		orderService.resubmitConsultation(orderId);
		return JSONMessage.success();
	}

	/**
	 * @api {get} /pack/order/confirmConsultation 接受会诊订单
	 * @apiVersion 1.0.0
	 * @apiName confirmConsultation
	 * @apiGroup 订单
	 * @apiDescription 接受会诊订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {String} groupId 集团Id(可不传递)
	 * @apiParam {Long} oppointTime 预约时间
	 * 
	 * @apiSuccess {Number} resultCode 标志
	 * 
	 * @apiAuthor 谢平
	 * @date 2016年1月20日
	 */
	@RequestMapping("/confirmConsultation")
	public JSONMessage confirmConsultation(OrderParam param) throws HttpApiException {
		orderService.confirmConsultation(param, ReqUtil.instance.getUserId());
		return JSONMessage.success(null);
	}
	

	/**
	 * @api {get} /pack/order/cancelConsultation 拒绝会诊订单
	 * @apiVersion 1.0.0
	 * @apiName cancelConsultation
	 * @apiGroup 订单
	 * @apiDescription 拒绝会诊订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {Integer} reason 拒绝理由（1病历不够详细、2不想接单）
	 * 
	 * @apiSuccess {Number} resultCode 标志
	 * 
	 * @apiAuthor 谢平
	 * @date 2016年1月20日
	 */
	@RequestMapping("/cancelConsultation")
	public JSONMessage cancelConsultation(Integer orderId, Integer reason) throws HttpApiException {
		orderService.cancelConsultation(orderId, reason, ReqUtil.instance.getUserId());
		return JSONMessage.success(null);
	}

	/**
	 * @api {get} /pack/order/cancelThroughTrainOrder 取消直通车订单
	 * @apiVersion 1.0.0
	 * @apiName 取消直通车订单
	 * @apiGroup 订单
	 * @apiDescription 取消直通车订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单Id
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年12月23日
	 */
	@RequestMapping("/cancelThroughTrainOrder")
	public JSONMessage cancelThroughTrainOrder(Integer orderId) {
		orderService.cancelThroughTrainOrder(orderId);
		return JSONMessage.success("success");
	}

	/**
	 * @api {get} /pack/order/updateOrderStatus 修改订单状态
	 * @apiVersion 1.0.0
	 * @apiName updateOrderStatus
	 * @apiGroup 订单
	 * @apiDescription 修改订单状态（线下护士使用）
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单Id
	 * @apiParam {Integer} orderStatus 订单状态（1待预约、2待支付、3已支付、4已完成、5已取消、6进行中、7待完善）
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年12月24日
	 */
	@RequestMapping("/updateOrderStatus")
	public JSONMessage updateOrderStatus(OrderParam param) {
		orderService.updateOrderStatus(param);
		return JSONMessage.success("success");
	}

	/**
	 * @api {get} /pack/order/getOrderStatus 获取订单状态
	 * @apiVersion 1.0.0
	 * @apiName getOrderStatus
	 * @apiGroup 订单
	 * @apiDescription 获取订单状态
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单Id
	 * 
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年12月24日
	 */
	@RequestMapping("/getOrderStatus")
	public JSONMessage getOrderStatus(OrderParam param) {
		return JSONMessage.success("success", orderService.getOrderStatus(param));
	}

	/**
	 * @api {get} /pack/order/getByStatus 获取直通车订单列表
	 * @apiVersion 1.0.0
	 * @apiName getByStatus
	 * @apiGroup 订单
	 * @apiDescription 获取直通车订单列表
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderStatus 订单状态
	 * 
	 * @apiSuccess {Order} order 订单详情
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年12月24日
	 */
	@RequestMapping("/getByStatus")
	public JSONMessage getByStatus(OrderParam param) {
		param.setOrderType(OrderEnum.OrderType.throughTrain.getIndex());
		return JSONMessage.success("success", orderService.getOrders(param));
	}

	/**
	 * @api {get} /pack/order/takePreChargeOrder 生成待支付订单
	 * @apiVersion 1.0.0
	 * @apiName takePreChargeOrder
	 * @apiGroup 订单
	 * @apiDescription 生成待支付订单和充值信息
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生id
	 * @apiParam {Integer} packId 套餐id
	 * @apiParam {Integer} patientId 病人id
	 * @apiParam {Integer} diseaseDesc 病情描述
	 * @apiParam {Integer} voice 语音描述
	 * @apiParam {String} telephone 电话号码
	 * @apiParam {String[]} imagePaths 病情图
	 * @apiParam {Integer} orderType 订单类型 （1：套餐订单；2：报到；3：门诊订单）空值则为套餐订单
	 * 
	 * @apiSuccess {String} orderId 订单ID
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * @apiSuccess {String} gid 订单会话ID
	 * @apiAuthor 张垠
	 * @date 2015年10月10日
	 */
	@RequestMapping("/takePreChargeOrder")
	public JSONMessage takePreChargeOrder(OrderParam param) throws HttpApiException {
		param.setUserId(ReqUtil.instance.getUserId());
		UserSession session = ReqUtil.instance.getUser();

		if ((param.getOrderType() == null) || (param.getOrderType() == OrderEnum.OrderType.order.getIndex())) {
			Pack pack = null;
			// 只有套餐类开型订单才需判断套餐Id不能为空
			if (param.getPackId() == null) {
				throw new ServiceException("请正确选择套餐");
			}
			// 查询套餐信息，判断该套餐是否属于该医生
			pack = packService.getPack(param.getPackId());
			if (pack == null) {
				throw new ServiceException("请正确选择套餐");
			}
			if (pack.getDoctorId().intValue() != param.getDoctorId().intValue()) {
				throw new ServiceException("套餐有误，请正确选择套餐");
			}
			if (pack.getPackType() == PackEnum.PackType.phone.getIndex()) {
				ConsultOrderParam param1 = BeanUtil.copy(param, ConsultOrderParam.class);
				GroupInfo groupInfo = guideService.createConsultOrder(param1, session);
				return JSONMessage.success(null, groupInfo);
			}
		}
		return JSONMessage.success(null, orderService.addPreCharge(param, session));
	}

	/**
	 * @api {get} /pack/order/takePreChargeSession 生成待支付订单会话
	 * @apiVersion 1.0.0
	 * @apiName takePreChargeSession
	 * @apiGroup 订单
	 * @apiDescription 生成待支付订单会话
	 *
	 * @apiParam {Integer} orderId 订单ID
	 * @apiParam {long} oppointTime 预约时间
	 * @apiParam {Integer} type 组类型
	 * @apiParam {String} gtype 关系类型
	 * @apiParam {String} fromUserId 创建者ID
	 * @apiParam {String} toUserId 组成员id（多人用 | 分隔）
	 * 
	 * @apiSuccess {String} orderId 订单ID
	 * @apiSuccess {String} msgGroupId 订单状态
	 * @apiSuccess {String} sessionId 订单会话ID
	 * @apiAuthor 张垠
	 * @date 2015年10月12日
	 */
	public JSONMessage takePreChargeSession(OrderParam param, CreateGroupRequestMessage createGroupParam) throws HttpApiException {
		if (param.getOrderId() == null) {
			throw new ServiceException("请选择合法的订单（null）");
		}
		return JSONMessage.success(null, orderService.addPreChargeSession(param, createGroupParam));

	}

	/**
	 * @api {get} /pack/order/payOrder 支付订单
	 * @apiVersion 1.0.0
	 * @apiName payOrder
	 * @apiGroup 订单
	 * @apiDescription 生成初始订单和充值信息
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Intege} orderId 订单ID
	 * @apiParam {Intege} payType 支付方式 1:微信,2:支付宝,3:余额,12:积分问诊
	 * 
	 * @apiSuccess {String} payNo 订单编号
	 * @apiSuccess {String} orderInfo 支付宝订单支付详情
	 * @apiSuccess {Object} payReq 微信支付详情
	 * @apiSuccess {String} payReq.appid 公众账号ID
	 * @apiSuccess {String} payReq.partnerid 商户号
	 * @apiSuccess {String} payReq.prepayid 预支付交易会话ID
	 * @apiSuccess {String} payReq.packageValue 扩展字段
	 * @apiSuccess {String} payReq.timestamp 时间戳
	 * @apiSuccess {String} payReq.noncestr 随机字符串
	 * @apiSuccess {String} payReq.sign 签名
	 * 
	 * @apiAuthor 谢佩
	 * @date 2015年7月2日
	 */
	@RequestMapping("/payOrder")
	public JSONMessage payOrder(OrderParam param) throws HttpApiException {
		param.setUserId(ReqUtil.instance.getUserId());
		return JSONMessage.success(null, orderService.addPayOrder(param, ReqUtil.instance.getUser()));
	}

	/**
	 * @api {get} /pack/order/payOrder4BDJL 支付订单（博德嘉联）
	 * @apiVersion 1.0.0
	 * @apiName payOrder4BDJL
	 * @apiGroup 订单
	 * @apiDescription 生成初始订单和充值信息
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Intege} orderId 订单ID
	 * @apiParam {Intege} payType 支付方式 1:微信,2:支付宝,3:余额
	 * 
	 * @apiSuccess {String} payNo 订单编号
	 * @apiSuccess {String} orderInfo 支付宝订单支付详情
	 * @apiSuccess {Object} payReq 微信支付详情
	 * @apiSuccess {String} payReq.appid 公众账号ID
	 * @apiSuccess {String} payReq.partnerid 商户号
	 * @apiSuccess {String} payReq.prepayid 预支付交易会话ID
	 * @apiSuccess {String} payReq.packageValue 扩展字段
	 * @apiSuccess {String} payReq.timestamp 时间戳
	 * @apiSuccess {String} payReq.noncestr 随机字符串
	 * @apiSuccess {String} payReq.sign 签名
	 * 
	 * @apiAuthor 谢佩
	 * @date 2015年7月2日
	 */
	@RequestMapping("/payOrder4BDJL")
	public JSONMessage payOrder4BDJL(OrderParam param) throws HttpApiException {
		param.setUserId(ReqUtil.instance.getUserId());
		return JSONMessage.success(null, orderService.addPayOrder(param, ReqUtil.instance.getUser(), true));
	}

	/**
	 * @api {get} /pack/order/findOrders 订单列表
	 * @apiVersion 1.0.0
	 * @apiName findOrders
	 * @apiGroup 订单
	 * @apiDescription 查询订单数据,如果根据医生查，则需要查到自己作为参与医生的关怀计划订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderStatus 订单状态 1:待预约，2：待支付，3：已支付，4：已完成，5：已取消
	 *           {@link OrderEnum.OrderStatus}
	 * @apiParam {Integer} pageIndex 页码 起使页为0
	 * @apiParam {Integer} userId 患者ID
	 * @apiParam {String} orderNo 订单号
	 * @apiParam {Integer} doctorId 医生ID
	 * @apiParam {Integer} orderType 订单类型
	 * @apiParam {String} groupId 集团ID
	 * @apiParam {String} userName 用户名
	 * @apiParam {String} telephone 电话
	 * @apiParam {String} diseaseTel 病情手机号
	 * @apiParam {String} packType 套餐类型
	 * @apiParam {String} payType 支付类型，退款专用默认值：2
	 *
	 * @apiParam {Integer} point 积分
	 * @apiParam {String} pointName 积分问诊套餐名称
	 *
	 * @apiSuccess {Integer} pageCount 总页数
	 * @apiSuccess {Integer} pageIndex 当前页数
	 * @apiSuccess {Long} total 总记录数
	 * 
	 * @apiSuccess {List} pageData.OrderVO 订单数据对象
	 * @apiSuccess {Integer} OrderVO.orderId 订单ID
	 * @apiSuccess {Integer} OrderVO.doctorId 医生ID
	 * @apiSuccess {Integer} OrderVO.orderType 订单类型
	 *             1：咨询套餐2：患者报道,3:门诊,4:健康关怀,5:随访计划
	 * @apiSuccess {Integer} OrderVO.packType 套餐类型 1：消息图文2:电话咨询
	 *             (报到类订单没有套餐为0),3:关怀计划，4：随访计划
	 * @apiSuccess {Integer} OrderVO.timeLong 套餐时间
	 * 
	 * @apiSuccess {Short} OrderVO.sex 患者性别
	 * @apiSuccess {Integer} OrderVO.age 患者年龄1:男，2:女，３保密
	 * @apiSuccess {String} OrderVO.topPath 患者头像
	 * @apiSuccess {String} OrderVO.area 患者地区
	 * @apiSuccess {String} OrderVO.relation 患者关系
	 * @apiSuccess {String} OrderVO.telephone 患者电话
	 * @apiSuccess {Long} OrderVO.price 套餐价格
	 * @apiSuccess {Integer} OrderVO.diseaseId 病情ID
	 * @apiSuccess {Integer} OrderVO.orderStatus 订单状态
	 * @apiSuccess {Integer} OrderVO.refundStatus 订单退款状态 1:未退款，3：已退款
	 * @apiSuccess {Long} OrderVO.createTime 创建时间单位毫秒数
	 * @apiSuccess {Long} OrderVO.payTime 支付订单时间单位毫秒数
	 * @apiSuccess {Long} OrderVO.payType 支付类型 1:微信支付，2：支付宝
	 * 
	 * @apiSuccess {Object} OrderVO.userVo 当前登陆患者信息
	 * @apiSuccess {Integer} userVo.userId 患者ID
	 * @apiSuccess {String} userVo.userName 患者名字
	 * @apiSuccess {String} userVo.headPriPath 患者图片
	 * 
	 * @apiSuccess {Object} OrderVO.doctorVo 医生信息,如果是患者端则返回医生信息
	 * @apiSuccess {String} doctorVo.doctorName 医生名称
	 * @apiSuccess {String} doctorVo.doctorPath 医生头像图片
	 * @apiSuccess {String} doctorVo.doctorSpecialty 医生所属专科
	 * @apiSuccess {String} doctorVo.doctorGroup 医生集团名称
	 * 
	 * @apiSuccess {String} OrderVO.orderSession 订单会话对象
	 * @apiSuccess {String} orderSession.msgGroupId 会话组id
	 * 
	 * @apiSuccess {Object} nurseVo 护士数据对象
	 * @apiSuccess {String} nurseVo.name 护士姓名
	 * @apiSuccess {String} nurseVo.telephone 护士电话
	 * 
	 * @apiAuthor 谢佩
	 * @date 2015年9月9日
	 */
	@RequestMapping("/findOrders")
	public JSONMessage findOrders(OrderParam param) {
		return JSONMessage.success(null, orderService.findOrders(param, ReqUtil.instance.getUser()));
	}

	/**
	 * @api {get} /pack/order/getRefundOrders 退款订单列表
	 * @apiVersion 1.0.0
	 * @apiName getRefundOrders
	 * @apiGroup 订单
	 * @apiDescription 退款订单列表
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} pageIndex 页码 起使页为0
	 * @apiParam {Integer} pageSize 每页显示条数，不传默认15条
	 * @apiParam {Integer} packType 套餐类型
	 *           1：消息图文，2：电话咨询，3：关怀计划，4：随访计划，8：会诊套餐，9：预约名医
	 * @apiParam {Integer} orderType 3：门诊
	 * @apiParam {String} orderNo 订单号
	 * @apiParam {Integer} refundStatus 退款状态
	 * @apiParam {String} userName 用户名
	 * @apiParam {String} telephone 电话
	 * 
	 * @apiSuccess {Integer} pageCount 总页数
	 * @apiSuccess {Integer} pageIndex 当前页数
	 * @apiSuccess {Long} total 总记录数
	 * @apiSuccess {List} pageData.OrderVO 订单数据对象
	 * @apiSuccess {Integer} OrderVO.orderId 订单ID
	 * @apiSuccess {Integer} OrderVO.orderNo 订单号
	 * @apiSuccess {Integer} OrderVO.doctorName 医生名称
	 * @apiSuccess {Integer} OrderVO.packType 套餐类型
	 *             1：消息图文，2：电话咨询，3：关怀计划，4：随访计划，8：会诊套餐，9：预约名医
	 * @apiSuccess {Long} OrderVO.price 套餐价格
	 * @apiSuccess {Integer} OrderVO.refundStatus 订单退款状态 2：待退款，3：退款成功，4：退款失败
	 * @apiSuccess {Long} OrderVO.createTime 支付时间，单位毫秒数
	 * @apiSuccess {Integer} OrderVO.payType 支付类型 1:微信支付，2：支付宝
	 * @apiSuccess {String} OrderVO.relation 患者关系
	 * @apiSuccess {String} OrderVO.userName 用户名字
	 * @apiSuccess {String} OrderVO.patientName 患者名字
	 * 
	 * @apiSuccess {String} OrderVO.doctorName 医生名称
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年9月9日
	 */
	@RequestMapping("/getRefundOrders")
	public JSONMessage getRefundOrders(OrderParam param) {
		return JSONMessage.success(orderRefundService.getRefundOrders(param));
	}

	/*
	 * @api {get} /pack/order/checkOrderPayStatus 检查订单状态
	 * 
	 * @apiVersion 1.0.0
	 * 
	 * @apiName checkOrderPayStatus
	 * 
	 * @apiGroup 订单
	 * 
	 * @apiDescription 查询支付订单状态
	 *
	 * @apiParam {String} access_token token
	 * 
	 * @apiParam {Integer} payNo 订单编号
	 * 
	 * @apiParam {Intege} payType 支付方式 1:微信,2:支付宝,3:余额
	 * 
	 * 
	 * @apiAuthor 谢佩
	 * 
	 * @date 2015年8月19日
	 */
	// @RequestMapping("/checkOrderPayStatus")
	public JSONMessage checkPayStatusByOrder(CheckOrderParam param) {
		param.setUserId(ReqUtil.instance.getUserId());
		return JSONMessage.success(null, orderService.checkOrderStatus(param, ReqUtil.instance.getUser()));
	}

	/**
	 * @api {get} /pack/order/detail 订单详情
	 * @apiVersion 1.0.0
	 * @apiName detail
	 * @apiGroup 订单
	 * @apiDescription 订单详情
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * 
	 * @apiSuccess {String} diseaseInfoNow 现病史
	 * @apiSuccess {String} diseaseInfoOld 既往史
	 * @apiSuccess {String} familyDiseaseInfo 家族史
	 * @apiSuccess {String} heigth 身高
	 * @apiSuccess {String} diseaseDesc 病情信息
	 * @apiSuccess {String} isSeeDoctor 是否就诊过
	 * @apiSuccess {String} menstruationdiseaseInfo 月经史
	 * @apiSuccess {String} profession 职业
	 * @apiSuccess {String} relation 关系
	 * @apiSuccess {String} isSeeDoctor 是否到医院就诊过 （True，False）
	 * @apiSuccess {String} seeDoctorMsg 诊治情况
	 * @apiSuccess {String} profession 职业
	 * @apiSuccess {String} marriage 婚姻
	 * @apiSuccess {String} weigth 体重
	 * @apiSuccess {Long} visitTime 就诊时间
	 * @apiSuccess {String[]} imgStringPath 病情或病例图片
	 * 
	 * @apiSuccess {List} cureRecordList 诊疗记录
	 * @apiSuccess {Object} cureRecordList.cureRecord 诊疗记录详情
	 * @apiSuccess {String} cureRecordList.cureRecord.treatAdvise 治疗建议
	 * @apiSuccess {String} cureRecordList.cureRecord.drugAdvise 用药建议
	 * @apiSuccess {String} cureRecordList.cureRecord.attention 检查建议（弃用）
	 * @apiSuccess {CheckSuggest[]} cureRecordList.cureRecord.checkSuggestList
	 *             检查建议对象数组（2015-11-16开始使用）
	 * @apiSuccess {String[]} cureRecordList.cureRecord.images 图片地址
	 * @apiSuccess {String[]} cureRecordList.cureRecord.voices 语音地址
	 * @apiSuccess {String} cureRecordList.cureRecord.consultAdvise 咨询结果
	 * 
	 * 
	 * @apiSuccess {Object} orderVO 订单信息
	 * @apiSuccess {Integer} orderVO.orderId 订单ID
	 * @apiSuccess {Integer} orderVO.doctorId 医生ID
	 * @apiSuccess {Integer} orderVO.orderType 订单类型 1：订单2：患者报道 3:门诊
	 * @apiSuccess {Integer} orderVO.packType 套餐类型 1：消息图文2:电话咨询 (报到类订单没有套餐为0）
	 * @apiSuccess {String} orderVO.packName 套餐名称
	 * @apiSuccess {Integer} orderVO.timeLong 套餐时间
	 * @apiSuccess {Integer} orderVO.appointTime 预约时间
	 * @apiSuccess {Integer} orderVO.preOrderStatus 订单取消状态
	 * 
	 * @apiSuccess {Short} orderVO.sex 患者性别
	 * @apiSuccess {Integer} orderVO.age 患者年龄1:男，2:女，３保密
	 * @apiSuccess {String} orderVO.topPath 患者头像
	 * @apiSuccess {String} orderVO.area 患者常住地
	 * @apiSuccess {String} orderVO.relation 患者关系
	 * @apiSuccess {String} orderVO.telephone 患者电话
	 * @apiSuccess {Long} orderVO.price 套餐价格
	 * @apiSuccess {Integer} orderVO.orderNo 订单号
	 * @apiSuccess {Integer} orderVO.diseaseId 病情ID
	 * @apiSuccess {Integer} orderVO.orderStatus 订单状态
	 * @apiSuccess {String} orderVO.msgGroupId 订单关联会话id
	 * @apiSuccess {String} orderVO.hospitalName 所属医院名称
	 * @apiSuccess {Integer} orderVO.orderSessionStatus 会话状态
	 *             1:待预约，2：待支付，3：已支付，4：已完成，5：已取消,17已支付服务中
	 *             {@link OrderEnum.OrderStatus}
	 * @apiSuccess {Integer} orderVO.refundStatus 订单退款状态
	 * @apiSuccess {Long} orderVO.createTime 创建时间单位毫秒数
	 * @apiSuccess {Object} userVo 当前登陆患者信息
	 * @apiSuccess {Integer} userVo.userId 患者ID
	 * @apiSuccess {String} userVo.userName 患者名字
	 * @apiSuccess {String} userVo.headPriPath 患者图片
	 * @apiSuccess {Integer} userVo.area 常住地
	 * @apiSuccess {Object} userVo.checkInVo 患者病例信息 ,如果订类型未报道类订单则返回
	 * @apiSuccess {Integer} checkInVo.caseId 病例id
	 * @apiSuccess {Integer} checkInVo.status 病例状态
	 * @apiSuccess {String} checkInVo.hospital 就诊医院
	 * @apiSuccess {Integer} checkInVo.recordNum 病例号
	 * @apiSuccess {Long} checkInVo.lastCureTime 上一次就诊时间
	 * @apiSuccess {String} checkInVo.description 诊断描述
	 * @apiSuccess {Integer} checkInVo.message 留言
	 * @apiSuccess {Object} orderVO.doctorVo 医生信息,如果是患者端则返回医生信息
	 * @apiSuccess {List} orderVO.doctorVos 医生信息列表,如果是患者端则返回医生信息
	 * @apiSuccess {String} orderVO.doctorVos.doctorName 医生名称
	 * @apiSuccess {String} orderVO.doctorVos.doctorPath 医生头像图片
	 * @apiSuccess {String} orderVO.doctorVos.doctorSpecialty 医生所属专科
	 * @apiSuccess {String} orderVO.doctorVos.doctorGroup 医生集团名称
	 * @apiSuccess {String} orderVO.doctorVos.title 医生职称
	 * @apiSuccess {String} orderVO.doctorVos.doctorId 医生Id
	 * @apiSuccess {String} orderVO.doctorVo.doctorId 医生Id
	 * @apiSuccess {String} orderVO.doctorVo.doctorName 医生名称
	 * @apiSuccess {String} orderVO.doctorVo.doctorPath 医生头像图片
	 * @apiSuccess {String} orderVO.doctorVo.doctorSpecialty 医生所属专科
	 * @apiSuccess {String} orderVO.doctorVo.doctorGroup 医生集团名称
	 * @apiSuccess {String} orderVO.doctorVo.title 医生职称
	 * 
	 * @apiAuthor 谢佩
	 * @date 2015年9月8日
	 */
	@RequestMapping("/detail")
	public JSONMessage detail(OrderParam param) throws HttpApiException {
		OrderDetailVO detailVO = orderService.detail(param, ReqUtil.instance.getUser());
		/*
		 * 电话咨询会进入导医流程，在支付之前是没有orderSession信息的。所以这里可能获取的预约时间为空。
		 * 如果预约时间为空，则获取电话咨询服务订单的预约时间。
		 */
		if ((detailVO.getOrderVo().getAppointTime() == null) || (detailVO.getOrderVo().getAppointTime() == 0)) {
			detailVO.getOrderVo().setAppointTime(guideService.getAppointTime(param.getOrderId()));
		}
		return JSONMessage.success(null, detailVO);
	}

	/**
	 * @api {get/post} /pack/order/orderDetail 订单详情 version2
	 * @apiVersion 1.0.0
	 * @apiName orderDetail
	 * @apiGroup 订单
	 * @apiDescription 订单详情 version2
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * 
	 * @apiSuccess {Integer} orderId 订单id
	 * @apiSuccess {String} msgGroupId 订单会话组id
	 * @apiSuccess {Integer} orderType 订单类型
	 * @apiSuccess {String} illHistoryInfoId 新版电子病历id
	 * @apiSuccess {Long} price 订单价格
	 * @apiSuccess {String} hostitalId 医院id
	 * @apiSuccess {Integer} point 积分
	 * @apiSuccess {String} pointName 积分问诊套餐名称
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * @apiSuccess {Long} appointmentStart 预约开始
	 * @apiSuccess {Long} appointmentEnd 预约结束
	 * @apiSuccess {Long} serviceBeginTime 服务开始时间
	 * @apiSuccess {Long} createTime 下单时间
	 * @apiSuccess {Long} finishTime 完成时间
	 * @apiSuccess {Long} payTime 支付时间
	 * @apiSuccess {Integer} payType 支付类型 1:微信支付，2：支付宝
	 * @apiSuccess {Integer} timeLong 时长
	 * @apiSuccess {String} cancelReason 取消原因
	 * @apiSuccess {String} cancelFrom 取消人
	 * @apiSuccess {Integer} patientUserId 患者的用户id
	 * @apiSuccess {String} patientUserName 患者的用户姓名
	 * @apiSuccess {String} patientTelephone 患者电话
	 * @apiSuccess {String} patientUserRelation 患者医生关系
	 * @apiSuccess {Integer} patientId 患者id
	 * @apiSuccess {Integer} patientName 患者名
	 * @apiSuccess {String} patientHeight 患者身高
	 * @apiSuccess {String} patientWeight 患者体重
	 * @apiSuccess {String} patientMarriage 患者婚姻
	 * @apiSuccess {String} patientProfessional 患者职业
	 * @apiSuccess {String} idtype 证件类型 (1身份证 2护照 3军官 4 台胞 5香港身份证)
	 * @apiSuccess {String} idcard 证件号码
	 * @apiSuccess {String} ageStr 患者年龄
	 * @apiSuccess {String} area 患者所在地
	 * @apiSuccess {Integer} sex 患者年龄
	 * @apiSuccess {Integer} headPicFileName 患者头像
	 * @apiSuccess {Integer} doctors.userId 医生id
	 * @apiSuccess {String} doctors.name 医生命长
	 * @apiSuccess {String} doctors.title 医生职称
	 * @apiSuccess {String} doctors.hospital 医生医院
	 * @apiSuccess {String} doctors.headPicFilleName 医生头像
	 * @apiSuccess {String} doctors.departments 医生部门
	 * @apiSuccess {String} doctors.groupName 医生集团
	 * @apiSuccess {Integer} doctors.doctorRole 医生角色（1：主医生/会诊医生，2：其他医生）
	 * @apiSuccess {String} doctors.telephone 医生电话
	 * 
	 * 
	 * @apiAuthor wangl
	 * @date 2016年3月4日
	 */
	@RequestMapping("/orderDetail")
	public JSONMessage orderDetail(@RequestParam(required = true) Integer orderId) {
		return JSONMessage.success(orderService.orderDetail(orderId));
	}

	/**
	 * @api {get/post} /pack/order/inner/orderDetail 订单详情 version2
	 * @apiVersion 1.0.0
	 * @apiName orderDetail
	 * @apiGroup 订单
	 * @apiDescription 订单详情 version2
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 *
	 * @apiSuccess {Integer} orderId 订单id
	 * @apiSuccess {String} msgGroupId 订单会话组id
	 * @apiSuccess {Integer} orderType 订单类型
	 * @apiSuccess {String} illCaseInfoId 病情id
	 * @apiSuccess {Long} price 订单价格
	 * @apiSuccess {String} hostitalId 医院id
	 * @apiSuccess {Integer} point 积分
	 * @apiSuccess {String} pointName 积分问诊套餐名称
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * @apiSuccess {Long} appointmentStart 预约开始
	 * @apiSuccess {Long} appointmentEnd 预约结束
	 * @apiSuccess {Long} serviceBeginTime 服务开始时间
	 * @apiSuccess {Long} createTime 下单时间
	 * @apiSuccess {Long} finishTime 完成时间
	 * @apiSuccess {Long} payTime 支付时间
	 * @apiSuccess {Integer} payType 支付类型 1:微信支付，2：支付宝
	 * @apiSuccess {Integer} timeLong 时长
	 * @apiSuccess {String} cancelReason 取消原因
	 * @apiSuccess {String} cancelFrom 取消人
	 * @apiSuccess {Integer} patientUserId 患者的用户id
	 * @apiSuccess {String} patientUserName 患者的用户姓名
	 * @apiSuccess {String} patientTelephone 患者电话
	 * @apiSuccess {String} patientUserRelation 患者医生关系
	 * @apiSuccess {Integer} patientId 患者id
	 * @apiSuccess {Integer} patientName 患者名
	 * @apiSuccess {String} patientHeight 患者身高
	 * @apiSuccess {String} patientWeight 患者体重
	 * @apiSuccess {String} patientMarriage 患者婚姻
	 * @apiSuccess {String} patientProfessional 患者职业
	 * @apiSuccess {String} idtype 证件类型 (1身份证 2护照 3军官 4 台胞 5香港身份证)
	 * @apiSuccess {String} idcard 证件号码
	 * @apiSuccess {String} ageStr 患者年龄
	 * @apiSuccess {String} area 患者所在地
	 * @apiSuccess {Integer} sex 患者年龄
	 * @apiSuccess {Integer} headPicFileName 患者头像
	 * @apiSuccess {Integer} doctors.userId 医生id
	 * @apiSuccess {String} doctors.name 医生命长
	 * @apiSuccess {String} doctors.title 医生职称
	 * @apiSuccess {String} doctors.hospital 医生医院
	 * @apiSuccess {String} doctors.headPicFilleName 医生头像
	 * @apiSuccess {String} doctors.departments 医生部门
	 * @apiSuccess {String} doctors.groupName 医生集团
	 * @apiSuccess {Integer} doctors.doctorRole 医生角色（1：主医生/会诊医生，2：其他医生）
	 * @apiSuccess {String} doctors.telephone 医生电话
	 *
	 */
	@RequestMapping("/inner/orderDetail")
	public JSONMessage innerOrderDetail(@RequestParam(required = true) Integer orderId) {
		return JSONMessage.success(orderService.orderDetail(orderId));
	}

	/**
	 * @api {get/post} /pack/order/consultationMember 会诊订单中转页面
	 * @apiVersion 1.0.0
	 * @apiName consultationMember
	 * @apiGroup 订单
	 * @apiDescription 会诊订单中转页面
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {Integer} roleType 当前用户角色 1：大医生（专家），2：小医生（助手）
	 * 
	 * @apiSuccess {Long} time 会诊时间
	 * @apiSuccess {Object} patient 患者信息
	 * @apiSuccess {String} patient.userName 患者姓名
	 * @apiSuccess {String} patient.sex 患者性别
	 * @apiSuccess {String} patient.area 患者区域
	 * @apiSuccess {String} patient.telephone 患者电话
	 * @apiSuccess {String} patient.topPath 患者头像
	 * @apiSuccess {Object} doctor 医生信息
	 * @apiSuccess {String} doctor.name 医生姓名
	 * @apiSuccess {String} doctor.doctor.title 医生职称
	 * @apiSuccess {String} doctor.doctor.hospital 医生医院
	 * @apiSuccess {String} doctor.doctor.departments 医生科室
	 * @apiSuccess {String} doctor.headPicFileName 医生头像
	 * @apiAuthor wangl
	 * @date 2016年3月26日
	 */
	@RequestMapping("/consultationMember")
	public JSONMessage consultationMember(@RequestParam(required = true) Integer orderId,
			@RequestParam(required = true) Integer roleType) {
		return JSONMessage.success(orderService.consultationMember(orderId, roleType));
	}

	/**
	 * @api {get} /pack/order/handelOrder 处理订单
	 * @apiVersion 1.0.0
	 * @apiName handelOrder
	 * @apiGroup 订单
	 * @apiDescription 处理订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} userId 创建会话对象ID，注：这里不是当前登陆ID
	 * @apiParam {Integer} orderId 订单ID
	 * 
	 * 
	 * @apiSuccess {Object} OrderSessionVO 会话对象
	 * @apiSuccess {String} OrderSessionVO.sessionId 会话ID
	 * @apiSuccess {String} OrderSessionVO.orderStatus 订单状态
	 * @apiSuccess {String} OrderSessionVO.msgGroupId 会话组ID
	 * @apiSuccess {String} OrderSessionVO.createTime 创建时间
	 * 
	 * @apiAuthor 谢佩
	 * @date 2015年9月8日
	 */
	@RequestMapping("/handelOrder")
	public JSONMessage handelOrder(OrderParam param) {
		return JSONMessage.success(null, orderService.addSession(param, ReqUtil.instance.getUser()));
	}

	/**
	 * @api {get} /pack/order/orderDisease 订单病情详情
	 * @apiVersion 1.0.0
	 * @apiName orderDisease
	 * @apiGroup 订单
	 * @apiDescription 订单病情详情
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} userId 用户ID
	 * @apiParam {Integer} orderId 订单ID
	 * 
	 * 
	 * @apiSuccess {List} pageData.OrderDetailVO 订单数据对象
	 * @apiSuccess {String} OrderDetailVO.diseaseDesc 病情描述
	 * 
	 * @apiSuccess {String[]} OrderDetailVO.imgStringPath 病情或病例图片
	 * @apiSuccess {String} OrderDetailVO.voice 病情语音
	 * @apiSuccess {Object} OrderDetailVO.orderVO 订单信息
	 * @apiSuccess {Integer} orderVO.orderId 订单ID
	 * @apiSuccess {Integer} orderVO.doctorId 医生ID
	 * @apiSuccess {Integer} orderVO.orderType 订单类型 1：订单2：患者报道 3:门诊
	 * @apiSuccess {Integer} orderVO.packType 套餐类型 1：消息图文2:电话咨询 (报到类订单没有套餐为0）
	 * @apiSuccess {Integer} orderVO.timeLong 套餐时间
	 * @apiSuccess {Short} orderVO.sex 患者性别
	 * @apiSuccess {Integer} orderVO.age 患者年龄1:男，2:女，３保密
	 * @apiSuccess {String} orderVO.topPath 患者头像
	 * @apiSuccess {String} orderVO.area
	 * @apiSuccess {Integer} orderVO.orderNo 订单号
	 * @apiSuccess {String} orderVO.relation 患者关系
	 * @apiSuccess {String} orderVO.telephone 患者电话
	 * @apiSuccess {Long} orderVO.price 套餐价格
	 * @apiSuccess {Integer} orderVO.diseaseId 病情ID
	 * @apiSuccess {Integer} orderVO.orderStatus 订单状态
	 * @apiSuccess {Integer} orderVO.refundStatus 订单退款状态
	 * @apiSuccess {Long} orderVO.createTime 创建时间单位毫秒数
	 * 
	 * @apiSuccess {Object} rderVO.userVo 当前登陆患者信息
	 * @apiSuccess {Integer} userVo.userId 患者ID
	 * @apiSuccess {String} userVo.userName 患者名字
	 * @apiSuccess {String} userVo.headPriPath 患者图片
	 * 
	 * @apiSuccess {Object} userVo.checkInVo 患者病例信息 ,如果订类型未报道类订单则返回
	 * @apiSuccess {Integer} checkInVo.status 病例状态
	 * @apiSuccess {String} checkInVo.hospital 就诊医院
	 * @apiSuccess {Integer} checkInVo.recordNum 病例号
	 * @apiSuccess {Long} checkInVo.lastCureTime 上一次就诊时间
	 * @apiSuccess {String} checkInVo.description 诊断描述
	 * @apiSuccess {Integer} checkInVo.message 留言
	 * 
	 * @apiAuthor 谢佩
	 * @date 2015年9月8日
	 */
	@RequestMapping("/orderDisease")
	public JSONMessage orderDisease(Integer orderId) throws HttpApiException {
		return JSONMessage.success(null, orderService.findOrderDisease(orderId));
	}

	/**
	 * 
	 * @api {[get,post]} /pack/order/cancel 取消订单
	 * @apiVersion 1.0.0
	 * @apiName cancel
	 * @apiGroup 订单
	 * @apiDescription 订单取消·
	 * @apiParam {String} access_token 凭证
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {String} pwd 取消时需要输入的密码
	 * @apiParam {String} cancelReason 取消原因
	 * @return @
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年9月16日
	 */
	@RequestMapping("/cancel")
	public JSONMessage cancel(OrderParam param, String pwd) throws HttpApiException {
		orderService.cancelOrder(param, pwd, OrderEnum.orderCancelEnum.manual.getIndex());
		return JSONMessage.success();

	}

	@RequestMapping("/pay")
	public JSONMessage testPay(Integer orderId) throws HttpApiException {
		PaymentVO paymentVo = new PaymentVO();
		paymentVo.setPayNo("150812970");
		paymentVo.setPaymentMoney(0);
		paymentVo.setPayType(2);
		paymentVo.setTradeStatus("TRADE_SUCCESS");
		paymentVo.setIsSuccess(true);

		handpayBusiness.handlePayBusinessLogic(paymentVo);
		return JSONMessage.success();

	}

	/**
	 * @api {[get,post]} /pack/order/serviceOrder 获取服务中的订单列表
	 * @apiVersion 1.0.0
	 * @apiName serviceOrder
	 * @apiDescription 指定医生的服务中的订单
	 * @apiGroup 订单
	 * 
	 * @apiParam {Integer} doctorId 医生id
	 * 
	 * @apiSuccess {List} data 订单id的list
	 * 
	 * @apiAuthor 张垠
	 * @date 2015年9月22日
	 */
	@RequestMapping("/serviceOrder")
	public JSONMessage getServingOrder(Integer doctorId) {
		return JSONMessage.success(null, orderService.getServingOrder(doctorId));
	}

	/**
	 * @api {get} /pack/order/findOrdersGroupByDay 订单列表(已按天分组)
	 * @apiVersion 1.0.0
	 * @apiName findOrdersGroupByDay
	 * @apiGroup 订单
	 * @apiDescription 根据订单状态查询订单数据（已按天分组）
	 *
	 * @apiParam {String} access_token token
	 * 
	 * @apiParam {Long} startCreateTime 开始查询时间
	 * 
	 * @apiParam {Integer} orderStatus 订单状态 1:待预约，2：待支付，3：已支付，4：已完成，5：已取消
	 *           {@link OrderEnum.OrderStatus}
	 * @apiParam {Integer} userId 患者ID
	 * @apiParam {Integer} doctorId 医生ID
	 * @apiParam {Integer} orderType 订单类型 （1：套餐订单；2：报到；3：门诊订单）
	 * @apiParam {String} groupId 集团ｉｄ
	 * 
	 * 
	 * 
	 * @apiSuccess {List} pageData.OrderVO 订单数据对象
	 * @apiSuccess {Integer} OrderVO.orderId 订单ID
	 * @apiSuccess {Integer} OrderVO.doctorId 医生ID
	 * @apiSuccess {Integer} OrderVO.orderType 订单类型 （1：套餐订单；2：报到；3：门诊订单）
	 * @apiSuccess {Integer} OrderVO.packType 套餐类型 1：消息图文2:电话咨询 (报到类订单没有套餐为0）
	 * @apiSuccess {Integer} OrderVO.timeLong 套餐时间
	 * 
	 * @apiSuccess {Short} OrderVO.sex 患者性别
	 * @apiSuccess {Integer} OrderVO.age 患者年龄1:男，2:女，３保密
	 * @apiSuccess {String} OrderVO.topPath 患者头像
	 * @apiSuccess {String} OrderVO.area 患者地区
	 * @apiSuccess {String} OrderVO.relation 患者关系
	 * @apiSuccess {String} OrderVO.telephone 患者电话
	 * @apiSuccess {Long} OrderVO.price 套餐价格
	 * @apiSuccess {Integer} OrderVO.diseaseId 病情ID
	 * @apiSuccess {Integer} OrderVO.orderStatus 订单状态
	 * @apiSuccess {Integer} OrderVO.refundStatus 订单退款状态
	 * @apiSuccess {Long} OrderVO.createTime 创建时间单位毫秒数
	 * 
	 * @apiSuccess {Object} OrderVO.userVo 当前登陆患者信息
	 * @apiSuccess {Integer} userVo.userId 患者ID
	 * @apiSuccess {String} userVo.userName 患者名字
	 * @apiSuccess {String} userVo.headPriPath 患者图片
	 * 
	 * @apiSuccess {Object} OrderVO.doctorVo 医生信息,如果是患者端则返回医生信息
	 * @apiSuccess {String} doctorVo.doctorName 医生名称
	 * @apiSuccess {String} doctorVo.doctorPath 医生头像图片
	 * @apiSuccess {String} doctorVo.doctorSpecialty 医生所属专科
	 * @apiSuccess {String} doctorVo.doctorGroup 医生集团名称
	 * 
	 * @apiSuccess {String} OrderVO.orderSession 订单会话对象
	 * @apiSuccess {String} orderSession.msgGroupId 会话组id
	 * 
	 * @apiAuthor 李淼淼
	 * @date 2015年9月9日
	 */
	@RequestMapping("/findOrdersGroupByDay")
	public JSONMessage findOrdersGroupByDay(OrderParam param) {
		return JSONMessage.success(null, orderService.findOrdersGroupByDay(param, ReqUtil.instance.getUser()));
	}

	/**
	 * @api {[get,post]} /pack/order/getOrderKeyInfoByOrderId 获取订单关键信息
	 * @apiVersion 1.0.0
	 * @apiName getOrderKeyInfoByOrderId
	 * @apiDescription 获取订单关键信息
	 * @apiGroup 订单
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {String[]} ids 订单id
	 * 
	 * @apiSuccess {List} data.orderId 订单id
	 * @apiSuccess {List} data.patientAge 订单关联患者的年龄
	 * @apiSuccess {List} data.patientName 订单关联患者的姓名
	 * @apiSuccess {List} data.patientName 订单关联患者的姓名
	 * @apiSuccess {List} data.patientArea 订单关联患者的地区
	 * 
	 * @apiAuthor 张垠
	 * @date 2015年10月13日
	 */
	@RequestMapping("/getOrderKeyInfoByOrderId")
	public JSONMessage getOrderKeyInfoByOrderId(String[] ids) throws HttpApiException {
		return JSONMessage.success(null, orderService.getOrderKeyInfoByOrderId(ids));
	}

	/**
	 * 
	 * @api {[get,post]} /pack/order/cancelOrderBySystem 门诊订单自动取消
	 * @apiVersion 1.0.0
	 * @apiName cancelOrderBySystem
	 * @apiGroup 订单
	 * @apiDescription 门诊订单超时未开始自动取消 cancelfrom是1
	 * @apiParam {String} access_token token
	 * @apiParam {String} orderId 订单id
	 * 
	 * @apiAuthor 张垠
	 * @author 张垠
	 * @date 2015年10月14日
	 */
	@RequestMapping("/cancelOrderBySystem")
	public JSONMessage cancelOrderBySystem(Integer orderId) {
		orderService.cancelAdvisoryOrderBySystem(orderId);
		return JSONMessage.success(null, "取消订单成功");
	}

	/**
	 * 
	 * @api {[get,post]} /pack/order/getOrderByDoctorAndUser
	 *      根据医生id和患者id获取未结束的门诊订单
	 * @apiVersion 1.0.0
	 * @apiName getOrderByDoctorAndUser
	 * @apiGroup 订单
	 * @apiDescription 根据医生id和患者id获取未结束的门诊订单，如果有就返回相应信息，没有则为空
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生ID
	 * @apiParam {Integer} userId 患都ID
	 * 
	 * @apiSuccess {String} orderId 订单ID
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * @apiSuccess {String} gid 订单会话ID
	 * @apiAuthor 张垠
	 * @author 张垠
	 * @date 2015年10月16日
	 */
	@RequestMapping("/getOrderByDoctorAndUser")
	public JSONMessage getOrderByDoctorAndUser(OrderParam param) {
		param.setOrderType(OrderType.outPatient.getIndex());
		return JSONMessage.success(orderService.getOrderByDocIdAndUserId(param));
	}

	/**
	 * 
	 * @api {[get,post]} /pack/order/getOrderByDoctorAndUserAndType 获取未结束的订单
	 * @apiVersion 1.0.0
	 * @apiName getOrderByDoctorAndUserAndType
	 * @apiGroup 订单
	 * @apiDescription 根据医生id和患者id和订单类型获取未结束的订单，如果有就返回相应信息，没有则为空
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生ID
	 * @apiParam {Integer} userId 患都ID
	 * @apiParam {Integer} orderType 订单类型（9：预约，10：收费单）
	 * 
	 * @apiSuccess {String} orderId 订单ID
	 * @apiSuccess {Integer} orderStatus 订单状态
	 * @apiSuccess {String} gid 订单会话ID
	 * @apiAuthor 张垠
	 * @author 张垠
	 * @date 2015年10月16日
	 */
	@RequestMapping("/getOrderByDoctorAndUserAndType")
	public JSONMessage getOrderByDoctorAndUserAndType(OrderParam param) {
		return JSONMessage.success(orderService.getOrderByDocIdAndUserId(param));
	}

	/**
	 * @api {get} /pack/order/getPaidOrders 已支付订单列表
	 * @apiVersion 1.0.0
	 * @apiName getPaidOrders
	 * @apiGroup 订单
	 * @apiDescription 已支付订单列表
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderStatus 订单状态 1:待预约，2：待支付，3：已支付，4：已完成，5：已取消
	 *           {@link OrderEnum.OrderStatus}
	 * @apiParam {Integer} pageIndex 页码 起使页为0
	 * @apiParam {Integer} userId 患者ID
	 * @apiParam {String} orderNo 订单号
	 * @apiParam {Integer} orderType 订单类型
	 * @apiParam {String} userName 用户名
	 * 
	 * @apiSuccess {Integer} pageCount 总页数
	 * @apiSuccess {Integer} pageIndex 当前页数
	 * @apiSuccess {Long} total 总记录数
	 * 
	 * @apiSuccess {List} pageData.OrderVO 订单数据对象
	 * @apiSuccess {Integer} OrderVO.orderId 订单ID
	 * @apiSuccess {Integer} OrderVO.doctorId 医生ID
	 * @apiSuccess {Integer} OrderVO.orderType 订单类型
	 *             1：咨询套餐2：患者报道,3:门诊,4:健康关怀,5:随访计划
	 * @apiSuccess {Integer} OrderVO.packType 套餐类型 1：消息图文2:电话咨询
	 *             (报到类订单没有套餐为0),3:关怀计划，4：随访计划
	 * @apiSuccess {Integer} OrderVO.timeLong 套餐时间
	 * 
	 * @apiSuccess {Short} OrderVO.sex 患者性别
	 * @apiSuccess {Integer} OrderVO.age 患者年龄1:男，2:女，３保密
	 * @apiSuccess {String} OrderVO.topPath 患者头像
	 * @apiSuccess {String} OrderVO.area 患者地区
	 * @apiSuccess {String} OrderVO.relation 患者关系
	 * @apiSuccess {String} OrderVO.telephone 患者电话
	 * @apiSuccess {Long} OrderVO.price 套餐价格
	 * @apiSuccess {Integer} OrderVO.diseaseId 病情ID
	 * @apiSuccess {Integer} OrderVO.orderStatus 订单状态
	 * @apiSuccess {Integer} OrderVO.refundStatus 订单退款状态 1:未退款，3：已退款
	 * @apiSuccess {Long} OrderVO.createTime 创建时间单位毫秒数
	 * @apiSuccess {Long} OrderVO.payTime 支付订单时间单位毫秒数
	 * @apiSuccess {Long} OrderVO.payType 支付类型 1:微信支付，2：支付宝
	 * 
	 * @apiSuccess {Object} OrderVO.userVo 当前登陆患者信息
	 * @apiSuccess {Integer} userVo.userId 患者ID
	 * @apiSuccess {String} userVo.userName 患者名字
	 * @apiSuccess {String} userVo.headPriPath 患者图片
	 * 
	 * @apiSuccess {Object} OrderVO.doctorVo 医生信息,如果是患者端则返回医生信息
	 * @apiSuccess {String} doctorVo.doctorName 医生名称
	 * @apiSuccess {String} doctorVo.doctorPath 医生头像图片
	 * @apiSuccess {String} doctorVo.doctorSpecialty 医生所属专科
	 * @apiSuccess {String} doctorVo.doctorGroup 医生集团名称
	 * 
	 * @apiSuccess {String} OrderVO.orderSession 订单会话对象
	 * @apiSuccess {String} orderSession.msgGroupId 会话组id
	 * 
	 * @apiSuccess {Object} nurseVo 护士数据对象
	 * @apiSuccess {String} nurseVo.name 护士姓名
	 * @apiSuccess {String} nurseVo.telephone 护士电话
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年9月9日
	 */
	@RequestMapping("/getPaidOrders")
	public JSONMessage getPaidOrders(OrderParam param) {
		return JSONMessage.success(orderService.findPaidOrders(param));
	}

	/**
	 * @api {[get,post]} /pack/order/cancelPaidOrder 取消已支付订单
	 * @apiVersion 1.0.0
	 * @apiName cancelPaidOrder
	 * @apiGroup 订单
	 * @apiDescription 取消已支付订单（限于web端使用）
	 * @apiParam {String} access_token token
	 * @apiParam {Integer...} orderIds 订单ID
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年11月25日
	 */
	@RequestMapping("/cancelPaidOrder")
	public JSONMessage cancelPaidOrder(Integer... orderIds) throws HttpApiException {
		if ((orderIds == null) || (orderIds.length == 0)) {
			throw new ServiceException("订单Id为空");
		}
		OrderParam param = new OrderParam();
		/**
		 * 马上更新到生产环境 产品说平台取消订单要添加取消原因 2016年8月11日22:25:50
		 */
		param.setOrderId(orderIds[0]);
		param.setCancelReason("平台已取消订单");
		orderService.cancelOrder(param, ReqUtil.instance.getUserId(), OrderEnum.orderCancelEnum.manual.getIndex());
		return JSONMessage.success();
	}

	/**
	 * @api {[get,post]} /pack/order/refund 订单退款
	 * @apiVersion 1.0.0
	 * @apiName refund
	 * @apiGroup 订单
	 * @apiDescription 订单退款
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单ID
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年11月25日
	 */
	@RequestMapping("/refund")
	public JSONMessage refund(Integer orderId) {
		return JSONMessage.success(null, orderRefundService.addRefund(orderId));
	}

	/**
	 * @api {[get,post]} /pack/order/getRefundDetail 获取退款详情
	 * @apiVersion 1.0.0
	 * @apiName getRefundDetail
	 * @apiGroup 订单
	 * @apiDescription 获取退款详情
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单ID
	 * 
	 * @apiSuccess {String} refundTime 退款时间
	 * @apiSuccess {String} refundAmt 退款金额（分）
	 * @apiSuccess {String} refundStatus 退款状态（3：成功，4：失败）
	 * @apiSuccess {String} serialNumber 流水号
	 * @apiSuccess {String} name 医生名字
	 * @apiSuccess {String} amount 医生金额
	 * @apiSuccess {RefundOrderVO} groupInfo 集团对象
	 * @apiSuccess {String} groupInfo.name 集团名字
	 * @apiSuccess {String} groupInfo.amount 集团金额
	 * @apiSuccess {RefundOrderVO} parentDoctorInfo 上级医生对象
	 * @apiSuccess {String} parentDoctorInfo.name 上级医生名字
	 * @apiSuccess {String} parentDoctorInfo.amount 上级医生金额
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年11月25日
	 */
	@RequestMapping("/getRefundDetail")
	public JSONMessage getRefundDetail(Integer orderId) {
		return JSONMessage.success(orderRefundService.getRefundDetail(orderId));
	}

	/**
	 * @api {[get,post]} /pack/order/setRemarks 修改备注
	 * @apiVersion 1.0.0
	 * @apiName setRemarks
	 * @apiGroup 订单
	 * @apiDescription 修改备注
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单ID
	 * @apiParam {String} remarks 备注
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年11月24日
	 */
	@RequestMapping("/setRemarks")
	public JSONMessage setRemarks(Integer orderId, String remarks) {
		orderService.updateRemarks(orderId, remarks);
		return JSONMessage.success(null);
	}

	/**
	 * @api {[get,post]} /pack/order/getRemarks 获取备注
	 * @apiVersion 1.0.0
	 * @apiName getRemarks
	 * @apiGroup 订单
	 * @apiDescription 获取备注
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单ID
	 * 
	 * @apiSuccess {String} remarks 备注
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年11月24日
	 */
	@RequestMapping("/getRemarks")
	public JSONMessage getRemarks(Integer orderId) {
		return JSONMessage.success(null, orderService.getRemarks(orderId));
	}

	/**
	 * @api {[get,post]} /pack/order/findOrderSchedule 获取医生是否有订单日程
	 * @apiVersion 1.0.0
	 * @apiName findOrderSchedule
	 * @apiGroup 订单
	 * @apiDescription 获取医生是否有订单日程
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生ID
	 * 
	 * @apiSuccess {Object[]} orderVo VO对象数据
	 * 
	 * @apiAuthor 谢佩
	 * @date 2015年12月08日
	 */
	@RequestMapping("/findOrderSchedule")
	public JSONMessage findOrderSchedule(OrderParam param) {
		param.setUserId(ReqUtil.instance.getUserId());
		return JSONMessage.success(null, orderService.findOrderSchedule(param));
	}

	/**
	 * @api {[get,post]} /pack/order/findExpiredOrder 获取图文，电话，报道过期订单
	 * @apiVersion 1.0.0
	 * @apiName findExpiredOrder
	 * @apiGroup 订单
	 * @apiDescription 获取图文，电话，报道过期订单
	 * 
	 * @apiParam {String} access_token token
	 * 
	 * @apiSuccess {Object[]} orderVo VO对象数据
	 * @apiSuccess {String} remarks 提示信息
	 * @apiSuccess {String} rderSession.msgGroupId 会话ID
	 * @apiAuthor 谢佩
	 * @date 2015年12月08日
	 */
	@RequestMapping("/findExpiredOrder")
	public JSONMessage findExpiredOrder(OrderParam param) throws HttpApiException {
		param.setDoctorId(ReqUtil.instance.getUserId());
		return JSONMessage.success(null, orderService.findExpiredOrder(param));
	}

	/**
	 * @api {[get,post]} /pack/order/submitCareOrder 分享预订单
	 * @apiVersion 1.0.0
	 * @apiName submitCareOrder
	 * @apiGroup 订单
	 * @apiDescription 分享预订单
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {String} packId 套餐ID
	 * @apiParam {Integer} patientId 患者id
	 * @apiParam {String} name 名字
	 * @apiParam {String} verifyCode 验证码
	 * @apiParam {String} diseaseDesc 病情描述
	 * 
	 * @apiSuccess {String} reg 是否注册 true :已注册， false：未注册
	 * @apiSuccess {String} link 需跳转的的链接地址。如果是已存在的订单，不会返回。
	 * @apiSuccess {Boolean} ifNewOrder 是否新的订单。如果是已存在的订单，会返回false，否则与原来一样。
	 * 
	 * @apiAuthor 谢佩
	 * @date 2015年12月24日
	 */
	@RequestMapping("/submitCareOrder")
	public JSONMessage submitCareOrder(CareOrderParam param) throws HttpApiException {
		
		PreOrderVO vo = orderService.sumbitCarePlanOrder(param);
		// 免费的关怀计划订单创建就开始服务（因为端会立刻跳转到IM，需要及时处理，不能异步）
		if (vo != null && null != vo.getIfNewOrder() && vo.getIfNewOrder()) {
			this.orderService.beginService4FreePlanImmediately(vo.getOrderId());
		}
		return JSONMessage.success(null, vo);
	}

	/*
	 * @RequestMapping("/refundOrder") public JSONMessage
	 * refundOrder(OrderRefundParam param) {
	 * orderRefundService.addOrderRfund(param); return
	 * JSONMessage.success(null); }
	 */

	/**
	 * @api {get} /pack/order/getOrderDetailById 获取订单详情
	 * @apiVersion 1.0.0
	 * @apiName getOrderDetailById 获取订单详情
	 * @apiGroup 订单
	 * @apiDescription 管理员获取订单详情（护士）
	 * @apiParam {String} access_token token
	 * @apiParam {String} orderId 订单id
	 * 
	 * @apiSuccess {String} orderNo 订单号
	 * @apiSuccess {String} orderType 订单类型
	 * @apiSuccess {String} nurseName 护士姓名
	 * @apiSuccess {String} patientName 患者姓名
	 * @apiSuccess {String} nurseTelephone 护士号码
	 * @apiSuccess {String} patientTelephone 患者手机号码
	 * @apiSuccess {String[]} hospitalList 医院列表
	 * @apiSuccess {String[]} departList 科室
	 * @apiSuccess {String} visitingTime 预约时间
	 * @apiSuccess {String} attachmentDoctorName 加号医生
	 * @apiSuccess {String} checkItems 检查项
	 * @apiSuccess {String} message 留言
	 * 
	 * @apiAuthor wangl
	 * @date 2015年12月30日
	 */
	@RequestMapping("/getOrderDetailById")
	public JSONMessage getOrderDetailById(Integer orderId) {
		Order order = orderService.getOne(orderId);
		if (order != null) {
			int orderType = order.getOrderType();
			if (OrderEnum.OrderType.throughTrain.getIndex() != orderType) {
				return null;
			} else {
				/**
				 * 暂时只实现护士类型的订单
				 */
				return JSONMessage.success(null, nurseOrderServiceImpl.getThroughTrainOrderDetail(orderId + ""));
			}
		}
		return null;
	}

	/**
	 * @api {get} /pack/order/getPatientsGid 获取患者最近的订单会话id
	 * @apiVersion 1.0.0
	 * @apiName getPatientsGid 获取患者订单会话id
	 * @apiGroup 订单
	 * @apiDescription 获取患者最近的订单会话id
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生Id
	 * @apiParam {String} userIds 用户ids
	 * @apiParam {String} patientIds 患者id
	 *
	 * @apiSuccess {Object[]} data 数据列表
	 * @apiSuccess {Integer} data.userId 订单号
	 * @apiSuccess {String} data.gid 订单号
	 * @apiAuthor wangl
	 * @date 2016年2月25日
	 */
	@RequestMapping("/getPatientsGid")
	public JSONMessage getPatientsGid(
			@RequestParam(name = "doctorId") Integer doctorId,
			@RequestParam(name = "userIds", required = false) String userIds,
			@RequestParam(name = "patientIds", required = false) String patientIds
	) {
		return JSONMessage.success(orderService.getPatientsGid(doctorId, userIds, patientIds));
	}

	/**
	 * 
	 * @api {[get,post]} /pack/order/getOrderInfoById 根据订单id来获取订单信息
	 * @apiVersion 1.0.0
	 * @apiName getOrderInfoById
	 * @apiGroup 订单
	 * @apiDescription getOrderInfoById 根据订单id来获取订单信息
	 * @apiParam {String} access_token token
	 * @apiParam {String} orderId 订单id
	 * 
	 * @apiAuthor 姜宏杰
	 * @author 姜宏杰
	 * @date 2016-3-10 19:34:46
	 */
	@RequestMapping("/getOrderInfoById")
	public JSONMessage getOrderInfoById(Integer orderId) {
		Order order = orderService.getOne(orderId);
		Group gp = groupService.getGroupById(order.getGroupId());
		order.setGroupName(gp.getName());
		return JSONMessage.success(null, order);
	}

	/**
	 * 
	 * @api {[get,post]} /pack/order/getOrderDoctorIds 获取订单专家组成员Id
	 * @apiVersion 1.0.0
	 * @apiName getOrderDoctorIds
	 * @apiGroup 订单
	 * @apiDescription 获取订单专家组成员Id
	 * @apiParam {String} access_token token
	 * @apiParam {String} orderId 订单id
	 */
	@RequestMapping("/getOrderDoctorIds")
	public JSONMessage getOrderDoctorIds(Integer orderId) {
		return JSONMessage.success(orderService.getOrderDoctorIds(orderId));
	}

	/**
	 * @api {get} /pack/order/getOrderList 多条件搜索订单
	 * @apiVersion 1.0.0
	 * @apiName getOrderList
	 * @apiGroup 订单
	 * @apiDescription 多条件搜索订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderStatus 订单状态 1:待预约，2：待支付，3：已支付，4：已完成，5：已取消
	 * @apiParam {Integer} orderType 订单类型（(4：健康关怀套餐,9：名医面对面,10:收费单）
	 * @apiParam {Integer} packType 套餐类型（1：图文咨询，2：电话咨询）
	 * @apiParam {Integer} orderType 订单类型
	 * @apiParam {Integer} pageIndex 页码 起始页为0
	 * @apiParam {Integer} pageSize 默认15
	 * @apiParam {String} groupId 集团ID
	 * @apiParam {String} doctorName 医生姓名
	 * @apiParam {String} patientName 患者姓名
	 * @apiParam {String} hostpitalName 医院名称
	 * @apiParam {Long} startCreateTime 起始时间
	 * @apiParam {Long} endCreateTime 结束时间
	 * 
	 * @apiSuccess {Integer} pageCount 总页数
	 * @apiSuccess {Integer} pageIndex 当前页数
	 * @apiSuccess {Long} total 总记录数
	 * @apiSuccess {List} pageData.OrderVO 订单数据对象
	 * 
	 * @apiSuccess {Integer} OrderVO.orderId 订单ID
	 * @apiSuccess {String} OrderVO.doctorName 医生名称
	 * @apiSuccess {String} OrderVO.patientName 患者名称
	 * @apiSuccess {Integer} OrderVO.orderType 订单类型(9, "预约套餐"),(10, "收费单")
	 * @apiSuccess {Integer} OrderVO.orderNo 订单编号
	 * @apiSuccess {Integer} OrderVO.orderStatus
	 *             订单状态(1:待预约，2：待支付，3：已支付，4：已完成，5：已取消)
	 * @apiSuccess {String} OrderVO.formateTime 订单时间
	 * @apiSuccess {Long} OrderVO.price 订单金额(分)
	 * @apiSuccess {String} OrderVO.hospitalName 医院名称
	 * 
	 * @apiAuthor 张垠
	 * @date 2016年04月29日
	 */
	@RequestMapping("/getOrderList")
	public JSONMessage getOrderListByMoreCondition(OrderParam param) {
		return JSONMessage.success(null, orderService.getOrderListByMC(param));
	}

	/**
	 * @api {get} /pack/order/downOrderList 下载多条件搜索订单结果列表
	 * @apiVersion 1.0.0
	 * @apiName downOrderList
	 * @apiGroup 订单
	 * @apiDescription 下载多条件搜索订单结果列表
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderStatus 订单状态 1:待预约，2：待支付，3：已支付，4：已完成，5：已取消
	 * @apiParam {Integer} orderType 订单类型
	 * @apiParam {String} groupId 集团ID
	 * @apiParam {String} doctorName 医生姓名
	 * @apiParam {String} patientName 患者姓名
	 * @apiParam {String} hostpitalName 医院名称
	 * @apiParam {Long} startCreateTime 起始时间
	 * @apiParam {Long} endCreateTime 结束时间
	 * 
	 * 
	 * @apiAuthor 张垠
	 * @date 2016年04月29日
	 */
	@RequestMapping("/downOrderList")
	public String downOrderList(OrderParam param, HttpServletResponse response) throws IOException {
		param.setPageSize(Integer.MAX_VALUE);
		List<OrderVO> list = orderService.getOrderListToDown(param);
		if ((list == null) || (list.size() == 0)) {
			return "没有找到下载数据";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String filenName = "收费服务订单" + sdf.format(System.currentTimeMillis());
		String[] keys = { "dName", "pName", "tStr", "orderNo", "time", "price", "sStr", "hName" };
		String[] coloumes = { "医生名", "患者名", "订单类型", "订单号 ", "订单时间", "订单金额(元)", "订单状态", "线下医院" };
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		DownOrder dOrder = new DownOrder(keys, coloumes, list);
		dOrder.createWorkBook(os);
		byte[] content = os.toByteArray();
		InputStream is = new ByteArrayInputStream(content);
		// 设置response参数，可以打开下载页面
		response.reset();
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		response.setHeader("Content-Disposition",
				"attachment;filename=" + new String((filenName + ".xlsx").getBytes(), "iso-8859-1"));
		ServletOutputStream out = response.getOutputStream();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		bis = new BufferedInputStream(is);
		bos = new BufferedOutputStream(out);
		byte[] buff = new byte[2048];
		int bytesRead;
		while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
			bos.write(buff, 0, bytesRead);
		}
		if (bis != null) {
			bis.close();
		}
		if (bos != null) {
			bos.close();
		}
		return null;
	}

	/**
	 * @api {get} /pack/order/sendDoctorCardInfo 发送医生卡片到IM会话
	 * @apiVersion 1.0.0
	 * @apiName sendDoctorCardInfo
	 * @apiGroup 订单
	 * @apiDescription 发送医生卡片到IM会话
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生id
	 * @apiParam {String} msgId 会话id
	 * 
	 * @apiSuccess {String} resultCode 返回码
	 * @apiAuthor wangl
	 * @date 2016年5月12日
	 */
	@RequestMapping("/sendDoctorCardInfo")
	public JSONMessage sendDoctorCardInfo(@RequestParam(required = true) Integer doctorId,
			@RequestParam(required = true) String msgId) throws HttpApiException {
		orderService.sendDoctorCardInfo(doctorId, msgId);
		return JSONMessage.success();
	}

	/**
	 * @api {get} /pack/order/sendIllCaseCardInfo 发送患者病情卡片到im会话
	 * @apiVersion 1.0.0
	 * @apiName sendIllCaseCardInfo
	 * @apiGroup 订单
	 * @apiDescription 发送患者病情卡片到im会话
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {String} illCaseInfoId 病情id
	 * @apiParam {String} msgId 会话id
	 * 
	 * @apiSuccess {String} resultCode 返回码
	 * @apiAuthor wangl
	 * @date 2016年5月12日
	 */
	@RequestMapping("/sendIllCaseCardInfo")
	public JSONMessage sendIllCaseCardInfo(Integer orderId, String illCaseInfoId,
			@RequestParam(required = true) String msgId) throws HttpApiException {
		orderService.sendIllCaseCardInfo(orderId, illCaseInfoId, msgId, ReqUtil.instance.getUserId() + "");
		return JSONMessage.success();
	}

	/**
	 * @api {get} /pack/order/transferRecords 转诊记录列表
	 * @apiVersion 1.0.0
	 * @apiName transferRecords
	 * @apiGroup 订单
	 * @apiDescription 获取我发起的或我接收到转诊记录列表
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} transferRecordType 1:我转诊的，2：我接受的
	 * @apiParam {Integer} pageIndex 页码 起使页为0
	 * @apiParam {Integer} pageSize 分页记录数
	 * 
	 * @apiSuccess {Integer} pageCount 总页数
	 * @apiSuccess {Integer} pageIndex 当前页数
	 * @apiSuccess {Long} total 总记录数
	 * 
	 * @apiSuccess {List} pageData.OrderVO 订单数据对象
	 * @apiSuccess {Integer} OrderVO.orderId 订单ID
	 * @apiSuccess {Integer} OrderVO.doctorId 医生ID
	 * @apiSuccess {Integer} OrderVO.orderType 订单类型
	 *             1：咨询套餐2：患者报道,3:门诊,4:健康关怀,5:随访计划
	 * @apiSuccess {Integer} OrderVO.packType 套餐类型 1：消息图文2:电话咨询
	 *             (报到类订单没有套餐为0),3:关怀计划，4：随访计划
	 * @apiSuccess {Integer} OrderVO.timeLong 套餐时间
	 * 
	 * @apiSuccess {Short} OrderVO.sex 患者性别
	 * @apiSuccess {Integer} OrderVO.age 患者年龄1:男，2:女，３保密
	 * @apiSuccess {String} OrderVO.topPath 患者头像
	 * @apiSuccess {String} OrderVO.area 患者地区
	 * @apiSuccess {String} OrderVO.relation 患者关系
	 * @apiSuccess {String} OrderVO.telephone 患者电话
	 * @apiSuccess {Long} OrderVO.price 套餐价格
	 * @apiSuccess {Integer} OrderVO.diseaseId 病情ID
	 * @apiSuccess {Integer} OrderVO.orderStatus 订单状态
	 * @apiSuccess {Integer} OrderVO.refundStatus 订单退款状态 1:未退款，3：已退款
	 * @apiSuccess {Long} OrderVO.createTime 创建时间单位毫秒数
	 * @apiSuccess {Long} OrderVO.payTime 支付订单时间单位毫秒数
	 * @apiSuccess {Long} OrderVO.payType 支付类型 1:微信支付，2：支付宝
	 * @apiSuccess {Long} OrderVO.illCaseInfoId 电子病历id
	 * @apiSuccess {Long} OrderVO.transferDoctorName 转诊医生名
	 * @apiSuccess {Long} OrderVO.receiveDoctorName 接诊医生名
	 * 
	 * @apiSuccess {Object} OrderVO.userVo 当前登陆患者信息
	 * @apiSuccess {Integer} userVo.userId 患者ID
	 * @apiSuccess {String} userVo.userName 患者名字
	 * @apiSuccess {String} userVo.headPriPath 患者图片
	 * 
	 * @apiSuccess {Object} OrderVO.doctorVo 医生信息,如果是患者端则返回医生信息
	 * @apiSuccess {String} doctorVo.doctorName 医生名称
	 * @apiSuccess {String} doctorVo.doctorPath 医生头像图片
	 * @apiSuccess {String} doctorVo.doctorSpecialty 医生所属专科
	 * @apiSuccess {String} doctorVo.doctorGroup 医生集团名称
	 * 
	 * @apiSuccess {String} OrderVO.orderSession 订单会话对象
	 * @apiSuccess {String} orderSession.msgGroupId 会话组id
	 * 
	 * @apiSuccess {Object} nurseVo 护士数据对象
	 * @apiSuccess {String} nurseVo.name 护士姓名
	 * @apiSuccess {String} nurseVo.telephone 护士电话
	 * 
	 * @apiAuthor wangl
	 * @date 2016年5月25日
	 */
	@RequestMapping("/transferRecords")
	public JSONMessage transferRecords(@RequestParam(required = true) Integer transferRecordType, Integer pageIndex,
			Integer pageSize) {
		return JSONMessage.success(orderService.transferRecords(transferRecordType, pageIndex, pageSize));
	}

	/**
	 * @api {get} /pack/order/refuseAppointOrder 医生拒绝预约订单
	 * @apiVersion 1.0.0
	 * @apiName refuseAppointOrder
	 * @apiGroup 订单
	 * @apiDescription 医生拒绝预约订单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * 
	 * @apiSuccess {String} resultCode 返回码
	 * @apiAuthor wangl
	 * @date 2016年6月15日21:09:54
	 */
	@RequestMapping("/refuseAppointOrder")
	public JSONMessage refuseAppointOrder(@RequestParam(required = true) Integer orderId) throws HttpApiException {
		orderService.refuseAppointOrder(orderId);
		return JSONMessage.success();
	}

	/**
	 * @api {get} /pack/order/updateRemark 修改名医面对面订单备注
	 * @apiVersion 1.0.0
	 * @apiName updateRemark
	 * @apiGroup 订单
	 * @apiDescription 修改名医面对面订单备注
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * @apiSuccess {String} remark 备注信息
	 * @apiSuccess {String} isSend 是否发送短信(true/false)
	 * @apiAuthor 姜宏杰
	 * @date 2016年6月16日14:45:22
	 */
	@RequestMapping("/updateRemark")
	public JSONMessage updateRemark(@RequestParam(required = true) Integer orderId,
			@RequestParam(required = true) String remark, String isSend) {
		if ((null == orderId) || (remark == null)) {
			return JSONMessage.success("订单id或者备注为空！！！");
		}
		orderService.updateRemark(orderId, remark, isSend);
		return JSONMessage.success();
	}

	@RequestMapping("/updateOrderByUserId")
	public JSONMessage updateOrderByUserId(Integer userId, Integer acStatus) {
		orderService.updateOrderByUserId(userId, acStatus);
		return JSONMessage.success();
	}

	/**
	 * @api {get} /pack/order/getAppointmentOrder4H5 h5页面获取订单详情
	 * @apiVersion 1.0.0
	 * @apiName getAppointmentOrder4H5
	 * @apiGroup 订单
	 * @apiDescription h5页面获取订单详情
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * 
	 * @apiSuccess {String} resultCode 返回码
	 * @apiSuccess {Long} price 价格
	 * @apiSuccess {Object} doctor 医生
	 * @apiSuccess {Integer} doctor.userId 用户id
	 * @apiSuccess {String} doctor.name 姓名
	 * @apiSuccess {String} doctor.headPicFileName 头像
	 * @apiSuccess {String} doctor.departments 部门
	 * @apiSuccess {String} doctor.cureNum 好评数
	 * @apiSuccess {String} doctor.title 职称
	 * @apiSuccess {String} doctor.goodRate 好评
	 * @apiSuccess {String} doctor.hospital 医院
	 * @apiSuccess {String} doctor.dateStr 预约时间
	 * @apiSuccess {String} doctor.skill 擅长
	 * 
	 * @apiSuccess {Object} patient 患者
	 * @apiSuccess {Integer} patient.id 患者id
	 * @apiSuccess {String} patient.ageStr 年龄
	 * @apiSuccess {String} patient.sex 性别
	 * @apiSuccess {String} patient.area 地区
	 * @apiSuccess {String} patient.topPath 头像
	 * 
	 * @apiAuthor wangl
	 * @date 2016年6月28日15:47:23
	 */
	@RequestMapping("/getAppointmentOrder4H5")
	public JSONMessage getAppointmentOrder4H5(@RequestParam(required = true) Integer orderId) {
		return JSONMessage.success(orderService.getAppointmentOrder4H5(orderId));
	}

	/**
	 * @api {get} /pack/order/expectAppointments 获取期望预约字符串列表
	 * @apiVersion 1.0.0
	 * @apiName expectAppointments
	 * @apiGroup 订单
	 * @apiDescription 获取期望预约字符串列表
	 *
	 * @apiParam {String} access_token token
	 * 
	 * @apiSuccess {String} resultCode 返回码
	 * @apiSuccess {String} id 期望id
	 * @apiSuccess {String} value 内容
	 * 
	 * @apiAuthor wangl
	 * @date 2016年6月28日15:47:23
	 */
	@RequestMapping("/expectAppointments")
	public JSONMessage expectAppointments() {
		return JSONMessage.success(baseDataService.getExpectAppointments());
	}
	
	/**
	 * @api {get} /pack/order/patientExpectedTimes 患者支付后填写期望预约时间
	 * @apiVersion 1.0.0
	 * @apiName patientExpectedTimes
	 * @apiGroup 订单
	 * @apiDescription 患者支付成功后填写期望预约时间（可不填），会出发发卡片动作。
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单ID
	 * @apiParam {String} expectAppointmentIds 期望预约时间id，多个用“,”分隔
	 * 
	 * @apiSuccess {String} resultCode 返回码
	 * 
	 * @apiAuthor zhy
	 * @date 2017年2月24日
	 */
	@RequestMapping("/patientExpectedTimes")
	public JSONMessage patientExpectedTime(Integer orderId,String expectAppointmentIds) throws HttpApiException {
		orderService.patientExpectedTime(orderId, expectAppointmentIds);
		return JSONMessage.success();
	}

	/**
	 * 
	 * @api {[get,post]} /pack/order/cancelOrderByAdmin 集团平台与运营后台，取消订单
	 * @apiVersion 1.0.0
	 * @apiName cancelOrderByAdmin
	 * @apiGroup 订单
	 * @apiDescription 集团平台与运营后台取消订单
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {String} cancelReason 取消原因
	 * 
	 * @apiSuccess {String} resultCode 返回码
	 * @author CQY
	 * @date 2016年8月18日
	 */
	@RequestMapping("/cancelOrderByAdmin")
	public JSONMessage cancelOrderByAdmin(OrderParam param) throws HttpApiException {
		orderService.cancelOrder(param, ReqUtil.instance.getUserId(), OrderEnum.orderCancelEnum.manual.getIndex());
		return JSONMessage.success();
	}

	/**
	 * 
	 * @api {[get,post]} /pack/order/queryOrderByConditions 集团平台与运营后台，多条件查询订单列表
	 * @apiVersion 1.0.0
	 * @apiName queryOrderByConditions
	 * @apiGroup 订单
	 * @apiDescription 集团平台与运营后台，多条件查询订单列表
	 * @apiParam {String} access_token token
	 * @apiParam {String} doctorName 医生姓名
	 * @apiParam {String} patientName 患者姓名
	 * @apiParam {String} userName 用户姓名
	 * @apiParam {String} orderNo 订单号
	 * @apiParam {String} telephone 手机号
	 * @apiParam {Integer} orderStatus 订单状态
	 * @apiParam {Integer} packType 订单类型
	 * @apiParam {String} groupId 集团id
	 * @apiParam {Long} startCreateTime 下单时间start
	 * @apiParam {Long} endCreateTime 下单时间end
	 * @apiParam {Integer} pageIndex 页码 起使页为0
	 * @apiParam {Integer} pageSize 分页记录数
	 * 
	 * @apiSuccess {Integer} pageCount 总页数
	 * @apiSuccess {Integer} pageIndex 当前页数
	 * @apiSuccess {Long} total 总记录数
	 * 
	 * @apiSuccess {List} pageData.OrderVO 订单数据对象
	 * @apiSuccess {Integer} OrderVO.orderId 订单ID
	 * @apiSuccess {Integer} OrderVO.orderNo 订单号
	 * @apiSuccess {String} OrderVO.doctorName 医生姓名
	 * @apiSuccess {String} OrderVO.userName 用户姓名
	 * @apiSuccess {String} OrderVO.patientName 患者姓名
	 * @apiSuccess {String} OrderVO.relation 患者关系
	 * @apiSuccess {Integer} OrderVO.packType 套餐类型 1：消息图文2:电话咨询
	 *             (报到类订单没有套餐为0),3:关怀计划，4：随访计划
	 * @apiSuccess {Integer} OrderVO.orderStatus 订单状态
	 * @apiSuccess {Integer} OrderVO.price 价格
	 * @apiSuccess {String} OrderVO.groupName 集团名称
	 * @apiSuccess {Long} OrderVO.createTime 创建时间
	 * @apiSuccess {Long} OrderVO.payTime 支付时间
	 * 
	 * 
	 * @author CQY
	 * @date 2016年8月18日
	 */
	@RequestMapping("/queryOrderByConditions")
	public JSONMessage queryOrderByConditions(OrderParam param) {
		return JSONMessage.success(orderService.queryOrderByConditions(param, ReqUtil.instance.getUser()));
	}

	/**
	 *
	 * @api {[get,post]} /pack/order/orderSimpleInfo 集团平台与运营后台，查看订单详情
	 * @apiVersion 1.0.0
	 * @apiName orderSimpleInfo
	 * @apiGroup 订单
	 * @apiDescription 集团平台与运营后台，查看订单详情
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单id
	 *
	 * @apiSuccess {Integer} id id
	 * @apiSuccess {Integer} orderStatus 订单状态(3:已支付4:已完成5:已取消)
	 * @apiSuccess {Integer} sessionStatus 会话状态
	 * @apiSuccess {String} disease 所患疾病
	 * @apiSuccess {String} diseaseDuration 病症时长
	 * @apiSuccess {String} diseaseDesc 病情描述
	 * @apiSuccess {String} drugInfo 用药情况
	 * @apiSuccess {String[]} drugNames 用药药品名称
	 * @apiSuccess {String[]} drugPics 用药图片地址
	 * @apiSuccess {String} hopeHelp 获得帮助
	 * @apiSuccess {String[]} pics 图片地址
	 * @apiSuccess {String[]} checkSuggestNames 检查建议
	 * @apiSuccess {Object[]} recipeDetailViews 用药建议
	 * @apiSuccess {String} recipeDetailViews.goodsId 品种id
	 * @apiSuccess {Integer} recipeDetailViews.goodsNumber 品种数量
	 * @apiSuccess {String} recipeDetailViews.patients 适用人群
	 * @apiSuccess {String} recipeDetailViews.periodUnit 用药周期 单位（例如 日，月，周）
	 * @apiSuccess {Integer} recipeDetailViews.periodNum 用药周期 长度
	 * @apiSuccess {Integer} recipeDetailViews.periodTimes 用药周期 服药次数
	 * @apiSuccess {String} recipeDetailViews.doseQuantity 每次服药 数量
	 * @apiSuccess {String} recipeDetailViews.doseUnit 每次服药 单位（例如 粒，克，瓶）
	 * @apiSuccess {String} recipeDetailViews.doseMothod 服药方法
	 * @apiSuccess {Integer} recipeDetailViews.doseDays 服药持续时间
	 * @apiSuccess {String} recipeDetailViews.goodsTitle 品种标题
	 * @apiSuccess {String} recipeDetailViews.goodsSpecification 品种规格
	 * @apiSuccess {String} recipeDetailViews.goodsPackSpecification 品种包装规格
	 * @apiSuccess {String} recipeDetailViews.goodsManufacturer 品种生产厂家
	 * @apiSuccess {String} recipeDetailViews.goodsPackUnit 品种包装单位
	 * @apiSuccess {String[]} evaluation 患者评价
	 *
	 * @author liangcs
	 * @date 2016年12月1日
	 */
	@RequestMapping("/orderSimpleInfo")
	public JSONMessage getOrderSimpleInfo(Integer orderId) throws HttpApiException {
		return JSONMessage.success(orderService.getOrderSimpleInfo(orderId));
	}

	/**
	 * @api {get}  /pack/order/findDoctorInfoGroup 获取订单的专家组成员用户信息
	 * @apiVersion 1.0.0
	 * @apiName findDoctorInfoGroup
	 * @apiGroup User
	 * @apiDescription 获取专家组成员用户信息
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单ID
	 * @apiParam {Integer[]} docIds 医生IDS
	 * 
	 * @apiSuccess {String} groupName 集团名称
	 * @apiSuccess {String} disease 擅长病种
	 * @apiSuccess {String} skill 擅长
	 * @apiSuccess {String} cureNum 就诊数
	 * @apiSuccess {String} doctorId 医生ID
	 * @apiSuccess {String} doctorName 医生名称
	 * @apiSuccess {String} headPicFileName 头像图片
	 * @apiSuccess {String} departments 科室
	 * @apiSuccess {String} title 职称
	 * @apiSuccess {String} groupType 类型1：主医生，默认为0
	 * 
	 * @apiAuthor 肖伟
	 * @date 2017年01月07日
	 * 
	 * 从/pack/carePlan/findDoctorInfoGroup 计划关怀专家组成员 迁移过来的接口
	 * 
	 */
	@RequestMapping("findDoctorInfoGroup")
	public JSONMessage findDoctorInfoGroup(@RequestParam(required=true) Integer orderId, Integer[] docIds) {
		if (null == docIds || 0 == docIds.length) {
			return JSONMessage.failure("参数有误");
		}

		List<CarePlanDoctorVO> list = orderService.findDoctorInfoGroup(orderId, Arrays.asList(docIds));
		return JSONMessage.success(null, list);
	}

    /**
     * @api {get}  /pack/order/getOrderTypeByGId 根据gid获取订单类型（用来区分IM菜单）
     * @apiVersion 1.0.0
     * @apiName getOrderTypeByGId
     * @apiGroup 订单
     * @apiDescription 根据gid获取订单类型（用来区分IM菜单）
     *
     * @apiParam {String} access_token token
     * @apiParam {String} gid 会话组id
     *
     * @apiSuccess {Integer} orderType 订单类型
     *
     * @apiAuthor 傅永德
     * @date 2017年03月17日
     *
     */
    @RequestMapping("getOrderTypeByGId")
	public JSONMessage getOrderTypeByGId(@RequestParam(name = "gid") String gid) {
		return JSONMessage.success(orderService.getOrderTypeByGId(gid));
	}
	
}

class DownOrder extends GeneralExcelUtil<OrderVO> {

	public DownOrder(String[] keys, String[] columes, List<OrderVO> list) {
		super(keys, columes, list);
	}

	@Override
	public List<Map<String, Object>> createExcelRecord() {
		List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
		if ((this.getDataList() == null) || (this.getDataList().size() == 0)) {
			return listmap;
		}
		// Map<String, Object> map = new HashMap<String, Object>();
		// map.put("sheetName", "sheet1");
		// listmap.add(map);
		for (int i = 0; i < this.getDataList().size(); i++) {
			OrderVO vo = this.getDataList().get(i);
			Map<String, Object> mapValue = new HashMap<String, Object>();
			mapValue.put("dName", vo.getDoctorName());
			mapValue.put("pName", vo.getPatientName());
			mapValue.put("tStr", vo.getOrderTypeStr());
			mapValue.put("orderNo", vo.getOrderNo());
			mapValue.put("time", vo.getFormateTime());
			mapValue.put("price", calculateMoney(Double.valueOf(vo.getPrice()), Double.valueOf(100)));
			mapValue.put("sStr", vo.getOrderStatusStr());
			mapValue.put("hName", vo.getHospitalName());
			listmap.add(mapValue);
		}
		return listmap;
	}

	private double calculateMoney(Double a, Double b) {
		if (a == null) {
			a = Double.valueOf(0);
		}
		if (b == null) {
			b = Double.valueOf(0);
		}
		return new BigDecimal(a).divide(new BigDecimal(b)).doubleValue();
	}



}
