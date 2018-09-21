package com.dachen.health.controller.group.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.group.group.entity.param.GroupProfitParam;
import com.dachen.health.group.group.service.IGroupProfitService;
import com.dachen.util.ReqUtil;

/**
 * ProjectName： health-im-api<br>
 * ClassName： GroupProfitController<br>
 * Description： 抽成关系controller<br>
 * 
 * @author fanp
 * @createTime 2015年9月2日
 * @version 1.0.0
 */
@RestController
@RequestMapping("/group/profit")
public class GroupProfitController extends AbstractController {

    @Autowired
    private IGroupProfitService groupProfitService;


    /**
     * @api {post} /group/profit/getTree 获取医生抽成树结构
     * @apiVersion 1.0.0
     * @apiName getTree
     * @apiGroup 抽成
     * @apiDescription 获取医生抽成树结构(返回字段新增两个：联系方式和组织全称)
     *
     * @apiParam   {String}   access_token                  token
     * @apiParam   {String}   groupId                       所属集团Id
     * 
     * @apiSuccess {Integer}  id                            id
     * @apiSuccess {Integer}  parentId                      父id
     * @apiSuccess {String}   name                          医生姓名
     * @apiSuccess {Map}      attributes    属性
     * @apiSuccess {String}   attributes.headPicFileName    医生头像
     * 
     * @apiSuccess {String}   attributes.parentProfit     上级抽成比例 （兼容以前的数据）
     * @apiSuccess {String}   attributes.groupProfit     集团抽成比例（兼容以前的数据）
     * 
     * @apiSuccess {String}   attributes.textParentProfit     图文资讯 上级抽成比例
     * @apiSuccess {String}   attributes.textGroupProfit     图文资讯 集团抽成比例
     * @apiSuccess {String}   attributes.phoneParentProfit     	 电话资讯 上级抽成比例
     * @apiSuccess {String}   attributes.phoneGroupProfit     	电话资讯 集团抽成比例
     * @apiSuccess {String}   attributes.carePlanParentProfit     	 健康关怀 上级抽成比例
     * @apiSuccess {String}   attributes.carePlanGroupProfit     	健康关怀 集团抽成比例
     * @apiSuccess {String}   attributes.clinicParentProfit     	 门诊 上级抽成比例
     * @apiSuccess {String}   attributes.clinicGroupProfit     		 门诊 集团抽成比例
     * @apiSuccess {String}   attributes.consultationParentProfit     	 会诊 上级抽成比例
     * @apiSuccess {String}   attributes.consultationGroupProfit     		 会诊 集团抽成比例
     * @apiSuccess {String}   attributes.appointmentParentProfit     	 名医面对面 上级抽成比例
     * @apiSuccess {String}   attributes.appointmentGroupProfit     	名医面对面  集团抽成比例
     * @apiSuccess {String}   attributes.chargeItemGroupProfit     	 收费项 集团抽成比例
     * @apiSuccess {String}   attributes.chargeItemParentProfit     	收费项  集团抽成比例
     * 
     * @apiSuccess {String}   attributes.hospital           医院
     * @apiSuccess {String}   attributes.departments        科室
     * @apiSuccess {String}   attributes.title              职称
     * @apiSuccess {String}   attributes.contactWay         联系方式
     * @apiSuccess {String}   attributes.departmentFullName 组织全称
     *
     * @apiAuthor 范鹏
     * @date 2015年8月11日
     */
    @RequestMapping("/getTree")
    public JSONMessage getTree(String groupId) {
        return JSONMessage.success(null, groupProfitService.getGroupProfit(groupId));
    }
    
    /**
     * @api {post} /group/profit/getList 获取医生抽成列表结构
     * @apiVersion 1.0.0
     * @apiName getList
     * @apiGroup 抽成
     * @apiDescription 获取医生抽成树结构
     *
     * @apiParam   {String}   access_token                 token
     * @apiParam   {String}   groupId                      所属集团Id
     * @apiParam   {String}   parentId                     父节点id
     * @apiParam   {String}   start                        查询页
     * @apiParam   {String}   pageSize                     每页显示条数
     * 
     * @apiSuccess {Object[]} pageData                     用户列表
     * @apiSuccess {String}   pageData.id                  医生id
     * @apiSuccess {String}   pageData.name                用户姓名
     * @apiSuccess {String}   pageData.doctorNum           医生号
     * @apiSuccess {Number}   pageData.hospital            医院
     * @apiSuccess {Number}   pageData.departments         科室
     * @apiSuccess {String}   pageData.title               职称
     * @apiSuccess {String}   pageData.headPicFileName     头像
     * @apiSuccess {String}   pageData.contactWay          联系方式
     * @apiSuccess {String}   pageData.remarks             备注
     * @apiSuccess {String}   pageData.groupProfit         集团抽成（兼容以前的数据）
     * @apiSuccess {String}   pageData.parentProfit        上级抽成（兼容以前的数据）
     * 
     * @apiSuccess {String}   pageData.textParentProfit     图文资讯 上级抽成比例
     * @apiSuccess {String}   pageData.textGroupProfit     图文资讯 集团抽成比例
     * @apiSuccess {String}   pageData.phoneParentProfit     	 电话资讯 上级抽成比例
     * @apiSuccess {String}   pageData.phoneGroupProfit     	电话资讯 集团抽成比例
     * @apiSuccess {String}   pageData.carePlanParentProfit     	 健康关怀 上级抽成比例
     * @apiSuccess {String}   pageData.carePlanGroupProfit     	健康关怀 集团抽成比例
     * @apiSuccess {String}   pageData.clinicParentProfit     	 门诊 上级抽成比例
     * @apiSuccess {String}   pageData.clinicGroupProfit     		 门诊 集团抽成比例
     * @apiSuccess {String}   pageData.consultationParentProfit     	 会诊 上级抽成比例
     * @apiSuccess {String}   pageData.consultationGroupProfit     		 会诊 集团抽成比例
     * @apiSuccess {String}   pageData.appointmentGroupProfit     	             名医面对面集团抽成比例
     * @apiSuccess {String}   pageData.appointmentParentProfit     名医面对面上级抽成比例
     * @apiSuccess {String}   pageData.chargeItemGroupProfit     	        收费项 集团抽成比例
     * @apiSuccess {String}   pageData.chargeItemParentProfit     	        收费项 上级抽成比例
     * 
     * @apiSuccess {String}   pageData.departmentFullName  所在部门
     * @apiSuccess {String}   pageData.childrenCount       直接下级人数
     *
     * @apiAuthor 范鹏
     * @date 2015年8月11日
     */
    @RequestMapping("/getList")
    public JSONMessage getList(GroupProfitParam param) {
    	//先修复profit的历史垃圾数据
    	groupProfitService.fixHistoryProfitData(param.getGroupId());
    	
        return JSONMessage.success(null, groupProfitService.getGroupProfit(param));
    }
    
    
    /**
     * @api {get} /group/profit/searchByKeyword 搜索获取医生抽成列表结构
     * @apiVersion 1.0.0
     * @apiName searchByKeyword
     * @apiGroup 抽成
     * @apiDescription 搜索获取医生抽成列表结构
     *
     * @apiParam   {String}   access_token                 token
     * @apiParam   {String}   groupId                      所属集团Id
     * @apiParam   {String}   keyword                      关键字
     * @apiParam   {String}   pageIndex                    查询页，从0开始
     * @apiParam   {String}   pageSize                     每页显示条数，不传默认15条
     * 
     * @apiSuccess {Object[]} pageData                     用户列表
     * @apiSuccess {String}   pageData.id                  医生id
     * @apiSuccess {String}   pageData.name                用户姓名
     * @apiSuccess {String}   pageData.doctorNum           医生号
     * @apiSuccess {Number}   pageData.hospital            医院
     * @apiSuccess {Number}   pageData.departments         科室
     * @apiSuccess {String}   pageData.title               职称
     * @apiSuccess {String}   pageData.headPicFileName     头像
     * @apiSuccess {String}   pageData.contactWay          联系方式
     * @apiSuccess {String}   pageData.remarks             备注
     * @apiSuccess {String}   pageData.groupProfit         集团抽成（兼容以前的数据）
     * @apiSuccess {String}   pageData.parentProfit        上级抽成（兼容以前的数据）
     * 
     * @apiSuccess {String}   pageData.textParentProfit     图文资讯 上级抽成比例
     * @apiSuccess {String}   pageData.textGroupProfit     图文资讯 集团抽成比例
     * @apiSuccess {String}   pageData.phoneParentProfit     	 电话资讯 上级抽成比例
     * @apiSuccess {String}   pageData.phoneGroupProfit     	电话资讯 集团抽成比例
     * @apiSuccess {String}   pageData.carePlanParentProfit     	 健康关怀 上级抽成比例
     * @apiSuccess {String}   pageData.carePlanGroupProfit     	健康关怀 集团抽成比例
     * @apiSuccess {String}   pageData.clinicParentProfit     	 门诊 上级抽成比例
     * @apiSuccess {String}   pageData.clinicGroupProfit     		 门诊 集团抽成比例
     * @apiSuccess {String}   pageData.consultationParentProfit     	 会诊 上级抽成比例
     * @apiSuccess {String}   pageData.consultationGroupProfit     		 会诊 集团抽成比例
     * @apiSuccess {String}   pageData.appointmentGroupProfit     	             名医面对面集团抽成比例
     * @apiSuccess {String}   pageData.appointmentParentProfit     名医面对面上级抽成比例
     * @apiSuccess {String}   pageData.chargeItemGroupProfit     	        收费项 集团抽成比例
     * @apiSuccess {String}   pageData.chargeItemParentProfit     	        收费项 上级抽成比例
     * 
     * @apiSuccess {String}   pageData.departmentFullName  所在部门
     * @apiSuccess {String}   pageData.childrenCount       直接下级人数
     *
     * @apiAuthor  谢平
     * @date 2015年11月5日
     */
	@RequestMapping("/searchByKeyword")
	public JSONMessage searchByKeyword(GroupProfitParam param) {
		//先修复profit的历史垃圾数据
    	groupProfitService.fixHistoryProfitData(param.getGroupId());
		return JSONMessage.success(null,groupProfitService.searchByKeyword(param));
	}
    

    /**
     * @api {post} /group/profit/update 调整抽成关系
     * @apiVersion 1.0.0
     * @apiName update
     * @apiGroup 抽成
     * @apiDescription 调整抽成关系
     *
     * @apiParam   {String}   access_token                  token
     * @apiParam   {String}   groupId                       所属集团Id
     * @apiParam   {Integer}  id                            移动的医生id
     * @apiParam   {Integer}  parentId                      移动父节点医生id
     * 
     * @apiSuccess {Number=1} resultCode                    返回状态吗
     *
     * @apiAuthor 范鹏
     * @date 2015年8月11日
     */
    @RequestMapping("/update")
    public JSONMessage update(String groupId,Integer id,Integer parentId) {

    	//parentId为空时，认为移动到根节点上
    	if(parentId == null){
    		parentId = 0;
    	}
    	
        groupProfitService.updateParentId(id,parentId,groupId);
        return JSONMessage.success();
    }
    
    /**
     * @api {post} /group/profit/delete 删除抽成关系
     * @apiVersion 1.0.0
     * @apiName delete
     * @apiGroup 抽成
     * @apiDescription 删除抽成关系
     *
     * @apiParam   {String}   access_token                  token
     * @apiParam   {String}   groupId                       所属集团Id
     * @apiParam   {Integer}  id                            删除的医生id
     * 
     * @apiSuccess {Number=1} resultCode                    返回状态吗
     *
     * @apiAuthor 范鹏
     * @date 2015年8月11日
     */
    @RequestMapping("/delete")
    public JSONMessage delete(Integer id,String groupId) {
    	
        groupProfitService.delete(id,groupId);
        return JSONMessage.success();
    }
    
    /**
     * @api {post} /group/profit/updateProfit 修改抽成比例
     * @apiVersion 1.0.0
     * @apiName updateProfit
     * @apiGroup 抽成
     * @apiDescription 修改抽成比例
     *
     * @apiParam   {String}   access_token                  token
     * @apiParam   {String}   groupId                       所属集团Id
     * @apiParam   {Integer}  id                            需修改的医生id
     * @apiParam   {Integer}  doctorId                    需修改的医生id
     * @apiParam   {Integer}  parentId                         上级医生id
     * @apiParam   {Integer}  groupProfit                   集团抽成比例（兼容以前的数据）
     * @apiParam   {Integer}  parentProfit                  上级抽成比例（兼容以前的数据）
     * 
     * @apiParam {String}   textParentProfit     图文资讯 上级抽成比例
     * @apiParam {String}   textGroupProfit     图文资讯 集团抽成比例
     * @apiParam {String}   phoneParentProfit     	 电话资讯 上级抽成比例
     * @apiParam {String}   phoneGroupProfit     	电话资讯 集团抽成比例
     * @apiParam {String}   carePlanParentProfit     	 健康关怀 上级抽成比例
     * @apiParam {String}   carePlanGroupProfit     	健康关怀 集团抽成比例
     * @apiParam {String}   clinicParentProfit     	 门诊 上级抽成比例
     * @apiParam {String}   clinicGroupProfit     		 门诊 集团抽成比例
     * @apiParam {String}   appointmentGroupProfit     	 名医面对面集团抽成比例
     * @apiParam {String}   appointmentParentProfit     名医面对面上级抽成比例
     * @apiParam {String}   consultationParentProfit     	 会诊 上级抽成比例
     * @apiParam {String}   consultationGroupProfit     		 会诊 集团抽成比例
     * @apiParam {String}   chargeItemGroupProfit     	        收费项 集团抽成比例
     * @apiParam {String}   chargeItemParentProfit     	        收费项 上级抽成比例
     * 
     * @apiSuccess {Number=1} resultCode                    返回状态吗
     *
     * @apiAuthor 王峭
     * @date 2016年1月26日
     */
    @RequestMapping("/updateProfit")
    public JSONMessage updateProfit(GroupProfitParam param) {
    	param.setUpdator(ReqUtil.instance.getUserId());
    	
    	if(param.getDoctorId() == null || param.getDoctorId() ==0){
    		param.setDoctorId(Integer.valueOf(param.getId()));
    	}
    	if(param.getDoctorId() == null || param.getDoctorId() ==0){
    		 throw new ServiceException("医生id为空");
    	}
    	
        groupProfitService.updateProfit(param);
        return JSONMessage.success();
    }
    

}
