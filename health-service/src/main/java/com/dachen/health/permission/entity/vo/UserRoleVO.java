package com.dachen.health.permission.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;

/**
 * @author 钟良
 * @desc
 * @date:2017/12/22 18:47
 * Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class UserRoleVO {
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;
    @ApiModelProperty(value = "角色Ids", required = true)
    private List<String> roleIds;
    @ApiModelProperty(value = "权限Ids")
    private List<String> permissionIds;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    public List<String> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<String> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
