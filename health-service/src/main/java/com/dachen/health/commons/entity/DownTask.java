package com.dachen.health.commons.entity;

import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(noClassnameStored=true)
public class DownTask {
	@Id
	private String id;
	private String recordId;
	private String bussessType;//业务类型
//	private String tableField;//关联字段
	private Integer status;//状态(待下载，下载中，下载成功，下载失败)
	private String sourceUrl;//待下载url
	private String toUrl;//目标url
	private long createTime;//创建时间
	private long lastUpdateTime;//更新时间（toUrl）
	private String filePath;
	private String orderId;//订单Id方便后面查询
	
	private  List<DownDetail> details;//下载明细
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getBussessType() {
		return bussessType;
	}
	public void setBussessType(String bussessType) {
		this.bussessType = bussessType;
	}
	
//	public String getTableName() {
//		return tableName;
//	}
//	public void setTableName(String tableName) {
//		this.tableName = tableName;
//	}
	
	//	public String getTableField() {
//		return tableField;
//	}
//	public void setTableField(String tableField) {
//		this.tableField = tableField;
//	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public String getToUrl() {
		return toUrl;
	}
	public void setToUrl(String toUrl) {
		this.toUrl = toUrl;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public List<DownDetail> getDetails() {
		return details;
	}
	public void setDetails(List<DownDetail> details) {
		this.details = details;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
}
