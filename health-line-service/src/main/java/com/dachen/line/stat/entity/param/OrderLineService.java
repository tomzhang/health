package com.dachen.line.stat.entity.param;


public class OrderLineService {
	private String id;
	private String title;//服务标题	 例如：“加号服务”，“检查项目”
	private String content;//服务介绍说明
	private double price;//服务指导价格
	private String type;//0是加号服务 1是检查类型  2是住院类型
	private String remarks;//备注
	private String subType;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
