package com.dachen.health.common.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * 
 * @author limiaomiao
 *
 */

import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.entity.SmsLog;
import com.dachen.health.commons.service.SmsLogService;

@RestController
@RequestMapping("/smsLog")
public class SmsLogController extends AbstractController{
	
	@Resource
	SmsLogService service;


	/**
	 * @api {[get,post]} /smsLog/find  短信日志查询
	 * @apiVersion 1.0.0
	 * @apiName 短信日志查询
	 * @apiGroup 短信日志	
	 * 
	 * @apiDescription 短信日志查询
	 * @apiParam  {String}    access_token          token
	 * @apiParam  {String}    content          		短信内容
	 * @apiParam  {String}    toPhone          		目标手机
	 * 
	 * @apiSuccess {String} userid  					发送人
	 *	@apiSuccess {String} content  					短信内容
	 * @apiSuccess {String} toPhone  				目标手机
	 * @apiSuccess {String} createTime  			创建时间
	 * @apiSuccess {String} result  						发送结果
	 * @apiAuthor 李淼淼
	 * @date 2015年7月30日
	 */
	@RequestMapping("find")
	public JSONMessage find(SmsLog param,
			@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
			@RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize
			){
		return JSONMessage.success(null, service.findSmsLog(param, pageIndex, pageSize));
	}
	
	

}
