package com.dachen.health.circle.controller.lightapp;

import com.dachen.commons.JSONMessage;
import com.dachen.health.circle.lightapp.service.GuestLightAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangl
 * @desc
 * @date:2017/6/1611:17
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@RestController
@RequestMapping("/guest/lightApp")
public class GuestLightAppController {

    private static final Logger logger = LoggerFactory.getLogger(LightAppController.class);


    @Autowired
    GuestLightAppService guestLightAppService;

    /**
     * @api {GET} /guest/lightApp/getLightApps 获取轻应用列表
     * @apiVersion 1.0.0
     * @apiName getLightApps
     * @apiGroup Guest
     * @apiDescription 获取轻应用列表
     * @apiParam {String} access_token token
     *
     * @apiSuccess {String} lightAppId  应用id
     * @apiSuccess {String} lightAppName 应用名
     * @apiSuccess {String} lightAppDesc 应用介绍
     * @apiSuccess {String} lightAppPic 应用图片
     * @apiSuccess {String} iosProtocol 应用协议
     * @apiSuccess {String} androidProtocol 应用协议
     * @apiAuthor wangl
     * @date 2017年6月12日
     */
    @RequestMapping(value = "/getLightApps")
    public JSONMessage getLightApps() {
        return JSONMessage.success(guestLightAppService.getLightApps());
    }
}
