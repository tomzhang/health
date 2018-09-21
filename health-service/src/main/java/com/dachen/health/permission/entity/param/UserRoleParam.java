package com.dachen.health.permission.entity.param;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;

/**
 * @author 钟良
 * @desc
 * @date:2017/12/22 18:16
 * Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class UserRoleParam {
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;
    @ApiModelProperty(value = "角色Ids", required = true)
    private List<String> roleIds;

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
}
