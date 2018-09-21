package com.dachen.health.circle.lightapp.entity;

/**
 * @author sharp
 * @desc
 * @date:2017/6/1216:22 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Deprecated
public class LightAppFilter {

    private String lightAppId;

    private Long createTime;

    private Long lastUpdateTime;

    public String getLightAppId() {
        return lightAppId;
    }

    public void setLightAppId(String lightAppId) {
        this.lightAppId = lightAppId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
