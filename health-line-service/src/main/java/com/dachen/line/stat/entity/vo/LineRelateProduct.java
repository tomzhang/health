package com.dachen.line.stat.entity.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;


@Entity(value = "v_line_relate_product", noClassnameStored = true)
public class LineRelateProduct {
	@Id
	private String id;
	private String sourceId;//产品服务ID
	private String lsId;//检查项id  检查项id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLsId() {
		return lsId;
	}
	public void setLsId(String lsId) {
		this.lsId = lsId;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
}
