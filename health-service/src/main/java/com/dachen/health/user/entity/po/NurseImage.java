package com.dachen.health.user.entity.po;

import org.mongodb.morphia.annotations.Entity;

import com.alibaba.fastjson.annotation.JSONField;

@Entity(value = "v_nurse_image")
public class NurseImage {
	@JSONField(serialize = false)
	private Integer userId;// 用户Id
	@JSONField(serialize = false)
	private String imageId;// 图片id 这个对图片服务器返回回来的id
	@JSONField(serialize = false)
	private String imageType="cert";// 图片类型
	@JSONField(serialize = false)
	private int order;// 图片id

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

}