package com.dachen.health.controller.group.department;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.group.department.entity.param.DepartmentParam;
import com.dachen.health.group.department.entity.po.Department;
import com.dachen.health.group.department.entity.vo.DepartmentVO;
import com.dachen.health.group.department.service.IDepartmentService;
import com.dachen.util.ReqUtil;

/**
 * 
 * @author pijingwei
 * @date 2015/8/11
 */
@RestController
@RequestMapping("/department")
public class DepartmentController extends AbstractController {

	@Autowired
	private IDepartmentService deparService;
	
	/**
     * @api {post} /department/saveByDepart 新建组织架构
     * @apiVersion 1.0.0
     * @apiName saveByDepart
     * @apiGroup 组织架构
     * @apiDescription 基于已有的寄生集团下，新建组织架构
     *
     * @apiParam  {String}    		access_token        			token
     * @apiParam {Object[]} 		department                  	部门（门诊）
     * @apiParam {String}   		department.groupId           	所属集团Id
     * @apiParam {String}   		department.parentId           	父节点Id
     * @apiParam {String}   		department.name       			组织名称
     * @apiParam {String}   		department.description       	组织描述
     *
     * @apiSuccess {Number} resultCode    返回状态吗
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月11日
	 */
	@RequestMapping("/saveByDepart")
	public JSONMessage saveByDepart(Department department) {
		department.setCreator(ReqUtil.instance.getUserId());
		department.setCreatorDate(new Date().getTime());
		department.setUpdator(ReqUtil.instance.getUserId());
		department.setUpdatorDate(new Date().getTime());
		deparService.addDepartment(department);
		return JSONMessage.success("success");
	}
	
	
	/**
     * @api {post} /department/updateByDepart 修改组织架构
     * @apiVersion 1.0.0
     * @apiName updateByDepart
     * @apiGroup 组织架构
     * @apiDescription 修改组织架构
     *
     * @apiParam  {String}    		access_token        			token
     * @apiParam  {Object[]} 		department                  	部门（门诊）
     * @apiParam  {String}   		department.id           		组织Id
     * @apiParam  {String}   		department.parentId           	父节点Id
     * @apiParam  {String}   		department.name       			组织名称
     * @apiParam  {String}   		department.description       	组织描述
     *
     * @apiSuccess {Number} resultCode    返回状态吗
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月11日
	 */
	@RequestMapping("/updateByDepart")
	public JSONMessage updateByDepart(Department department) {
		Integer operationUserId = ReqUtil.instance.getUserId();
		
		deparService.updateDepartmentById(department.getId(), department.getName(), department.getDescription(), department.getParentId(), operationUserId);
//		deparService.updateDepartment(department);
		return JSONMessage.success("success");
	}
	
	/**
     * @api {post} /department/searchByDepart 条件查找组织架构列表
     * @apiVersion 1.0.0
     * @apiName searchByDepart
     * @apiGroup 组织架构
     * @apiDescription 条件查找组织架构列表
     *
     * @apiParam  {String}    	access_token        			token
     * @apiParam {Object[]} 	department                  	组织架构
     * @apiParam {String}   	department.groupId           	所属集团Id
     * @apiParam {String}   	department.parentId           	父节点Id
     * @apiParam {String}   	department.name       			组织名称
     * @apiParam {String}   	department.description       	组织描述
     *
     * @apiSuccess {Object[]} 	department              		组织架构
     * @apiSuccess {String}  	department.id					组织Id
     * @apiSuccess {String}  	department.groupId				集团Id
     * @apiSuccess {String}  	department.parentId        		父节点Id
     * @apiSuccess {String}  	department.name        			组织名称
     * @apiSuccess {String}  	department.description        	组织描述
     * @apiSuccess {Integer}  	department.creator        		创建人
     * @apiSuccess {Long}  		department.creatorDate       	创建时间
     * @apiSuccess {Integer}  	department.updator        		更新人
     * @apiSuccess {Long}  		department.updatorDate       	更新时间
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月11日
	 */
	@RequestMapping("/searchByDepart")
	public JSONMessage searchByDepart(DepartmentParam department) {
//		return JSONMessage.success(null, deparService.searchDepartment(department));
		//参数校验
		if(department == null){
			throw new ServiceException("参数不能为空");
		}
		List<DepartmentVO> list = deparService.searchDepartment(department.getGroupId(), department.getParentId(), department.getName(), department.getDescription());
		
		return JSONMessage.success(null, list);
	}
	
	
	/**
     * @api {post} /department/getAllDataById 获取所有组织架构列表
     * @apiVersion 1.0.0
     * @apiName getAllDataById
     * @apiGroup 组织架构
     * @apiDescription 获取所有组织架构列表
     *
     * @apiParam  {String}    	access_token        					token
     * @apiParam  {String}   	department.groupId           			集团Id
     *
     * @apiSuccess {Object[]} 	department              				组织架构
     * @apiSuccess {String}  	department.id							组织Id
     * @apiSuccess {String}  	department.groupId						集团Id
     * @apiSuccess {String}  	department.parentId        				父节点Id
     * @apiSuccess {String}  	department.name        					组织名称
     * @apiSuccess {String}  	department.description        			组织描述
     * @apiSuccess {Integer}  	department.creator        				创建人
     * @apiSuccess {Long}  		department.creatorDate       			创建时间
     * @apiSuccess {Integer}  	department.updator        				更新人
     * @apiSuccess {Long}  		department.updatorDate       			更新时间
     * @apiSuccess {Object[]}  	department.subList       				子科室
     * @apiSuccess {String}  	department.subList.id					组织Id
     * @apiSuccess {String}  	department.subList.groupId				集团Id
     * @apiSuccess {String}  	department.subList.parentId        		父节点Id
     * @apiSuccess {String}  	department.subList.name        			组织名称
     * @apiSuccess {String}  	department.subList.description        	组织描述
     * @apiSuccess {Integer}  	department.subList.creator        		创建人
     * @apiSuccess {Long}  		department.subList.creatorDate       	创建时间
     * @apiSuccess {Integer}  	department.subList.updator        		更新人
     * @apiSuccess {Long}  		department.subList.updatorDate       	更新时间
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月11日
	 */
	@RequestMapping("/getAllDataById")
	public JSONMessage getAllDataById(String groupId) {
		return JSONMessage.success(null, deparService.findAllListById(groupId));
	}
	
	
	/**
     * @api {post} /department/deleteByDepart 删除组织架构
     * @apiVersion 1.0.0
     * @apiName deleteByDepart
     * @apiGroup 组织架构
     * @apiDescription 根据Id删除组织架构
     *
     * @apiParam  {String}    	access_token    token
     * @apiParam {String[]}   	ids      		组织Id
     *
     * @apiSuccess {Number} resultCode    返回状态吗
     *
     * @apiAuthor  pijingwei
     * @date 2015年8月11日
	 */
	@RequestMapping("/deleteByDepart")
	public JSONMessage deleteByDepart(String[] ids) {
		deparService.deleteDepartment(ids);
		return JSONMessage.success("success");
	}
	
}
