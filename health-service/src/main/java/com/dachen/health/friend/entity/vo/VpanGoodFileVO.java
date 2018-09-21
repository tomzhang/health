package com.dachen.health.friend.entity.vo;

/**
 * 文件  查询结果vo对象
 *@author wangqiao
 *@date 2016年1月13日
 *
 */
public class VpanGoodFileVO {
	/**
	 * 文件名
	 */
	private String name;
	
	/**
	 * 下载url
	 */
	private String url;
	
	private  String  id;
	
	private  String  suffix;

	private  String  mimeType;
	//文件大小
	private  long  size;

	public long getSize() {
		return size;
	}


	public void setSize(long size) {
		this.size = size;
	}


	public String getMimeType() {
		return mimeType;
	}


	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getSuffix() {
		return suffix;
	}


	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}
	
	
}

