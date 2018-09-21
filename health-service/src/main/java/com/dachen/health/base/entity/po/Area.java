package com.dachen.health.base.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

/**
 * ProjectName： health-service<br>
 * ClassName： Area<br>
 * Description： 省市县地区分类表<br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
@Entity(value = "b_area", noClassnameStored = true)
public class Area {

    @Id
    private Integer id;

    /* 编码 */
    @Indexed
    private String code;

    /* 名称 */
    private String name;

    /* 父编码 */
    @Indexed
    private String pcode;
    
    /* 最后写操作时间     */
    private Long lastUpdatorTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

	public Long getLastUpdatorTime() {
		return lastUpdatorTime;
	}

	public void setLastUpdatorTime(Long lastUpdatorTime) {
		this.lastUpdatorTime = lastUpdatorTime;
	}
    
}
