package com.dachen.line.stat.entity.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;


//v_nurse_department


@Entity(value = "v_user_department", noClassnameStored = true)
public class UserDepartment {

	@Id
	private String id;
	private String sourceId;
	private String department;// 科室名称
	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}


	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	

}
