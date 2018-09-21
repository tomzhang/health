package com.dachen.health.permission.entity.po;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/9 16:47 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Entity(value = "t_permission", noClassnameStored = true)
public class Permission {
    @Id
    private String id;
    @ApiModelProperty(value = "权限名称")
    private String name;
    @ApiModelProperty(value = "权限菜单标识")
    private String webIdentify;
    @ApiModelProperty(value = "权限类型1:功能，2：菜单")
    private Integer type;
    @ApiModelProperty(value = "类型选项id")
    private String pid;

    @Embedded
    @ApiModelProperty(value = "子菜单")
    private List<Permission> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebIdentify() {
        return webIdentify;
    }

    public void setWebIdentify(String webIdentify) {
        this.webIdentify = webIdentify;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<Permission> getChildren() {
        return children;
    }

    public void setChildren(List<Permission> children) {
        this.children = children;
    }
}
