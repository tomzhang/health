package com.dachen.health.base.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.health.commons.constants.EvaluationItemEnum;

@Entity(value = "b_evaluation_item", noClassnameStored = true)
public class EvaluationItem {

	@Id
	private String id;
	
	private String name;

	/**
	 * 评价项级别：1好评、2一般、3差评
	 * @see EvaluationItemEnum
	 */
	private Integer level;
	
	/**
	 * 与PackEnum.PackType保持一致
	 * 100为通用评价项
	 */
	private Integer packType;

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

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getPackType() {
		return packType;
	}

	public void setPackType(Integer packType) {
		this.packType = packType;
	}
}
