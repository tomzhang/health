package com.dachen.health.circle.lightapp.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author sharp
 * @desc 用户 可使用的轻应用
 * @date:2017/6/917:07 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Deprecated
@Entity(value="c_light_app_user", noClassnameStored = true)
public class UserLightApp extends LightAppFilter {

    @Id
    private ObjectId id;

    private Integer userId;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}
