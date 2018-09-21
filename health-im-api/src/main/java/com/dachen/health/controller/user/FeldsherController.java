package com.dachen.health.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.commons.service.UserManager;
import com.dachen.util.ReqUtil;

/**
 * 医生助手Controller
 * 
 * @author 钟良
 * @date 2016-07-25
 */

@RestController
@RequestMapping("/feldsher")
public class FeldsherController extends AbstractController {
	@Autowired
	private UserManager userManager;
	
	/**
	 * @api {[get,post]} /feldsher/getFeldsherInfo 获取医生助手详情
	 * @apiVersion 1.0.0
	 * @apiName getFeldsherInfo
	 * @apiGroup Feldsher
	 * @apiDescription 获取用户详情
	 * 
	 * @apiParam {String} access_token token
	 * 
	 * @apiSuccess {String} resultCode  返回码
	 * @apiSuccess {Integer} userId 用户id
	 * @apiSuccess {String} name 姓名
	 * @apiSuccess {String} telephone 电话
	 * @apiSuccess {String} headPicFileName 头像
	 * @apiSuccess {String} status 状态：启用：1；禁用：2  
	 * 
	 * @apiAuthor 钟良
	 * @date 2016年7月25日
	 */
	@RequestMapping("/getFeldsherInfo")
	public JSONMessage getFeldsherInfo() {
		int userId = ReqUtil.instance.getUserId();
		return JSONMessage.success(userManager.getUser(userId));
	}
	
	/**
	 * @api {[get,post]} /feldsher/getFeldsherByDoctor 获取医生助手，根据医生id
	 * @apiVersion 1.0.0
	 * @apiName getFeldsherByDoctor
	 * @apiGroup Feldsher
	 * @apiDescription 获取医生助手，根据医生id
	 * 
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} doctorId 医生id
	 * 
	 * @apiSuccess {String} resultCode  返回码
	 * @apiSuccess {Integer} userId 用户id
	 * @apiSuccess {String} name 姓名
	 * @apiSuccess {String} telephone 电话
	 * @apiSuccess {Long} createTime 创建时间
	 * @apiSuccess {String} status 状态：启用：1；禁用：2  
	 * 
	 * @apiAuthor 钟良
	 * @date 2016年7月25日
	 */
	@RequestMapping("/getFeldsherByDoctor")
	public JSONMessage getFeldsherByDoctor(Integer doctorId) {
		return JSONMessage.success(userManager.getFeldsherByDoctor(doctorId));
	}
}
