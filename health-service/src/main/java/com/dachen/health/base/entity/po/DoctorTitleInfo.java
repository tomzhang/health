package com.dachen.health.base.entity.po;

import java.util.HashMap;
import java.util.Map;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.health.base.constant.DataStatusEnum;
import com.dachen.health.base.constant.EnableStatusEnum;

/**
 * 
 * @author Cwei
 * 医生职称基础资料
 */
@Entity(value = "b_doctortitle", noClassnameStored = true)
public class DoctorTitleInfo extends BaseInfo{

	@Id
	private String id;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 启用状态---EnableStatusEnum
	 */
	private Integer enableStatus;
	
	/**
	 * 数据状态---DataStatusEnum
	 */
	private Integer dataStatus;
	
	private Long lastUpdatorTime;

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
