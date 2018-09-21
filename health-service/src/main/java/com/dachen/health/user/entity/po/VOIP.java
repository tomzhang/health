package com.dachen.health.user.entity.po;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * ProjectName： health-service<br>
 * ClassName： VOIP<br>
 * Description：voip帐号信息 <br>
 * @author limiaomiao
 * @crateTime 2015年7月27日
 * @version 1.0.0
 */
public class VOIP {

	/**
	 * 创建时间
	 */
	private String dateCreated;
	
	/**
	 * 子帐号token
	 */
	@JSONField(serialize=false)
	private String subToken;
	
	/**
	 * voip密码
	 */
	@JSONField(serialize=false)
	private String voipPwd;
	/**
	 * 子帐号
	 */
	private String subAccountSid;
	/**
	 * voip帐号
	 */
	private String voipAccount;
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getSubToken() {
		return subToken;
	}
	public void setSubToken(String subToken) {
		this.subToken = subToken;
	}
	public String getVoipPwd() {
		return voipPwd;
	}
	public void setVoipPwd(String voipPwd) {
		this.voipPwd = voipPwd;
	}
	public String getSubAccountSid() {
		return subAccountSid;
	}
	public void setSubAccountSid(String subAccountSid) {
		this.subAccountSid = subAccountSid;
	}
	public String getVoipAccount() {
		return voipAccount;
	}
	public void setVoipAccount(String voipAccount) {
		this.voipAccount = voipAccount;
	}
	
}
