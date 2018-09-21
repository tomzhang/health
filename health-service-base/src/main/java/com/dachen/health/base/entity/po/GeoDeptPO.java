package com.dachen.health.base.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

/**
 * 科室（附近医生使用）
 * 
 * @author 钟良
 *
 */
@Entity(value = "b_hospital_geo_dept", noClassnameStored = true)
public class GeoDeptPO {
	@Property("_id")
    private String id;

    private String deptId;//关联b_hospitaldept
    
    private Integer enableStatus;
    
    private Integer weight;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public Integer getEnableStatus() {
		return enableStatus;
	}

	public void setEnableStatus(Integer enableStatus) {
		this.enableStatus = enableStatus;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

}
