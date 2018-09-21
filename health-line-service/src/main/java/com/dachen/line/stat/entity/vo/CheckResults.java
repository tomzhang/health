package com.dachen.line.stat.entity.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

@Entity(value = "v_check_results", noClassnameStored = true)
public class CheckResults {

	@Id
	private String id;
	private String orderId;// 患者服务表
	private String lsIds;// 线下服务id(检查项id)
	private String title;// 资源id(检查项名称)
	private int from;// 0是护士1是患者 2是客服 3是系统对接
	private long time;// 创建时间
	private String results;// 检查结果
	private String remarks;
	@Embedded
	private List<ServiceImage> imageList = new ArrayList<ServiceImage>();

	private @NotSaved LineService lineService;

//	private @NotSaved List<String> images;

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getLsIds() {
		return lsIds;
	}

	public void setLsIds(String lsIds) {
		this.lsIds = lsIds;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

//	public List<String> getImages() {
//		return images;
//	}
//
//	public void setImages(List<String> images) {
//		this.images = images;
//	}

	public List<ServiceImage> getImageList() {
		return imageList;
	}

	public void setImageList(List<ServiceImage> imageList) {
		this.imageList = imageList;
	}

	public LineService getLineService() {
		return lineService;
	}

	public void setLineService(LineService lineService) {
		this.lineService = lineService;
	}

}
