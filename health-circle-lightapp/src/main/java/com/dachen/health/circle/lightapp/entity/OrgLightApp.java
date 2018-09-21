package com.dachen.health.circle.lightapp.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author sharp
 * @desc 圈子/科室 可使用的轻应用
 * @date:2017/6/917:07 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Deprecated
@Entity(value="c_light_app_org", noClassnameStored = true)
public class OrgLightApp extends LightAppFilter {

    @Id
    private ObjectId id;

    /**
     * 组织类型（科室、圈子、平台）
     * @see OrgType
     */
    private String orgType;

    /**
     * 组织ID（科室ID、圈子ID、平台）
     */
    private String orgId;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public enum OrgType {
        group, departments, platform
    }

}
