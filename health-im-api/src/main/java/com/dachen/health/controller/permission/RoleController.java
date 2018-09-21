package com.dachen.health.controller.permission;

import com.dachen.commons.JSONMessage;
import com.dachen.health.permission.entity.param.RoleParam;
import com.dachen.health.permission.entity.po.Role;
import com.dachen.health.permission.service.IRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/19 17:58 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Api(value = "角色管理", description = "角色管理", produces = MediaType.APPLICATION_JSON_VALUE, protocols = "http")
@RestController
@RequestMapping("role")
public class RoleController {
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private IRoleService roleService;

    @ApiOperation(value = "新增角色", notes = "根据对象新增角色", response = JSONMessage.class)
    @ApiImplicitParam(name = "param", value = "角色实体", required = true, dataType = "RoleParam")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public JSONMessage postRole(@RequestBody RoleParam param) {
        roleService.insert(param);
        return JSONMessage.success();
    }

    @ApiOperation(value = "编辑角色", notes = "编辑角色", response = JSONMessage.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "角色Id", dataType = "string", paramType = "path"),
        @ApiImplicitParam(name = "param", value = "角色实体", required = true, dataType = "RoleParam")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public JSONMessage update(@PathVariable String id, @RequestBody RoleParam param) {
        param.setId(id);
        roleService.update(param);
        return JSONMessage.success();
    }

    @ApiOperation(value = "启用角色", notes = "启用角色", response = JSONMessage.class)
    @RequestMapping(value = "enable/{id}", method = RequestMethod.PUT)
    public JSONMessage enableRole(@ApiParam("角色Id") @PathVariable String id) {

        return JSONMessage.success();
    }

    @ApiOperation(value = "禁用角色", notes = "禁用角色", response = JSONMessage.class)
    @RequestMapping(value = "disable/{id}", method = RequestMethod.PUT)
    public JSONMessage disableRole(@ApiParam("图书Id") @PathVariable String id) {
        return JSONMessage.success();
    }

    @ApiOperation(value = "获取角色列表", notes = "获取角色列表", response = Role.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageIndex", value = "pageIndex", dataType = "int", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "pageSize", value = "pageSize", dataType = "int", paramType = "query", defaultValue = "10")
    })
    @RequestMapping(value = "", method = RequestMethod.GET)
    public JSONMessage list(@RequestParam(defaultValue = "0") Integer pageIndex,
        @RequestParam(defaultValue = "10") Integer pageSize) {
        return JSONMessage.success(roleService.getAllPaging(pageIndex, pageSize));
    }

}
