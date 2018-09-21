package com.dachen.health.app.entity.po;

import java.io.Serializable;
/**
 * app 信息
 * @author tan.yf
 *
 */
public class App implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;// id
	private String code;// 编码
	private String name; // 名称
	private String info; // 升级信息
	private String device; // 设备 ios/android
	private String version; // 版本号
	private String downloadUrl; // 下载地址
	
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
}
