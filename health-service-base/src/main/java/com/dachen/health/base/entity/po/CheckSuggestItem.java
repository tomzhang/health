package com.dachen.health.base.entity.po;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

/**
 * 
 * 检查项单项指标
 * 
 * @author xiaowei
 *
 */
@Entity(value = "b_checkup_item", noClassnameStored = true)
public class CheckSuggestItem implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	private ObjectId id;
	
	@Indexed
	private String checkupId;
	
	/* 中文名*/
	private String name;
	/*缩写或别名*/
	private String alias;
	/*单位*/
	private String unit;
	
	private String regionLeft;
	
	private String regionRight;
	
	/**
	 * 适用性别
	 * 0通用，1男，2女
	 */
	private Integer fitSex;
	
	private Long createTime;
	
	private Long updateTime;

	public String getCheckupId() {
		return checkupId;
	}

	public void setCheckupId(String checkupId) {
		this.checkupId = checkupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getFitSex() {
		return fitSex;
	}

	public void setFitSex(Integer fitSex) {
		this.fitSex = fitSex;
	}

	public String getRegionLeft() {
		return regionLeft;
	}

	public void setRegionLeft(String regionLeft) {
		this.regionLeft = regionLeft;
	}

	public String getRegionRight() {
		return regionRight;
	}

	public void setRegionRight(String regionRight) {
		this.regionRight = regionRight;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	
}
