package com.dachen.health.base.constant;

import com.dachen.health.commons.constants.IBaseEnum;

public enum GenderEnum implements IBaseEnum{
	  MAN(1,"男"),
	  WOMAN(2, "女"),
	  NONE(-1, "未知");
	  
	  private int value;
      private String alias;

      public int getValue() {
		return value;
      }


      public String getAlias() {
    	  return alias;
      }


      private GenderEnum(int value, String  alias) {
          this.value = value;
          this.alias =  alias;
      }

      
      public static GenderEnum getEnum(String alias)
      {
    	  if(alias.equals(MAN.alias)) 
    	  {
    		  return MAN;
    	  }
    	  else if(alias.equals(WOMAN.alias)) 
    	  {
    		  return WOMAN;
    	  }
    	  return NONE;
      }
      
      public static GenderEnum getEnum(int value)
      {
    	  if(value==MAN.value) 
    	  {
    		  return MAN;
    	  }
    	  else if(value==WOMAN.value) 
    	  {
    		  return WOMAN;
    	  }
    	  return null;
      }
      
}
