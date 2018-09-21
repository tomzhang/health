package com.dachen.health.controller.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.pack.stat.service.IDoctorStatService;

/**
 * 附近医生搜索controller
 * 
 * @author 钟良
 * @date 2016-11-10
 *
 */
@RestController
@RequestMapping("geoNear")
public class GeoNearController {
	
	@Autowired
	private IDoctorStatService doctorStatService;
	@Autowired
	private IBaseDataService baseDataService;
	
	/**
     * @api {get} /geoNear/findDoctorByLocation 通过当前位置搜索附近医生
     * @apiVersion 1.0.0
     * @apiName /geoNear/findDoctorByLocation
     * @apiGroup 附近医生搜索
     * @apiDescription 通过当前位置搜索附近医生
     *
     * @apiParam   {String}     access_token            token
     * @apiParam   {String}     lng                     经度
     * @apiParam   {String}     lat                     纬度
     *
     * @apiSuccess {Integer} 	doctorId 				医生id
     * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	hospitalId 				医院id
	 * @apiSuccess {String} 	hospital 				医院
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			        好评率
	 * @apiSuccess {String} 	price 			                最低价格
	 * @apiSuccess {String} 	textOpen 			   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpen 	   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	appointmentOpen			"1":开启；"0"：关闭
	 * @apiSuccess {String} 	distance 	   			距离
	 * @apiSuccess {String} 	lng 	   				经度
	 * @apiSuccess {String} 	lat 	   				纬度
	 * 
     * @apiAuthor  钟良
     * @date 2016年11月10日
     */
    @RequestMapping("/findDoctorByLocation")
    public JSONMessage findDoctorByLocation(String lng, String lat) {
        return JSONMessage.success(doctorStatService.findDoctorByLocation(lng, lat));
    }
    
    /**
     * @apiIgnore Not used Method
     * @api {get} /geoNear/findDoctorByHospitalId 通过医院id搜索医生
     * @apiVersion 1.0.0
     * @apiName /geoNear/findDoctorByHospitalId
     * @apiGroup 附近医生搜索
     * @apiDescription 通过医院id搜索医生
     *
     * @apiParam   {String}     access_token            token
     * @apiParam   {String}     hospitalId              医院Id
     * @apiParam   {String}     lng                     经度
     * @apiParam   {String}     lat                     纬度
     *
     * @apiSuccess {Integer} 	doctorId 				医生id
     * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	hospitalId 				医院id
	 * @apiSuccess {String} 	hospital 				医院
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			        好评率
	 * @apiSuccess {String} 	price 			                最低价格
	 * @apiSuccess {String} 	textOpen 			   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpen 	   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	appointmentOpen			"1":开启；"0"：关闭
	 * @apiSuccess {String} 	distance 	   			距离
	 * @apiSuccess {String} 	lng 	   				经度
	 * @apiSuccess {String} 	lat 	   				纬度
	 * 
     * @apiAuthor  钟良
     * @date 2016年11月10日
     */
    @RequestMapping("/findDoctorByHospitalId")
    public JSONMessage findDoctorByHospitalId(String hospitalId, String lng, String lat) {
        return JSONMessage.success(doctorStatService.findDoctorByHospitalId(hospitalId, lng, lat));
    }
    
    /**
     * @api {get} /geoNear/findDoctorByCondition 通过条件（城市+科室）搜索医生
     * @apiVersion 1.0.0
     * @apiName /geoNear/findDoctorByCondition
     * @apiGroup 附近医生搜索
     * @apiDescription 通过条件（城市+科室）搜索医生
     *
     * @apiParam   {String}     access_token            token
     * @apiParam   {String}     city                    城市编码
     * @apiParam   {String}     deptId                  科室Id
     * @apiParam   {String}     lng                     经度（可选）
     * @apiParam   {String}     lat                     纬度（可选）
     *
     * @apiSuccess {Integer} 	doctorId 				医生id
     * @apiSuccess {String} 	doctorName 				医生名称
	 * @apiSuccess {String} 	hospitalId 				医院id
	 * @apiSuccess {String} 	hospital 				医院
	 * @apiSuccess {String} 	doctorPath 				医生头像
	 * @apiSuccess {String} 	doctorTitle 			医生职称
	 * @apiSuccess {String} 	doctorDept 				所属科室
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			        好评率
	 * @apiSuccess {String} 	price 			                最低价格
	 * @apiSuccess {String} 	textOpen 			   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpen 	   	"1":开启；"0"：关闭
	 * @apiSuccess {String} 	appointmentOpen			"1":开启；"0"：关闭
	 * @apiSuccess {String} 	distance 	   			距离
	 * @apiSuccess {String} 	lng 	   				经度
	 * @apiSuccess {String} 	lat 	   				纬度
	 * @apiSuccess {String} 	curCondition 	   		是否是当前条件搜索的结果：true-是；false-否
	 * 
     * @apiAuthor  钟良
     * @date 2016年11月10日
     */
    @RequestMapping("/findDoctorByCondition")
    public JSONMessage findDoctorByCondition(String city, String deptId, String lng, String lat) {
        return JSONMessage.success(doctorStatService.findDoctorByCondition(city, deptId, lng, lat));
    }
    
    /**
	 * @api {post} /geoNear/getAllGeoDepts 获取所有科室
	 * @apiVersion 1.0.0
	 * @apiName /geoNear/getAllGeoDepts
	 * @apiGroup 附近医生搜索
	 * @apiDescription 获取所有科室
	 *
	 * @apiParam  {String}    	access_token    token
	 *
	 * @apiSuccess {Number} 	resultCode    	返回状态码
     * @apiSuccess {String} 	id				科室Id
     * @apiSuccess {String} 	name    		科室名称
     * @apiSuccess {String} 	parentId		科室父节点
     * @apiSuccess {String} 	isLeaf      	科室是否为叶子节点
     * 
	 * @apiAuthor  钟良
	 * @date 2016年11月14日
	 */
	@RequestMapping("/getAllGeoDepts")
	public JSONMessage getAllGeoDepts() {
		return JSONMessage.success(baseDataService.getAllGeoDepts());
	}
}
