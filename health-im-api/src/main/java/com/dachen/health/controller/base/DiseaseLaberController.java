package com.dachen.health.controller.base;

import com.dachen.commons.JSONMessage;
import com.dachen.health.recommand.service.IDiseaseLaberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Created by liangcs on 2016/12/2.
 */
@RestController
@RequestMapping("/diseaseLaber")
public class DiseaseLaberController {

    @Autowired
    private IDiseaseLaberService laberService;

    /**
     *
     * @api 	{[get,post]} 	/diseaseLaber/save			运营平台添加疾病标签
     * @apiVersion 		1.0.0
     * @apiName 		save
     * @apiGroup 		疾病标签
     * @apiDescription 	运营平台添加疾病标签
     * @apiParam  		{String}    					access_token         		 凭证
     * @apiParam  		{String}    					id         		 		疾病id
     *
     * @apiSuccess {Number} 	resultCode    返回状态码
     *
     * @apiAuthor  liangcs
     * @date 2016年12月5日
     */
    @RequestMapping("save")
    public JSONMessage addLable(String[] id) {
    	
    	List<String> ids = Arrays.asList(id);
    	
        laberService.addLaber(ids);
        return JSONMessage.success();
    }

    /**
    *
    * @api 	{[get,post]} 	/diseaseLaber/labers			获取所有疾病标签
    * @apiVersion 		1.0.0
    * @apiName 		labers
    * @apiGroup 		疾病标签
    * @apiDescription 获取所有疾病标签
    * @apiParam  		{String}    					access_token         		 凭证
    *
    * @apiSuccess {String} 	id    		疾病id
    * @apiSuccess {String}     name		疾病名称
    * @apiSuccess {Boolean}    leaf		叶子节点标识
    * @apiSuccess {String}     parent		父节点id
    * @apiSuccess {Integer}     weight		权重
	* @apiSuccess {List}     	children	孩子节点
	* @apiSuccess {Integer}	followed    当前用户是否关注该标签 1:已关注
	* 
    * @apiSuccess {Number} 	resultCode    返回状态码
    *
    * @apiAuthor  liangcs
    * @date 2016年12月5日
    */
    @RequestMapping("labers")
    public JSONMessage getLabers() {
        return JSONMessage.success(laberService.getLaberTree());
    }

    /**
     *
     * @api 	{[get,post]} 	/diseaseLaber/getWebLabers			获取所有疾病标签
     * @apiVersion 		1.0.0
     * @apiName 		labers
     * @apiGroup 		疾病标签
     * @apiDescription 获取所有疾病标签
     * @apiParam  		{String}    					access_token         		 token
     *
     * @apiSuccess {String} 	diseaseId    		疾病id
     * @apiSuccess {String}     diseaseName		疾病名称
     * @apiSuccess {Long}      count        关注疾病标签的人数
     *
     * @apiSuccess {Number} 	resultCode    返回状态码
     *
     * @apiAuthor  liangcs
     * @date 2016年12月5日
     */
    @RequestMapping("getWebLabers")
    public JSONMessage getWebLabers(){

        return JSONMessage.success(laberService.getWebLaber());
    }
    
    /**
    * @api 	{[get,post]} 	/diseaseLaber/user/check			判断用户是否推荐关注
    * @apiVersion 		1.0.0
    * @apiName 		check
    * @apiGroup 		疾病标签
    * @apiDescription 判断用户是否推荐关注
    * @apiParam  		{String}    					access_token         		 凭证
    *
	* @apiSuccess {String}     check        0:未关注过 1:已关注
    * @apiSuccess {Number} 	resultCode    返回状态码
    *
    * @apiAuthor  liangcs
    * @date 2016年12月5日
    */
    @RequestMapping("user/check")
    public JSONMessage checkLaber() {
    	return JSONMessage.success(laberService.checkFollow());
    }
    
    /**
    * @api 	{[get,post]} 	/diseaseLaber/user/add			用户添加疾病标签
    * @apiVersion 		1.0.0
    * @apiName 		user/add
    * @apiGroup 		疾病标签
    * @apiDescription 用户添加疾病标签
    * @apiParam  		{String}    					access_token         		 凭证
    * @apiParam		{String[]}						id					疾病id
    *
    * @apiSuccess {Number} 	resultCode    返回状态码
    *
    * @apiAuthor  liangcs
    * @date 2016年12月5日
    */
    @RequestMapping("user/add")
    public JSONMessage addUserLabers(String[] id) {
    	List<String> list = Arrays.asList(id);
    	laberService.addUserLaber(list);
    	return JSONMessage.success();
    }
    
    /**
    * @api 	{[get,post]} 	/diseaseLaber/user/labers			获取用户绑定的标签
    * @apiVersion 		1.0.0
    * @apiName 		user/labers
    * @apiGroup 		疾病标签
    * @apiDescription 获取用户绑定的标签
    * @apiParam  		{String}    					access_token         		 凭证
    *
    * @apiSuccess {String} 	id    					疾病id
    * @apiSuccess {String} 	name    				疾病名称
    * @apiSuccess {Number} 	resultCode    返回状态码
    *
    * @apiAuthor  liangcs
    * @date 2016年12月5日
    */
    @RequestMapping("user/labers")
    public JSONMessage getMyLabers() {
    	return JSONMessage.success(laberService.getMyLabers());
    }
    
    /**
    *
    * @api 	{[get,post]} 	/diseaseLaber/user/del			用户删除自身疾病标签
    * @apiVersion 		1.0.0
    * @apiName 		user/del
    * @apiGroup 		疾病标签
    * @apiDescription 用户删除自身疾病标签
    * @apiParam  		{String}    					access_token         		 凭证
    * @apiParam  		{String[]}    					id         		 	 疾病id
    *
    * @apiSuccess {Number} 	resultCode    返回状态码
    *
    * @apiAuthor  liangcs
    * @date 2016年12月5日
    */
    @RequestMapping("user/del")
    public JSONMessage delUserLabers(String[] id) {
    	List<String> ids = Arrays.asList(id);
    	laberService.delUserLaber(ids);
    	return JSONMessage.success();
    }

}
