package com.dachen.health.commons.entity;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

/**
 * 
 * @author vincent
 *
 */
@Entity(noClassnameStored=true,value="t_telephone_account")
public class TelephoneAccount {

	/**
	 * 电话
	 */
	
	@Id
	private String id;
	
	/**
	 * 电话号码
	 */
	@Indexed(unique=true)
	private String telephone;
	
	/**
	 * 第三方账号
	 */
	private String clientNumber;
	
	/**
	 * 第三方密码
	 */
	private String clientPwd;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getClientPwd() {
		return clientPwd;
	}

	public void setClientPwd(String clientPwd) {
		this.clientPwd = clientPwd;
	}
	
	
}
