package com.dachen.health.controller.inner;

import com.dachen.commons.JSONMessage;
import com.dachen.health.base.entity.param.*;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.service.IQrCodeService;
import com.dachen.health.group.impl.HospitalBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;

@RestController
@RequestMapping("inner_api/base")
public class InnerBaseDataController {
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private HospitalBaseService hospitalBaseService;
	@Autowired
	private IQrCodeService qrCodeService;

	/**
	 * @api {get} inner_api/base/getAreaNameByCode 通过地区编码获取地区名称
	 * @apiVersion 1.0.0
	 * @apiName /base/getAreaNameByCode
	 * @apiGroup 内部api
	 * @apiDescription 通过地区编码获取地区名称
	 *
	 * @apiParam {Number} code 地区编码
	 * 
	 * @apiSuccess {String} name 名称
	 *
	 * @apiAuthor 钟良
	 * @date 2016年10月28日
	 */
	@RequestMapping("getAreaNameByCode")
	public JSONMessage getAreaNameByCode(Integer code) {
		return JSONMessage.success(null, baseDataService.getAreaNameByCode(code));
	}
	/**
	 * @api {get} inner_api/base/getIncrementAreaInfoS 	增量读取区域信息  
	 * @apiVersion 1.0.0
	 * @apiName /base/getIncrementAreaInfoS
	 * @apiGroup 内部api
	 * @apiDescription 增量读取区域信息  
	 *
	 * @apiParam {Integer} pageIndex 页码
	 * @apiParam {Integer} pageSize 页面大小（默认200条）
	 * @apiParam {Long} lastUpdatorTime 时间戳（毫秒级，不够位数用0代替）
	 * 
	 * @apiSuccess {String} name 名称
	 *
	 * @apiAuthor 张垠
	 * @date 2016年11月23日
	 */
	@RequestMapping("getIncrementAreaInfoS")
	public JSONMessage getAreaInfoS(AreaParam param){
		return JSONMessage.success(null, baseDataService.getIncAreaInfos(param));
	}
	
	/**
	 * @api {get} inner_api/base/getIncrementDocTitleInfoS 	增量读取职称信息
	 * @apiVersion 1.0.0
	 * @apiName /base/getIncrementDocTitleInfoS
	 * @apiGroup 内部api
	 * @apiDescription 增量读取职称信息
	 *
	 * @apiParam {Integer} pageIndex 页码
	 * @apiParam {Integer} pageSize 页面大小（默认200条）
	 * @apiParam {Long} lastUpdatorTime 时间戳（毫秒级，不够位数用0代替）
	 * 
	 * @apiSuccess {String} name 名称
	 *
	 * @apiAuthor 张垠
	 * @date 2016年11月23日
	 */
	@RequestMapping("getIncrementDocTitleInfoS")
	public JSONMessage getDocTitleInfoS(DoctTitleParam param){
		return JSONMessage.success(null, baseDataService.getIncDocTitleInfos(param));
	}
	
	/**
	 * @api {get} inner_api/base/getIncrementDeptInfoS 	增量读取科室信息
	 * @apiVersion 1.0.0
	 * @apiName /base/getIncrementDeptInfoS
	 * @apiGroup 内部api
	 * @apiDescription 增量读取科室信息
	 *
	 * @apiParam {Integer} pageIndex 页码
	 * @apiParam {Integer} pageSize 页面大小（默认200条）
	 * @apiParam {Long} lastUpdatorTime 时间戳（毫秒级，不够位数用0代替）
	 * 
	 * @apiSuccess {String} name 名称
	 *
	 * @apiAuthor 张垠
	 * @date 2016年11月23日
	 */
	@RequestMapping("getIncrementDeptInfoS")
	public JSONMessage getDocDeptInfoS(HospitalDeptParam param){
		return JSONMessage.success(null, baseDataService.getIncHospitalDeptInfos(param));
	}
	
	/**
	 * @api {get} inner_api/base/getIncrementHospitalInfoS 	增量读取医院信息 
	 * @apiVersion 1.0.0
	 * @apiName /base/getIncrementHospitalInfoS
	 * @apiGroup 内部api
	 * @apiDescription 增量读取医院信息
	 *
	 * @apiParam {Integer} pageIndex 页码
	 * @apiParam {Integer} pageSize 页面大小（默认200条）
	 * @apiParam {Long} lastUpdatorTime 时间戳（毫秒级，不够位数用0代替）
	 * 
	 * @apiSuccess {String} name 名称
	 *
	 * @apiAuthor 张垠
	 * @date 2016年11月23日
	 */
	@RequestMapping("getIncrementHospitalInfoS")
	public JSONMessage getHospitalInfoS(HospitalParam param){
		return JSONMessage.success(null, baseDataService.getIncHospitalInfos(param));
	}

	/**
	 * @api {get} inner_api/base/getIncrementDoctInfoS 	增量读取医生信息（只需要返回认证通过的医生信息) 
	 * @apiVersion 1.0.0
	 * @apiName /base/getIncrementDoctInfoS
	 * @apiGroup 内部api
	 * @apiDescription 增量读取医生信息（只需要返回认证通过的医生信息)
	 *
	 * @apiParam {Integer} pageIndex 页码
	 * @apiParam {Integer} pageSize 页面大小（默认200条）
	 * @apiParam {Long} modifyTime 时间戳（毫秒级，不够位数用0代替）
	 * 
	 * @apiSuccess {String} name 名称
	 *
	 * @apiAuthor 张垠
	 * @date 2016年11月23日
	 */
	@RequestMapping("getIncrementDoctInfoS")
	public JSONMessage getDocInfoS(DoctorParam param){
		return JSONMessage.success(null, baseDataService.getIncDoctorInfos(param));
	}

	@RequestMapping("getAllDoctorInfo")
	public JSONMessage getAllDoctorInfo(DoctorParam param){
		return JSONMessage.success(null, baseDataService.getAllDoctorInfo(param));
	}

	/**
	 * 根据医院名称集合查询医院等级
	 * @param listName
	 * @return
     */
	@ApiIgnore
	@RequestMapping("findLevelByHospitalName")
	public JSONMessage findLevelByHospitalName(String[] listName){
		return JSONMessage.success(null, hospitalBaseService.findLevelByHospitalName(ObjectUtils.isEmpty(listName) ? null : Arrays.asList(listName)));
	}

	/**
	 * @api {get} inner_api/base/findHospitalByName 获取医院根据医院名称
	 * @apiVersion  1.0.0
	 * @apiName findHospitalByName
	 * @apiGroup 内部api
	 * @apiDescription 获取医院根据医院名称
	 * @apiParam {string} name 医院名称 全匹配
	 *
	 * @apiAuthor 李敏
	 * @data 2017年10月24日11:21:09
	 */
	@RequestMapping(value = "/findHospitalByName")
	public JSONMessage findHospitalByName(String name){
		return JSONMessage.success(null,baseDataService.findHospitalByName(name));
	}

	/**
	 * @api {[post]} /qr/generateMeetingActivityQRImage 生成大会活动二维码
	 * @apiVersion 1.0.0
	 * @apiName generateMeetingActivityQRImage
	 * @apiGroup 二维码
	 * @apiDescription 生成大会活动二维码
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {String} id   会议id
	 * @apiParam {String} logoUrl    会议宣传图
	 *
	 * @apiSuccess String data.url 用户对象实体
	 * @apiAuthor wj
	 * @date 2018/03/10
	 */
	@RequestMapping(value = "/qr/generateMeetingActivityQRImage",method = { RequestMethod.POST })
	public JSONMessage generateMeetingActivityQRImage(String id, String logoUrl,String type) {
		return JSONMessage.success(null,qrCodeService.generateMeetingActivityQr(id, logoUrl,type));
	}

}
