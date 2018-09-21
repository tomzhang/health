package com.dachen.health.base.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Reference;

/**
 * 医药代表分管医院
 * @author Cwei
 */
@Entity(value = "b_druphospital", noClassnameStored = true)
public class DrupHospitalInfo extends BaseInfo{
	//医院编号
//	@Property("my_integer") 
	@Id
	private ObjectId id;
	
	/**
	 * 医药代表
	 */
//	@Reference
	private String drugPerson;

	/**
	 * 分管医院
	 */
//	@Reference
	private String hospital;
	
	/**
	 * 医药代表（医助）对应的用户ID
	 */
	private Integer userId;
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getDrugPerson() {
		return drugPerson;
	}

	public void setDrugPerson(String drugPerson) {
		this.drugPerson = drugPerson;
	}
}
