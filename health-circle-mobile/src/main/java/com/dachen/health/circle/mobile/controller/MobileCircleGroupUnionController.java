package com.dachen.health.circle.mobile.controller;

import com.dachen.commons.JSONMessage;
import com.dachen.health.circle.form.GroupUnionAddForm;
import com.dachen.health.circle.service.Group2Service;
import com.dachen.health.circle.service.GroupUnionService;
import com.dachen.health.circle.vo.MobileGroupUnionHomePageVO;
import com.dachen.health.circle.vo.MobileGroupUnionVO;
import com.dachen.health.circle.vo.MobileGroupVO;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/m/circle/group/union")
public class MobileCircleGroupUnionController extends MobileCircleBaseController {

    @Autowired
    protected GroupUnionService groupUnionService;

    /**
     * @api {POST} /m/circle/group/union/create 创建医联体
     * @apiVersion 1.0.0
     * @apiName GroupUnion.create
     * @apiGroup 医联体
     * @apiDescription 创建医联体
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId 主体圈子或科室
     * @apiParam {String} name name
     * @apiParam {String} intro intro
     * @apiParam {String} logoPicUrl logoPicUrl
     *
     * @apiSuccess {Object} GroupUnion groupUnion
     *
     * @apiAuthor 肖伟
     * @date 2017年5月19日
     */
    @RequestMapping("/create")
    public JSONMessage create(@Valid GroupUnionAddForm form) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupUnionVO ret = groupUnionService.createAndVO(currentUserId, form.getGroupId(), form.getName(), form.getIntro(), form.getLogoPicUrl());
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /m/circle/group/union/findHomePage 获取主页详情
     * @apiVersion 1.0.0
     * @apiName GroupUnion.findHomePage
     * @apiGroup 医联体
     * @apiDescription 获取主页详情
     *
     * @apiParam {String} access_token token
     * @apiParam {String} unionId unionId
     * @apiParam {String} groupId 科室id（以科室的身份查看医联体主页）
     *
     * @apiSuccess {Object} GroupUnion groupUnion
     * @apiSuccess {String} groupUnion.unionId unionId
     * @apiSuccess {String} groupUnion.name 名称
     * @apiSuccess {String} groupUnion.intro 介绍
     * @apiSuccess {String} groupUnion.logoPicUrl logoPicUrl
     * @apiSuccess {Object} groupUnion.group 创建主体信息
     * @apiSuccess {String} groupUnion.role null时表示普通用户，admin为普通管理员，root为主体管理员
     * @apiSuccess {Integer} groupUnion.totalMember 总团队数
     * @apiSuccess {Integer} groupUnion.totalDoctor 总人数
     * @apiSuccess {Object} groupUnion.member 成员记录，如果有，表示科室已经是成员，否则看apply
     * @apiSuccess {Object} groupUnion.apply 科室的申请记录信息，当apply.statusId=1时表示申请在处理中
     *
     * @apiAuthor 肖伟
     * @date 2017年05月19日
     */
    @RequestMapping("/findHomePage")
    public JSONMessage findHomePage(@RequestParam("unionId") String id, String groupId) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupUnionHomePageVO group = groupUnionService.findGroupHomePagAndVO(currentUserId, id, groupId);
        return JSONMessage.success(null, group);
    }

    @Autowired
    protected Group2Service group2Service;

    /**
     * @api {GET} /m/circle/group/union/ifCanCreate 判断是否可以创建医联体
     * @apiVersion 1.0.0
     * @apiName GroupUnion.ifCanCreate
     * @apiGroup 医联体
     * @apiDescription 判断是否可以创建医联体
     *
     * @apiParam {String} access_token token
     *
     * @apiSuccess {Boolean} success success
     *
     * @apiAuthor 肖伟
     * @date 2017年05月27日
     */
    @RequestMapping("/ifCanCreate")
    public JSONMessage ifCanCreate() throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = this.groupUnionService.ifCanCreate(currentUserId);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/union/updateLogo 更新logo
     * @apiVersion 1.0.0
     * @apiName GroupUnion.updateLogo
     * @apiGroup 医联体
     * @apiDescription 更新logo
     *
     * @apiParam {String} access_token token
     * @apiParam {String} unionId unionId
     * @apiParam {String} logoPicUrl logo图片地址
     *
     * @apiSuccess {Object} GroupUnion groupUnion
     *
     * @apiAuthor 肖伟
     * @date 2017年05月19日
     */
    @RequestMapping("/updateLogo")
    public JSONMessage updateLogo(@RequestParam("unionId") String id, @RequestParam String logoPicUrl) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupUnionVO group = groupUnionService.updateLogoAndVO(currentUserId, id, logoPicUrl);
        return JSONMessage.success(null, group);
    }

    /**
     * @api {POST} /m/circle/group/union/updateName 更新名称
     * @apiVersion 1.0.0
     * @apiName GroupUnion.updateName
     * @apiGroup 医联体
     * @apiDescription 更新名称
     *
     * @apiParam {String} access_token token
     * @apiParam {String} unionId unionId
     * @apiParam {String} name 新的名称
     *
     * @apiSuccess {Object} GroupUnion groupUnion
     *
     * @apiAuthor 肖伟
     * @date 2017年05月19日
     */
    @RequestMapping("/updateName")
    public JSONMessage updateName(@RequestParam("unionId") String id, @RequestParam String name) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupUnionVO group = groupUnionService.updateNameAndVO(currentUserId, id, name);
        return JSONMessage.success(null, group);
    }

    /**
     * @api {POST} /m/circle/group/union/updateIntro 更新简介
     * @apiVersion 1.0.0
     * @apiName GroupUnion.updateIntro
     * @apiGroup 医联体
     * @apiDescription 更新名称
     * @apiParam {String} access_token token
     * @apiParam {String} unionId unionId
     * @apiParam {String} intro 新的简介
     *
     * @apiSuccess {Object} GroupUnion
     *
     * @apiAuthor 肖伟
     * @date 2017年05月19日
     */
    @RequestMapping("/updateIntro")
    public JSONMessage updateIntro(@RequestParam("unionId") String id, @RequestParam String intro) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupUnionVO group = groupUnionService.updateIntroAndVO(currentUserId, id, intro);
        return JSONMessage.success(null, group);
    }



    /**
     * @api {POST} /m/circle/group/union/dismiss 解散
     * @apiVersion 1.0.0
     * @apiName GroupUnion.dismiss
     * @apiGroup 医联体
     * @apiDescription 解散
     * @apiParam {String} access_token token
     * @apiParam {String} unionId unionId
     *
     * @apiSuccess {Boolean} success success
     *
     * @apiAuthor 肖伟
     * @date 2017年05月19日
     */
    @RequestMapping("/dismiss")
    public JSONMessage dismiss(@RequestParam("unionId") String id) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupUnionService.dismiss(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

}
