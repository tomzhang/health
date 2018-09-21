package com.dachen.line.stat.entity.vo;

import java.util.Arrays;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;


@Entity(value="v_line_service_product", noClassnameStored = true)
public class LineServiceProduct {
	@Id
	private String id;//
	private String title;//服务标题	 例如：“加号服务”，“检查项目”
	private String content;//服务介绍说明
	private double price;//服务指导价格
	private String infoURL;//服务详情介绍说明	 每项服务对应的是静态的HTML页面
	private String[] picIds = new String[10];//背景图片	 多图片的ID用,分割
	private int type;//0是加号服务 1是检查类型  2是住院类型
	private String remarks;
	private int productType;//5就医直通车、6专家直通车、7检查直通车
	
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
	
	public String getPicIds() {
		return Arrays.toString(picIds);
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
	public static void main(String[] args) {
		//
		
		String[] ss  = new String[2];
		ss[0]="http://192.168.3.7:8081/cert/1908/11908/c9/470988625.505843.png.jpg";
		ss[1]="http://192.168.3.7:8081/cert/1908/11908/c9/470988625.505843.png.jpg";
		System.out.println(Arrays.toString(ss));

	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getProductType() {
		return productType;
	}
	public void setProductType(int productType) {
		this.productType = productType;
	}
	
}
