package com.dachen.health.controller.pub;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.pub.model.param.PubParam;
import com.dachen.pub.service.PubAccountService;
import com.dachen.pub.service.PubCustomerService;
import com.dachen.pub.service.PubGroupService;

/**
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/pub")
public class PubAccountController {
	
	@Autowired
	private PubCustomerService pubCustomerService;
	
	@Autowired
	private PubAccountService pubAccountService;
	
	/**
     * @api {get} /pub/saveDoctorPub 保存医生公共号信息
     * @apiVersion 1.0.0
     * @apiName saveDoctorPub
     * @apiGroup 公共号
     * @apiDescription 保存公共号信息
     * @apiParam {String}   access_token           token
     * @apiParam {String}   pid             公共号Id
     * @apiParam {String}   name            公共号名称
     * @apiParam {String}   photourl        公共号头像Url
     * @apiParam {String}   note            公共号介绍
     * @apiAuthor xieping
     * @date 2015年11月3日
     */
	@RequestMapping(value = "/saveDoctorPub")
	public JSONMessage saveDoctorPub(PubParam pubParam) throws HttpApiException {
		pubAccountService.saveDoctorPub(pubParam);
		return JSONMessage.success(null);
	}
	
	@RequestMapping(value = "/welcome")
	public JSONMessage welcome(Integer userId) throws HttpApiException {
		pubCustomerService.welcome(userId);
		return JSONMessage.success();
	}
	
	
}
