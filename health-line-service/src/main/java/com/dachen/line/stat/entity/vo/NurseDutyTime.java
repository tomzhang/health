package com.dachen.line.stat.entity.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
@Entity(value="v_nurse_dutytime", noClassnameStored = true)
public class NurseDutyTime {
  
	@Id
	private String id;
	private Integer userId;
	private String time;// 时间 yyyy-MM-dd
	private Integer status;// 0 不 可以接单  1 可以接单
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}


	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	

}
