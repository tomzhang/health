package com.dachen.health.controller.pack.expand;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.order.service.IOrderExpandService;
import com.dachen.health.pack.schedule.entity.po.Schedule.ScheduleType;
import com.dachen.health.pack.schedule.entity.vo.ScheduleParam;
import com.dachen.health.pack.schedule.service.IScheduleService;
import com.dachen.util.ReqUtil;

@RestController
@RequestMapping("/pack/orderExpand")
public class OrderExpandController {
	
	
	 @Autowired
	 private IOrderExpandService orderExpandService;
	
    /**
     * @api {get} /pack/orderExpand/getSchedule 当天日程列表
     * @apiVersion 1.0.0
     * @apiName getSchedule
     * @apiGroup 日程
     * @apiDescription 当天日程列表（医生、患者、导医通用）
     *
     * @apiParam   {String}          access_token                    token
     * @apiParam   {String}          searchDate                      查询日期 yyyy-MM-dd
     * 
     * @apiSuccess {List}            orderScheduleVo                 订单日程对象
     * @apiSuccess {String}          orderScheduleVo.scheduleTime    日程时间
     * @apiSuccess {String}          orderScheduleVo.title			  套餐名称
     * @apiSuccess {String}          orderScheduleVo.patientName     患者名称
     * @apiSuccess {String}          orderScheduleVo.patientHeadIcon 患者头像
     * @apiSuccess {String}          orderScheduleVo.patientTele     患者电话
     * @apiSuccess {Integer}         orderScheduleVo.patientId       患者Id
     * @apiSuccess {Integer}         orderScheduleVo.doctorId        医生Id
     * @apiSuccess {String}          orderScheduleVo.doctorName      医生名称
     * @apiSuccess {String}          orderScheduleVo.doctorHeadIcon  医生头像
     * @apiSuccess {String}          orderScheduleVo.doctorTele      医生电话
     * @apiSuccess {String}          pageData.troubleFree     		   免打扰（1：正常、2：免打扰）
     * @apiSuccess {String}          orderScheduleVo.price           套餐价格
     * @apiSuccess {String}          orderScheduleVo.orderId      	   订单Id
     * @apiSuccess {Long}          	 orderScheduleVo.appointTime     预约时间
     * 
     * @apiSuccess {List}            onDutyScheduleVo                线下日程对象
     * @apiSuccess {String}          onDutyScheduleVo.scheduleTime   日程时间:上午，下午，晚上
     * @apiSuccess {String}          onDutyScheduleVo.hospital       医院
     * @apiSuccess {String}          onDutyScheduleVo.clinicType     专长
     * @apiSuccess {String}          onDutyScheduleVo.offlineName    线下门诊
     * 
     * @apiSuccess {Number=1}        resultCode                      返回状态码
     * 
     * @apiAuthor  谢佩
     * @date 2015年9月21日
     */
	@RequestMapping("/getSchedule")
    public JSONMessage getDocSchedule(ScheduleParam param) throws HttpApiException {
        param.setUserId(ReqUtil.instance.getUserId());
        param.setUserType(ReqUtil.instance.getUser().getUserType());
        return JSONMessage.success(null,orderExpandService.getDocSchedule(param));
    }
	
	/**
     * @api {get} /pack/orderExpand/scheduleDetail 当天日程详情
     * @apiVersion 1.0.0
     * @apiName scheduleDetail
     * @apiGroup 日程
     * @apiDescription 当天日程详情（医生、患者、导医通用）
     *
     * @apiParam   {String}          access_token    token
     * @apiParam   {String}          searchDate      查询日期 yyyy-MM-dd
     * 
     * @apiSuccess {String}          scheduleTime    日程时间
     * @apiSuccess {String}          title			  套餐名称
     * @apiSuccess {String}          patientName     患者名称
     * @apiSuccess {String}          patientHeadIcon 患者头像
     * @apiSuccess {String}          patientTele     患者电话
     * @apiSuccess {Integer}         patientId       患者Id
     * @apiSuccess {Integer}         doctorId        医生Id
     * @apiSuccess {String}          doctorName      医生名称
     * @apiSuccess {String}          doctorHeadIcon  医生头像
     * @apiSuccess {String}          doctorTele      医生电话
     * @apiSuccess {String}          troubleFree     		   免打扰（1：正常、2：免打扰）
     * @apiSuccess {String}          price           套餐价格
     * @apiSuccess {String}          orderId      	   订单Id
     * @apiSuccess {String}          carePlanName    关怀计划名称
     * @apiSuccess {String}          careItemId      关怀项Id
     * @apiSuccess {Long}          	 appointTime     预约时间
     * 
     * @apiSuccess {OrderDetailVO}   orderDetail     订单详情（请参考order/detail）
     * @apiSuccess {List}   		 callRecordList  通话记录
     * 
     * @apiSuccess {Number=1}        resultCode                      返回状态码
     * 
     * @apiAuthor  谢平
     * @date 2015年12月18日
     */
	@RequestMapping("/scheduleDetail")
	public JSONMessage scheduleDetail(ScheduleParam param) throws HttpApiException {
		param.setUserId(ReqUtil.instance.getUserId());
        param.setUserType(ReqUtil.instance.getUser().getUserType());
		return JSONMessage.success("success", orderExpandService.scheduleDetail(param));
	}
	
	/**
     * @api {get} /pack/orderExpand/get3Schedule 我的日程
     * @apiVersion 1.0.0
     * @apiName get3Schedule
     * @apiGroup 日程
     * @apiDescription 我的日程（患者，只显示3条）
     *
     * @apiParam   {String}          access_token    token
     * @apiParam   {Long}          	 startDate       查询日期（包含时分秒）
     * 
     * @apiSuccess {String}          scheduleTime    日程时间
     * @apiSuccess {String}          title			  套餐名称
     * @apiSuccess {String}          doctorName      医生名称
     * @apiSuccess {String}          orderId      	   订单Id
     * @apiSuccess {Long}          	 appointTime     预约时间
     * 
     * @apiAuthor  谢平
     * @date 2015年12月8日
     */
	@RequestMapping("/get3Schedule")
	public JSONMessage get3Schedule(ScheduleParam param) throws HttpApiException {
		param.setUserId(ReqUtil.instance.getUserId());
        param.setUserType(ReqUtil.instance.getUser().getUserType());
        param.setPageIndex(0);
        param.setPageSize(3);
        return JSONMessage.success(null,orderExpandService.getPatSchedule(param));
	}
	
	
	
	/**
     * @api {get} /pack/orderExpand/scheduleTime 日程记录列表
     * @apiVersion 1.0.0
     * @apiName scheduleTime
     * @apiGroup 日程
     * @apiDescription 日程记录列表，根据当前查询日期，查询本月每天是否有日程（医生、患者、导医通用）
     *
     * @apiParam   {String}          access_token          token
     * @apiParam   {String}          searchDate            查询日期 yyyy-MM-dd
     * 
     * @apiSuccess {String}          dayNum                日程记录
     * @apiSuccess {Integer}         isTrue                是否  0:否，1：有    
     * 
     * @apiSuccess {Number=1}        resultCode            返回状态码
     * 
     * @apiAuthor  谢佩
     * @date 2015年9月21日
     */
	@RequestMapping("/scheduleTime")
    public JSONMessage getScheduleTime(ScheduleParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        param.setUserType(ReqUtil.instance.getUser().getUserType());
        return JSONMessage.success(null,orderExpandService.getDocScheduleRecord(param));
    }
	
	/**
     * @api {get} /pack/orderExpand/getSchedules 全部日程列表
     * @apiVersion 1.0.0
     * @apiName getSchedules
     * @apiGroup 日程
     * @apiDescription 根据当前查询日期，获取当前用户的所有日程列表
     *
     * @apiParam   {String}          access_token          token
     * @apiParam   {String}          searchDate            查询日期 yyyy-MM-dd
     * @apiParam  {Integer}   		 pageIndex             查询页，从0开始
     * @apiParam  {Integer}   		 pageSize              每页显示条数，不传默认15条
     * 
     * @apiSuccess {List}            pageData                 订单日程对象
     * @apiSuccess {String}          pageData.scheduleTime    日程时间
     * @apiSuccess {String}          pageData.packName        套餐名称
     * @apiSuccess {String}          pageData.patientName     患者名称
     * @apiSuccess {String}          pageData.patientHeadIcon 患者头像
     * @apiSuccess {String}          pageData.patientTele     患者电话
     * @apiSuccess {Integer}         pageData.patientId       患者Id
     * @apiSuccess {Integer}         pageData.doctorId        医生Id
     * @apiSuccess {String}          pageData.doctorName      医生名称
     * @apiSuccess {String}          pageData.doctorHeadIcon  医生头像
     * @apiSuccess {String}          pageData.doctorTele      医生电话
     * @apiSuccess {String}          pageData.troubleFree     免打扰（1：正常、2：免打扰）
     * @apiSuccess {String}          pageData.price           套餐价格
     * @apiSuccess {Integer}         pageData.orderId         订单Id
     * @apiSuccess {String}          pageData.carePlanName    关怀计划名称
     * @apiSuccess {String}          pageData.careItemId      关怀项Id
     * @apiSuccess {Long}          	 pageData.appointTime     预约时间
     * @apiSuccess {Integer}         pageData.flag     		     拨打标志（0未拨打、1拨打成功、2拨打失败、3正在拨打）
     * @apiSuccess {Integer}         pageData.scheduleType    日程类型（1订单日程、2病情跟踪、3量表、4调查表、5检查项、6提醒）
     * 
     * @apiAuthor  谢平
     * @date 2015年11月19日
     */
	@RequestMapping("/getSchedules")
    public JSONMessage getSchedules(ScheduleParam param) throws HttpApiException {
        param.setUserId(ReqUtil.instance.getUserId());
        param.setUserType(ReqUtil.instance.getUser().getUserType());
		return JSONMessage.success(null, orderExpandService.getSchedules(param));
    }
	
	/**
     * @api {get} /pack/orderExpand/getNoService 全部日程列表中当天之后尚未进行服务的订单数量
     * @apiVersion 1.0.0
     * @apiName getNoService
     * @apiGroup 日程
     * @apiDescription 根据当前查询日期，当天之后尚未进行服务的订单数量
     *
     * @apiParam   {Integer}          userId        登录id
     * 
     * @apiSuccess {List}            pageData                 订单日程对象
     * @apiSuccess {String}          pageData.scheduleTime    日程时间
     * @apiSuccess {String}          pageData.packName        套餐名称
     * @apiSuccess {String}          pageData.patientName     患者名称
     * @apiSuccess {String}          pageData.patientHeadIcon 患者头像
     * @apiSuccess {String}          pageData.patientTele     患者电话
     * @apiSuccess {Integer}         pageData.patientId       患者Id
     * @apiSuccess {Integer}         pageData.doctorId        医生Id
     * @apiSuccess {String}          pageData.doctorName      医生名称
     * @apiSuccess {String}          pageData.doctorHeadIcon  医生头像
     * @apiSuccess {String}          pageData.doctorTele      医生电话
     * @apiSuccess {String}          pageData.troubleFree     免打扰（1：正常、2：免打扰）
     * @apiSuccess {String}          pageData.price           套餐价格
     * @apiSuccess {String}          pageData.orderId         订单Id
     * @apiSuccess {String}          pageData.carePlanName    关怀计划名称
     * @apiSuccess {String}          pageData.careItemId      关怀项Id
     * @apiSuccess {Long}          	 pageData.appointTime     预约时间
     * @apiSuccess {Integer}         pageData.flag     		     拨打标志（0未拨打、1拨打成功、2拨打失败、3正在拨打）
     * @apiSuccess {Integer}         pageData.scheduleType    日程类型（1订单日程、2病情跟踪、3量表、4调查表、5检查项、6提醒）
     * 
     * @apiAuthor  姜宏杰
     * @date 2016年2月19日10:10:46
     */
	@RequestMapping("/getNoService")
    public JSONMessage getNoService(ScheduleParam param) throws HttpApiException {
        param.setUserId(param.getUserId());
        //param.setUserType(ReqUtil.instance.getUser().getUserType());
        param.setType(ScheduleType.order);
		return JSONMessage.success(null, orderExpandService.getNoServiceCount(param));
    }
	
	
	@Autowired
	private IScheduleService schedule;
	@RequestMapping("/autoSendSchedule")
	public JSONMessage autoSendSchedule() {
		schedule.scheduleRemind();
		return JSONMessage.success();
	}
	
	
}
