package com.dachen.health.controller.pack.patient;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.pack.patient.model.Disease;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IDiseaseService;
import com.dachen.util.DateUtil;
import com.dachen.util.ReqUtil;

/**
 * 
 * ProjectName： health-im-api<br>
 * ClassName： DiseaseController<br>
 * Description： <br>
 * 
 * @author 李淼淼
 * @createTime 2015年8月12日
 * @version 1.0.0
 */
@RestController
@RequestMapping(value = "/disease")
public class DiseaseController extends BaseController<Disease, Integer> {

	@Resource
	private IDiseaseService service;

	@Autowired
	IOrderService orderService;

	@Resource 
	PatientMapper patientMapper;
	
	public IDiseaseService getService() {
		return service;
	}

	/**
	 * 
	 * @api {[get,post]} /disease/create 病情创建
	 * @apiVersion 1.0.0
	 * @apiName create
	 * @apiGroup 病情
	 * @apiDescription 病情创建
	 * @apiParam {String} access_token token
	 * @apiParam {String} patientId 患者id
	 * @apiParam {String} needHelp 需要帮助 1是 2否
	 * @apiParam {String} diseaseInfo 病情
	 * @apiParam {String} telephone 手机号
	 * 
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("create")
	public JSONMessage create(Disease intance) throws HttpApiException {
		Integer userId = ReqUtil.instance.getUserId();
		intance.setCreateUserId(userId);
		intance.setCreatedTime(new Date().getTime());
		return super.create(intance);

	}

	/**
	 * 
	 * @api {[get,post]} /disease/update 病情修改
	 * @apiVersion 1.0.0
	 * @apiName update
	 * @apiGroup 病情
	 * @apiDescription 病情修改
	 * @apiParam {String} access_token token
	 * @apiParam {String} patientId 患者id
	 * @apiParam {String} needHelp 需要帮助 1是 2否
	 * @apiParam {String} diseaseInfo 病情
	 * @apiParam {String} id idID
	 * @apiParam {String} telephone 手机号
	 * @apiParam {String[]} diseaseImgs 图片数组
	 * @apiParam {String} voice 语音
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("update")
	public JSONMessage update(Disease intance) {
		service.updateDisease(intance, ReqUtil.instance.getUserId());
		return JSONMessage.success(null);

	}

	/**
	 * 
	 * @api {[get,post]} /disease/deleteByPk 根据主键删除
	 * @apiVersion 1.0.0
	 * @apiName deleteByPk
	 * @apiGroup 病情
	 * @apiDescription 根据主键删除病情
	 * @apiParam {String} access_token token
	 * @apiParam {String} id id
	 * 
	 *
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping(value = "deleteByPk")
	public JSONMessage deleteByPk(Integer id) {
		return super.deleteByPk(id);

	}

	/**
	 * 
	 * @api {[get,post]} /disease/findById 根据主键查找
	 * @apiVersion 1.0.0
	 * @apiName findById
	 * @apiGroup 病情
	 * @apiDescription 根据主键查找
	 * @apiParam {String} access_token token
	 * @apiParam {String} id id
	 * 
	 * 
	 * @apiSuccess {String} patientId 患者id
	 * @apiSuccess {String} needHelp 需要帮助 1是 2否
	 * @apiSuccess {String} diseaseInfo 病情
	 * @apiSuccess {String} id id
	 * @apiSuccess {String} createdTime 创建时间
	 * @apiSuccess {String} createUserId 创建人
	 * @apiSuccess {String} telephone 手机号
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping(value = "findById")
	public JSONMessage findById(Integer id) throws HttpApiException {
		return super.findById(id);
	}

	/**
	 * 
	 * @api {[get,post]} /disease/findByCreateUser 根据创建人(登录用户)查找病情
	 * @apiVersion 1.0.0
	 * @apiName findByCreateUser
	 * @apiGroup 病情
	 * @apiDescription 根据创建人查找病情
	 * @apiParam {String} access_token token
	 * 
	 * 
	 * @apiSuccess {String} patientId 患者id
	 * @apiSuccess {String} needHelp 需要帮助
	 * @apiSuccess {String} diseaseInfo 病情
	 * @apiSuccess {String} id id
	 * @apiSuccess {String} createdTime 创建时间
	 * @apiSuccess {String} createUserId 创建人
	 * @apiSuccess {String} telephone 手机号
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月13日
	 */
	@RequestMapping(value = "findByCreateUser")
	public JSONMessage findByCreateUser() {
		int createUserId = ReqUtil.instance.getUserId();
		List<Disease> data = service.findByCreateUser(createUserId);
		return JSONMessage.success(null, data);

	}

	/**
	 * 
	 * @api {[get,post]} /disease/findByPatient 根据患者查找病情
	 * @apiVersion 1.0.0
	 * @apiName findByPatient
	 * @apiGroup 病情
	 * @apiDescription 根据患者查找病情
	 * @apiParam {String} access_token token
	 * @apiParam {String} patientId 患者id
	 * 
	 * 
	 * @apiSuccess {String} patientId 患者id
	 * @apiSuccess {String} needHelp 需要帮助
	 * @apiSuccess {String} diseaseInfo 病情
	 * @apiSuccess {String} id id
	 * @apiSuccess {String} createdTime 创建时间
	 * @apiSuccess {String} createUserId 创建人
	 * @apiSuccess {String} telephone 手机号
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月13日
	 */
	@RequestMapping(value = "findByPatient")
	public JSONMessage findByPatient(Integer patientId) {
		// int createUserId=ReqUtil.instance.getUserId();
		List<Disease> data = service.findByPatient(patientId);
		return JSONMessage.success(null, data);

	}

	/**
	 * 
	 * @api {[get,post]} /disease/findDeaseaseByOrderId 根据订单id查询患者病情
	 * @apiVersion 1.0.0
	 * @apiName findDeaseaseByOrderId
	 * @apiGroup 病情
	 * @apiDescription 根据订单id查询患者病情
	 * @apiParam {String} access_token token
	 * @apiParam {String} orderId     订单id
	 * @apiSuccess {String} patientId 患者id
	 * @apiSuccess {String} needHelp   需要帮助 1是 2否
	 * @apiSuccess {String} diseaseInfo 病情描述
	 * @apiSuccess {String} id          病情id
	 * @apiSuccess {String} createdTime 创建时间
	 * @apiSuccess {String} createUserId 创建人
	 * @apiSuccess {String} telephone 手机号
	 * 
	 * @apiSuccess {String} age 年龄
	 * @apiSuccess {String} area 所在地区
	 * @apiSuccess {String} diseaseInfoNow 现病史
	 * @apiSuccess {String} diseaseInfoOld 既往史
	 * @apiSuccess {String} familyDiseaseInfo 家族史
	 * @apiSuccess {String} heigth 身高
	 * @apiSuccess {String} familyDiseaseInfo 家族史
	 * @apiSuccess {String} isSeeDoctor 是否就诊过
	 * @apiSuccess {String} marriage 婚姻
	 * @apiSuccess {String} menstruationdiseaseInfo 月经史
	 * @apiSuccess {String} patientAge 患者年龄
	 * @apiSuccess {String} profession 职业
	 * @apiSuccess {String} relation 关系
	 * @apiSuccess {String} seeDoctorMsg 诊治情况
	 * @apiSuccess {String} sex 职业
	 * @apiSuccess {String} weigth 体重
     * @apiAuthor liwei 
	 * @author    liwei 
	 * @date      2016年1月22日
	 */
	@RequestMapping(value = "findDeaseaseByOrderId")
	public JSONMessage findDeaseaseByOrderId(String orderId) throws HttpApiException {
		
		JSONMessage json = new JSONMessage();
		if (StringUtils.isEmpty(orderId)) {
			throw new ServiceException("参数订单id不能够为空");
		}
		Order order = orderService.getOne(Integer.parseInt(orderId));
		if (null == order) {
			throw new ServiceException("病情信息不存在");
		}
		Integer disesaseId = order.getDiseaseId();
		
		if(null==disesaseId || disesaseId.intValue()==0)
		{	
			Patient patient = patientMapper.selectByPrimaryKey(order.getPatientId());
			Disease diseaseParam = 	 new Disease();
    		diseaseParam.setAge(DateUtil.calcAge(patient.getBirthday()));
    		diseaseParam.setUserName(patient.getUserName());
    		diseaseParam.setBirthday(patient.getBirthday());
    		diseaseParam.setSex(patient.getSex()==null?null:(int)patient.getSex());
    		diseaseParam.setArea(patient.getArea());
    		diseaseParam.setRelation(patient.getRelation());
    		
    		json =JSONMessage.success(diseaseParam);
		}
		else
		{	
			json=super.findById(disesaseId);
		}
		return json;
	}

	/**
	 * 
	 * @api {[get,post]} /disease/updateDoctorDisease 医生修改患者病情
	 * @apiVersion 1.0.0
	 * @apiName updateDoctorDisease
	 * @apiGroup 病情
	 * @apiDescription 医生修改患者病情
	 * @apiParam {String} access_token token
	 * @apiParam {String} diseaseInfo 病情描述
	 * @apiParam {Integer} id id
	 * @apiParam {Integer} orderId 订单id
	 * @apiParam {String} telephone 手机号
	 * 
	 * @apiParam {Integer} age 年龄
	 * @apiParam {String} area 所在地区
	 * @apiParam {String} diseaseInfoNow 现病史
	 * @apiParam {String} diseaseInfoOld 既往史
	 * @apiParam {String} familyDiseaseInfo 家族史
	 * @apiParam {String} heigth 身高
	 * @apiParam {String} familyDiseaseInfo 家族史
	 * @apiParam {String} isSeeDoctor 是否就诊过
	 * @apiParam {String} marriage 婚姻
	 * @apiParam {String} menstruationdiseaseInfo 月经史
	 * @apiParam {String} profession 职业
	 * @apiParam {String} relation 关系
	 * @apiParam {String} seeDoctorMsg 诊治情况
	 * @apiParam {String} profession 职业
	 * @apiParam {String} weigth     体重
	 * @apiParam {Integer} sex        性别
	 * @apiParam {Long}    visitTime   就诊时间
     * @apiParam {String[]} imagePaths 病情图
	 * @apiAuthor liwei 
	 * @author    liwei 
	 * @date      2016年1月22日
	 */
	@RequestMapping("/updateDoctorDisease")
	public JSONMessage updateDoctorDisease(Disease intance,String orderId) throws HttpApiException {
		
		Integer diseaseId = intance.getId();
		if(null==diseaseId || diseaseId.intValue()==0)
		{	
			if(StringUtils.isEmpty(orderId))
			{	
				throw new ServiceException("订单id不能够为空！");
			}
			JSONMessage json=	create(intance);
			if(null!=json.getData())
			{	
				Disease result = (Disease)	json.getData();
				diseaseId = result.getId();
				Order order =orderService.getOne(Integer.parseInt(orderId));
				order.setDiseaseId(diseaseId);
				orderService.updateOrder(order);
			}
		}
		else
		{	
			service.updateDiseaseMsg(intance);
		}
		
		return JSONMessage.success();
	}

}
