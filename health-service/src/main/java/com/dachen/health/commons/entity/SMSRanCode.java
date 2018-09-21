package com.dachen.health.commons.entity;

import java.util.Date;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
public class SMSRanCode {

	@Id
	private String id;

	private String code;

	private Date createTime;

	public SMSRanCode() {
	}
	public SMSRanCode(String code) {
		this(code, new Date());
	}

	public SMSRanCode(String code, Date createTime) {
		this.code = code;
		this.createTime = createTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "SMSRanCode [id=" + id + ", code=" + code + ", createTime="
				+ createTime + "]";
	}

}
