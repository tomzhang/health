package com.dachen.health.circle.mobile.controller;

import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.form.GroupDeptAddForm;
import com.dachen.health.circle.service.Group2Service;
import com.dachen.health.circle.service.GroupDoctor2Service;
import com.dachen.health.circle.vo.MobileGroupHomePageVO;
import com.dachen.health.circle.vo.MobileGroupVO;
import com.dachen.health.circle.vo.UserGroupAndUnionMapVO;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.json.JSONMessage;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/m/circle/group")
public class MobileCircleGroupController extends MobileCircleBaseController {

    @Autowired
    private Group2Service group2Service;

    @Autowired
    private GroupDoctor2Service groupDoctor2Service;

    /**
     * @api {GET} /m/circle/group/findHomePage 获取科室主页详情
     * @apiVersion 1.0.0
     * @apiName Group.findHomePage
     * @apiGroup 科室
     * @apiDescription 获取科室主页详情
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId 科室id
     *
     * @apiSuccess {Object} Group group
     * @apiSuccess {String} group.id 科室id
     * @apiSuccess {String} group.name 科室名称
     * @apiSuccess {String} group.hospitalName 医院名称
     * @apiSuccess {String} group.deptName 科室名称
     * @apiSuccess {String} group.childName 子科室名称
     * @apiSuccess {String} group.intro 科室介绍
     * @apiSuccess {String} group.logo 科室logo
     * @apiSuccess {String} group.expertNum 专家数量
     * @apiSuccess {Integer} group.ifManager 是否管理员，1是
     * @apiSuccess {Object} group.follow 关注详情，如果已关注，将返回。取消关注时需要使用follow的id取消
     * @apiSuccess {Object} group.groupDoctor 科室医生成员对象，如果有值，说明是科室成员。此时判断status为C表示状态是正常的成员；如果是I或J表示正在申请的成员；
     * @apiSuccess {Object} group.creator 创建人详情
     * @apiSuccess {Integer} group.totalMember 总成员数
     * @apiSuccess {Integer} group.totalManager 总管理员数
     *
     * @apiAuthor 肖伟
     * @date 2017年05月10日
     */
    @RequestMapping("/findHomePage")
    public JSONMessage findHomePage(String groupId) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupHomePageVO group = group2Service.findGroupHomePageAndVO(currentUserId, groupId);
        return JSONMessage.success(null, group);
    }

    /**
     * @api {GET} /m/circle/group/findUserGroupAndUnionMap 获取用户的组织列表
     * @apiVersion 1.0.0
     * @apiName Group.findUserGroupMap
     * @apiGroup 科室
     * @apiDescription 获取用户的组织列表
     *
     * @apiParam {String} access_token token
     *
     * @apiSuccess {Object} data data
     * @apiSuccess {Object[]} data.groupDoctors 组织关系列表（包括医院、圈子、科室）
     * @apiSuccess {Object[]} data.unionMembers 医联体关系列表
     * @apiSuccess {Object[]} data.unions 医联体列表
     *
     * @apiAuthor 肖伟
     * @date 2017年05月23日
     */
    @RequestMapping("/findUserGroupAndUnionMap")
    public JSONMessage findUserGroupAndUnionMap() throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        UserGroupAndUnionMapVO ret = group2Service.findUserGroupAndUnionMapAndVO(currentUserId);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /m/circle/group/findUserGroupAndUnionHomeMap 获取圈子主页
     * @apiVersion 1.0.0
     * @apiName Group.findUserGroupAndUnionHomeMap
     * @apiGroup 科室
     * @apiDescription 获取圈子主页
     *
     * @apiParam {String} access_token token
     *
     * @apiSuccess {Object} data data
     * @apiSuccess {Object[]} data.groupDoctors 组织关系列表（包括医院、圈子、科室），没有groupDoctorId的数据表示是推荐的数据
     * @apiSuccess {Object[]} data.unionMembers 医联体关系列表
     * @apiSuccess {Object[]} data.unions 医联体列表
     * @apiSuccess {Integer} data.ifUnionCanCreate 是否可创建医联体，1表示可创建医联体
     *
     * @apiAuthor 肖伟
     * @date 2017年05月23日
     */
    @RequestMapping("/findUserGroupAndUnionHomeMap")
    public JSONMessage findUserGroupAndUnionHomeMap() throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        UserGroupAndUnionMapVO ret = group2Service.findUserGroupAndUnionHomeMapAndVO(currentUserId);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /m/circle/group/findPage 获取所有圈子或科室的列表
     * @apiVersion 1.0.0
     * @apiName Group.findPage
     * @apiGroup 科室
     * @apiDescription 获取所有圈子或科室的列表
     *
     * @apiParam {String} access_token token
     * @apiParam {Integer} pageIndex pageIndex
     * @apiParam {Integer} pageSize pageSize
     *
     * @apiSuccess {Object} Page page
     * @apiSuccess {Object[]} page.list 科室详情列表
     *
     * @apiAuthor 肖伟
     * @date 2017年05月10日
     */
    @RequestMapping("/findPage")
    public JSONMessage findPage(@RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "20") Integer pageSize) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupVO> ret= group2Service.findPageAndVO(currentUserId, pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /m/circle/group/findDeptPage 获取科室的分页数据
     * @apiVersion 1.0.0
     * @apiName Group.findDeptPage
     * @apiGroup 科室
     * @apiDescription 获取科室的分页数据
     *
     * @apiParam {String} access_token token
     * @apiParam {String} keyword 关键字
     * @apiParam {Integer} pageIndex pageIndex
     * @apiParam {Integer} pageSize pageSize
     *
     * @apiSuccess {Object} Page page
     * @apiSuccess {Object[]} page.list 科室详情列表
     *
     * @apiAuthor 肖伟
     * @date 2017年05月31日
     */
    @RequestMapping("/findDeptPage")
    public JSONMessage findDeptPage(String keyword, @RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "20") Integer pageSize) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupVO> ret= group2Service.findDeptPageAndVO(currentUserId, keyword, pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /m/circle/group/findRecList 获取我的推荐的科室列表
     * @apiVersion 1.0.0
     * @apiName Group.findRecList
     * @apiGroup 科室
     * @apiDescription 获取我的推荐的科室列表
     *
     * @apiParam {String} access_token token
     * @apiParam {Integer} pageIndex pageIndex
     * @apiParam {Integer} pageSize pageSize
     *
     * @apiSuccess {Object} Page page
     * @apiSuccess {Object[]} page.list 科室详情
     * @apiSuccess {String} page.list.id id
     * @apiSuccess {String} page.list.name 名称
     * @apiSuccess {String} page.list.intro 介绍
     * @apiSuccess {String} page.list.logoPicUrl 科室logo
     * @apiSuccess {Object} page.list.creator 创建人信息
     *
     * @apiAuthor 肖伟
     * @date 2017年05月10日
     */
    @RequestMapping("/findRecList")
    public JSONMessage findRecList() throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        List<MobileGroupVO> ret= group2Service.findRecListAndVO(currentUserId);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/createDept 创建科室
     * @apiVersion 1.0.0
     * @apiName Group.createDept
     * @apiGroup 科室
     * @apiDescription 创建科室
     *
     * @apiParam {String} access_token token
     * @apiParam {String} hospitalId 医院id（必填）
     * @apiParam {String} deptId 科室id（必填）
     * @apiParam {String} childName 子科室名称
     * @apiParam {String} intro 科室介绍
     * @apiParam {String} logoPicUrl 科室logo
     *
     * @apiSuccess {Object} Group group
     *
     * @apiAuthor 肖伟
     * @date 2017年5月16日
     */
    @RequestMapping("/createDept")
    public JSONMessage createDept(@Valid GroupDeptAddForm form) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupVO ret = group2Service.createDeptAndVO(currentUserId, form.getHospitalId(), form.getDeptId(), form.getChildName(), form.getIntro(), form.getLogoPicUrl());
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/isDismiss 科室是否解散
     * @apiVersion 1.0.0
     * @apiName Group.isDismiss
     * @apiGroup 科室
     * @apiDescription  科室是否解散
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId groupId
     *
     * @apiSuccess {Boolean} success true表示 没有解散，false表示 有解散
     *
     * @apiAuthor 李敏
     * @date 2017年6月23日15:26:46
     */
    @RequestMapping("/isDismiss")
    public JSONMessage isDismiss(@RequestParam("groupId") String id) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = false;
        try {
            Group2 group2 = group2Service.findAndCheckById(id);
            ret = true;
        } catch (Exception e) {
            // ignore exception
        }
        return JSONMessage.success(null, ret);
    }
    /**
     * @api {POST} /m/circle/group/dismiss 解散
     * @apiVersion 1.0.0
     * @apiName Group.dismiss
     * @apiGroup 科室
     * @apiDescription  解散
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId groupId
     *
     * @apiSuccess {Boolean} success true表示成功，false表示失败
     *
     * @apiAuthor 肖伟
     * @date 2017年5月23日
     */
    @RequestMapping("/dismiss")
    public JSONMessage dismiss(@RequestParam("groupId") String id) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = group2Service.dismissDept(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/updateLogo 更新logo
     * @apiVersion 1.0.0
     * @apiName Group.updateLogo
     * @apiGroup 科室
     * @apiDescription 更新logo
     *
     * @apiParam {String} access_token token
     * @apiParam {String} id groupId
     * @apiParam {String} logoPicUrl logo图片地址
     *
     * @apiSuccess {Object} Group
     *
     * @apiAuthor 肖伟
     * @date 2017年05月10日
     */
    @RequestMapping("/updateLogo")
    public JSONMessage updateLogo(String id, @RequestParam String logoPicUrl) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupVO group = group2Service.updateLogoAndVO(currentUserId, id, logoPicUrl);
        return JSONMessage.success(null, group);
    }

    /**
     * @api {POST} /m/circle/group/updateChildName 更新名称
     * @apiVersion 1.0.0
     * @apiName Group.updateName
     * @apiGroup 科室
     * @apiDescription 更新名称
     *
     * @apiParam {String} access_token token
     * @apiParam {String} id groupId
     * @apiParam {String} childName 新的子科室名称
     *
     * @apiSuccess {Object} Group
     *
     * @apiAuthor 肖伟
     * @date 2017年05月10日
     */
    @RequestMapping("/updateChildName")
    public JSONMessage updateChildName(String id, @RequestParam String childName) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupVO group = group2Service.updateNameAndVO(currentUserId, id, childName);
        return JSONMessage.success(null, group);
    }

    /**
     * @api {POST} /m/circle/group/updateIntro 更新简介
     * @apiVersion 1.0.0
     * @apiName Group.updateIntro
     * @apiGroup 科室
     * @apiDescription 更新Group名称
     * @apiParam {String} access_token token
     * @apiParam {String} id groupId
     * @apiParam {String} intro 新的简介
     *
     * @apiSuccess {Object} Group
     *
     * @apiAuthor 肖伟
     * @date 2017年05月10日
     */
    @RequestMapping("/updateIntro")
    public JSONMessage updateIntro(String id, @RequestParam String intro) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupVO group = group2Service.updateIntroAndVO(currentUserId, id, intro);
        return JSONMessage.success(null, group);
    }

    /**
     * @api {POST} /m/circle/group/isJoinDept 是否加入科室
     * @apiVersion 1.0.0
     * @apiName Group.isJoinDept
     * @apiGroup 科室
     * @apiDescription 是否加入科室
     * @apiParam  {String} access_token token
     * @apiSuccess  {Boolean} data  有 true没有false
     * @apiAuthor  李敏
     * @date  2017年6月16日16:15:15
     */
    @RequestMapping("/isJoinDept")
    public JSONMessage isJoinDept(){
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupDoctor2Service.isJoinDept(currentUserId);
        return JSONMessage.success(null, ret);
    }


    /**
     * @api {POST} /m/circle/group/infoIsDept 职业信息是否属于该科室
     * @apiVersion 1.0.0
     * @apiName Group.infoIsDept
     * @apiGroup 科室
     * @apiDescription 职业信息是否属于该科室
     * @apiParam  {String} access_token token
     * @apiParam  {String} groupId groupId
     * @apiSuccess  {Boolean} data  属于该科室 true  不属于false
     * @apiAuthor  李敏
     * @date  2017年6月16日16:15:15
     */
    @RequestMapping("/infoIsDept")
    public JSONMessage infoIsDept(String groupId){
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupDoctor2Service.infoIsDept(currentUserId,groupId);
        return JSONMessage.success(null, ret);
    }

}
