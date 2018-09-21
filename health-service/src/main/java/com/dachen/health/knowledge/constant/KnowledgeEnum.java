package com.dachen.health.knowledge.constant;

public class KnowledgeEnum {
	public static final String OTHER_CATEGORY = "其它"; 
	public enum CreaterType{
		group(2,"集团"),
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


		private CreaterType(int index, String title) {
			this.index = index;
			this.title = title;
		}
	}
	
	public enum ShowStatus{
		hide(0,"不显示"),
		show(1,"显示");
		
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
		private int index;
		private String title;
		private ShowStatus(int index, String title) {
			this.index = index;
			this.title = title;
		}
	}
}
