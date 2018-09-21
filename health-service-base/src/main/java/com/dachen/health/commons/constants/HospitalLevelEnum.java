package com.dachen.health.commons.constants;

public enum HospitalLevelEnum implements IBaseEnum{
	   Community(0,"社区医院"),
	   One1(1, "一级丙等"), 
	   One2(2, "一级乙等"),
	   One3(3, "一级甲等"),
	   Two1(4, "二级丙等"),
	   Two2(5, "二级乙等"),
	   Two3(6, "二级甲等"),
	   Three1(7, "三级丙等"),
	   Three2(8, "三级乙等"),
	   Three3(9, "三级甲等"),
	   Prihos(10,"民营医院"),
	   NONE(-1,"");

		
	   private int value;
	   private String alias;

       private HospitalLevelEnum(int value, String alias) {
            this.value = value;
            this.alias = alias;
       }
       public static HospitalLevelEnum getEnum(String alias)
       {
//      	 HospitalTypeEnum.valueOf(alias);
    	   HospitalLevelEnum[]enums = HospitalLevelEnum.values();
    	   for(HospitalLevelEnum e:enums)
      	 	{
    		   if(e.alias==alias)
    		   {
    			   return e;
      		 	}
      	 	}
      	 return NONE;
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
