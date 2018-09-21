package com.dachen.line.stat.comon.constant;


/**
 * 0 未设置   1 打开 2 关闭

 * @author weilit
 *
 */
public enum NurseServiceSetEnum {
    open(1, "打开"), 
    closed(2, "关闭"),
    un_set(0, "未设置");
    private int index;
    private String title;

    private NurseServiceSetEnum(int index, String title) {
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

    public static  NurseServiceSetEnum  getEnum(int index)
    {
    	NurseServiceSetEnum e=null;
        for(NurseServiceSetEnum e1:NurseServiceSetEnum.values())
            if(e1.index==index){
                e=e1;
                break;
            }
        return e;	
    }
}
