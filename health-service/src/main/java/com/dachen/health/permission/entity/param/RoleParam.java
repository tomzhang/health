package com.dachen.health.permission.entity.param;

import com.dachen.health.permission.entity.po.Permission;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/13 15:41 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class RoleParam {

    @ApiModelProperty(value = "ID", hidden = true)
    private String id;

    @ApiModelProperty(value = "角色名称", required = true)
    private String roleName;

    @ApiModelProperty(value = "权限列表")
    private List<Permission> permissionList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(
        List<Permission> permissionList) {
        this.permissionList = permissionList;
    }
}
