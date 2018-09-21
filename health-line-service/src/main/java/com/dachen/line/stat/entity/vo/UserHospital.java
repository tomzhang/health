package com.dachen.line.stat.entity.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value="v_user_hospital", noClassnameStored = true)
public class UserHospital {

	@Id	
	private String id;
	private String sourceId;  //这里可以是患者服务id 或者护士id
	private String hospitalId;// 医院的ID
	private String hospital;// 医院名称
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}
	
}
