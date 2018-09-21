package com.dachen.health.base.constant;

import com.dachen.health.commons.constants.IBaseEnum;

import java.util.stream.Stream;

public enum EnableStatusEnum implements IBaseEnum{
	ENABLE(1,"启用"),
	DISABLE(2,"禁用");
	
	private int value;
    private String alias;
	private EnableStatusEnum(int value,String alias)
	{
		this.value = value;
        this.alias =  alias;
	}
	@Override
	public int getValue() {
		return value;
	}
	@Override
	public String getAlias() {
		return alias;
	}

	public static String getAlias(int v){
		EnableStatusEnum e = Stream.of(EnableStatusEnum.values()).filter(i -> v == i.value).findAny().orElse(null);
		return e != null ? e.alias : "";
	}
}
