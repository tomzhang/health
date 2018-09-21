package com.dachen.health.auto.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by liming on 2016/11/10.
 * 身体部位实体类
 */
@Entity(value = "b_auto_body",noClassnameStored=true)
public class Body {
    @Id
    private String id;
    /**
     * 身体部位code
     */
    private String code;
    /**
     * 部位名称
     */
    private String name;
    /**
     * 是否生效
     */
    private boolean enable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
