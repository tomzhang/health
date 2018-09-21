package com.dachen.health.permission.entity.po;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/12 15:35 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Entity(value = "t_role", noClassnameStored = true)
public class Role {
    @Id
    private String id;

    @ApiModelProperty(value = "角色名称",required = true)
    private String roleName;
    @ApiModelProperty(value = "状态【0禁用/1启用】",required = true)
    /**
     * @see com.dachen.health.permission.enums.PermissionEnum.RoleStatus
     */
    private Integer status;

    @Embedded
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(
        List<Permission> permissionList) {
        this.permissionList = permissionList;
    }
}
