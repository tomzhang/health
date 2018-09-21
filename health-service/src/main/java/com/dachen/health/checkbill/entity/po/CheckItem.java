package com.dachen.health.checkbill.entity.po;

import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.util.JSONUtil;

@Entity(value = "t_check_item",noClassnameStored = true)
public class CheckItem {

	@Id
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

	public List<String> getIndicatorIds() {
		return indicatorIds;
	}

	public void setIndicatorIds(List<String> indicatorIds) {
		this.indicatorIds = indicatorIds;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getCheckUpId() {
		return checkUpId;
	}

	public void setCheckUpId(String checkUpId) {
		this.checkUpId = checkUpId;
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

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

}
