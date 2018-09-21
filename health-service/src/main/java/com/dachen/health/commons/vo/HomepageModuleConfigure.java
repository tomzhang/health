package com.dachen.health.commons.vo;



/**
 * Created by wangjin on 2017/12/19.
 * 首页改造模块功能配置
 */
public class HomepageModuleConfigure {

	private Integer id;
	
	private String imageUrl;
	
	private String name;
	
	private String type;
	//1显示 0 不显示
	private Integer isShow;
	//排序
	private Integer sort;
	//最低下显示版本
	private String minmumVersion;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getIsShow() {
		return isShow;
	}
	public void setIsShow(Integer isShow) {
		this.isShow = isShow;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getMinmumVersion() {
		return minmumVersion;
	}
	public void setMinmumVersion(String minmumVersion) {
		this.minmumVersion = minmumVersion;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
