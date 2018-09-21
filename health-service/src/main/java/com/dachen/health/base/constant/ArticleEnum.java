package com.dachen.health.base.constant;

public class ArticleEnum {
	
	public static int TOP_SIZE = 5;
	
	public enum CreaterType{
		system(1,"患教中心"),
		group(2,"医生集团"),
		doctor(3,"医生");
		
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
		
		private CreaterType(int index, String title){
			this.index = index;
			this.title = title;
		}
	}
	
	public enum CollecterType{
		system(1,"患教中心"),
		group(2,"医生集团"),
		doctor(3,"医生");
		
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
		
		private CollecterType(int index,String title){
			this.index = index;
			this.title = title;
		}
	}
	
	public enum CollectType{
		collect(1,"收藏"),
		create(2,"创建");
		
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
		
		private CollectType(int index,String title){
			this.index = index;
			this.title = title;
		}
	}

}
