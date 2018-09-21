package com.dachen.line.stat.comon.constant;

/**
 * 
 * @author weilit
 *
 */
public enum LineServiceTypeEnum {
    Check(1, "检查"), 
    jiaHao(2, "加号"), 
    zhuYuan(3, "住院");

    private int index;
    private String title;

    private LineServiceTypeEnum(int index, String title) {
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

    public static  LineServiceTypeEnum  getEnum(int index)
    {
    	LineServiceTypeEnum e=null;
        for(LineServiceTypeEnum e1:LineServiceTypeEnum.values())
            if(e1.index==index){
                e=e1;
                break;
            }
        return e;	
    }
}
