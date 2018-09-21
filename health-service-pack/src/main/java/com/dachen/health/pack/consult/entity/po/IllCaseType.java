package com.dachen.health.pack.consult.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.util.JSONUtil;

@Entity(value = "t_ill_case_type",noClassnameStored = true)
public class IllCaseType {

	@Id
	private String id;
	
	private String typeName;
	
	/*排列顺序从小到大*/
	private Double typeOrder;
	
	/*{1: 基础病史分类，2：就医资料分类}*/
	private Integer categoryId ;
	
	/*{1:基础数据类型（初始化数据），2：医生扩展数据类型}*/
	private Integer dataType;
	
	private boolean required;

	/**病历改造，是否医生可以访问**/
	private Boolean forDoctor;
	/**病历改造，是否患者可以访问**/
	private Boolean forPatient;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getTypeName() {
		return typeName;
	}


	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}


	public Double getTypeOrder() {
		return typeOrder;
	}


	public void setTypeOrder(Double typeOrder) {
		this.typeOrder = typeOrder;
	}


	public Integer getCategoryId() {
		return categoryId;
	}


	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Boolean getForDoctor() {
		return forDoctor;
	}

	public void setForDoctor(Boolean forDoctor) {
		this.forDoctor = forDoctor;
	}

	public Boolean getForPatient() {
		return forPatient;
	}

	public void setForPatient(Boolean forPatient) {
		this.forPatient = forPatient;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

}
