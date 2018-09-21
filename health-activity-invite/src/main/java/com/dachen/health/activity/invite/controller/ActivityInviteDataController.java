package com.dachen.health.activity.invite.controller;

import com.dachen.commons.JSONMessage;
import com.dachen.health.activity.invite.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/2 9:58 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@RestController
@RequestMapping("/activity/invite/data")
public class ActivityInviteDataController extends ActivityInviteBaseController {

    @Autowired
    private ActivityService activityService;

    /**
     * @api {get} /activity/invite/data/activity 活动列表
     * @apiVersion 1.0.0
     * @apiName /activity/invite/data/activity
     * @apiGroup 活动管理
     * @apiDescription 活动列表
     *
     * @apiParam {String}      access_token             token
     * @apiParam {Integer}     type                     活动类型（邀请活动-1、注册活动-2）
     *
     * @apiSuccess {String}         resultCode          返回状态码
     * @apiSuccess {Object[]}     	data  				data对象
     * @apiSuccess {String}     	data.id  			活动Id
     * @apiSuccess {String}     	data.name  			活动名称
     *
     * @apiSuccessExample   {json} Success-Response:
     *     HTTP/1.1 200 OK
     *     {
        "data": [
            {
                "code": "we8qhrapb",
                "content": "拉新活动内容",
                "createTime": 1495448653000,
                "deleted": false,
                "endTime": 1500480000000,
                "id": "5922bc80471de95e79639cbb",
                "name": "拉新活动",
                "startTime": 1495209600000,
                "type": 1,
                "updateTime": 1495448653000,
                "valid": true
            },
            {
                "code": "weegsbd93",
                "content": "注册活动内容",
                "createTime": 1495448653000,
                "deleted": false,
                "endTime": 1500480000000,
                "id": "5922c24e471de95e79639cbc",
                "name": "注册活动",
                "startTime": 1495209600000,
                "type": 2,
                "updateTime": 1495448653000,
                "valid": true
            }
        ],
        "resultCode": 1
        }
     *
     * @apiAuthor 钟良
     * @date 2017年6月5日
     */
    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    public JSONMessage listActivity(@RequestParam Integer type) {
        return JSONMessage.success(activityService.findList(type));
    }
}
