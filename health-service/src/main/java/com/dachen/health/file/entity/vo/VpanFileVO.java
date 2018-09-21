package com.dachen.health.file.entity.vo;

import java.text.DecimalFormat;

/**
 * 文件  查询结果vo对象
 *@author wangqiao
 *@date 2016年1月13日
 *
 */
public class VpanFileVO {

	private static double ONE_BYTES = 1;
	private static double ONE_KB = ONE_BYTES * 1024;
	private static double ONE_MB = ONE_KB * 1024;
	private static double ONE_GB = ONE_MB * 1024;
	private static double ONE_TB = ONE_GB * 1024;
	private static double ONE_PB = ONE_TB * 1024;
	
	/**
	 * 文件上传  记录id
	 */
	private String fileId;
	
	/**
	 * 文件发送 记录 id
	 */
	private String fileSendId;
	
	/**
	 * 文件 接收者id
	 */
	private Integer receiveUserId;
	
	/**
	 * 文件 发送者id
	 */
	private Integer sendUserId;
	
	/**
	 *  文件发送时间
	 */
	private Long sendDate;
	
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
	private Long size = 0l;
	
	/**
	 * 文件大小（不满1KB，显示单位为bytes；不满1MB，显示单位为KB；不满1GB的，显示单位为MB）
	 */
	private String sizeStr;
	
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
	 * 上传者id
	 */
	private Integer uploader;
	
	/**
	 * 上传者姓名
	 */
	private String uploaderName;
	
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
	
	public String getSizeStr() {
		return sizeStr;
	}

	public void setSizeStr() {
		String sizeStr = null;
		double size = 0d;
		// 格式化小数
		DecimalFormat df = new DecimalFormat("0.0");
		if (this.size < ONE_BYTES) {
			sizeStr = "0bytes";
		} else if (ONE_BYTES <= this.size && this.size < ONE_KB) {
			size = this.size / ONE_BYTES;
			sizeStr = df.format(size) + "bytes";
		} else if (ONE_KB <= this.size && this.size < ONE_MB) {
			size = this.size / ONE_KB;
			sizeStr = df.format(size) + "KB";
		} else if (ONE_MB <= this.size && this.size < ONE_GB) {
			size = this.size / ONE_MB;
			sizeStr = df.format(size) + "MB";
		} else if (ONE_GB <= this.size && this.size < ONE_TB) {
			size = this.size / ONE_GB;
			sizeStr = df.format(size) + "GB";
		} else if (ONE_TB <= this.size && this.size < ONE_PB) {
			size = this.size / ONE_TB;
			sizeStr = df.format(size) + "TB";
		} else {
			size = this.size / ONE_PB;
			sizeStr = df.format(size) + "PB";
		}
		this.sizeStr = sizeStr;
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

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileSendId() {
		return fileSendId;
	}

	public void setFileSendId(String fileSendId) {
		this.fileSendId = fileSendId;
	}

	public Integer getReceiveUserId() {
		return receiveUserId;
	}

	public void setReceiveUserId(Integer receiveUserId) {
		this.receiveUserId = receiveUserId;
	}

	public Integer getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(Integer sendUserId) {
		this.sendUserId = sendUserId;
	}

	public Long getSendDate() {
		return sendDate;
	}

	public void setSendDate(Long sendDate) {
		this.sendDate = sendDate;
	}

	public String getUploaderName() {
		return uploaderName;
	}

	public void setUploaderName(String uploaderName) {
		this.uploaderName = uploaderName;
	}
	
	
	
}

