package com.dachen.health.pack.consult.entity.vo;

import java.util.List;

public class IllCaseTypeContentPageVo {

	private String id;
	
	private String illCaseInfoId;
	
	private String illCaseTypeId;
	
	private String typeName;
	
	private String contentTxt;
	
	private List<String> contentImages;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIllCaseInfoId() {
		return illCaseInfoId;
	}

	public void setIllCaseInfoId(String illCaseInfoId) {
		this.illCaseInfoId = illCaseInfoId;
	}

	public String getIllCaseTypeId() {
		return illCaseTypeId;
	}

	public void setIllCaseTypeId(String illCaseTypeId) {
		this.illCaseTypeId = illCaseTypeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getContentTxt() {
		return contentTxt;
	}

	public void setContentTxt(String contentTxt) {
		this.contentTxt = contentTxt;
	}

	public List<String> getContentImages() {
		return contentImages;
	}

	public void setContentImages(List<String> contentImages) {
		this.contentImages = contentImages;
	}

	@Override
	public String toString() {
		return "IllCaseTypeContentPageVo [id=" + id + ", illCaseInfoId=" + illCaseInfoId + ", illCaseTypeId="
				+ illCaseTypeId + ", typeName=" + typeName + ", contentTxt=" + contentTxt + ", contentImages="
				+ contentImages + "]";
	}
	
}
