package com.dachen.health.base.constant;

import com.dachen.health.commons.constants.IBaseEnum;

public enum HospitalTypeEnum implements IBaseEnum{
	
	PUBLIC(1,"公立医院"),
	PRIVATE(2,"私立医院"),
	SPECIALTY(3,"专科医院"),
	COMMUNITY(4,"社康"),
	CLINIC(5,"诊所"),
	NONE(-1,"");
	
	 private int value;
     private String alias;

     public int getValue() {
		return value;
     }


     public String getAlias() {
   	  return alias;
     }


     private HospitalTypeEnum(int index, String  alias) {
         this.value = index;
         this.alias =  alias;
     }

     
     public static HospitalTypeEnum getEnum(String alias)
     {
//    	 HospitalTypeEnum.valueOf(alias);
    	 HospitalTypeEnum[]enums = HospitalTypeEnum.values();
    	 for(HospitalTypeEnum e:enums)
    	 {
    		 if(e.alias==alias)
    		 {
    			 return e;
    		 }
    	 }
    	 return NONE;
     }
     
     public static HospitalTypeEnum getEnum(int value)
     {
    	 HospitalTypeEnum[]enums = HospitalTypeEnum.values();
    	 for(HospitalTypeEnum e:enums)
    	 {
    		 if(e.value==value)
    		 {
    			 return e;
    		 }
    	 }
    	 return NONE;
     }
     
}
