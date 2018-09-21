package com.dachen.health.recommend.constant;

public class DoctorRecommendEnum {
	public enum IsRecommendStatus{
		recommend(1,"推荐"),
		unRecommend(2,"取消推荐");
		
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



		private IsRecommendStatus(int index,String title){
			this.index = index;
			this.title = title;
		}
	}
	
	public enum ShowStatus{
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
		private ShowStatus(int index,String title){
			this.index = index;
			this.title = title;
		}
		
	}
}
