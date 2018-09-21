package com.dachen.health.base.entity.po;

import java.util.HashMap;
import java.util.Map;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import com.dachen.health.base.constant.DataStatusEnum;
import com.dachen.health.base.constant.EnableStatusEnum;

/**
 * 医院-科室基本信息
 * @author Cwei
 */
@Entity(value = "b_hospitaldept", noClassnameStored = true)
@Indexes({ @Index(fields = { @Field(value = "name") }) })
public class HospitalDeptInfo extends BaseInfo{
	@Id
	private String id;
	
	/**
	 * 上级科室
	 */
//	@Reference
//	private HospitalDeptInfo parent;
	private String parent;
	
	private String name;
	
	/**
	 * 启用状态---EnableStatusEnum
	 */
	private Integer enableStatus;
	
	/**
	 * 数据状态---DataStatusEnum
	 */
	private Integer dataStatus;
	
	/**
	 * 权重
	 */
	private Integer weight;
	
	private Long lastUpdatorTime;
	
	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getEnableStatus() {
		return enableStatus;
	}

	public void setEnableStatus(Integer enableStatus) {
		this.enableStatus = enableStatus;
	}

	public Integer getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(Integer dataStatus) {
		this.dataStatus = dataStatus;
	}

	public Long getLastUpdatorTime() {
		return lastUpdatorTime;
	}

	public void setLastUpdatorTime(Long lastUpdatorTime) {
		this.lastUpdatorTime = lastUpdatorTime;
	}

	@Override
	public Map<String,Class> getEnumMapping()
	{
		Map<String,Class> enumFileds = new HashMap<String,Class>();
		enumFileds.put("enableStatus", EnableStatusEnum.class);
		enumFileds.put("dataStatus", DataStatusEnum.class);
		return enumFileds;
	}
}
