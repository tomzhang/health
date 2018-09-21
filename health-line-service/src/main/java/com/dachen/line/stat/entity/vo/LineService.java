package com.dachen.line.stat.entity.vo;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.health.base.entity.po.CheckSuggest;


@Entity(value = "v_line_service", noClassnameStored = true)
public class LineService {
	
	@Id
	private String id;
	private String title;//服务标题	 例如：“加号服务”，“检查项目”
	private String content;//服务介绍说明
	private double price;//服务指导价格
	private int type;//0是加号服务 1是检查类型  2是住院类型
	private String remarks;//备注
	private String subType;
	private String basicId;//基础id
	
	@Embedded
	private  CheckSuggest checkSuggest;
	
	public String getBasicId() {
		return basicId;
	}
	public void setBasicId(String basicId) {
		this.basicId = basicId;
	}
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
	
	public CheckSuggest getCheckSuggest() {
		return checkSuggest;
	}
	public void setCheckSuggest(CheckSuggest checkSuggest) {
		this.checkSuggest = checkSuggest;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
