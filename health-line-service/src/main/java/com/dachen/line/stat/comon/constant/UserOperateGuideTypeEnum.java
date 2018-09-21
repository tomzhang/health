package com.dachen.line.stat.comon.constant;


/**
 * @author weilit
 *
 */
public enum UserOperateGuideTypeEnum {
	电话短信指引(0, "电话短信指引"), 
    上传结果指引(1, "上传结果指引");
    private int index;
    private String title;

    private UserOperateGuideTypeEnum(int index, String title) {
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

    public static  UserOperateGuideTypeEnum  getEnum(int index)
    {
    	UserOperateGuideTypeEnum e=null;
        for(UserOperateGuideTypeEnum e1:UserOperateGuideTypeEnum.values())
            if(e1.index==index){
                e=e1;
                break;
            }
        return e;	
    }
}
