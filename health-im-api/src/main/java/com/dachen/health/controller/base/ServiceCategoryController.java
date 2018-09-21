package com.dachen.health.controller.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.cate.service.IServiceCategoryService;

@RestController
@RequestMapping("/serviceCate")
public class ServiceCategoryController {
	
	@Autowired
	private IServiceCategoryService  serviceCategoryService;
	
	
	/**
	 * 
	 * @api 			{[get,post]} 			    /serviceCate/getByGId			根据集团Id查找
	 * @apiVersion 		1.0.0
	 * @apiName 		getByGId	
	 * @apiGroup 		serviceCate
	 * @apiDescription 	根据集团Id查找
	 * @apiParam  		{String}    					access_token         		 凭证
	 * @apiParam  		{String}    					groupId         		 	集团ID
	 * 
	 * @apiSuccess  	{String}    					groupId         		 	集团ID
	 * @apiSuccess  	{String}    					name         		 		名称
	 * @apiSuccess  	{String}    					id         		 			主键id
	 *
	 * @author 			张垠
	 * @date 2016年4月28日
	 */
	@RequestMapping("/getByGId")
	public JSONMessage getServiceCategoryGId(String groupId){
		return JSONMessage.success(null, serviceCategoryService.getServiceCategoryByGroupId(groupId));
	}
	
	/**
	 * 
	 * @api 			{[get,post]} 			    /serviceCate/getById			根据主键Id查找
	 * @apiVersion 		1.0.0
	 * @apiName 		getById	
	 * @apiGroup 		serviceCate		
	 * @apiDescription 	根据主键Id查找
	 * @apiParam  		{String}    					access_token         		 凭证
	 * @apiParam  		{String}    					Id         		 			主键Id
	 * 
	 * @apiSuccess  	{String}    					groupId         		 	集团ID
	 * @apiSuccess  	{String}    					name         		 		名称
	 * @apiSuccess  	{String}    					id         		 			主键id
	 *
	 * @author 			张垠
	 * @date 2016年4月28日
	 */
	@RequestMapping("/getById")
	public JSONMessage getServiceCategoryId(String Id){
		return JSONMessage.success(null, serviceCategoryService.getServiceCategoryById(Id));
	}

}
