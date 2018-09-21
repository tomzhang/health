package com.dachen.line.stat.entity.vo;

import org.mongodb.morphia.annotations.Entity;


@Entity(value="v_nurse_service_set", noClassnameStored = true)
public class NurseLineServiceSet {
	private String title;//服务标题	 例如：“加号服务”，“检查项目”
	private String content;//服务介绍说明
	private double price;//服务指导价格
	private String infoURL;//服务详情介绍说明	 每项服务对应的是静态的HTML页面
	private String[] picIds;//背景图片	 多图片的ID用,分割
	private int type;//0是加号服务 1是检查类型  2是住院类型
	private String remarks;
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
	public String getInfoURL() {
		return infoURL;
	}
	public void setInfoURL(String infoURL) {
		this.infoURL = infoURL;
	}
	
	public String[] getPicIds() {
		return picIds;
	}
	public void setPicIds(String[] picIds) {
		this.picIds = picIds;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
