package com.dachen.health.base.constant;

import com.dachen.health.commons.constants.IBaseEnum;

public enum DataStatusEnum implements IBaseEnum{
	normal(1,"正常"),	
	frozen(2,"冻结");
	
	private int value;
    private String alias;
	private DataStatusEnum(int value,String alias)
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
	
	
}
