package com.dachen.health.controller.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;

/**
 * 科室相关接口
 * @author vincent
 *
 */

@RestController
@RequestMapping("/dept")
public class DeptController {
	
	@Resource
	private IBaseDataService baseDataService;
	
	/**
	 * 
	 * @api 			{[get,post]} 					/dept/findByParent			根据父科室查找科室
	 * @apiVersion 		1.0.0
	 * @apiName 		findByParent	
	 * @apiGroup 		专长	
	 * @apiDescription 	根据父科室查找科室
	 * @apiParam  		{String}    					access_token         		 凭证
	 * @apiParam  		{String}    					parentId         		 	父科室id(为空则找顶级)
	 * 
	 * @apiSuccess  	{String}    					parentId         		 	父科室id
	 * @apiSuccess  	{String}    					name         		 		科室名称
	 * @apiSuccess  	{String}    					id         		 			科室id
	 *
	
	 * 
	 * @apiAuthor 		李淼淼
	 * @author 			李淼淼
	 * @date 2015年9月21日
	 */
	@RequestMapping("/findByParent")
	public JSONMessage findByParent(String parentId){
		if(StringUtils.isBlank(parentId)){
			parentId="A";
		}
		List<DeptVO> depts=baseDataService.findByParent(parentId);
	/*	Map<String,Object> data=new HashMap<String,Object>();
		data.put("dept", depts);
		List<DiseaseType> diseaseTypes=diseaseTypeRepository.findByDept(parentId);
		data.put("diseaseType", diseaseTypes);*/
		return JSONMessage.success(null,depts);
	}

}
