package com.dachen.health.recommend.entity.vo;

import java.util.List;

import com.dachen.health.recommend.entity.po.DoctorRecommend;

public class DoctorRecommendVO extends DoctorRecommend{
	
	private String name;
	private String title;
	private String departments;
	private String headPicFileName;
	private List<String> groups;//归属的集团
	private boolean isSelect;//是否已被选择
	private String documentUrl;//文档地址
	
	public String getDocumentUrl() {
		return documentUrl;
	}
	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	public boolean isSelect() {
		return isSelect;
	}
	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDepartments() {
		return departments;
	}
	public void setDepartments(String departments) {
		this.departments = departments;
	}
	public String getHeadPicFileName() {
		return headPicFileName;
	}
	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}
	
	
}
