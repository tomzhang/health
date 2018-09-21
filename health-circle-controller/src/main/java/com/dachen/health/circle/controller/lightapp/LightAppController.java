package com.dachen.health.circle.controller.lightapp;

import com.dachen.common.auth.data.AccessToken;
import com.dachen.commons.JSONMessage;
import com.dachen.health.circle.lightapp.entity.LightApp;
import com.dachen.health.circle.lightapp.entity.OrgLightApp;
import com.dachen.health.circle.lightapp.entity.UserLightApp;
import com.dachen.health.circle.lightapp.service.LightAppService;
import com.dachen.health.circle.lightapp.vo.LightAppParam;
import com.dachen.health.circle.lightapp.vo.LightAppVO;
import com.dachen.health.circle.lightapp.vo.UserInfo;
import com.dachen.util.ReqUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author sharp
 * @desc
 * @date:2017/6/1217:30 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@RestController
@RequestMapping("/lightApp")
@Api(value = "轻应用", description = "轻应用")
public class LightAppController {

    private static final Logger logger = LoggerFactory.getLogger(LightAppController.class);

    @Autowired
    private LightAppService lightAppService;

    @ApiOperation(value = "获取轻应用列表", response = LightAppVO[].class)
    @RequestMapping(value = "/getLightApps", method = {RequestMethod.GET,RequestMethod.POST})
    public JSONMessage getLightApps(Integer userId) {
        if (userId == null) {
            logger.warn("userId is null, please check gateway @requestHeader(\"userID\")");
        }
        return JSONMessage.success(lightAppService.getLightApps());
    }

    @ApiOperation(value = "获取用户信息", response = UserInfo.class)
    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public JSONMessage userinfo(@RequestParam String openId) {
        return JSONMessage.success(lightAppService.getUserInfo(openId));
    }

    @ApiOperation(value = "获取用户openId列表", response = AccessToken[].class)
    @RequestMapping(value = "/getOpenIdList", method = RequestMethod.POST)
    public JSONMessage getOpenIdList(@RequestBody LightAppParam param) {
        return JSONMessage.success(lightAppService.getOpenIdList(param.getUserIdList()));
    }

    @ApiOperation(value = "获取用户Id列表", response = AccessToken[].class)
    @RequestMapping(value = "/getUserIdList", method = RequestMethod.POST)
    public JSONMessage getUserIdList(@RequestBody LightAppParam param) {
        return JSONMessage.success(lightAppService.getUserIdList(param.getOpenIdList()));
    }

    @ApiOperation(value = "获取发现页是否显示该应用", response = Boolean.class)
    @RequestMapping(value = "/discovery/{appId}", method = RequestMethod.GET)
    public JSONMessage discovery(@PathVariable String appId) {
        return JSONMessage.success(lightAppService.discovery(appId));
    }




    /**
     *↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓已废弃↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     */
    @Deprecated
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JSONMessage insert(@RequestBody LightApp lightApp) {
        return JSONMessage.success(lightAppService.insert(lightApp));
    }

    @Deprecated
    @RequestMapping(value = "/org", method = RequestMethod.POST)
    public JSONMessage insert(@RequestBody OrgLightApp lightApp) {
        return JSONMessage.success(lightAppService.insert(lightApp));
    }

    @Deprecated
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public JSONMessage insert(@RequestBody UserLightApp lightApp) {
        return JSONMessage.success(lightAppService.insert(lightApp));
    }
}
