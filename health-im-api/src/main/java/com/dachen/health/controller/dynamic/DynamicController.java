package com.dachen.health.controller.dynamic;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.pack.dynamic.entity.param.DynamicParam;
import com.dachen.health.pack.dynamic.service.IDynamicService;
import com.dachen.util.ReqUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dynamic")
public class DynamicController {
	
	@Resource
	private IDynamicService dynamicService;

	/**
     * @api  {get} /dynamic/getGroupAndDoctorDynamicListByGroupId 查询该集团和所有集团医生的动态列表(分页)（患者端）
     * @apiVersion 1.0.0
     * @apiName getGroupAndDoctorDynamicListByGroupId
     * @apiGroup dynamic
     * @apiDescription 查询该集团和所有集团医生的动态列表（患者端）
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    groupId               集团id
     * @apiParam  {String}    pageIndex            页码
     * @apiParam  {String}    pageSize             每页大小

     * 
     * @apiSuccess {Object[]}  pageData           数据列表

     * @apiSuccess {String}     pageData.id                动态id
     * @apiSuccess {String}     pageData.groupId           集团id
     * @apiSuccess {String}     pageData.userId            用户id
     * @apiSuccess {String}     pageData.category           动态类别  0 医生 1 集团
     * @apiSuccess {String}     pageData.styleType         动态展示样式 ： 0 文本+图片方式； 1  富文本h5方式
     * @apiSuccess {String}     pageData.name               名称（集团名称 或者医生名称）
     * @apiSuccess {String}     pageData.headImage         头像（集团logo 或者医生头像）

     * @apiSuccess {Long}       pageData.createTime         发布时间
     * @apiSuccess {String}     pageData.title             标题
     * @apiSuccess {String}     pageData.content           内容
     * @apiSuccess {String}     pageData.contentShow       内容 去除html标签 （移动端显示）
     * @apiSuccess {String}     pageData.url               html5路径
     * @apiSuccess {String[]}   pageData.urlList           图片列表
     * @apiSuccess {String}     pageData.contentUrl        题图
     * 
     * @apiAuthor  李伟
     * @date 2016年7月27日
     */
    @RequestMapping("/getGroupAndDoctorDynamicListByGroupId")
	public JSONMessage getGroupAndDoctorDynamicListByGroupId(String groupId,Integer pageIndex,Integer pageSize){
    	
		return JSONMessage.success(dynamicService.getGroupAndDoctorDynamicListByGroupId(groupId, pageIndex, pageSize));
	}
    
    /**
     * @api  {get} /dynamic/getPatientRelatedDynamicList  查询 患者相关的动态列表(分页)(患者端)
     * @apiVersion 1.0.0
     * @apiName getPatientRelatedDynamicList
     * @apiGroup dynamic
     * @apiDescription 查询 患者相关的动态列表(分页)（患者端）
     * @apiParam  {String}    access_token          token
     * @apiParam  {Long}      createTime            最后一条数据的时间
     * @apiParam  {String}    pageSize              每页大小

     * 
     * @apiSuccess {Object[]}  pageData           数据列表

     * @apiSuccess {String}     pageData.id                动态id
     * @apiSuccess {String}     pageData.groupId           集团id
     * @apiSuccess {String}     pageData.userId            用户id
  
     * @apiSuccess {String}     pageData.category           动态类别  0 医生 1 集团
     * @apiSuccess {String}     pageData.styleType         动态展示样式 ： 0 文本+图片方式； 1  富文本h5方式
     * @apiSuccess {String}     pageData.name               名称（集团名称 或者医生名称）
     * @apiSuccess {String}     pageData.headImage         头像（集团logo 或者医生头像）
     * @apiSuccess {Long}       pageData.createTime         发布时间
     * @apiSuccess {String}     pageData.title             标题
     * @apiSuccess {String}     pageData.content           内容
     * @apiSuccess {String}     pageData.contentShow       内容 去除html标签 （移动端显示）

     * @apiSuccess {String}     pageData.url               html5路径
     * @apiSuccess {String[]}   pageData.imageList         图片列表
     * @apiSuccess {String}     pageData.contentUrl        题图
     * 
     * @apiAuthor  李伟
     * @date 2016年7月27日
     */
    @RequestMapping("/getPatientRelatedDynamicList")
	public JSONMessage getPatientRelatedDynamicList(Long createTime, Integer pageSize){
		return JSONMessage.success(dynamicService.getPatientRelatedDynamicList(ReqUtil.instance.getUserId(), createTime, pageSize));
	}
    

    /**
     * @api  {get} /dynamic/addDoctorDynamic  新增医生动态（医生端）
     * @apiVersion 1.0.0
     * @apiName addDoctorDynamic
     * @apiGroup dynamic
     * @apiDescription 新增医生动态（医生端）
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    content             内容
     * @apiParam  {String[]}    imageList         图片列表
     * @apiParam  {Integer[]}    userIds          可以查看该动态的用户id（若全部患者可以查看，则传0）
     * @apiSuccess {String}     resultCode        1 成功
     *
     * @apiAuthor  李伟
     * @date 2016年7月27日
     */
    @RequestMapping("/addDoctorDynamic")
	public JSONMessage addDoctorDynamic(String content,String[] imageList, Integer[] userIds){
    	
    	dynamicService.addDoctorDynamic(ReqUtil.instance.getUserId(),content, imageList, userIds);
		return JSONMessage.success();
	}
    
    /**
     * @api  {get} /dynamic/deleteDoctorDynamic  删除医生或集团动态（医生端或web端）
     * @apiVersion 1.0.0
     * @apiName deleteDoctorDynamic
     * @apiGroup dynamic
     * @apiDescription 删除医生或集团动态（医生端或web端）
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    id             动态id
     * @apiSuccess {String}     resultCode        1 成功
     * 
     * @apiAuthor  李伟
     * @date 2016年7月27日
     */
    @RequestMapping("/deleteDoctorDynamic")
	public JSONMessage deleteDoctorDynamic(String id){
    	
    	dynamicService.deleteDoctorDynamic(id);
		return JSONMessage.success();
	}
    
    /**
     * @api  {get} /dynamic/getDoctorDynamicList  查询 医生的动态列表(分页)（医生端或web端）
     * @apiVersion 1.0.0
     * @apiName getDoctorDynamicList
     * @apiGroup dynamic
     * @apiDescription 查询 医生的动态列表(分页)（医生端或web端）
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    pageIndex            页码
     * @apiParam  {String}    pageSize             每页大小

     * 
     * @apiSuccess {Object[]}  pageData           数据列表

     * @apiSuccess {String}     pageData.id                动态id
     * @apiSuccess {String}     pageData.groupId           集团id
     * @apiSuccess {String}     pageData.userId            用户id
     * @apiSuccess {String}     pageData.category          动态类别  0 医生 1 集团
     * @apiSuccess {String}     pageData.styleType         动态展示样式 ： 0 文本+图片方式； 1  富文本h5方式
     * @apiSuccess {String}     pageData.name              名称（集团名称 或者医生名称）
     * @apiSuccess {String}     pageData.headImage         头像（集团logo 或者医生头像）

     * @apiSuccess {Long}       pageData.createTime         发布时间
     * @apiSuccess {String}     pageData.title             标题
     * @apiSuccess {String}     pageData.content           内容
     * @apiSuccess {String}     pageData.contentShow       内容 去除html标签 （移动端显示）

     * @apiSuccess {String}     pageData.url               html5路径
     * @apiSuccess {String[]}   pageData.imageList         图片列表
     * @apiSuccess {String}     pageData.contentUrl        题图
     * 
     * @apiAuthor  李伟
     * @date 2016年7月27日
     */
    @RequestMapping("/getDoctorDynamicList")
	public JSONMessage getDoctorDynamicList(Integer pageIndex,Integer pageSize){
    	
		return JSONMessage.success(dynamicService.getMyDynamicList(ReqUtil.instance.getUserId(), pageIndex, pageSize));
	}
    
    
    /**
     * @api  {get} /dynamic/getDoctorDynamicListByDoctorId 查询 医生的动态列表(分页)（患者端）
     * @apiVersion 1.0.0
     * @apiName getDoctorDynamicListByDoctorId
     * @apiGroup dynamic
     * @apiDescription 查询 医生的动态列表(分页)（患者端）
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    pageIndex            页码
     * @apiParam  {String}    pageSize             每页大小
     * @apiParam  {String}    doctorId             医生id

     * 
     * @apiSuccess {Object[]}  pageData           数据列表

     * @apiSuccess {String}     pageData.id                动态id
     * @apiSuccess {String}     pageData.groupId           集团id
     * @apiSuccess {String}     pageData.userId            用户id
     * @apiSuccess {String}     pageData.category          动态类别  0 医生 1 集团
     * @apiSuccess {String}     pageData.styleType         动态展示样式 ： 0 文本+图片方式； 1  富文本h5方式
     * @apiSuccess {String}     pageData.name              名称（集团名称 或者医生名称）
     * @apiSuccess {String}     pageData.headImage         头像（集团logo 或者医生头像）

     * @apiSuccess {Long}       pageData.createTime         发布时间
     * @apiSuccess {String}     pageData.title             标题
     * @apiSuccess {String}     pageData.content           内容
     * @apiSuccess {String}     pageData.contentShow       内容 去除html标签 （移动端显示）

     * @apiSuccess {String}     pageData.url               html5路径
     * @apiSuccess {String[]}   pageData.imageList         图片列表
     * @apiSuccess {String}     pageData.contentUrl        题图
     * 
     * @apiAuthor  李伟
     * @date 2016年7月27日
     */
    @RequestMapping("/getDoctorDynamicListByDoctorId")
	public JSONMessage getDoctorDynamicListByDoctorId(Integer doctorId,Integer pageIndex,Integer pageSize){
    	if(null==doctorId||doctorId.intValue()<=0) {
			throw new ServiceException("用户id不能够为空！");
		}
		return JSONMessage.success(dynamicService.getDoctorDynamicList(doctorId, pageIndex, pageSize));
	}
    
    
    /**
     * @api  {get} /dynamic/addDoctorDynamicForWeb  新增医生动态（web端）
     * @apiVersion 1.0.0
     * @apiName addDoctorDynamicForWeb
     * @apiGroup dynamic
     * @apiDescription 新增医生动态（web端）
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    title               标题
     * @apiParam  {String}    content             内容
     * @apiParam  {String}    contentUrl          题图
     * @apiParam  {Integer[]}    userIds          可以查看该动态的用户id（若全部患者可以查看，则传0）
     * @apiSuccess {String}     resultCode        1 成功
     * 
     * @apiAuthor  李伟
     * @date 2016年7月27日
     */
    @RequestMapping("/addDoctorDynamicForWeb")
	public JSONMessage addDoctorDynamicForWeb(DynamicParam param){
    	
    	param.setUserId(ReqUtil.instance.getUserId());
    	dynamicService.addDoctorDynamicForWeb(param);
		return JSONMessage.success();
	}
    
    
    /**
     * @api  {get} /dynamic/addGroupDynamicForWeb  新增集团动态（web端）
     * @apiVersion 1.0.0
     * @apiName addGroupDynamicForWeb
     * @apiGroup dynamic
     * @apiDescription 新增集团动态（web端）
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    groupId            集团id
     * 
     * @apiParam  {String}     title               标题
     * @apiParam  {String}     contentUrl          题图
     * @apiParam  {String}     content             内容
     * @apiParam  {Integer[]}    userIds          可以查看该动态的用户id（若全部患者可以查看，则传0）

     * @apiSuccess {String}     resultCode        1 成功
     * 
     * @apiAuthor  李伟
     * @date 2016年7月27日
     */
    @RequestMapping("/addGroupDynamicForWeb")
	public JSONMessage addGroupDynamicForWeb(DynamicParam param){
    	
    	param.setUserId(ReqUtil.instance.getUserId());
    	dynamicService.addGroupDynamicForWeb(param);
		return JSONMessage.success();
	}
    
    /**
     * @api  {get} /dynamic/getDynamicListByGroupIdForWeb 根据集团id查询该集团动态列表(分页)（web端）
     * @apiVersion 1.0.0
     * @apiName getDynamicListByGroupIdForWeb
     * @apiGroup dynamic
     * @apiDescription 根据集团id查询该集团动态列表（web端）
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    groupId             集团id
     * @apiParam  {String}    pageIndex            页码
     * @apiParam  {String}    pageSize            每页大小

     * 
     * @apiSuccess {Object[]}  pageData           数据列表
     * @apiSuccess {String}     pageData.id                动态id
     * @apiSuccess {String}     pageData.groupId           集团id
     * @apiSuccess {String}     pageData.userId            用户id
     * @apiSuccess {String}     pageData.category           动态类别  0 医生 1 集团
     * @apiSuccess {String}     pageData.styleType         动态展示样式 ： 0 文本+图片方式； 1  富文本h5方式
     * @apiSuccess {String}     pageData.name               名称（集团名称 或者医生名称）
     * @apiSuccess {Long}       pageData.createTime         发布时间
     * @apiSuccess {String}     pageData.title             标题
     * @apiSuccess {String}     pageData.content           内容
     * @apiSuccess {String}     pageData.url               html5路径
     * @apiSuccess {String[]}   pageData.urlList           图片列表
     * @apiSuccess {String}     pageData.contentUrl        题图
     * 
     * @apiAuthor  李伟
     * @date 2016年7月27日
     */
    @RequestMapping("/getDynamicListByGroupIdForWeb")
	public JSONMessage getDynamicListByGroupIdForWeb(String groupId,Integer pageIndex,Integer pageSize){
		return JSONMessage.success(dynamicService.getDynamicListByGroupIdForWeb(groupId, pageIndex, pageSize));
	}
}
