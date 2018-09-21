package com.dachen.line.stat.entity.param;



public class CheckResultLineServiceParm {
	private String lsIds;  // 线下服务id(检查项id)
	private String results;// 检查结果 描述
	private String imageList="";
	public String getLsIds() {
		return lsIds;
	}
	public String getResults() {
		return results;
	}
	public void setResults(String results) {
		this.results = results;
	}
	public String getImageList() {
		return imageList;
	}
	public void setImageList(String imageList) {
		this.imageList = imageList;
	}
	public void setLsIds(String lsIds) {
		this.lsIds = lsIds;
	}
}
