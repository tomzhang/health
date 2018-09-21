package com.dachen.health.base.constant;

import java.util.EnumSet;

import com.dachen.health.commons.constants.IBaseEnum;

public class EnumUtil {
	public static int getEnumValue(Class clazz,String alias)
	{
		EnumSet set = EnumSet.allOf(clazz);
		IBaseEnum baseEnum;
		for(Object obj:set)
		{
			baseEnum =(IBaseEnum)obj ;
			if(baseEnum.getAlias().equals(alias))
			{
				return baseEnum.getValue();
			}
		}
		return -1;
	}
}
