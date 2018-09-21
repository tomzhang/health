package com.dachen.health.document.constant;

import com.dachen.util.PropertiesUtil;

public class DocumentEnum {
	public static Integer MAX_TOP = 5;//最多置顶数
	
	
//	public  static final String  DEFAULT_BUCKET= PropertiesUtil.getContextProperty("qiniu.doc.bucket");
    public  static final String  DEFAULT_BUCKET() {
      return PropertiesUtil.getContextProperty("qiniu.doc.bucket");
    }

//	public static final String DEFALUT_DOMAIN = PropertiesUtil.getContextProperty("qiniu.doc.domain");
    public static final String DEFALUT_DOMAIN() {
        return PropertiesUtil.getContextProperty("qiniu.doc.domain");
    }


	public  static final String  DOWN_PATH_WINDOW = "D:\\doc\\"; 
	public static final String  DOWN_PATH_LINUX = "/data/www/resources/doc/";
	public static final boolean isWindows;  
    public static final String path;  
	 static {  
	        if (System.getProperty("os.name") != null && System.getProperty("os.name").toLowerCase().contains("windows")) {  
	            isWindows = true;  
	            path = DocumentEnum.DOWN_PATH_WINDOW;
	        } else {  
	            isWindows = false;  
	            path = DocumentEnum.DOWN_PATH_LINUX ;
	        }  
	    }  
	
	public enum DocumentType{
		adv(1,"患者广告"),
		science(2,"健康科普"),
		care(3,"健康关怀"),
		doctorIndex(4,"医生首页"),
		groupIndex(5,"集团首页");
		
		private int index;
		private String title;
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
		
		private DocumentType(int index,String title){
			this.index = index;
			this.title = title;
		}
	}
	
	public enum DocumentShowStatus{
		show(1,"显示"),
		hide(2,"隐藏");
		private int index;
		private String title;
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
		
		private DocumentShowStatus(int index,String title){
			this.index = index;
			this.title = title;
		}
	}
	
	public enum DocumentEnabledStatus{
		enabled(1,"未删除"),
		disabled(2,"已删除");
		private int index;
		private String title;
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
		
		private DocumentEnabledStatus(int index,String title){
			this.index = index;
			this.title = title;
		}
	}
	
	public enum DocumentTopStatus{
		top(1,"已置顶"),
		unTop(2,"未置顶");
		
		private int index;
		private String title;
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
		
		private DocumentTopStatus(int index,String title){
			this.index = index;
			this.title = title;
		}
	}
	
	public enum RecommendType {
		group(1,"集团类型"),
		doctor(2,"医生类型");
		
		private int index;
		private String type;
		
		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		private RecommendType(int index, String type) {
			this.index = index;
			this.type = type;
		}
	}

}
