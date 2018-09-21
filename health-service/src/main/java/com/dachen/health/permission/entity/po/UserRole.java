package com.dachen.health.permission.entity.po;

import io.swagger.annotations.ApiModelProperty;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/12 15:41 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Entity(value = "t_user_role", noClassnameStored = true)
public class UserRole {
    @Id
    private String id;
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;
    @ApiModelProperty(value = "角色id", required = true)
    private String roleId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
