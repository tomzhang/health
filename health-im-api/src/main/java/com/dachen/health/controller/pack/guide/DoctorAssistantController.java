package com.dachen.health.controller.pack.guide;

import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.AssistantSessionRelation;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.vo.OrderVO;
import com.dachen.health.pack.order.service.IAssistantSessionRelationService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.ReqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctorAssistant")
public class DoctorAssistantController {


    @Autowired
    private IOrderService orderService;


    @Autowired
    private UserManager userManager;

    @Autowired
    private IAssistantSessionRelationService assistantSessionRelationService;


    /**
     * @api {get} /doctorAssistant/setConsultationTime	填写患者咨询时间（医生助手端）
     * @apiVersion 1.0.0
     * @apiName setConsultationTime
     * @apiGroup 医生助手
     * @apiDescription 填写患者咨询时间
     * @apiParam {String} 	access_token 		token
     * @apiParam {Integer} 	orderId 				订单Id
     * @apiParam {Long}		appointTime		预约电话咨询时间，时间戳
     * @apiSuccess {String} 	resultCode			1 成功
     * @apiAuthor CQY
     * @date 2016年7月22日
     */
    @RequestMapping(value = "/setConsultationTime")
    public JSONMessage setConsultationTime(Integer orderId, Long appointTime) throws HttpApiException {
        orderService.setConsultationTime(orderId, appointTime);
        //系统到了咨询时间给医生助手发送短信
        return JSONMessage.success(null);
    }

    /**
     * @api {get} /doctorAssistant/isOverPayTimeTwoDays	判断订单是否支付超过48小时
     * @apiVersion 1.0.0
     * @apiName isOverPayTimeTwoDays
     * @apiGroup 医生助手
     * @apiDescription 判断订单是否支付超过48小时
     * @apiParam {String} 	access_token 		token
     * @apiParam {Integer} 	orderId 				订单Id
     * @apiSuccess {String} 	resultCode			1 成功
     * @apiSuccess {Object} 	data					1 超过48小时；0没有超过48小时
     * @apiAuthor CQY
     * @date 2016年7月22日
     */
    @RequestMapping(value = "/isOverPayTimeTwoDays")
    public JSONMessage isOverPayTimeTwoDays(Integer orderId) {

        Order order = orderService.getOne(orderId);
        Integer data = 0;
        if (System.currentTimeMillis() >= order.getPayTime() + 48 * 60 * 60 * 1000) {
            data = 1;
        }
        return JSONMessage.success(data);
    }

    /**
     * @api {get} /doctorAssistant/cancelOrder	取消订单
     * @apiVersion 1.0.0
     * @apiName cancelOrder
     * @apiGroup 医生助手
     * @apiDescription 取消订单
     * @apiParam {String} 	access_token 		token
     * @apiParam {Integer} 	orderId 				订单Id
     * @apiParam {String}		msg					取消订单原因
     * @apiParam {Integer} 	cancelType 		取消类型（1.超时患者取消；2.联系医生助手取消；3.医生助手取消订单）
     * @apiSuccess {String} 	resultCode			1 成功
     * @apiAuthor CQY
     * @date 2016年7月22日
     */
    @RequestMapping(value = "/cancelOrder")
    public JSONMessage cancelOrder(String msg, Integer orderId, Integer cancelType) throws HttpApiException {
        if (cancelType != 1 && cancelType != 2 && cancelType != 3) {
            return JSONMessage.failure("请求参数不正确");
        }
        if (msg.length() > 50) {
            return JSONMessage.failure("取消原因不能超过50个字");
        }
        orderService.cancelOrder(msg, orderId, cancelType);
        return JSONMessage.success(null);
    }


    /**
     * @api {get}  /doctorAssistant/queryOrderByConditions 医生助手根据条件查询订单列表
     * @apiVersion 1.0.0
     * @apiName queryOrderByConditions
     * @apiGroup 医生助手
     * @apiDescription 医生助手根据条件查询订单列表
     * @apiParam {String} access_token token
     * @apiParam {Integer} pageIndex 页码 起使页为0
     * @apiParam {Integer} pendingOrderStatus 待处理订单状态：0.查询全部订单，1.查询待处理订单，，不传默认待处理
     * @apiParam {Integer} orderStatus 订单状态 1:待预约，2：待支付，3：已支付，4：已完成，5：已取消
     * {@link OrderEnum.OrderStatus}
     * @apiParam {Integer} packType 套餐类型 1.图文咨询 2.电话咨询 3.健康关怀 12.积分问诊
     * {@link PackEnum.PackType}
     * @apiParam {Integer} doctorId 医生ID
     * @apiParam {Integer} userId 下单用户ID
     * @apiParam {Integer} patientId 病人ID
     * @apiSuccess {Integer} pageCount 总页数
     * @apiSuccess {Integer} pageIndex 当前页数
     * @apiSuccess {Long} total 总记录数
     * @apiSuccess {List} pageData.OrderVO 订单数据对象
     * @apiSuccess {Integer} OrderVO.orderId 订单ID
     * @apiSuccess {Integer} OrderVO.doctorId 医生ID
     * @apiSuccess {Integer} OrderVO.orderType 订单类型
     * @apiSuccess {Integer} OrderVO.packType 套餐类型（用作过滤） 1.图文咨询 2.电话咨询 3.健康关怀 12.积分问诊
     * @apiSuccess {Integer} OrderVO.timeLong 套餐时间
     * @apiSuccess {Short} OrderVO.sex 患者性别1:男，2:女，３保密
     * @apiSuccess {Integer} OrderVO.age 患者年龄
     * @apiSuccess {String} OrderVO.topPath 患者头像
     * @apiSuccess {String} OrderVO.area 患者地区
     * @apiSuccess {String} OrderVO.relation 患者关系
     * @apiSuccess {String} OrderVO.telephone 患者电话
     * @apiSuccess {Long} OrderVO.price 套餐价格
     * @apiSuccess {Integer} OrderVO.diseaseId 病情ID
     * @apiSuccess {Integer} OrderVO.orderStatus 订单状态:1.待预约 ;2.待支付;3.已支付;4.已完成;5.已取消;6.进行中;7.待完善;8.已拒绝;10.预约成功;
     * @apiSuccess {Integer} OrderVO.refundStatus 订单退款状态　1:未退款，3：已退款
     * @apiSuccess {Long} OrderVO.createTime 创建时间单位毫秒数
     * @apiSuccess {Long} OrderVO.payTime 支付订单时间单位毫秒数
     * @apiSuccess {Long} OrderVO.payType 支付类型 1:微信支付，2：支付宝
     * @apiSuccess {Long} OrderVO.assistantId 医生助手ID
     * @apiSuccess {String} OrderVO.pointName 积分问诊套餐名称
     * @apiSuccess {Integer} OrderVO.point 积分问诊点数
     * @apiSuccess {Integer} OrderVO.pendingOrderStatus 订单待处理状态：1.待处理，0.其他(待处理订单特有字段)
     * @apiSuccess {Integer} OrderVO.pendingOrderWaitType 订单待处理类型：1.图文订单-医生未回复，2.电话订单-医生未开始，3.电话订单-医生未结束，4.健康关怀-患者未答题(待处理订单特有字段)
     * @apiSuccess {Long} OrderVO.waitTime 等待时间(待处理订单特有字段)
     * @apiSuccess {Object} OrderVO.userVo 下单用户信息
     * @apiSuccess {Integer} OrderVO.userVo.userId 下单用户ID
     * @apiSuccess {String} OrderVO.userVo.userName 下单用户名字
     * @apiSuccess {String} OrderVO.userVo.headPriPath 下单用户图片
     * @apiSuccess {Object} OrderVO.doctorVo 医生信息
     * @apiSuccess {String} OrderVO.doctorVo.doctorName 医生名称
     * @apiSuccess {String} OrderVO.doctorVo.hospital 医生所属医院
     * @apiSuccess {String} OrderVO.doctorVo.title 医生职称
     * @apiSuccess {String} OrderVO.doctorVo.doctorPath 医生头像图片
     * @apiSuccess {String} OrderVO.doctorVo.doctorSpecialty 医生所属专科
     * @apiSuccess {String} OrderVO.doctorVo.telephone 医生手机号
     * @apiSuccess {String} OrderVO.doctorVo.doctorGroup 医生集团名称
     * @apiSuccess {String} OrderVO.orderSession 						订单会话对象
     * @apiSuccess {String} OrderVO.orderSession.msgGroupId 					患者医生会话组id
     * @apiSuccess {String} OrderVO.orderSession.assistantPatientGroupId 	医助患者会话组id
     * @apiSuccess {String} OrderVO.orderSession.assistantDoctorGroupId 	医助医生会话组id
     * @apiAuthor CQY
     * @date 2016年7月25日
     */
    @RequestMapping(value = "/queryOrderByConditions")
    public JSONMessage queryOrderByConditions(OrderParam param) {
        param.setAssistantId(ReqUtil.instance.getUserId());
        if (param.getPendingOrderStatus() == null) {
            param.setPendingOrderStatus(1);//默认待处理
        }
        return JSONMessage.success(orderService.queryOrderByConditions(param));
    }

    /**
     * @api {get}  /doctorAssistant/queryOrderByConditionsForDoctor 根据条件查询订单列表(医生)
     * @apiVersion 1.0.0
     * @apiName queryOrderByConditionsForDoctor
     * @apiGroup 医生助手
     * @apiDescription 根据条件查询订单列表（医生）
     * @apiParam {String} access_token token
     * @apiParam {Integer} pageIndex 页码 起使页为0
     * @apiParam {Integer} orderStatus 订单状态 1:待预约，2：待支付，3：已支付，4：已完成，5：已取消
     * {@link OrderEnum.OrderStatus}
     * @apiParam {Integer} packType 套餐类型 1.图文咨询 2.电话咨询 3.健康关怀 12.积分问诊
     * {@link PackEnum.PackType}
     * @apiParam {Integer} doctorId 医生ID
     * @apiSuccess {Integer} pageCount 总页数
     * @apiSuccess {Integer} pageIndex 当前页数
     * @apiSuccess {Long} total 总记录数
     * @apiSuccess {List} pageData.OrderVO 订单数据对象
     * @apiSuccess {Integer} OrderVO.orderId 订单ID
     * @apiSuccess {Integer} OrderVO.doctorId 医生ID
     * @apiSuccess {Integer} OrderVO.orderType 订单类型
     * @apiSuccess {Integer} OrderVO.packType 套餐类型（用作过滤） 1.图文咨询 2.电话咨询 3.健康关怀 12.积分问诊
     * @apiSuccess {Integer} OrderVO.timeLong 套餐时间
     * @apiSuccess {Short} OrderVO.sex 患者性别1:男，2:女，３保密
     * @apiSuccess {Integer} OrderVO.age 患者年龄
     * @apiSuccess {String} OrderVO.topPath 患者头像
     * @apiSuccess {String} OrderVO.area 患者地区
     * @apiSuccess {String} OrderVO.relation 患者关系
     * @apiSuccess {String} OrderVO.telephone 患者电话
     * @apiSuccess {String} OrderVO.userName 患者电话
     * @apiSuccess {Long} OrderVO.price 套餐价格
     * @apiSuccess {Integer} OrderVO.diseaseId 病情ID
     * @apiSuccess {Integer} OrderVO.orderStatus 订单状态:1.待预约 ;2.待支付;3.已支付;4.已完成;5.已取消;6.进行中;7.待完善;8.已拒绝;10.预约成功;
     * @apiSuccess {Integer} OrderVO.refundStatus 订单退款状态　1:未退款，3：已退款
     * @apiSuccess {Long} OrderVO.createTime 创建时间单位毫秒数
     * @apiSuccess {Long} OrderVO.payTime 支付订单时间单位毫秒数
     * @apiSuccess {Long} OrderVO.payType 支付类型 1:微信支付，2：支付宝
     * @apiSuccess {Long} OrderVO.assistantId 医生助手ID
     * @apiSuccess {String} OrderVO.pointName 积分问诊套餐名称
     * @apiSuccess {Integer} OrderVO.point 积分问诊点数
     * @apiSuccess {Long} OrderVO.waitTime 等待时间(待处理订单特有字段)
     * @apiAuthor CQY
     * @date 2017年1月5日
     */
    @RequestMapping(value = "/queryOrderByConditionsForDoctor")
    public JSONMessage queryOrderByConditionsForDoctor(OrderParam param) {
        param.setAssistantId(ReqUtil.instance.getUserId());
        return JSONMessage.success(orderService.queryOrderByConditionsForDoctor(param));
    }

    /**
     * @api {get}  /doctorAssistant/queryOrderByConditionsForPatient 根据条件查询订单列表(患者)
     * @apiVersion 1.0.0
     * @apiName queryOrderByConditionsForPatient
     * @apiGroup 医生助手
     * @apiDescription 医生助手根据条件查询订单列表
     * @apiParam {String} access_token token
     * @apiParam {Integer} pageIndex 页码 起使页为0
     * @apiParam {Integer} orderStatus 订单状态 1:待预约，2：待支付，3：已支付，4：已完成，5：已取消
     * {@link OrderEnum.OrderStatus}
     * @apiParam {Integer} packType 套餐类型 1.图文咨询 2.电话咨询 3.健康关怀 12.积分问诊
     * {@link PackEnum.PackType}
     * @apiParam {Integer} patientId 病人ID
     * @apiParam {Integer} doctorId  医生Id
     * @apiSuccess {Integer} pageCount 总页数
     * @apiSuccess {Integer} pageIndex 当前页数
     * @apiSuccess {Long} total 总记录数
     * @apiSuccess {List} pageData.OrderVO 订单数据对象
     * @apiSuccess {Integer} OrderVO.orderId 订单ID
     * @apiSuccess {Integer} OrderVO.doctorId 医生ID
     * @apiSuccess {Integer} OrderVO.orderType 订单类型
     * @apiSuccess {Integer} OrderVO.packType 套餐类型（用作过滤） 1.图文咨询 2.电话咨询 3.健康关怀 12.积分问诊
     * @apiSuccess {Integer} OrderVO.timeLong 套餐时间
     * @apiSuccess {Short} OrderVO.sex 患者性别1:男，2:女，３保密
     * @apiSuccess {Integer} OrderVO.age 患者年龄
     * @apiSuccess {String} OrderVO.topPath 患者头像
     * @apiSuccess {String} OrderVO.area 患者地区
     * @apiSuccess {String} OrderVO.relation 患者关系
     * @apiSuccess {String} OrderVO.telephone 患者电话
     * @apiSuccess {Long} OrderVO.price 套餐价格
     * @apiSuccess {Integer} OrderVO.diseaseId 病情ID
     * @apiSuccess {Integer} OrderVO.orderStatus 订单状态:1.待预约 ;2.待支付;3.已支付;4.已完成;5.已取消;6.进行中;7.待完善;8.已拒绝;10.预约成功;
     * @apiSuccess {Integer} OrderVO.refundStatus 订单退款状态　1:未退款，3：已退款
     * @apiSuccess {Long} OrderVO.createTime 创建时间单位毫秒数
     * @apiSuccess {Long} OrderVO.payTime 支付订单时间单位毫秒数
     * @apiSuccess {Long} OrderVO.payType 支付类型 1:微信支付，2：支付宝
     * @apiSuccess {Long} OrderVO.assistantId 医生助手ID
     * @apiSuccess {String} OrderVO.pointName 积分问诊套餐名称
     * @apiSuccess {Integer} OrderVO.point 积分问诊点数
     * @apiSuccess {Long} OrderVO.waitTime 等待时间(待处理订单特有字段)
     * @apiSuccess {Object} OrderVO.doctorVo 医生信息
     * @apiSuccess {String} OrderVO.doctorVo.doctorName 医生名称
     * @apiSuccess {String} OrderVO.doctorVo.hospital 医生所属医院
     * @apiSuccess {String} OrderVO.doctorVo.title 医生职称
     * @apiSuccess {String} OrderVO.doctorVo.doctorPath 医生头像图片
     * @apiSuccess {String} OrderVO.doctorVo.doctorSpecialty 医生所属专科
     * @apiSuccess {String} OrderVO.doctorVo.telephone 医生手机号
     * @apiSuccess {String} OrderVO.doctorVo.doctorGroup 医生集团名称
     * @apiAuthor CQY
     * @date 2017年1月5日
     */
    @RequestMapping(value = "/queryOrderByConditionsForPatient")
    public JSONMessage queryOrderByConditionsForPatient(OrderParam param) {
        param.setAssistantId(ReqUtil.instance.getUserId());
        return JSONMessage.success(orderService.queryOrderByConditionsForPatient(param));
    }


    /**
     * @api {get} /doctorAssistant/queryDoctorListFromOrder 	医生助手查询对应所有订单关联的医生
     * @apiVersion 1.0.0
     * @apiName queryDoctorListFromOrder
     * @apiGroup 医生助手
     * @apiDescription 医生助手查询对应所有订单关联的医生
     * @apiParam {String} 	access_token 		token
     * @apiSuccess {String} 	resultCode			1 成功
     * @apiSuccess {List}		user					医生集合
     * @apiSuccess {String}	user.name			医生姓名
     * @apiSuccess {Integer}	user.userId		医生ID
     * @apiSuccess {String}	user.headPicFileName	头像
     * @apiSuccess {String}	user.Doctor.title	医生职称
     * @apiAuthor CQY
     * @date 2016年7月22日
     */
    @RequestMapping(value = "/queryDoctorListFromOrder")
    public JSONMessage queryDoctorListFromOrder() {
        Integer userId = ReqUtil.instance.getUserId();
        return JSONMessage.success(orderService.queryDoctors(userId));
    }

    /**
     * @api {get} /doctorAssistant/queryDoctorList 	医生助手查询与其关联的医生
     * @apiVersion 1.0.0
     * @apiName queryDoctorList
     * @apiGroup 医生助手
     * @apiDescription 医生助手查询与其关联的医生
     * @apiParam {String} 	access_token 		token
     * @apiParam {String} 	keywords 		关键字
     * @apiSuccess {String} 	resultCode						1 成功
     * @apiSuccess {List}		user							医生集合
     * @apiSuccess {String}		user.name						医生姓名
     * @apiSuccess {Integer}	user.userId						医生ID
     * @apiSuccess {String}		user.headPicFileName			头像
     * @apiSuccess {String}		user.Doctor.title				医生职称
     * @apiSuccess {String}		user.Doctor.introduction		医生介绍
     * @apiSuccess {String}		user.Doctor.skill				擅长领域
     * @apiAuthor CQY
     * @date 2016年7月22日
     */
    @RequestMapping(value = "/queryDoctorList")
    public JSONMessage queryDoctorList(String keywords) {
        Integer userId = ReqUtil.instance.getUserId();
        return JSONMessage.success(userManager.queryDoctorsByAssistantId(keywords, userId));
    }

    /**
     * @api {get} /doctorAssistant/queryDocInfo 	根据医生用户ID获取医生简要信息
     * @apiVersion 1.0.0
     * @apiName queryDocInfo
     * @apiGroup 医生助手
     * @apiDescription 根据医生用户ID获取医生简要信息
     * @apiParam {String} 	access_token 		token
     * @apiParam {Integer} 	doctorId 		医生用户ID
     * @apiSuccess {String} 	resultCode						1 成功
     * @apiSuccess {String}		doctorName						医生姓名
     * @apiSuccess {Integer}	doctorId						医生ID
     * @apiSuccess {String}		doctorPath			头像
     * @apiSuccess {String}		telephone			电话
     * @apiSuccess {String}		title				医生职称
     * @apiSuccess {String}		hospital		医生医院
     * @apiSuccess {String}		doctorSpecialty				科室
     * @apiSuccess {String}		doctorGroup				       集团
     * @apiSuccess {String} 	introduction 	个人介绍
     * @apiSuccess {String} 	skill 			擅长领域
     * @apiAuthor CQY
     * @date 2016年7月22日
     */
    @RequestMapping(value = "/queryDocInfo")
    public JSONMessage queryDocInfo(Integer doctorId) {
        return JSONMessage.success(null, orderService.findDoctorVo(doctorId));
    }

    /**
     * @api {get} /doctorAssistant/queryOrderById 根据订单ID查询订单详情
     * @apiVersion 1.0.0
     * @apiName queryOrderById
     * @apiGroup 医生助手
     * @apiDescription 订单详情
     * @apiParam {String} access_token token
     * @apiParam {Integer} orderId 订单id
     * @apiSuccess {Integer} 	orderId 订单ID
     * @apiSuccess {Integer} 	doctorId 医生ID
     * @apiSuccess {Integer} 	orderType 订单类型
     * @apiSuccess {Integer} 	packType 套餐类型  1.图文咨询 2.电话咨询 3.健康关怀 12.积分问诊
     * @apiSuccess {Integer} 	timeLong 套餐时间
     * @apiSuccess {String} 	illHistoryInfoId 电子病历ID
     * @apiSuccess {String} 	assistantComment 备注内容
     * @apiSuccess {String} 	expectAppointment  患者期望预约时间
     * @apiSuccess {Short} 		sex 患者性别1:男，2:女，３保密
     * @apiSuccess {Integer} 	age 患者年龄
     * @apiSuccess {String} 	topPath 患者头像
     * @apiSuccess {String} 	area 患者地区
     * @apiSuccess {String} 	relation 患者关系
     * @apiSuccess {String} 	telephone 患者电话
     * @apiSuccess {Long} 		price 套餐价格
     * @apiSuccess {Integer} 	diseaseId 病情ID
     * @apiSuccess {Integer} 	orderStatus 订单状态:1.待预约 ;2.待支付;3.已支付;4.已完成;5.已取消;6.进行中;7.待完善;8.已拒绝;10.预约成功;
     * @apiSuccess {Integer} 	refundStatus 订单退款状态　1:未退款，3：已退款
     * @apiSuccess {Long} 		createTime 创建时间单位毫秒数
     * @apiSuccess {Long} 		payTime 支付订单时间单位毫秒数
     * @apiSuccess {Long} 		payType 支付类型 1:微信支付，2：支付宝
     * @apiSuccess {Long} 		assistantId 医生助手ID
     * @apiSuccess {String} 	pointName 积分问诊套餐名称
     * @apiSuccess {Integer} 	point 积分问诊点数
     * @apiSuccess {Integer} 	pendingOrderStatus 订单待处理状态：1.待处理，0.其他(待处理订单特有字段)
     * @apiSuccess {Integer} 	pendingOrderWaitType 订单待处理类型：1.图文订单-医生未回复，2.电话订单-医生未开始，3.电话订单-医生未结束，4.健康关怀-患者未答题(待处理订单特有字段)
     * @apiSuccess {Long} 		waitTime 等待时间(待处理订单特有字段)
     * @apiSuccess {Object} 	userVo 下单用户信息
     * @apiSuccess {Integer} 		userVo.userId 下单用户ID
     * @apiSuccess {String} 		userVo.userName 下单用户名字
     * @apiSuccess {String} 		userVo.headPriPath 下单用户图片
     * @apiSuccess {String} 		userVo.telephone 下单用户手机号
     * @apiSuccess {Object} 	doctorVo 医生信息
     * @apiSuccess {String} 		doctorVo.doctorName 医生名称
     * @apiSuccess {String} 		doctorVo.hospital 医生所属医院
     * @apiSuccess {String} 		doctorVo.title 医生职称
     * @apiSuccess {String} 		doctorVo.doctorPath 医生头像图片
     * @apiSuccess {String} 		doctorVo.doctorSpecialty 医生所属专科
     * @apiSuccess {String} 		doctorVo.telephone 医生手机号
     * @apiSuccess {String} 		doctorVo.doctorGroup 医生集团名称
     * @apiAuthor CQY
     * @date 2016年1月3日
     */
    @RequestMapping(value = "/queryOrderById")
    public JSONMessage queryOrderById(@RequestParam(required = true) Integer orderId) {
        OrderVO orderVO = orderService.doctorAssistantQueryOrderById(orderId);
        return JSONMessage.success(null, orderVO);
    }

    /**
     * @api {get} /doctorAssistant/updateOrderComment 	助手修改订单备注
     * @apiVersion 1.0.0
     * @apiName updateOrderComment
     * @apiGroup 医生助手
     * @apiDescription 助手修改订单备注
     * @apiParam {String} 	access_token 		token
     * @apiParam {String} 	assistantComment 	备注内容
     * @apiParam {Integer} 	orderId 		医生ID
     * @apiSuccess {String} 	resultCode						1 成功
     * @apiAuthor zhy
     * @date 2017年1月5日
     */
    @RequestMapping("updateOrderComment")
    public JSONMessage updateOrderAssistantComment(Integer orderId, String assistantComment) {
        orderService.updateOrderAssistantComment(orderId, assistantComment);
        return JSONMessage.success(null, null);
    }

    /**
     * @api {get} /doctorAssistant/getMsgGroupInfo 	根据医生或者患者与助手查询创建的会话
     * @apiVersion 1.0.0
     * @apiName getMsgGroupInfo
     * @apiGroup 医生助手
     * @apiDescription 医助:医生端调用时  需要传一个医生ID，即可查到对应的会话 ; 患助:患者端调用时  需要传一个 患者用户ID 患者ID,医生ID ; 助医:助手端调用时，需要传一个助手ID，医生ID; 助患:患者用户ID 患者ID 医生ID 助手ID
     * @apiParam {String} 	access_token 		token
     * @apiParam {Integer} 	doctorId 		医生ID
     * @apiParam {Integer} 	patientId 		患者ID
     * @apiParam {Integer} 	userId 		           患者对应用户ID
     * @apiParam {Integer} 	assistantId     助手ID
     * @apiParam {Integer} 	type            1： 医助;2:患助;3：助医;4:助患
     * @apiSuccess {String} 	resultCode						1 成功
     * @apiAuthor zhy
     * @date 2017年1月5日
     */
    @RequestMapping(value = "/getMsgGroupInfo")
    public JSONMessage getMsgGroupInfo(AssistantSessionRelation param, Integer type) {
        return JSONMessage.success(null, assistantSessionRelationService.add(param, type));
    }

}

