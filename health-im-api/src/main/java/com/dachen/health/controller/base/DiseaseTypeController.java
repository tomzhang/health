package com.dachen.health.controller.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;

/**
 * 病种相关
 * @author vincent
 *
 */
@RestController
@RequestMapping("/diseaseType")
public class DiseaseTypeController {
	
	@Resource
	private DiseaseTypeRepository diseaseTypeRepository;
	
	@Resource
	private IBaseDataService baseDataService;

	/**
	 * 
	 * @api 			{[get,post]} 					/diseaseType/findByDept			根据科室查找对应科室下的病种
	 * @apiVersion 		1.0.0
	 * @apiName 		findByDept	
	 * @apiGroup 		专长	
	 * @apiDescription 	根据科室查找对应科室下的病种
	 * @apiParam  		{String}    					access_token         		 凭证
	 * @apiParam  		{String}    					deptId         		 		科室id
	 * @param deptId
	 * @return
	 * @apiSuccess  	{String}    					dept.parentId         		 	父科室id
	 * @apiSuccess  	{String}    					dept.name         		 		科室名称
	 * @apiSuccess  	{String}    					dept.id         		 		科室id
	 *
	 * @apiSuccess  	{String}    					diseaseType.department         	科室
	 * @apiSuccess  	{String}    					diseaseType.name         		病种名
	 * @apiSuccess  	{String}    					diseaseType.id         		 	病种id
	 * @apiAuthor 		李淼淼
	 * @author 			李淼淼
	 * @date 2015年9月21日
	 */
	
	@RequestMapping("findByDept")
	public JSONMessage findByDept(String deptId){//增加下级科室
		if(StringUtils.isBlank(deptId)){
			deptId="A";
		}
		List<DeptVO> depts=baseDataService.findByParent(deptId);
		Map<String,Object> data=new HashMap<String,Object>();
		data.put("dept", depts);
		List<DiseaseType> diseaseTypes=diseaseTypeRepository.findByDept(deptId);
		data.put("diseaseType", diseaseTypes);
		return JSONMessage.success(null,data);
	}
	
	/**
	 * 
	 * @api 			{[get,post]} 					/diseaseType/findByName			根据病种名查找病种（支持模糊查询）
	 * @apiVersion 		1.0.0
	 * @apiName 		findByName	
	 * @apiGroup 		专长	
	 * @apiDescription 	根据病种名查找病种（支持模糊查询）
	 * @apiParam  		{String}    					access_token         		 凭证
	 * @apiParam  		{String}    					name         		 		病种名
	 * @param deptId
	 * @return
	 * @apiSuccess 		{String} 							id 								病种id 
	 * @apiSuccess 		{String} 							name 						病种名称
	 * @apiAuthor 		李淼淼
	 * @author 			李淼淼
	 * @date 2015年9月21日
	 */
	
	@RequestMapping("findByName")
	public JSONMessage findByName(String name){
		List<DiseaseType> diseaseTypes=diseaseTypeRepository.findByName(name);
		return JSONMessage.success(null,diseaseTypes);
	}
	
	
	/**
	 * @api {post} /diseaseType/getCommonDiseasesForWeb 运营平台获取常见疾病列表
	 * @apiVersion 1.0.0
     * @apiName getCommonDiseasesForWeb
     * @apiGroup 病种
     * @apiDescription 获取常见疾病列表
     * 
     * @apiParam	{String}    	access_token    token
     * @apiParam	{Integer}		pageIndex		页码
     * @apiParam	{Integer}		pageSize		页容量
     * 
     * 
     * @apiSuccess	{Object[]}		disease			疾病列表
     * @apiSuccess	{String}		disease.diseaseId		疾病id
     * @apiSuccess	{String}		disease.name	疾病名称
     * @apiSuccess	{Long}			disease.weight			权重
     * 
     * @apiSuccess {Number} 	resultCode    返回状态码
     * 	 
     * @apiAuthor  liangcs
     * @date 2016年7月25日
     */
	@RequestMapping("getCommonDiseasesForWeb")
	public JSONMessage getCommonDiseases(Integer pageIndex, Integer pageSize) {
		return JSONMessage.success(diseaseTypeRepository.getDiseaseList(pageIndex, pageSize));
	}
	
	/**
	 * @api {post} /diseaseType/getCommonDiseases 获取常见疾病列表
	 * @apiVersion 1.0.0
     * @apiName getCommonDiseases
     * @apiGroup 病种
     * @apiDescription 获取常见疾病列表
     * 
     * @apiParam	{String}    	access_token    token
     * @apiParam	{Integer}		pageIndex		页码
     * @apiParam	{Integer}		pageSize		页容量
     * 
     * 
     * @apiSuccess	{Object[]}		disease					疾病列表
     * @apiSuccess	{String}		disease.diseaseId		疾病id
     * @apiSuccess	{String}		disease.name			疾病名称
     * @apiSuccess	{Long}			disease.weight			权重
     * 
     * @apiSuccess {Number} 	resultCode    返回状态码
     * 	 
     * @apiAuthor  liangcs
     * @date 2016年7月25日
     */
	@RequestMapping("getCommonDiseases")
	public JSONMessage getCommonDiseasesAfterSort(Integer pageIndex, Integer pageSize) {
		return JSONMessage.success(diseaseTypeRepository.getDiseaseListAfterSort(pageIndex, pageSize));
	}
	
	/**
	 * @api {post} /diseaseType/addCommonDisease 新增常见疾病
	 * @apiVersion 1.0.0
     * @apiName addCommonDisease
     * @apiGroup 病种
     * @apiDescription 新增常见疾病
     * 
     * @apiParam	{String}    	access_token    token
     * @apiParam	{String}    	diseaseId		疾病id
     * @apiParam	{String}    	name			疾病学术名或者曾用名
     * 
     * @apiSuccess {Number} 	resultCode    返回状态码
     * 	 
     * @apiAuthor  liangcs
     * @date 2016年7月25日
     */
	@RequestMapping("addCommonDisease")
	public JSONMessage addCommonDisease(String diseaseId, String name) {
		diseaseTypeRepository.addCommonDisease(diseaseId, name);
		return JSONMessage.success();
	}
	
	/**
	 * @api {post} /diseaseType/removeCommonDisease 移除常见疾病
	 * @apiVersion 1.0.0
     * @apiName removeCommonDisease
     * @apiGroup 病种
     * @apiDescription 移除常见疾病
     * 
     * @apiParam	{String}    	access_token    token
     * @apiParam	{String}    	diseaseId		疾病id
     * 
     * @apiSuccess {Number} 	resultCode    返回状态码
     * 	 
     * @apiAuthor  liangcs
     * @date 2016年7月25日
     */
	@RequestMapping("removeCommonDisease")
	public JSONMessage removeCommonDisease(String diseaseId) {
		diseaseTypeRepository.removeCommonDisease(diseaseId);
		return JSONMessage.success();
	}
	
	/**
	 * @api {post} /diseaseType/riseCommonDisease 上移常见疾病
	 * @apiVersion 1.0.0
     * @apiName riseCommonDisease
     * @apiGroup 病种
     * @apiDescription 上移常见疾病
     * 
     * @apiParam	{String}    	access_token    token
     * @apiParam	{String}    	diseaseId				疾病id
     * 
     * @apiSuccess {Number} 	resultCode    返回状态码
     * 	 
     * @apiAuthor  liangcs
     * @date 2016年7月25日
     */
	@RequestMapping("riseCommonDisease")
	public JSONMessage riseCommonDisease(String diseaseId) {
		diseaseTypeRepository.upWeight(diseaseId);
		return JSONMessage.success();
	}
	
	/**
	 * @api {post} /diseaseType/searchDiseaseTreeByKeyword 根据名称查询疾病树
	 * @apiVersion 1.0.0
     * @apiName searchDiseaseTreeByKeyword
     * @apiGroup 病种
     * @apiDescription 根据名称查询疾病树
     * 
     * @apiParam	{String}    	access_token    token
     * @apiParam	{String}    	keyword			搜索关键字
     * 
     * @apiSuccess {String} 	id    		疾病id
     * @apiSuccess {String}     name		疾病名称
     * @apiSuccess {Boolean}    leaf		叶子节点标识
	 * @apiSuccess {String}     parent		父节点id
	 * @apiSuccess {Integer}     weight		权重
	 * @apiSuccess {List}     	children	孩子节点
     * 
     * @apiSuccess {Number} 	resultCode    返回状态码
     * 	 
     * @apiAuthor  liangcs
     * @date 2016年7月25日
     */
	@RequestMapping("searchDiseaseTreeByKeyword")
	public JSONMessage searchDiseaseTreeByKeyword(String keyword) {
		return JSONMessage.success(diseaseTypeRepository.getTreeByKeyword(keyword));
	}
	
	/**
	 * @api {post} /diseaseType/getDiseaseAlias 获取疾病别名
	 * @apiVersion 1.0.0
     * @apiName getDiseaseAlias
     * @apiGroup 病种
     * @apiDescription 获取疾病别名
     * 
     * @apiParam	{String}    	access_token    token
     * @apiParam	{String}    	diseaseId				疾病id
     * 
	 * @apiSuccess {List}		alias		疾病别名
     * 
     * @apiSuccess {Number} 	resultCode    返回状态码
     * 	 
     * @apiAuthor  liangcs
     * @date 2016年7月25日
     */
	@RequestMapping("getDiseaseAlias")
	public JSONMessage getDiseaseAlias(String diseaseId) {
		return JSONMessage.success(diseaseTypeRepository.getDiseaseAlias(diseaseId));
	}
	
	
	/**
	 * 
	 * @api 			{[get,post]} 					/diseaseType/findById			根据id查询病种
	 * @apiVersion 		1.0.0
	 * @apiName 		findById	
	 * @apiGroup 		专长	
	 * @apiDescription 	根据id查询病种
	 * 
	 * @apiParam  		{String}    					access_token         		 凭证
	 * @apiParam  		{String}    					diseaseId         		 		病种id
	 * 
	 * @apiSuccess 		{String} 							id 								病种id 
	 * @apiSuccess 		{String} 							name 						病种名称
	 * @apiSuccess 		{String} 							introduction 				病种简介
	 * @apiSuccess 		{String[]} 							remark 						常见症状
	 * @apiSuccess 		{String} 							attention 					注意事项
	 * @apiSuccess 		{String} 							alias						别名
	 * 
	 * @apiAuthor 		傅永德
	 * @date 2016年7月25日
	 */
	@RequestMapping("findById")
	public JSONMessage findById(String diseaseId) {
		return JSONMessage.success(diseaseTypeRepository.findByIds(diseaseId));
	}
	
	/**
	 * 
	 * @api 			{[get,post]} 					/diseaseType/findByKeyword			根据关键字查询病种
	 * @apiVersion 		1.0.0
	 * @apiName 		findByKeyword
	 * @apiGroup 		专长	
	 * @apiDescription 	根据id查询病种
	 * 
	 * @apiParam  		{String}    					keyword         		 	 关键字
	 * @apiParam  		{Integer}    					pageIndex         		 	 页码
	 * @apiParam  		{Integer}    					pageSize         		 	 页面大小
	 * 
	 * @apiSuccess 		{String} 							id 							病种id 
	 * @apiSuccess 		{String} 							name 						病种名称
	 * @apiSuccess 		{String} 							introduction 				病种简介
	 * @apiSuccess 		{String} 							remark 						常见症状
	 * @apiSuccess 		{String} 							attention 					注意事项
	 * @apiSuccess 		{String[]} 							alias						别名
	 * 
	 * @apiAuthor 		傅永德
	 * @date 2016年7月25日
	 */
	@RequestMapping("findByKeyword")
	public JSONMessage findByKeyword(
			@RequestParam(name = "keyword", required = true)String keyword,
			@RequestParam(name = "pageIndex", defaultValue = "0")Integer pageIndex,
			@RequestParam(name = "pageSize", defaultValue = "2")Integer pageSize
	) {
		return JSONMessage.success(diseaseTypeRepository.findByKeyword(keyword, pageIndex, pageSize));
	}
	
	/**
	 * @api 			{[get,post]} 					/diseaseType/setDiseaseInfo			设置疾病的详情
	 * @apiVersion 		1.0.0
	 * @apiName 		setDiseaseInfo				设置疾病的详情
	 * @apiGroup 		专长	
	 * @apiDescription 	设置疾病的详情
	 * 
	 * @apiParam  		{String}    					access_token         		 凭证
	 * @apiParam		{String}						diseaseId					疾病id
	 * @apiParam  		{String}    					introduction         		 简介
	 * @apiParam  		{alias}    						alias         		 		别名、常用名
	 * @apiParam  		{String}    					remark  					 症状
	 * @apiParam  		{String}    					attention         		 	 注意事项
	 * 
	 * @apiSuccess  		{Integer}    					resultCode         		 1表示成功
	 * 
	 * @apiAuthor 		傅永德
	 */
	@RequestMapping("setDiseaseInfo")
	public JSONMessage setDiseaseInfo(
			@RequestParam(name="diseaseId", required = true)String diseaseId,
			@RequestParam(name="introduction", required = false)String introduction,
			@RequestParam(name="alias", required = false)String alias,
			@RequestParam(name="remark", required = false)String remark,
			@RequestParam(name="attention", required = false)String attention
	) {
		diseaseTypeRepository.setDiseaseInfo(diseaseId, introduction, alias, remark, attention);
		return JSONMessage.success();
	}
}
