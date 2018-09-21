package com.dachen.health.file.constant;

/**
 * vpan文件相关的 枚举常量
 *@author wangqiao
 *@date 2016年1月13日
 *
 */
public class VpanFileEnum {

	/**
	 * 文件 分类 
	 *@author wangqiao
	 *@date 2016年1月13日
	 *
	 */
	public enum VpanFileType{
		document("document","文档"),
		picture("picture","图片"),
		video("video","视频"),
		music("music","音乐"),
		other("other","其它");
		
		private String value;
		private String title;
		
		public String getValue() {
			return value;
		}
		public void setIndex(String value) {
			this.value = value;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		
		private VpanFileType(String value,String title){
			this.value = value;
			this.title = title;
		}
	}
	
	/**
	 * 文件查询 方式
	 *@author wangqiao
	 *@date 2016年1月13日
	 *
	 */
	public enum VpanFileMode{
		upload("upload","我上传的文件"),
		receive("receive","我接收的文件"),
		drug("drug","品种文件");
		private String value;
		private String title;
		
		public String getValue() {
			return value;
		}
		public void setIndex(String value) {
			this.value = value;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		
		private VpanFileMode(String value,String title){
			this.value = value;
			this.title = title;
		}
	}
	
	/**
	 * 文件的bucket类型  公有，私有
	 *@author wangqiao
	 *@date 2016年1月13日
	 *
	 */
	public enum VpanFileBucketType{
		pub("public","公有"),
		pri("private","私有");
		
		private String value;
		private String title;
		
		public String getValue() {
			return value;
		}
		public void setIndex(String value) {
			this.value = value;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		
		private VpanFileBucketType(String value,String title){
			this.value = value;
			this.title = title;
		}
	}
	
	/**
	 * 文件的状态   C=正常，  D=已删除
	 *@author wangqiao
	 *@date 2016年1月13日
	 *
	 */
	public enum VpanFileStatus{
		common("C","正常"),
		delete("D","已删除");
		
		private String value;
		private String title;
		
		public String getValue() {
			return value;
		}
		public void setIndex(String value) {
			this.value = value;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		
		private VpanFileStatus(String value,String title){
			this.value = value;
			this.title = title;
		}
	}
	
}

