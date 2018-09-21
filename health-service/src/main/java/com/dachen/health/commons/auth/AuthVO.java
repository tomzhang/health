package com.dachen.health.commons.auth;

import org.bson.types.ObjectId;

public class AuthVO {

	private ObjectId id;
	
	/**
	 * 手机号、微信号；可扩展：QQ号etc
	 */
	private String accountNum;//账号

	/**
	 * @see AccountTypeEnum
	 * 数据库存储 AccountType.QQ.name() 属性}
	 */
	private String accountType;

	/**
	 * 当accountType为"tel"时才用到此字段
	 */
	private String password;
	
	/**
	 * 为业务服务提供的用户ID
	 */
	private Integer userId;
	
	/**
	 * 记录业务服务用户的类型
	 */
	private Integer userType;
	
	/**
	 * 设备号ID
	 */
	private String deviceId;
	
	/**
	 * 创建时间
	 */
	private Long createTime;
	
	
	private String access_token;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(String accountNum) {
		this.accountNum = accountNum;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	
	public enum AccountTypeEnum {
		tel, wechat;
	}

}
