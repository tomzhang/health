package com.dachen.health.auto.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by liming on 2016/11/10.
 * 病症实体类
 */
@Entity(value = "b_auto_symptoms",noClassnameStored=true)
public class Symptoms {
    @Id
    private String id;
    /**
     * 病症code
     */
    private String code;
    /**
     * 身体部位code
     */
    private String bodyCode;
    /**
     * 病症名称
     */
    private String name;
    /**
     * 性别 0 公共属性 1 男 2 女
     */
    private String sex;

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

    public String getBodyCode() {
        return bodyCode;
    }

    public void setBodyCode(String bodyCode) {
        this.bodyCode = bodyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
