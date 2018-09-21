package com.dachen.health.controller.group.group;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.group.group.service.IGroupServiceItemService;

@RestController
@RequestMapping("/group/serviceItem")
public class GroupServiceItemController extends AbstractController {
	
	@Resource
	private IGroupServiceItemService gserviceItemService;

	/**
     * @api {post} /group/serviceItem/getHospitals 获取医院
     * @apiVersion 1.0.0
     * @apiName submitCert
     * @apiGroup 集团服务项
     * @apiDescription 获取医院
     *
     * @apiParam  {String}    	access_token    token
     * @apiParam  {String}   	groupId         集团Id
     * 
     * @apiSuccess  {List}   	list      集合
     * @apiSuccess  {String}   	id        医院Id
     * @apiSuccess  {String}   	name      医院名称
     *
     * @apiAuthor  谢平
     * @date 2015年11月12日
	 */
	@RequestMapping("/getHospitals")
	public JSONMessage getHospitals(String groupId) {
		return JSONMessage.success(gserviceItemService.getHospitals(groupId));
	}
	
	/**
     * @api {post} /group/serviceItem/getGroupServiceItem 获取集团服务项
     * @apiVersion 1.0.0
     * @apiName getGroupServiceItem
     * @apiGroup 集团服务项
     * @apiDescription 获取集团服务项
     *
     * @apiParam  {String}    	access_token    		  token
     * @apiParam  {String}   	groupId         		     集团Id
     * @apiParam  {String}   	hospitalId         		     医院Id
     * 
     * @apiSuccess	{String}	serviceItemId			     服务项Id
     * @apiSuccess	{String}	serviceItemName			     服务项名称
     * @apiSuccess	{List}		children			               叶子节点服务项集合
     * @apiSuccess	{String}	children.serviceItemId	     叶子节点服务项Id
     * @apiSuccess  {String}   	children.serviceItemName  叶子节点服务项名称
     * @apiSuccess  {String}   	children.price	     	     下级服务项
     *
     * @apiAuthor  谢平
     * @date 2015年11月12日
	 */
	@RequestMapping("/getGroupServiceItem")
	public JSONMessage getGroupServiceItem(String groupId, String hospitalId) {
		return JSONMessage.success(gserviceItemService.getGroupServiceItem(groupId, hospitalId));
	}
	
}
