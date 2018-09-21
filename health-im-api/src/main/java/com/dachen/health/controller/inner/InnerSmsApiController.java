package com.dachen.health.controller.inner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.manager.ISMSManager;
import com.mobsms.sdk.MobSmsSdk;

@RestController
@RequestMapping("/inner_api/sms")
public class InnerSmsApiController {
	@Autowired
	MobSmsSdk mobSmsSdk;
	@Autowired
	private ISMSManager smsManager;

	/**
	 * @api {[get,post]} /inner_api/sms/sendMessage 发送短信
	 * @apiVersion 1.0.0
	 * @apiName sendMessage
	 * @apiGroup 内部api
	 * @apiDescription 发送短信
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {String} phone 目标手机
	 * @apiParam {String} content 短信内容
	 * 
	 * @apiSuccess {String} result 发送结果
	 * @apiAuthor 谢佩
	 * @date 2016年1月26日
	 */
	@RequestMapping("sendMessage")
	public JSONMessage sendMessage(String phone, String content) {
		return JSONMessage.success(null, mobSmsSdk.send(phone, content));
	}

	/**
	 * @api {get，post} inner_api/sms/randcode/verifyCode 校验验证码
	 * @apiVersion 1.0.0
	 * @apiName verifyCode
	 * @apiGroup 内部api
	 * @apiDescription 校验验证码
	 * @apiParam {String} telephone 手机号码
	 * @apiParam {String} randcode 验证码
	 * @apiParam {String} templateId 模板ID，非必填
	 * 
	 * @apiAuthor 张垠
	 * @date 2015年11月6日
	 */
	@RequestMapping("/randcode/verifyCode")
	public JSONMessage verifyCode(@RequestParam String telephone, @RequestParam String randcode, String templateId) {
		// templateId都为1 已发版bug后台处理
		templateId = "1";
		boolean isOK = smsManager.isAvailable(telephone, randcode, templateId);
		JSONMessage jMessage;
		if (isOK) {
			jMessage = JSONMessage.success();
		} else {
			jMessage = JSONMessage.failure("验证码错误");
		}
		return jMessage;
	}
}
