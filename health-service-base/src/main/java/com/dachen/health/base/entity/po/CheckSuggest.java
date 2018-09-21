package com.dachen.health.base.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

/**
 * 检查建议
 * 
 * @author Cwei
 */
@Entity(value = "b_checkup", noClassnameStored = true)
@Indexes({ @Index(fields = { @Field(value = "parent"), @Field(value = "name") }) })
public class CheckSuggest {

	@Id
	private String id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 */
	private String include;

	private String except;

	private String unit;

	/**
	 * 价格
	 */
	private String price;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 父Id
	 */
	private String parent;

	/**
	 * 是否使用
	 */
	private boolean enable = true;

	/**
	 * 是否叶子节点
	 */
	private boolean isLeaf = false;

	private int weight = 0;
	
//    "insurance" : "", 
//    "attnum" : "", 
//    "code" : "(Y)YXYX", 
//    "pricetype" : "21", 
//    "medinsurance" : "", 
	
	private String insurance;
	
	private String attnum;
	
	private String code;
	
	private String pricetype;
	
	private String medinsurance;
	
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

	public String getInclude() {
		return include;
	}

	public void setInclude(String include) {
		this.include = include;
	}

	public String getExcept() {
		return except;
	}

	public void setExcept(String except) {
		this.except = except;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getInsurance() {
		return insurance;
	}

	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	public String getAttnum() {
		return attnum;
	}

	public void setAttnum(String attnum) {
		this.attnum = attnum;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPricetype() {
		return pricetype;
	}

	public void setPricetype(String pricetype) {
		this.pricetype = pricetype;
	}

	public String getMedinsurance() {
		return medinsurance;
	}

	public void setMedinsurance(String medinsurance) {
		this.medinsurance = medinsurance;
	}
	
	

}
