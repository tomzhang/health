package com.dachen.health.base.entity.vo;

import org.mongodb.morphia.annotations.Property;

/**
 * 医联体vo
 * Created By lim
 * Date: 2017/6/6
 * Time: 10:00
 */
public class GroupUnionVo {
    @Property("_id")
    private String id;

    private String name;

    private Integer statusId;

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

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
}
