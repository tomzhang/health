package com.dachen.health.controller.group.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.group.group.entity.param.GroupSettingParam;
import com.dachen.health.group.group.service.IGroupSettingService;


/**
 * ProjectName： health-im-api<br>
 * ClassName： GroupSearchController<br>
 * Description： 集团设置controller<br>
 * 
 * @author 谢佩
 * @createTime 2015年9月25日
 * @version 1.0.0
 */
@RestController
@RequestMapping("/group/settings")
public class GroupSettingController extends AbstractController{
	@Autowired
	IGroupSettingService groupSettingService;
	
	/**
     * @api {post/get} /group/settings/setResExpert 设置预约专家
     * @apiVersion 1.0.0
     * @apiName setResExpert
     * @apiGroup 集团设置
     * @apiDescription 设置预约专家
     *
     * @apiParam   {String}   access_token                  token
     * @apiParam   {String}   docGroupId                    医生集团ID
     * @apiParam   {String[]} expertIds                     预约专家ID数组
     * 
     * @apiAuthor 谢佩
     * @date 2015年9月25日
     */
	@RequestMapping("/setResExpert")
	public JSONMessage setResExpert(GroupSettingParam param) {
		groupSettingService.setResExpert(param);
		return JSONMessage.success();
	}
	
	/**
     * @api {post/get} /group/settings/setSpecialty 设置专长
     * @apiVersion 1.0.0
     * @apiName setSpecialty
     * @apiGroup 集团设置
     * @apiDescription 设置专长
     *
     * @apiParam   {String}   access_token                  token
     * @apiParam   {String}   docGroupId                    医生集团ID
     * @apiParam   {String[]} specialtyIds                  专长ID数组
     * 
     * @apiAuthor 谢佩
     * @date 2015年9月25日
     */
	@RequestMapping("/setSpecialty")
	public JSONMessage setSpecialty(GroupSettingParam param) {
		groupSettingService.setSpecialty(param);
		return JSONMessage.success();
	}
	
	
	/**
     * @api {post/get} /group/settings/setWeights  设置权重
     * @apiVersion 1.0.0
     * @apiName setWeights
     * @apiGroup 集团设置
     * @apiDescription 设置权重
     *
     * @apiParam   {String}    access_token                  token
     * @apiParam   {String}    docGroupId                     医生集团ID
     * @apiParam   {Integer}   weightsNum                    权重数
     * 
     *
     * @apiAuthor 谢佩
     * @date 2015年9月25日
     */
	@RequestMapping("/setWeights")
	public JSONMessage setWeights(GroupSettingParam param) {
		groupSettingService.setWeights(param);
		return JSONMessage.success();
	}
	
	
	/**
     * @api {post/get} /group/settings/addGoodNum  增加擅长医生个数
     * @apiVersion 1.0.0
     * @apiName addGoodNum
     * @apiGroup 集团设置
     * @apiDescription 增加擅长医生个数
     *
     * @apiParam   {String}    access_token                  token
     * @apiParam   {String}    docGroupId                    医生集团ID
     * @apiParam   {Integer}   countNum                      医生个数
     * 
     * @apiAuthor 谢佩
     * @date 2015年9月25日
     *//*
	@RequestMapping("/addGoodNum")
	public JSONMessage addGoodNum() {
		
		return JSONMessage.success();
	}*/
	
	
	@RequestMapping("/setMsgDisturb")
	public JSONMessage setMsgDisturb(GroupSettingParam param) {
		groupSettingService.setMsgDisturb(param);
		return JSONMessage.success();
	}
	
	/**
     * @api {post} /group/settings/updateDutyTime 设置集团的值班时间段
     * @apiVersion 1.0.0
     * @apiName updateDutyTime
     * @apiGroup 集团设置
     * @apiDescription 设置集团的值班时间段(24小时制)，默认为早上08:00到晚上22:00
     *
     * @apiParam  {String}    	access_token    token
     * @apiParam  {String}   	groupId         集团Id
     * @apiParam  {String}   	dutyStartTime   值班的开始时间，如：08:00
     * @apiParam  {String}   	dutyEndTime     值班的结束时间，如：22:00
     *
     * @apiSuccess {Number} 	resultCode    返回状态码
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/updateDutyTime")
	public JSONMessage updateDutyTime(String groupId, String dutyStartTime, String dutyEndTime) {
		boolean is = false;
		is = groupSettingService.updateDutyTime(groupId, dutyStartTime, dutyEndTime);
		if (is) {
			return JSONMessage.success("success");
		}
		return JSONMessage.failure("failure");
	}
	
}

