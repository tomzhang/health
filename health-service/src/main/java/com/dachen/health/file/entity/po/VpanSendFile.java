package com.dachen.health.file.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * 文件分发 po对象
 *@author wangqiao
 *@date 2016年1月13日
 *
 */
@Entity(value = "f_vpan_send",noClassnameStored=true)
public class VpanSendFile {

	
	/**
	 * 文件发送id
	 */
	@Id
	private ObjectId id;
	/**
	 * 文件发送人id
	 */
	private Integer sendUserId;
	/**
	 * 文件接收人id
	 */
	private Integer receiveUserId;
	/**
	 * 文件id
	 */
	private String fileId;
	/**
	 * 文件发送时间
	 */
	private Long sendDate;
	
	/**
	 * 文件名（冗余）
	 */
	private String name;
	/**
	 * 文件后缀（冗余）
	 */
	private String suffix;
	/**
	 * mime类型（冗余）
	 */
	private String mimeType;
	/**
	 * 文件分类（冗余）
	 */
	private String type;
	/**
	 * 文件大小（冗余）
	 */
	private Long size;
	/**
	 * 文件下载地址（冗余）
	 */
	private String url;
	
	/**
	 * 文件类型（公有，私有，会影响下载所以必须冗余）
	 */
	private String bucketType;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Integer getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(Integer sendUserId) {
		this.sendUserId = sendUserId;
	}

	public Integer getReceiveUserId() {
		return receiveUserId;
	}

	public void setReceiveUserId(Integer receiveUserId) {
		this.receiveUserId = receiveUserId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public Long getSendDate() {
		return sendDate;
	}

	public void setSendDate(Long sendDate) {
		this.sendDate = sendDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBucketType() {
		return bucketType;
	}

	public void setBucketType(String bucketType) {
		this.bucketType = bucketType;
	}
	
	
}

