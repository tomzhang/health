package com.dachen.health.pack.order.entity.po;

import org.mongodb.morphia.annotations.Id;

public abstract class BaseInfo {
	@Id	
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
