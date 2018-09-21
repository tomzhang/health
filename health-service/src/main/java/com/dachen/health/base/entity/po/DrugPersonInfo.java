package com.dachen.health.base.entity.po;

import java.util.HashMap;
import java.util.Map;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import com.dachen.health.base.constant.GenderEnum;
import com.dachen.health.base.constant.JobStatus;

/**
 * 医药代表---区域人员表
 * @author Cwei
 *
 */
@Entity(value = "b_drugperson", noClassnameStored = true)
@Indexes({ @Index(fields = { @Field(value = "name") }) })
public class DrugPersonInfo extends BaseInfo{
	//医院编号
	@Id
	private String id;
	
	/**
	 * 所属公司
	 */
	private String company;
	/**
	 * 大区
	 */
	private String largeArea; 
	
	/**
	 * 省区
	 */
	private String provinceArea;
	
	/**
	 * 地区
	 */
	private String cityArea;
	
	/**
	 * 姓名
	 */
	private String name;
	
	/**
	 * 电话
	 */
	private String telephone;
	
	/**
	 * 性别----->GenderEnum
	 */
	private Integer gender;
	
	/**
	 * 岗位
	 */
	private String job;
	
	/**
	 * 状态：在职/不在职（JobStatus）
	 */
	private Integer status;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLargeArea() {
		return largeArea;
	}

	public void setLargeArea(String largeArea) {
		this.largeArea = largeArea;
	}

	public String getProvinceArea() {
		return provinceArea;
	}

	public void setProvinceArea(String provinceArea) {
		this.provinceArea = provinceArea;
	}

	public String getCityArea() {
		return cityArea;
	}

	public void setCityArea(String cityArea) {
		this.cityArea = cityArea;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
	
	@Override
	public Map<String,Class> getEnumMapping()
	{
		Map<String,Class> enumFileds = new HashMap<String,Class>();
		enumFileds.put("status", JobStatus.class);
		enumFileds.put("gender", GenderEnum.class);
		return enumFileds;
	}
	
}
