package com.dachen.health.base.constant;

public class DownTaskEnum {
	
	public  static final String  DOWN_PATH_WINDOW = "D:\\tel\\"; 
	public static final String  DOWN_PATH_LINUX = "/data/www/resources/tel/"; 
	
	public enum DownStatus{
		recordAdd(64,"已添加到任务表"),
		recordDownFail(66,"录音下载失败"),
		recordDownSuccess(67,"录音下载成功"),
		recordUploadFail(68,"录音上传失败"),
		recordUploadSuccess(69,"录音上传成功");
		
		private Integer index;
		private String value;
		public Integer getIndex() {
			return index;
		}
		public void setIndex(Integer index) {
			this.index = index;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
		private DownStatus(Integer index, String value){
			this.index = index;
			this.value = value;
		}
	}
	
	public enum TableToClass{
		
		confRecord("A","CallRecord","t_call_record"),
		callRecord("B","CallResult","t_call_result");
		
		private String busessType;
		private String className;
		private String tableName;
		
		
		public String getBusessType() {
			return busessType;
		}


		public void setBusessType(String busessType) {
			this.busessType = busessType;
		}


		public String getTableName() {
			return tableName;
		}


		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}


		private TableToClass(String busessType,String tableName,String className){
			this.busessType = busessType;
			this.tableName = tableName;
			this.className = className;
		}
	}
	
	public static TableToClass getAll(String className){
		TableToClass[] tc = TableToClass.values();
		for(TableToClass temp : tc){
			if(temp.className.equals(className.trim())){
				return temp;
			}
		}
		return null;
	}

}
