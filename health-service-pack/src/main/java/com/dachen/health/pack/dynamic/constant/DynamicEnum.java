package com.dachen.health.pack.dynamic.constant;


public class DynamicEnum {

	public enum CategoryEnum {
		doctor(0,"医生"),
		dorctr_group(1,"集团");
		private int index;

	    private String title;

	    private CategoryEnum(int index, String title) {
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

	    public static  CategoryEnum  getEnum(int index)
	    {
	    	CategoryEnum e=null;
	        for(CategoryEnum e1:CategoryEnum.values())
	            if(e1.index==index){
	                e=e1;
	                break;
	            }
	        return e;	
	    }
	}
	
	public enum StyleEnum {
		text(0,"文本+图片方式"),
		html_five(1,"富文本h5方式");
		private int index;

	    private String title;

	    private StyleEnum(int index, String title) {
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

	    public static  StyleEnum  getEnum(int index)
	    {
	    	StyleEnum e=null;
	        for(StyleEnum e1:StyleEnum.values())
	            if(e1.index==index){
	                e=e1;
	                break;
	            }
	        return e;	
	    }
	}

	public enum DeleteFlagEnum {
		un_delete(0,"未删除"),
		deleted(1,"已经删除");
		private int index;

	    private String title;

	    private DeleteFlagEnum(int index, String title) {
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

	    public static  DeleteFlagEnum  getEnum(int index)
	    {
	    	DeleteFlagEnum e=null;
	        for(DeleteFlagEnum e1:DeleteFlagEnum.values())
	            if(e1.index==index){
	                e=e1;
	                break;
	            }
	        return e;	
	    }
	}

}
