package com.dachen.health.pack.guide.entity;

public enum PayStateEnum {
	WAIT(0,"等待预约"),
	NOT_PAY(1,"未支付"),
	HAS_PAY(2,"已支付");
	private int value;
	private String alias;
	private PayStateEnum(int value,String alias)
	{
		this.alias = alias;
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	public String getAlias() {
		return alias;
	}
	
	public static PayStateEnum getEnum(int value)
	{
		PayStateEnum[]enums = PayStateEnum.values();
  	   	for(PayStateEnum e:enums)
    	{
  		   if(e.value==value)
  		   {
  			   return e;
  		   }
    	}
  	   	return null;
	}
}
