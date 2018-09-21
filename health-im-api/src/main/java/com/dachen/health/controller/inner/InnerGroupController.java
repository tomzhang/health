package com.dachen.health.controller.inner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.group.group.service.IGroupService;

@RestController
@RequestMapping("/inner_api/group")
public class InnerGroupController {
	
	@Autowired
	private IGroupService groupService;
	
	/**
     * @api {post} /group/getGroupById 获取集团详情
     * @apiVersion 1.0.0
     * @apiName InnerAPI-getGroupById
     * @apiGroup 集团模块
     * @apiDescription 根据集团Id获取集团详情
     *
     * @apiParam  {String}    	access_token                token
     * @apiParam  {String} 		id                  		集团Id
     * 
     * @apiSuccess {String}   	id           				记录Id
     * @apiSuccess {String}   	companyId           		所属公司Id
     * @apiSuccess {String}   	name       					集团名称
     * @apiSuccess {String}   	introduction       			集团简介
     * @apiSuccess {Integer}  	creator        				创建人
     * @apiSuccess {Long}  		creatorDate       			创建时间
     * @apiSuccess {Integer}  	updator        				更新人
     * @apiSuccess {Long}  		updatorDate       			更新时间
     * @apiSuccess {Long}  		logoUrl       				logo
     * @apiParam  {String}   	standard					医生加入集团标准
     * @apiSuccess {GroupConfig}  	config    				集团设置信息
     * @apiSuccess {boolean}  	config.memberInvite    		是否允许成员邀请医生加入  true：允许，false：不允许
     * @apiSuccess {boolean}  	config.passByAudit   		成员邀请是否需要审核  true：允许，false：不允许
     * @apiSuccess  {boolean}  	config.memberApply   	是否允许医生申请加入  true：允许，false：不允许
     * @apiSuccess  {boolean}  	config.openAppointment   	是否开启名医面对面  true：开启，false：不开启
     * @apiSuccess {String}   	config.dutyStartTime     	值班的开始时间，如：08:00
     * @apiSuccess {String}   	config.dutyEndTime     		值班的结束时间，如：22:00
     * @apiSuccess {Integer}  	outpatientPrice        		值班价格
     * @apiSuccess  {Integer}   	config.textParentProfit     图文资讯 上级抽成比例
     * @apiSuccess  {Integer}   	config.textGroupProfit     图文资讯 集团抽成比例
     * @apiSuccess  {Integer}   	config.phoneParentProfit     	 电话资讯 上级抽成比例
     * @apiSuccess  {Integer}   	config.phoneGroupProfit     	电话资讯 集团抽成比例
     * @apiSuccess  {Integer}   	config.carePlanParentProfit     	 健康关怀 上级抽成比例
     * @apiSuccess  {Integer}   	config.carePlanGroupProfit     	健康关怀 集团抽成比例
     * @apiSuccess  {Integer}   	config.clinicParentProfit     	 门诊 上级抽成比例
     * @apiSuccess  {Integer}   	config.clinicGroupProfit     		 门诊 集团抽成比例
     * @apiSuccess  {Integer}   	config.consultationParentProfit     	 会诊 上级抽成比例
     * @apiSuccess  {Integer}   	config.consultationGroupProfit     		会诊 集团抽成比例
     * @apiSuccess  {Integer}   	config.appointmentGroupProfit     	 预约(名医面对面)集团抽成比例
     * @apiSuccess  {Integer}   	config.appointmentParentProfit     	 预约(名医面对面)上级抽成比例
     * 
     * @apiAuthor  pijingwei
     * @date 2015年8月10日
	 */
	@RequestMapping("/getGroupById")
	public JSONMessage getGroupById(String id) {
		return JSONMessage.success(null, groupService.getGroupById(id));
	}
}
