package com.dachen.health.base.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * ProjectName： health-service<br>
 * ClassName： HospitalLevelVo<br>
 * Description：医院级别VO <br>
 * 
 * @author 傅永德
 * @crateTime 2016年5月17日
 * @version 1.0.0
 */
@Entity(value = "b_hospital_level", noClassnameStored = true)
public class HospitalLevelPo {
	@Id
	private String id;
	private String level;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
}
