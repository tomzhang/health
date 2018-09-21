package com.dachen.health.controller.permission;

import com.dachen.commons.JSONMessage;
import com.dachen.health.permission.entity.po.Permission;
import com.dachen.health.permission.service.IPermissionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/13 17:32 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@RestController
@RequestMapping("permission")
public class PermissionController {

    @Autowired
    private IPermissionService permissionService;

    @ApiOperation(value = "获取权限列表", notes = "获取权限列表", response = Permission.class)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public JSONMessage list() {
        return JSONMessage.success(permissionService.getAll());
    }
}
