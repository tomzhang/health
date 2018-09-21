package com.dachen.pub.model;

public enum PubTypeEnum {
//	PUB_CUSTOMER_DOCTOR("pub_001","玄关健康医生客服"),
//	PUB_CUSTOMER_PATIENT("pub_002","玄关健康客服"),
	PUB_CUSTOMER("pub_customer","玄关健康客服"),
	PUB_GROUP_PATIENT("pub_1","患者之声"),
	PUB_GROUP_DOCTOR("pub_3","集团动态"),
	PUB_DOCTOR("pub_doc","医生公公号"),
	PUB_NEWS("pub_news","健康动态");
	//PUB_HOSPITAL("pub_hospital","医院公众号");
	
	private String alias;
	private String value;
	private PubTypeEnum(String value,String alias)
	{
		this.value = value;
		this.alias = alias;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
