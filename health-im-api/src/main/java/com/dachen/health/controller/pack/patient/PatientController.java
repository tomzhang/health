package com.dachen.health.controller.pack.patient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.invite.service.IInvitePatientService;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.util.CheckUtils;
import com.dachen.util.JSONUtil;
import com.dachen.util.ReqUtil;

/**
 * 
 * ProjectName： health-im-api<br>
 * ClassName： PatientController<br>
 * Description： <br>
 * 
 * @author 李淼淼
 * @createTime 2015年8月12日
 * @version 1.0.0
 */

@RestController("packpatient")
@RequestMapping(value = "/packpatient")
public class PatientController extends BaseController<Patient, Integer> { 

	@Resource
	private IPatientService service;
	
	@Resource
	private IInvitePatientService invitePatientService;
	

	public IPatientService getService() {
		return service;
	}

	/**
	 * 
	 * @api {[get,post]} /packpatient/create 患者创建
	 * @apiVersion 1.0.0
	 * @apiName create
	 * @apiGroup 患者
	 * @apiDescription 患者创建
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			userName 				姓名
	 * @apiParam {String} 			sex 					性别1男，2女 3 保密
	 * @apiParam {Long} 			birthday 				生日（long（13））
	 * @apiParam {String} 			relation 				关系
	 * @apiParam {String} 			area 					所在地区
	 * @apiParam {String} 			telephone 				手机号
	 * @apiParam {String} 			topPath 				患者头像
	 * @apiParam {String} 			idcard				   	证件号码（必填）
	 * @apiParam {Integer} 			idtype				          证件类型（必填1身份证 2护照  3军官  4 台胞  5香港身份证）
	 * @apiParam {String} 			height				          身高(cm)
	 * @apiParam {String} 			weight				          体重(kg)
	 * @apiParam {String} 			marriage				婚姻
	 * @apiParam {String} 			professional			职业
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("create")
	public JSONMessage create(Patient intance) throws HttpApiException {
		
		String  telephone=intance.getTelephone();
		
		if(!CheckUtils.checkMobile(telephone))
		{	
			throw new ServiceException("手机号码格式不正确");
		}
		int loginUserId = ReqUtil.instance.getUserId();
		boolean bdjl=ReqUtil.instance.isBDJL();
		if(bdjl){
			//校验身份证号码是否重复
			Map<String,Object> map=service.checkIdCard(loginUserId, intance.getIdcard(), intance.getId());
			if(map!=null&&map.containsKey("is_repeat")){
				boolean falg=(boolean) map.get("is_repeat");
				if(falg){
					throw new ServiceException("该证件号已经注册过，请您使用注册过的帐号重新登录");
				}
			}
		}
		
		
		intance.setUserId(loginUserId);
		return super.create(intance);
	}
	
	@RequestMapping("savePatientByUser")
	public JSONMessage savePatientByUser(String userJson) {
		service.save(JSONUtil.parseObject(User.class, userJson));
		return JSONMessage.success();
	}
	
	@RequestMapping("sendNoticeByUser")
	public JSONMessage sendNoticeByUser(String userJson) throws HttpApiException {
		invitePatientService.sendNotice(JSONUtil.parseObject(User.class, userJson));
		return JSONMessage.success();
	}

	/**
	 * 
	 * @api {[get,post]} /packpatient/update 患者信息更新
	 * @apiVersion 1.0.0
	 * @apiName update
	 * @apiGroup 患者
	 * @apiDescription 患者信息更新
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			userName 				姓名
	 * @apiParam {String} 			sex 					性别（1男、2女）
	 * @apiParam {Long} 			birthday 				生日（long（13））
	 * @apiParam {String} 			relation 				关系
	 * @apiParam {String} 			area 					所在地区
	 * @apiParam {int} 				userId 					用户id
	 * @apiParam {int} 				id	 					id（必填)
	 * @apiParam {String} 			telephone 				手机号
	 * @apiParam {String} 			topPath				          患者头像
	 * @apiParam {String} 			idcard				   	证件号码（必填）
	 * @apiParam {Integer} 			idtype				          证件类型（必填1身份证 2护照  3军官  4 台胞  5香港身份证）
	 * @apiParam {String} 			height				          身高(cm)
	 * @apiParam {String} 			weight				          体重(kg)
	 * @apiParam {String} 			marriage				婚姻
	 * @apiParam {String} 			professional			职业
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("update")
	public JSONMessage update(Patient intance) throws HttpApiException {
		// 手机号码不为空的时候需要做校验
		String telephone = intance.getTelephone();
		if(!CheckUtils.checkMobile(telephone))
		{	
			throw new ServiceException("手机号码格式不正确");
		}
		int loginUserId = ReqUtil.instance.getUserId();
		boolean bdjl=ReqUtil.instance.isBDJL();
		if(bdjl){
			//校验身份证号码是否重复
			Map<String,Object> map=service.checkIdCard(loginUserId, intance.getIdcard(), intance.getId());
			if(map!=null&&map.containsKey("is_repeat")){
				boolean falg=(boolean) map.get("is_repeat");
				if(falg){
					throw new ServiceException("该证件号已经注册过，请您使用注册过的帐号重新登录");
				}
			}
		}
		
		
		return super.update(intance);
	}
	
	

	/**
	 * 
	 * @api {[get,post]} /packpatient/deleteByPk 根据主键删除
	 * @apiVersion 1.0.0
	 * @apiName deleteByPk
	 * @apiGroup 患者
	 * @apiDescription 根据主键删除患者信息
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			id 						id
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping(value="deleteByPk")
	public JSONMessage deleteByPk(Integer id) {
		return super.deleteByPk(id);
	}
	
	/**
	 * 
	 * @api {[get,post]} /packpatient/existsBizData 是否已有业务数据
	 * @apiVersion 1.0.0
	 * @apiName existsBizData
	 * @apiGroup 患者
	 * @apiDescription 是否已有业务数据（删除患者记录时调用）
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			id 						id
	 * @apiAuthor 谢平
	 * @date 2016年1月12日
	 */
	@RequestMapping(value="existsBizData")
	public JSONMessage existsBizData(Integer id) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("existsBizData", service.existsBizData(id));
		
		return JSONMessage.success(map);
	}
	/**
	 * 
	 * @api {[get,post]} /packpatient/findById 根据主键查询
	 * @apiVersion 1.0.0
	 * @apiName findById
	 * @apiGroup 患者
	 * @apiDescription 根据主键查询
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			id 						id
	 * 
	 * 
	 * @apiSuccess {String} 			userName 				姓名
	 * @apiSuccess {String} 			sex 					性别1男，2女 3 保密
	 * @apiSuccess {Long} 				birthday 				生日（long（13））
	 * @apiSuccess {String} 			relation 				关系
	 * @apiSuccess {String} 			area 					所在地区
	 * @apiSuccess {int} 				userId 					用户id
	 * @apiSuccess {int} 				id	 					id(必填)
	 * @apiSuccess {String} 			telephone 				手机号
	 * @apiSuccess {String} 			age 					年龄
	 * @apiSuccess {String} 			topPath 				头像
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping(value="findById")
	public JSONMessage findById(Integer id) throws HttpApiException {

		return super.findById(id);
	}
	


	/**
	 * 
	 * @api {[get,post]} /packpatient/findByCreateUser 根据创建人查询患者
	 * @apiVersion 1.0.0
	 * @apiName findByCreateUser
	 * @apiGroup 患者
	 * @apiDescription 根据创建人查询患者
	 * @apiParam {String} 			access_token 			token
	 * 
	 * 
	 * 
	 * @apiSuccess {String} 			userName 				姓名
	 * @apiSuccess {String} 			sex 					性别1男，2女 3 保密
	 * @apiSuccess {Long} 				birthday 				生日（long（13））
	 * @apiSuccess {String} 			relation 				关系
	 * @apiSuccess {String} 			area 					所在地区
	 * @apiSuccess {int} 				userId 					用户id
	 * @apiSuccess {int} 				id	 					id(必填)
	 * @apiSuccess {String} 			telephone 				手机号
	 * @apiSuccess {String} 			age 					年龄
	 * @apiSuccess {String} 			topPath 			           头像
	 * @apiSuccess {String} 			idcard 			                     证件号码
	 * @apiSuccess {Integer} 			idtype 			    	证件类型
	 * @apiSuccess {String} 			weight 			  		体重
	 * @apiSuccess {String} 			height 			                    身高
	 * @apiSuccess {String} 			marriage 			          婚姻
	 * @apiSuccess {String} 			professional			职业
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("findByCreateUser")
	public JSONMessage findByCreateUser(){
		
		int createUserId=ReqUtil.instance.getUserId();
		List<Patient> data=service.findByCreateUser(createUserId);
		return JSONMessage.success(null, data);
	}
	
	
	/**
	 * 
	 * @api {[get,post]} /packpatient/findByCreateUserId 根据创建人查询患者
	 * @apiVersion 1.0.0
	 * @apiName findByCreateUserId
	 * @apiGroup 患者
	 * @apiDescription 根据创建人查询患者
	 * @apiParam {String} 			userId 			用户id
	 * 
	 * 
	 * 
	 * @apiSuccess {String} 			userName 				姓名
	 * @apiSuccess {String} 			sex 					性别1男，2女 3 保密
	 * @apiSuccess {Long} 				birthday 				生日（long（13））
	 * @apiSuccess {String} 			relation 				关系
	 * @apiSuccess {String} 			area 					所在地区
	 * @apiSuccess {int} 				userId 					用户id
	 * @apiSuccess {int} 				id	 					id(必填)
	 * @apiSuccess {String} 			telephone 				手机号
	 * @apiSuccess {String} 			age 					年龄
	 * @apiSuccess {String} 			topPath 			            头像
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("findByCreateUserId")
	public JSONMessage findByCreateUserId(String userId){
		int createUserId=Integer.valueOf(userId);
		List<Patient> data=service.findByCreateUser(createUserId);
		return JSONMessage.success(null, data);
	}
	
	/**
	 * 
	 * @api {[get,post]} /packpatient/checkIdCard 检查患者身份证号码是否重复
	 * @apiVersion 1.0.0
	 * @apiName checkIdCard
	 * @apiGroup 患者
	 * @apiDescription 检查患者身份证号码是否重复
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			idcard					证件号码(必填)
	 * @apiParam {Integer} 			id						患者id
	 * @apiSuccess {String} 		idcard					证件号码
	 * @apiSuccess {String} 		is_repeat				是否重复（true 重复  false 未重复）
	 * @apiAuthor 李明
	 * @author 李明
	 * @date 2016年6月6日
	 */
	@RequestMapping(value="checkIdCard")
	public JSONMessage checkIdCard(String idcard,Integer id) {
		int createUserId=ReqUtil.instance.getUserId();
		return JSONMessage.success(null,service.checkIdCard(createUserId,idcard,id));
		
	}

}
