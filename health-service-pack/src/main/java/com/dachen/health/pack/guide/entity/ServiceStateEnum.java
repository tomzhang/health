package com.dachen.health.pack.guide.entity;


public enum ServiceStateEnum {
	NO_START(1,"未开始服务"),
	SERVING(2,"服务中"),
	SERVCE_END(3,"服务结束");
	
	private int value;
	private String alias;
	private ServiceStateEnum(int value,String alias)
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
	
	public static ServiceStateEnum getEnum(int value)
	{
		ServiceStateEnum[]enums = ServiceStateEnum.values();
  	   	for(ServiceStateEnum e:enums)
    	{
  		   if(e.value==value)
  		   {
  			   return e;
  		   }
    	}
  	   	return null;
	}
}
