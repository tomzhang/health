package com.dachen.health.commons.entity;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.util.PropertiesUtil;
import com.dachen.util.StringUtil;

/**
 * 存储全国银行卡代码，来识别用户输入的卡号是属于哪个银行
 * @author xiepei
 *
 */
@Entity(noClassnameStored = true, value = "t_bank_data")
public class BankBinNoData {
	
	@Id
	private String id;
	
	
	private String bankName;//发卡银行名
	
	private String bankCode;//发卡行代码
	
	private String bankCateName;//卡种名称
	
	private String bankNoType;//银行卡类型
	
	private Integer bankNoLength;//卡号长度
	
	private String bankBinNo;//起始代码
	
	private String bankIoc;//银行图标
	
	

	public String getBankIoc() {
		if(StringUtil.isBlank(bankIoc)){
			return bankIoc;
		}
		StringBuffer sb=new StringBuffer();
		sb.append(PropertiesUtil.getContextProperty("fileserver.protocol")).append("://");
		sb.append(PropertiesUtil.getContextProperty("fileserver.host")).append(":").append(PropertiesUtil.getContextProperty("fileserver.port"));
		return sb.toString()+bankIoc;
	}

	public void setBankIoc(String bankIoc) {
		this.bankIoc = bankIoc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankCateName() {
		return bankCateName;
	}

	public void setBankCateName(String bankCateName) {
		this.bankCateName = bankCateName;
	}

	public String getBankNoType() {
		return bankNoType;
	}

	public void setBankNoType(String bankNoType) {
		this.bankNoType = bankNoType;
	}

	public Integer getBankNoLength() {
		return bankNoLength;
	}

	public void setBankNoLength(Integer bankNoLength) {
		this.bankNoLength = bankNoLength;
	}

	public String getBankBinNo() {
		return bankBinNo;
	}

	public void setBankBinNo(String bankBinNo) {
		this.bankBinNo = bankBinNo;
	}
}
