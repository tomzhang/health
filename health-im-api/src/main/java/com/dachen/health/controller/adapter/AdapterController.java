package com.dachen.health.controller.adapter;

import com.dachen.commons.JSONMessage;
import com.dachen.line.stat.entity.UserRequestParam;
import com.dachen.line.stat.service.IAdapterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author tianhong
 * @Description 适配病例库第三方接口（认证授权）
 * @date 2018/4/24 20:29
 * @Copyright (c) 2018, DaChen All Rights Reserved.
 */
@RestController
@RequestMapping("/adapter")
@Api(value = "病例库接入服务")
public class AdapterController {

    @Autowired
    private IAdapterService iAdapterService;

    @ApiOperation(value = "安全认证，获取token和是否有显示菜单权限", httpMethod = "GET", response = JSONMessage.class, notes = "成功返回：{token:'xxx',display:1},注:display的值>0 表示显示菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "communityId", value = "圈子id", required = true, dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/authenticationInfo/{userId}/{communityId}", method = RequestMethod.GET)
    public JSONMessage authenticationInfo(@PathVariable(value = "userId") String userId
            , @PathVariable(value = "communityId") String communityId) {
        return JSONMessage.success(iAdapterService.getAuthenticationInfo(userId,communityId));
    }

    @ApiOperation(value = "修改用户信息", httpMethod = "POST", response = JSONMessage.class, notes = "成功返回：{code:1,success:0 or 1 ,...}")
    @RequestMapping(value = "/updateUser",method = RequestMethod.POST)
    public JSONMessage updateUser(@RequestBody UserRequestParam userRequestParam){
        return JSONMessage.success(iAdapterService.updateUser(userRequestParam));
    }

}
