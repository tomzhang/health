package com.dachen.health.base.constant;

import com.dachen.health.commons.constants.IBaseEnum;

public enum JobStatus implements IBaseEnum{
	ON_JOB    (1,"在职"),
	NOT_ON_JOB(0,"不在职");
	
	 private int value;
     private String alias;

     public int getValue() {
		return value;
     }

     public String getAlias() {
   	  	return alias;
     }

	 private JobStatus(int value,String alias)
	 {
		this.alias = alias;
		this.value = value;
	 }
}
