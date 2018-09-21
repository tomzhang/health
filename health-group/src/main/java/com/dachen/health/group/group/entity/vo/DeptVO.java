package com.dachen.health.group.group.entity.vo;

import java.io.Serializable;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/13 22:38 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class DeptVO implements Serializable {
    private String id;
    private String name;

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
