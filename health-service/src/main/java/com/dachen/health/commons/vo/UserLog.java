package com.dachen.health.commons.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "user_log")
public class UserLog {
	
	@Id
	private String id;
	/**用户id**/
	private Integer userId;
	/**操作人id**/
	private Integer operaterId;
	/**操作类型**/
	private Integer operateType;
	/**操作类型**/
	private Long operateTime;
	
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
	public Integer getOperaterId() {
		return operaterId;
	}
	public void setOperaterId(Integer operaterId) {
		this.operaterId = operaterId;
	}
	public Integer getOperateType() {
		return operateType;
	}
	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}
	public Long getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(Long operaterTime) {
		this.operateTime = operaterTime;
	}
	
}
