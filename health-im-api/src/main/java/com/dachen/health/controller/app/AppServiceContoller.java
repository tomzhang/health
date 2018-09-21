package com.dachen.health.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.app.entity.po.App;
import com.dachen.health.app.service.IAppService;

@RestController
@RequestMapping("/appService")
public class AppServiceContoller {
	@Autowired
	private IAppService appService;
	/**
	 * @api {GET，POST} /appService/getVersion 获取app版本信息
	 * @apiVersion 1.0.0
	 * @apiName getVersion
	 * @apiGroup APP
	 * @apiDescription 获取app版本信息
	 * @apiParam {String} 			appCode 		app 编码 【"code": "com.bestunimed.dgroupdoctor" "博德嘉联医生端"android,
	 * 														 "code": "com.bestunimed.dgrouppatient" 博德嘉联患者端  android,
	 * 														 "code": "com.bd.DGroupDoctor" "博德嘉联医生端"-ios,
     * 														 "code": "com.bd.DGroupPatient" 博德嘉联患者端 -ios】
	 * 
	 * 
	 * @apiSuccess {Integer}         id                app Id
	 * @apiSuccess {String}          code              编码
	 * @apiSuccess {String}          name              中文名称
	 * @apiSuccess {String}          info              信息
	 * @apiSuccess {String}          device            设备 ios/andorid
	 * @apiSuccess {String}          version           版本号
	 * @apiSuccess {String}          downloadUrl       下载地址
     * @apiAuthor  谭永芳
     * 
     * @date 2016年6月13
	 */
	@RequestMapping(value="/getVersion")
	public JSONMessage getVersion(@RequestParam(required=true) String appCode) throws Exception {
		App appVersion = appService.getAppVersion(appCode);
		if(appVersion != null ){
			return JSONMessage.success(appVersion);
		}else{
			return JSONMessage.failure("app版本信息不存在");
		}
	}
}
