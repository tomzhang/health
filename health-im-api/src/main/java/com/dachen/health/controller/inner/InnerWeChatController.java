package com.dachen.health.controller.inner;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.wx.remote.WXRemoteManager;

@RestController
@RequestMapping("/inner_api/wx")
public class InnerWeChatController {

	@Resource
	private WXRemoteManager wxManager;
	
	@Resource
	private UserManager userManager;
	
	/**
	 * @api {get} /inner_api/wx/pointsChangeNotify 积分变动通知
	 * @apiVersion 1.0.0
	 * @apiName pointsChangeNotify
	 * @apiGroup 内部api
	 * @apiDescription 公众号中给指定微信用户推送积分变动消息
	 *
	 * @apiParam {Integer} userId 患者用户Id
	 * @apiParam {String} first 消息标题
	 * @apiParam {String} oncePoints 本次获取或消耗积分
	 * @apiParam {String} totalPoints 剩余积分
	 * @apiParam {String} reason 积分获取或消耗原因
	 * @apiParam {String} remark 备注
	 * 
	 * @apiSuccess {Number} resultCode 返回状态码
	 *
	 * @apiAuthor 谢平
	 * @date 2016年11月07日
	 */
	@RequestMapping("/pointsChangeNotify")
	public JSONMessage pointsChangeNotify(@RequestParam Integer userId, String first, String oncePoints,
			String totalPoints, String reason, String remark) {
		User user = userManager.getUser(userId);
		if (user.getWeInfo() == null || user.getWeInfo().getMpOpenid() == null) {
			JSONMessage.failure("not found openid！");
		}
		wxManager.pointsChangeNotify(user.getWeInfo().getMpOpenid(), first, user.getName(), oncePoints, totalPoints, reason, remark);
		return JSONMessage.success();
	}
	
	/**
	 * @api {get} /inner_api/wx/purchaseDrugNotify 购药提醒
	 * @apiVersion 1.0.0
	 * @apiName purchaseDrugNotify
	 * @apiGroup 内部api
	 * @apiDescription 公众号中给指定用户推送购药提醒
	 *
	 * @apiParam {Integer} userId 患者用户Id
	 * @apiParam {String} first 消息标题
	 * @apiParam {String} name 药品名
	 * @apiParam {String} quantity 数量
	 * @apiParam {String} comment 备注内容
	 * @apiParam {String} remark 备注
	 * 
	 * @apiSuccess {Number} resultCode 返回状态码
	 *
	 * @apiAuthor 谢平
	 * @date 2016年12月16日
	 */
	@RequestMapping("/purchaseDrugNotify")
	public JSONMessage purchaseDrugNotify(@RequestParam String id,@RequestParam Integer userId, String first, String name, String quantity,
			String comment, String remark) {
		User user = userManager.getUser(userId);
		if (user.getWeInfo() == null || user.getWeInfo().getMpOpenid() == null) {
			JSONMessage.failure("not found openid！");
		}
		wxManager.purchaseDrugNotify(user.getWeInfo().getMpOpenid(), id, first, name, quantity, comment, remark);
		return JSONMessage.success();
	}
}
