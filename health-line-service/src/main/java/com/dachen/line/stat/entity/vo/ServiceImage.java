package com.dachen.line.stat.entity.vo;

import org.mongodb.morphia.annotations.Entity;

/**
 * 统一图片
 * @author weilit
 *
 */
@Entity(value = "v_service_image", noClassnameStored = true)
public class ServiceImage {
	private String id;
	private String sourceId;
	private String imageId;
	private String remarks;
	private int type ;  //图片类型
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
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
