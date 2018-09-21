package com.dachen.health.base.entity.vo;

import org.mongodb.morphia.annotations.Property;

/**
 * ClassName： GroupVO<br>
 * Description：集团圈子VO <br>
 * 
 * @author 李敏
 * @crateTime 2017年6月8日18:39:34
 * @version 1.0.0
 */
public class GroupVO {

    @Property("_id")
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
