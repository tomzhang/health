package com.dachen.health.controller.group.group;

import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.group.group.service.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/14 10:59 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@RestController
@RequestMapping("/group/dept")
public class GroupDeptController extends AbstractController {

    @Autowired
    private IGroupService groupService;

    /**
     * @api {get} /group/dept/nologin/findActiveDeptListByKeyword 根据关键字查找激活的科室列表
     * @apiVersion 1.0.0
     * @apiName /group/dept/nologin/findActiveDeptListByKeyword
     * @apiGroup 医院平台
     * @apiDescription 根据关键字查找激活的科室列表
     *
     * @apiParam {String} keyword 关键字
     *
     * @apiSuccess {String} id 科室Id
     * @apiSuccess {String} name 科室名称
     *
     * @apiAuthor 钟良
     * @date 2017年6月20日
     */
    @RequestMapping("/nologin/findActiveDeptListByKeyword")
    public JSONMessage findActiveDeptListByKeyword(String keyword){
        return JSONMessage.success(groupService.findActiveDeptListByKeyword(keyword));
    }
}
