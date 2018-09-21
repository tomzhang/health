package com.dachen.line.stat.service;

import com.dachen.sdk.exception.HttpApiException;

import java.text.ParseException;



/**
 * 护士订单服务
 * @author weilit
 * 2015 12 04 
 */
public interface IExceptionOrderService {
	/**
	 * 查询患者下单超过2小时没人接的订单
	 * author：jianghj
	 * date:2015年12月17日13:55:06
	 * 
	 */
	public void getOderOvertopHour() throws HttpApiException;
	/**
	 * 查询预约时间前一天晚上10点之后还未接单的订单
	 * author：jianghj
	 * date:2015年12月17日13:55:06
	 * 
	 */
	public void getBeforeExceptionOrder() throws ParseException, HttpApiException;
	/**
	 * 服务已经结束 但是患者依然没有进行评价 此时要将护士订单进行关闭 并且将这次服务的服务费更新到护士的账户
	 * author：jianghj
	 * date:2015年12月17日13:55:06
	 * 
	 */
	public void getExceptionOrderNoEvaluate() throws ParseException;
	
	/**
	 * 抢单成功--超过30分钟未给患者打电话或者发短信
	 * author：jianghj
	 * date：2015年12月21日11:05:14
	 */
	public void getExceptionNoCallPhone() throws HttpApiException;
	/**
	 * 护士在预约时间之前点击了开始服务--通知客服 目前先将其操作介入日志表
	 * author：jianghj
	 * date：2015年12月21日11:05:14
	 */
	public void getExceptionOfNurseService() throws Exception;
	/**
	 * 过了预约时间护士还没有点击开始服务
	 * author：jianghj
	 * date：2015年12月21日11:05:14
	 */
	public void getExceptionOfNurseServiceNoClick() throws Exception;
}
