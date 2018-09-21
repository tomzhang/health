package com.dachen.health.commons.vo;

public class WeChatUserInfo {

	/**
	 * 通过开放平台账号获取到的openid
	 */
	private String openid;
	
	/**
	 * 通过公众号账号获取到的openid
	 */
	private String mpOpenid;
	
	private String nickname;
	
//	private String sex;
	
	private String province;
	
	private String city;
	
	private String country;
	
//	private String headimgurl;
	
//	private String[] privilege;
	
	private String unionid;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getMpOpenid() {
		return mpOpenid;
	}

	public void setMpOpenid(String mpOpenid) {
		this.mpOpenid = mpOpenid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	

}
