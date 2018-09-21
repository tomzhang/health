package com.dachen.health.pack.consult.entity.po;

import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.util.JSONUtil;

@Entity(value = "t_ill_case_type_content",noClassnameStored = true)
public class IllCaseTypeContent {

	@Id
	private String id;
	
	private String illCaseInfoId;
	
	private String illCaseTypeId;
	
	private String contentTxt;
	
	private List<String> contentImages;
	
	private Long createTime;
	
	private Long updateTime;

	private Boolean deal;

	public Boolean getDeal() {
		return deal;
	}

	public void setDeal(Boolean deal) {
		this.deal = deal;
	}

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
	
	public Long getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}


	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}


	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

}
