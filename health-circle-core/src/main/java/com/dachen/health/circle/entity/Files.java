package com.dachen.health.circle.entity;

public class Files {

	/**
	 * 附件id
	 */
	private String file_id;
	/**
	 * 附件地址
	 */
	private String file_url;
	/**
	 * 附件大小
	 */
	private String size;
	/**
	 * 附件大小（显示值）
	 */
	private String sizeStr;
	/**
	 * 附件名称
	 */
	private String file_name;
	/**
	 * 附件类型
	 */
	private String type;
	/**
	 * 文件后缀名
	 */
	private String suffix;

	public String getFile_url() {
		return file_url;
	}

	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getSizeStr() {
		return sizeStr;
	}

	public void setSizeStr(String sizeStr) {
		this.sizeStr = sizeStr;
	}

}
