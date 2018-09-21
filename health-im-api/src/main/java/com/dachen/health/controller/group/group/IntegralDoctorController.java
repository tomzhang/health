package com.dachen.health.controller.group.group;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.recommend.service.IIntegralDoctorService;

/**
 * 积分问诊查询医生Controller
 * 
 * @author 钟良
 * @date 2016-12-06
 *
 */
@RestController
@RequestMapping("/integralDoctor")
public class IntegralDoctorController {
	
	@Autowired
	private IIntegralDoctorService integralDoctorService;
	
	/**
     * @api {post/get} /integralDoctor/getIntegralDoctorList 获取开通了积分问诊服务套餐的医生（分页）
     * @apiVersion 1.0.0
     * @apiName /integralDoctor/getIntegralDoctorList
     * @apiGroup 集团模块
     * @apiDescription 获取开通了积分问诊服务套餐的医生（分页）
     *
     * @apiParam   {String}   access_token	token
     * @apiParam   {Integer}  pageIndex		当前页码
     * @apiParam   {Integer}  pageSize		页面大小
     * 
     * @apiSuccess {String} 	name 					医生名称
     * @apiSuccess {String} 	doctorId				医生ID
	 * @apiSuccess {String} 	headPicFileName 		医生头像
	 * @apiSuccess {String} 	departments 			所属科室
	 * 
     * @apiAuthor  钟良
     * @date 2016年12月06日
     */
	@RequestMapping("getIntegralDoctorList")
	public JSONMessage getIntegralDoctorList(Integer pageIndex, Integer pageSize){
		return JSONMessage.success(null, integralDoctorService.getIntegralDoctorList(pageIndex, pageSize));
	}
	
	/**
     * @api {post/get} /integralDoctor/getIntegralPackByDoctorId 获取某医生开通的积分问诊服务套餐
     * @apiVersion 1.0.0
     * @apiName /integralDoctor/getIntegralPackByDoctorId
     * @apiGroup 集团模块
     * @apiDescription 通过医生Id获取该医生开通的积分问诊套餐服务列表，并且根据患者是否有足够对应的积分做过滤
     *
     * @apiParam   {String}   access_token	token
     * @apiParam   {Integer}  doctorId		医生Id
     * 
     * @apiSuccess {Integer} 	id 					积分套餐id
     * @apiSuccess {String} 	goodsGroupId 		品种组id
     * @apiSuccess {String} 	name 				积分套餐名称
     * @apiSuccess {Integer} 	doctorId			医生ID
	 * @apiSuccess {Integer} 	point 				套餐积分
	 * @apiSuccess {Integer} 	balance 			患者的积分与套餐积分的差额，有可能是负数。
	 * @apiSuccess {Integer} 	patientPoint 		患者可用积分
	 * 
     * @apiAuthor  钟良
     * @date 2016年12月06日
     */
	@RequestMapping("getIntegralPackByDoctorId")
	public JSONMessage getIntegralPackByDoctorId(Integer doctorId) throws HttpApiException {
		return JSONMessage.success(null, integralDoctorService.getIntegralPackByDoctorId(doctorId));
	}
}
