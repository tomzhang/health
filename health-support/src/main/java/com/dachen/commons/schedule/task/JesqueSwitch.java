package com.dachen.commons.schedule.task;

import com.dachen.util.StringUtils;

public abstract class JesqueSwitch {
public static final String BEAN_ID = "JesqueSwitch";
	
	public abstract String getJesqueSpace();
	
	
	boolean isOpen(){
		if(StringUtils.isEmpty(getJesqueSpace())){
			return false;
		}
		return true;
	}
}
