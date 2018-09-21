package com.dachen.line.stat.comon.constant;

/**
 * 
 * @author weilit
 *
 */
public enum LineServiceProductTypeEnum {
    Check(1, "检查直通车"), 
    JiuYi(2, "就医"), 
    VIP就医(3, "VIP就医");

    private int index;
    private String title;

    private LineServiceProductTypeEnum(int index, String title) {
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

    public static  LineServiceProductTypeEnum  getEnum(int index)
    {
    	LineServiceProductTypeEnum e=null;
        for(LineServiceProductTypeEnum e1:LineServiceProductTypeEnum.values())
            if(e1.index==index){
                e=e1;
                break;
            }
        return e;	
    }
}
