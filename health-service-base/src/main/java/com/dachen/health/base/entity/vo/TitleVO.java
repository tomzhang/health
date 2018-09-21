package com.dachen.health.base.entity.vo;

import org.mongodb.morphia.annotations.Property;

/**
 * ProjectName： health-service<br>
 * ClassName： TitleVO<br>
 * Description：职称VO <br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
public class TitleVO {

    @Property("_id")
    private String id;

    private String name;
    
    private Integer rank;

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

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

}
