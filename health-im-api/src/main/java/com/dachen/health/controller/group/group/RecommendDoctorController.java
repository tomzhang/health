package com.dachen.health.controller.group.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.recommend.entity.param.DoctorRecommendParam;
import com.dachen.health.recommend.service.IDoctorRecommendService;

@RestController
@RequestMapping("recommend")
public class RecommendDoctorController {
	
	@Autowired
	private IDoctorRecommendService doctorRecommendService;
	
	
	/**
     * @api {post} /recommend/addDoctor 新增推荐名医
     * @apiVersion 1.0.0
     * @apiName addDoctor
     * @apiGroup 集团模块
     * @apiDescription 添加推荐名医
     *
     * @apiParam {String}    	access_token        token
     * @apiParam {String}   	groupId           	集团ID(患者端首页、平台调用时该值为"platform")
     * @apiParam {Integer}   	doctorId       		医生Id
     *
     * @apiSuccess DoctorRecommendVO {@link DoctorRecommendVO}    
     *
     * @apiAuthor  张垠
     * @date 2016年6月06日
	 */
	@RequestMapping("addDoctor")
	public JSONMessage createRecommendDoctor(DoctorRecommendParam param){
		return JSONMessage.success(null, doctorRecommendService.createDoctorRecommend(param));
	}
	
	/**
     * @api {post} /recommend/addDoctors 运营平台推荐多个名医
     * @apiVersion 1.0.0
     * @apiName addDoctorForPlatform
     * @apiGroup 运营平台
     * @apiDescription 平台新增推荐名医
     *
     * @apiParam {String}    	access_token        token
     * @apiParam {String}   	groupId           	集团ID(患者端首页、平台调用时该值为"platform")
     * @apiParam {Integer[]}   	doctorIds       	医生Id列表
     *
     * @apiSuccess {Number} 	resultCode    返回状态码
     *
     * @apiAuthor  liangcs
     * @date 2016年7月29日
	 */
	@RequestMapping("addDoctors")
	public JSONMessage createRecommendDoctorForPlatform(DoctorRecommendParam param){
		doctorRecommendService.createRecommendDoctorForPlatform(param);
		return JSONMessage.success();
	}
	
	
	/**
     * @api {post} /recommend/delDoctor 移除推荐名医
     * @apiVersion 1.0.0
     * @apiName delDoctor
     * @apiGroup 集团模块
     * @apiDescription 移除推荐名医
     *
     * @apiParam {String}    	access_token        token
     * @apiParam {Integer}   	id       			名医推荐ID
     *
     * @apiSuccess {Map} map
     * @apiSuccess {Boolean} map.result   true:成功;false：失败;
     * @apiSuccess {String}  map.msg      提示消息
     *    
     *
     * @apiAuthor  张垠
     * @date 2016年6月06日
	 */
	@RequestMapping("delDoctor")
	public JSONMessage delRecommendDoctor(String id){
		return JSONMessage.success(null, doctorRecommendService.delDoctorRecommend(id));
	}
	
	/**
     * @api {post/get} /recommend/getRecommendDocList 根据集团ID获取对应推荐名医列表
     * @apiVersion 1.0.0
     * @apiName getRecommendDocList
     * @apiGroup 集团模块
     * @apiDescription 根据集团ID获取对应推荐名医列表
     *
     * @apiParam   {String}   access_token                         token
     * @apiParam   {Integer}  groupId                             医生集团ID(患者端首页、平台调用时该值为"platform")
     * @apiParam   {Boolean}  isApp                             true：移动端调用，false :web端调用
     * @apiParam   {Integer}  pageIndex                            当前页码
     * @apiParam   {Integer}  pageSize                             页面大小
     * 
     * @apiSuccess {String} 	id						id
     * @apiSuccess {String} 	name 					医生名称
     * @apiSuccess {String} 	doctorId				医生ID
	 * @apiSuccess {String} 	doctorGroup 			医生所属集团，为空为非集团医生
	 * @apiSuccess {String[]}	groups					运营平台医生归属集团列表 
	 * @apiSuccess {String} 	headPicFileName 		医生头像
	 * @apiSuccess {String} 	title 					医生职称
	 * @apiSuccess {String} 	departments 			所属科室
	 * @apiSuccess {String} 	isRecommend				是否推荐（1为推荐，2为不推荐）
	 * @apiSuccess {String} 	recommendId				推荐的ID
	 * @apiSuccess {String} 	isShow					是否展示H5
	 * @apiSuccess {String} 	documentUrl				H5地址（没有，则不返回该字段）
	 * @apiSuccess {String} 	doctorSkill 			擅长
	 * @apiSuccess {Integer} 	doctorCureNum 			问诊数
	 * @apiSuccess {String} 	is3A 			        "1":三甲；"0"：非三甲
	 * @apiSuccess {String} 	groupCert 			    "1":认证；"0"：非认证
	 * @apiSuccess {String} 	goodRate 			          好评率
	 * @apiSuccess {String} 	price 			                      最低价格
	 * @apiSuccess {String} 	textOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	careOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	phoneOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	clinicOpen 			   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	consultationOpe 	   "1":开启；"0"：关闭
	 * @apiSuccess {String} 	appointmentOpen 	   "1":开启；"0"：关闭
     * @apiAuthor  张垠
     * @date 2016年6月06日
     */
	@RequestMapping("getRecommendDocList")
	public JSONMessage getDoctorRecommendList(DoctorRecommendParam param){
		return JSONMessage.success(null, doctorRecommendService.getRecommendDoctorList(param));
	}
	
	
	/**
     * @api {post} /recommend/setRecommend 推荐/取消推荐名医
     * @apiVersion 1.0.0
     * @apiName setRecommend
     * @apiGroup 集团模块
     * @apiDescription 推荐/取消推荐名医
     *
     * @apiParam {String}    	access_token        token
     * @apiParam {Integer}   	id       			名医推荐ID
     * @apiParam {String}   	isRecommend       	1:推荐，2：取消推荐
     *
     * @apiSuccess {Map} map
     * @apiSuccess {Boolean} map.result   true:成功;false：失败;
     * @apiSuccess {String}  map.msg      提示消息
     *    
     *
     * @apiAuthor  张垠
     * @date 2016年6月06日
	 */
	@RequestMapping("setRecommend")
	public JSONMessage setDoctorRecommend(DoctorRecommendParam param){
		return JSONMessage.success(null, doctorRecommendService.setRecommend(param));
	}
	
	/**
     * @api {post} /recommend/getRecommentDoc 根据ID获取对应个性化页面
     * @apiVersion 1.0.0
     * @apiName getRecommentDoc
     * @apiGroup 集团模块
     * @apiDescription 根据ID获取对应个性化页面
     *
     * @apiParam {String}    	access_token        token
     * @apiParam {String}   	recommendId       	id
     *
     * @apiSuccess DocumentVO {@link DocumentVO}   
     *    
     *
     * @apiAuthor  张垠
     * @date 2016年6月06日
	 */
	@RequestMapping("getRecommentDoc")
	public JSONMessage getRecommendDoct(String recommendId){
		return JSONMessage.success(null, doctorRecommendService.getRecommendDoc(recommendId));
	}
	
	
	/**
     * @api {post} /recommend/upWeight 上移推荐
     * @apiVersion 1.0.0
     * @apiName upWeight
     * @apiGroup 集团模块
     * @apiDescription 上移推荐
     *
     * @apiParam {String}    	access_token        token
     * @apiParam {String}   	id       			id
     *
     * @apiSuccess {Map} map
     * @apiSuccess {Boolean} map.result   true:成功;false：失败;
     * @apiSuccess {String}  map.msg      提示消息
     *    
     *
     * @apiAuthor  张垠
     * @date 2016年6月06日
	 */
	@RequestMapping("upWeight")
	public JSONMessage upWeight(String id){
		return JSONMessage.success(null,doctorRecommendService.upWeight(id));
	}
	
	/**
     * @api {post} /recommend/searchByKeyword 模糊搜索集团或者医生
     * @apiVersion 1.0.0
     * @apiName searchByKeyword
     * @apiGroup 集团模块
     * @apiDescription 模糊搜索集团或者医生
     *
     * @apiParam {String}    	access_token        token
     * @apiParam {String}   	keyword       		模糊搜索关键字
     * @apiParam {Integer}		pageIndex			页码
     * @apiParam {Integer}		pageSize			页容量
     *
     * @apiSuccess {Object[]}		obj				医生结果列表
     * @apiSuccess {String}			obj.name   		医生名称
     * @apiSuccess {String}			obj.doctorId	医生ID
     * @apiSuccess {String}			obj.headPicFileName	头像地址
     * @apiSuccess {String}			obj.departments	医生科室
     * @apiSuccess {String}			obj.groups		所属集团
     * @apiSuccess {String}			obj.title		职称
     * @apiSuccess {String}			obj.select		是否已在列表
     * 
     * @apiAuthor  liangcs
     * @date 2016年7月28日
	 */
	@RequestMapping("searchByKeyword")
	public JSONMessage searchByKeyword(String keyword, Integer pageIndex, Integer pageSize) {
		return JSONMessage.success(doctorRecommendService.getDoctorsByKeyword(keyword, pageIndex, pageSize));
	}
	
}
