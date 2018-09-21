package com.dachen.health.circle.controller;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.JSONMessage;
import com.dachen.health.circle.AdScopeEnum;
import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.form.AreaInfo;
import com.dachen.health.circle.service.Group2Service;
import com.dachen.health.circle.service.User2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/circle/group")
public class CircleGroupController extends CircleBaseController {

    @Autowired
    protected Group2Service group2Service;

    @Autowired
    protected User2Service user2Service;

    /**
     * @api {GET} /circle/group/findDeptById 获取科室信息
     * @apiVersion 1.0.0
     * @apiName Group.findDeptById
     * @apiGroup 科室
     * @apiDescription 获取科室信息
     * @apiParam {String} access_token token
     * @apiParam {String} id 科室id
     * @apiSuccess {Object} dept 科室实体对象
     * @apiSuccess {String} dept.id 科室id
     * @apiSuccess {String} dept.name 科室名称
     * @apiSuccess {String} dept.hospitalName 医院名称
     * @apiSuccess {String} dept.deptName 科室名称
     * @apiSuccess {String} dept.childName 子科室名称
     * @apiSuccess {String} dept.introduction 科室介绍
     * @apiSuccess {String} dept.logoUrl 科室头像
     * @apiAuthor 肖伟
     * @date 2017年5月25日
     */
    @RequestMapping(value = "/findDeptById")
    public JSONMessage findDeptById(@RequestParam String id) {
        Integer currentUserId = this.getCurrentUserId();
        Group2 ret = this.group2Service.findAndCheckDept(id);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /circle/group/findDeptHomePageById 获取科室主页信息
     * @apiVersion 1.0.0
     * @apiName Group.findDeptHomePageById
     * @apiGroup 科室
     * @apiDescription 获取科室主页信息
     * @apiParam {String} access_token token
     * @apiParam {String} id 科室id
     * @apiSuccess {Object} dept 科室实体对象
     * @apiSuccess {String} dept.id 科室id
     * @apiSuccess {String} dept.name 科室名称
     * @apiSuccess {String} dept.hospitalName 医院名称
     * @apiSuccess {String} dept.deptName 科室名称
     * @apiSuccess {String} dept.childName 子科室名称
     * @apiSuccess {String} dept.introduction 科室介绍
     * @apiSuccess {String} dept.logoUrl 科室头像
     * @apiSuccess {Object} group.groupUser2 管理员对象，如果有，说明是管理员，否则不是管理员
     * @apiAuthor 肖伟
     * @date 2017年6月6日
     */
    @RequestMapping(value = "/findDeptHomePageById")
    public JSONMessage findDeptHomePageById(@RequestParam String id) {
        Integer currentUserId = this.getCurrentUserId();
        Group2 ret = this.group2Service.findGroupHomePage(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /circle/group/updateDept 更新科室信息
     * @apiVersion 1.0.0
     * @apiName Group.updateDept
     * @apiGroup 科室
     * @apiDescription 更新科室信息
     * @apiParam {String} access_token token
     * @apiParam {String} id 科室id
     * @apiParam {String} childName 子科室名称
     * @apiParam {String} logoUrl 头像
     * @apiParam {String} introduction 介绍
     *
     * @apiSuccess {Object} dept 科室实体对象
     * @apiAuthor 肖伟
     * @date 2017年5月25日
     */
    @RequestMapping(value = "/updateDept")
    public JSONMessage updateDept(@RequestParam String id, @RequestParam String childName,
                                  String logoUrl, String introduction) {
        Integer currentUserId = this.getCurrentUserId();
        Group2 ret = this.group2Service.updateDept(currentUserId, id, childName, logoUrl, introduction);
        return JSONMessage.success(null, ret);
    }

   /* @RequestMapping(value = "/getUserIdByAdSope", method = RequestMethod.POST)
    public JSONMessage getUserIdByAdSope(@RequestParam Integer type,
                                         @RequestParam(required = false)List<String> areaJson,
                                         @RequestParam(required = false)List<Integer> cityIds,
                                         @RequestParam(required = false)List<String> levels,
                                         @RequestParam(required = false)List<String> deptIds,
                                         @RequestParam(required = false)List<String> titles,
                                         @RequestParam(required = false)List<String> groupIds,
                                         @RequestParam(required = false)List<String> unionIds,
                                         @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                         @RequestParam(name = "pageSize", defaultValue = "100") Integer pageSize) {
        //List<AreaInfo> provinces= JSON.parseArray(areaJson,AreaInfo.class);
        if (type.equals(AdScopeEnum.all.getIndex())) {
            return JSONMessage.success(null, user2Service.getNormalUserPage(pageIndex,pageSize));
        } else if (type.equals(AdScopeEnum.condition.getIndex())) {
            if(areaJson==null) areaJson=new ArrayList<>();
            if(levels==null) levels=new ArrayList<>();
            if(deptIds==null) deptIds=new ArrayList<>();
            if(titles==null) titles=new ArrayList<>();
            return JSONMessage.success(null, user2Service.getNormalUserIdByCityAndLevelAndDepartmentsAndTitlePage(areaJson, levels, deptIds, titles,pageIndex,pageSize));
        } else if (type.equals(AdScopeEnum.organization.getIndex())) {
            if(groupIds==null) groupIds=new ArrayList<>();
            if(unionIds==null) unionIds=new ArrayList<>();
            return JSONMessage.success(null, user2Service.getNormalUserIdByUnionIdAndGroupIdPage(groupIds, unionIds,pageIndex,pageSize));
        }
        return JSONMessage.success(null, null);
    }*/
}
