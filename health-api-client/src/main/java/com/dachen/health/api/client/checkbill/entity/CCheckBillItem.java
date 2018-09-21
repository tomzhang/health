package com.dachen.health.api.client.checkbill.entity;

import java.io.Serializable;
import java.util.List;

public class CCheckBillItem implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String checkUpId;
	
	private String itemName;
	//1:医患系统，2:线下服务
	private Integer from;
	
	private String fromId;
	
	private String results;
	
	private List<String> imageList;
	
	private Long createTime;
	
	private Long updateTime;
	
	private Long visitingTime;

	/**指标的id**/
	private List<String> indicatorIds;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCheckUpId() {
		return checkUpId;
	}

	public void setCheckUpId(String checkUpId) {
		this.checkUpId = checkUpId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getFrom() {
		return from;
	}

	public void setFrom(Integer from) {
		this.from = from;
	}

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	public List<String> getImageList() {
		return imageList;
	}

	public void setImageList(List<String> imageList) {
		this.imageList = imageList;
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

	public Long getVisitingTime() {
		return visitingTime;
	}

	public void setVisitingTime(Long visitingTime) {
		this.visitingTime = visitingTime;
	}

	public List<String> getIndicatorIds() {
		return indicatorIds;
	}

	public void setIndicatorIds(List<String> indicatorIds) {
		this.indicatorIds = indicatorIds;
	}

}
