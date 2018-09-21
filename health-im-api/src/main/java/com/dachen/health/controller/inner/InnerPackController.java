package com.dachen.health.controller.inner;

import java.util.List;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.JSONMessage;
import com.dachen.health.controller.pack.patient.BaseController;
import com.dachen.health.pack.pack.entity.param.PackDoctorParam;
import com.dachen.health.pack.pack.service.IPackDoctorService;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IPatientService;

@RestController
@RequestMapping("/inner_api/packpatient")
public class InnerPackController extends BaseController<Patient, Integer> {

	@Resource
	private IPatientService service;
	
	@Autowired
	private IPackService packService;

	@Autowired
	private IPackDoctorService iPackDoctorServiceImpl;

	@Override
	public IPatientService getService() {
		return service;
	}


	/**
	 * 
	 * @api {[get,post]} inner_api/packpatient/findById 根据主键查询
	 * @apiVersion 1.0.0
	 * @apiName InnerAPI-findById
	 * @apiGroup 内部api
	 * @apiDescription 根据主键查询
	 * @apiParam {String} access_token token
	 * @apiParam {String} id id
	 * 
	 * @apiSuccess {String} userName 姓名
	 * @apiSuccess {String} sex 性别1男，2女 3 保密
	 * @apiSuccess {Long} birthday 生日（long（13））
	 * @apiSuccess {String} relation 关系
	 * @apiSuccess {String} area 所在地区
	 * @apiSuccess {int} userId 用户id
	 * @apiSuccess {int} id id(必填)
	 * @apiSuccess {String} telephone 手机号
	 * @apiSuccess {String} age 年龄
	 * @apiSuccess {String} topPath 头像
	 * @apiAuthor 傅永德
	 * @author 傅永德
	 * @date 2016年9月9日
	 */
	@Override
	@RequestMapping(value = "findById")
	public JSONMessage findById(Integer id) throws HttpApiException {
		return super.findById(id);
	}

	/**
	 * 
	 * @api {[get,post]} inner_api/packpatient/findByCreateUserId 根据创建人查询患者
	 * @apiVersion 1.0.0
	 * @apiName InnerAPI-findByCreateUserId
	 * @apiGroup 内部api
	 * @apiDescription 根据创建人查询患者
	 * @apiParam {String} userId 用户id
	 * 
	 * @apiSuccess {String} userName 姓名
	 * @apiSuccess {String} sex 性别1男，2女 3 保密
	 * @apiSuccess {Long} birthday 生日（long（13））
	 * @apiSuccess {String} relation 关系
	 * @apiSuccess {String} area 所在地区
	 * @apiSuccess {int} userId 用户id
	 * @apiSuccess {int} id id(必填)
	 * @apiSuccess {String} telephone 手机号
	 * @apiSuccess {String} age 年龄
	 * @apiSuccess {String} topPath 头像
	 * @apiAuthor 傅永德
	 * @author 傅永德
	 * @date 2016年9月9日
	 */
	@RequestMapping("findByCreateUserId")
	public JSONMessage findByCreateUserId(String userId) {
		int createUserId = Integer.valueOf(userId);
		List<Patient> data = service.findByCreateUser(createUserId);
		return JSONMessage.success(null, data);
	}
	
	/**
     * @api {[get,post]} /inner_api/packpatient/saveIntegralPack 保存积分问诊套餐
     * @apiVersion 1.0.0
     * @apiName /inner_api/packpatient/saveIntegralPack
     * @apiGroup 内部api
     * @apiDescription 保存积分问诊套餐（包含开通和关闭，提供给药企圈Web平台-积分管理调用）
     * 
     * @apiParam {Integer}	doctorId		医生id
     * @apiParam {String}	goodsGroupId	品种组id
     * @apiParam {String}	goodsGroupName	品种组名称
     * @apiParam {Integer}	point			积分
     * @apiParam {Integer}	status			操作状态：1-开通；2-关闭
     * 
     * @apiSuccess {String}	resultCode		返回状态码

     * @apiAuthor 钟良
     * @date 2016年12月2日
     * 
     */
    @RequestMapping(value = "/saveIntegralPack")
    public JSONMessage saveIntegralPack(Integer doctorId, String goodsGroupId, String goodsGroupName, Integer point, Integer status) {
    	packService.saveIntegralPack(doctorId, goodsGroupId, goodsGroupName, point, status);
        return JSONMessage.success();
    }

	/**
	 * @api {[get,post]} /inner_api/packpatient/getDoctorByGoodsGroupId 获取开通积分套餐的医生
	 * @apiVersion 1.0.0
	 * @apiName /inner_api/packpatient/getDoctorByGoodsGroupId
	 * @apiGroup 内部api
	 * @apiDescription 获取开通积分套餐的医生（健康社区使用）
	 *
	 *
	 * @apiSuccess {String}	resultCode		返回状态码

	 * @apiAuthor 李明
	 * @date 2016年12月2日
	 *
	 */
	@RequestMapping("getDoctorByGoodsGroupId")
	public JSONObject getDoctorByGoodsGroupId(PackDoctorParam param){
		return JSONMessage.success(iPackDoctorServiceImpl.getDoctorByGoodsGroupId(param.getGoodsGroupIds()));
	}

}
