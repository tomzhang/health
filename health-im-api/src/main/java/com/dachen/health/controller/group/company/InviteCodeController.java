package com.dachen.health.controller.group.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.group.company.entity.po.InviteCode;
import com.dachen.health.group.company.service.InviteCodeService;

/**
 * 
 * @author pijingwei
 * @date 2015/8/20
 */
@RestController
@RequestMapping("/invite/code")
@Deprecated //暂时废弃，没有这块业务
public class InviteCodeController extends AbstractController {

	@Autowired
	private InviteCodeService codeService;
	
	/**
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/getInviteCode")
	public JSONMessage getInviteCode(InviteCode code) {
		return JSONMessage.success(null, codeService.saveInviteCode(code));
	}
	
	/**
     * @api {post} /invite/code/sendInviteCode 发送邀请码
     * @apiVersion 1.0.0
     * @apiName sendInviteCode
     * @apiGroup 集团医生
     * @apiDescription 发送邀请码
     *
     * @apiParam  {String}    	access_token        	token
     * @apiParam  {String}   	companyId				公司Id
     * @apiParam  {Integer}   	doctorId				医生Id
     * @apiParam  {String}   	telephone				发送的手机号码
     *
     * @apiSuccess {String}  	id						记录Id
     * @apiSuccess {Integer}  	doctorId				医生Id
     * @apiSuccess {String}  	companyId				公司Id
     * @apiSuccess {String}  	code					邀请码
     * @apiSuccess {String}  	telephone				手机号
     * @apiSuccess {String}  	status					状态
     * @apiSuccess {Long}  		generateDate			生成时间
     * @apiSuccess {Long}  		useDate					使用时间
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/sendInviteCode")
	public JSONMessage sendInviteCode(InviteCode code) {
		return JSONMessage.success(null, codeService.saveInviteCode(code));
	}
	
	/**
     * @api {post} /invite/code/verifyInviteCode 验证邀请码
     * @apiVersion 1.0.0
     * @apiName verifyInviteCode
     * @apiGroup 集团医生
     * @apiDescription 验证邀请码
     *
     * @apiParam  {String}    	access_token        	token
     * @apiParam  {String}   	code					邀请码
     *
     * @apiSuccess {String}  	id						记录Id
     * @apiSuccess {Integer}  	doctorId				医生Id
     * @apiSuccess {String}  	companyId				公司Id
     * @apiSuccess {String}  	code					邀请码
     * @apiSuccess {String}  	telephone				手机号
     * @apiSuccess {String}  	status					状态
     * @apiSuccess {Long}  		generateDate			生成时间
     * @apiSuccess {Long}  		useDate					使用时间
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/verifyInviteCode")
	public JSONMessage verifyInviteCode(String code) {
		return JSONMessage.success(null, codeService.updateInviteCode(code));
	}
	
}
