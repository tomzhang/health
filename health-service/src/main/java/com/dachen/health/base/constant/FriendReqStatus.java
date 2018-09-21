package com.dachen.health.base.constant;

import com.dachen.health.commons.constants.IBaseEnum;

public enum FriendReqStatus implements IBaseEnum{
	WAIT_ACCEPT   (1,"等待验证"),
	ACCEPTED    (2,"已接受"),
	REFUSED    (3,"已拒绝");
	 private int value;
     private String alias;

     public int getValue() {
		return value;
     }

     public String getAlias() {
   	  	return alias;
     }

	 private FriendReqStatus(int value,String alias)
	 {
		this.alias = alias;
		this.value = value;
	 }
}
