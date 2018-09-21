package com.dachen.pub.util;

public enum UserRoleEnum {
	CREATOR(1, "创建者"), 
    ADMIN(2, "管理员"), 
    CUSTOMER(3, "客服"),
    SUBSCRIBE(4, "订阅者"),
	SPEAKER(5, "发言人");

    private int value;

    private String name;

    private UserRoleEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static UserRoleEnum getEnum(int value)
    {
    	UserRoleEnum e=null;
        for(UserRoleEnum e1:UserRoleEnum.values())
            if(e1.value==value){
                e=e1;
                break;
            }
        return e;	
    }

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
