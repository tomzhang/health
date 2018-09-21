package com.dachen.health.file.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * 文件上传 po对象
 *@author wangqiao
 *@date 2016年1月13日
 *
 */
@Entity(value = "f_vpan_upload",noClassnameStored=true)
public class VpanUploadFile {
	
	@Id
	private ObjectId id;
	
	/**
	 * 文件名
	 */
	private String name;
	
	/**
	 * 文件后缀
	 */
	private String suffix;
	
	/**
	 * mime
	 */
	private String mimeType;
	
	/**
	 * 文件大小（字节单位）
	 */
	private Long size;
	
	/**
	 * 空间名
	 */
	private String spaceName;
	
	/**
	 * 下载url
	 */
	private String url;
	
	/**
	 * 文件hash值
	 */
	private String hash;
	
	/**
	 * 文件分类
	 */
	private String type;
	
	/**
	 * 公有？私有
	 */
	private String bucketType;
	
	/**
	 * 文件状态
	 */
	private String status;
	
	/**
	 * 上传者
	 */
	private Integer uploader;

	/***
	 * 上传者的用户类型，用来判断用户属于企业用户还是属于医生，若该值为空，说明为历史数据，属于医生。
	 */
	private Integer userType;
	
	/**
	 * 最后更新人
	 */
	private Integer lastUpdator;
	
	/**
	 * 上传时间
	 */
	private Long uploadDate;
	
	/**
	 * 最后更新时间
	 */
	private Long lastUpdateDate;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getSpaceName() {
		return spaceName;
	}

	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBucketType() {
		return bucketType;
	}

	public void setBucketType(String bucketType) {
		this.bucketType = bucketType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getUploader() {
		return uploader;
	}

	public void setUploader(Integer uploader) {
		this.uploader = uploader;
	}

	public Integer getLastUpdator() {
		return lastUpdator;
	}

	public void setLastUpdator(Integer lastUpdator) {
		this.lastUpdator = lastUpdator;
	}

	public Long getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Long uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Long getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Long lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}
}

