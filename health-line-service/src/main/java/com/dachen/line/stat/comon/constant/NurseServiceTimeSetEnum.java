package com.dachen.line.stat.comon.constant;


/**
 * @author weilit
 *
 */
public enum NurseServiceTimeSetEnum {
    YES(1, "可以接单"), 
    NO(0, "不可以接单");
    private int index;
    private String title;

    private NurseServiceTimeSetEnum(int index, String title) {
        this.index = index;
        this.title = title;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static  NurseServiceTimeSetEnum  getEnum(int index)
    {
    	NurseServiceTimeSetEnum e=null;
        for(NurseServiceTimeSetEnum e1:NurseServiceTimeSetEnum.values())
            if(e1.index==index){
                e=e1;
                break;
            }
        return e;	
    }
}
