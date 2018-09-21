package com.dachen.health.auto.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by liming on 2016/11/10.
 * 疑似病症实体类
 */
@Entity(value = "b_auto_disease",noClassnameStored = true)
public class Disease {
    @Id
    private String id;
    /**
     * 疑似病症编码
     */
    private String code;

    /**
     * 疑似病症描述
     */
    private String content;
    /**
     * 拼音
     */
    private String name;

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


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
