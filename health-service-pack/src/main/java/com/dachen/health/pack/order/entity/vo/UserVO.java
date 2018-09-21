package com.dachen.health.pack.order.entity.vo;

public class UserVO {
	
	private Integer userId;
	
	private String userName;
	
	private String headPriPath;
	
	//add by  liwei   20160217 订单中增加患者常住地
	private String area;

	//add by CQY 
	private String telephone;
	
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHeadPriPath() {
		return headPriPath;
	}

	public void setHeadPriPath(String headPriPath) {
		this.headPriPath = headPriPath;
	}
	
}
