package com.dachen.health.pack.dynamic.entity.vo;

import org.mongodb.morphia.annotations.Embedded;

import com.dachen.health.pack.dynamic.util.HtmlRegexpUtil;
import com.dachen.util.StringUtils;

public class DynamicVO {

	private String id;

	// com.dachen.health.dynamic.entity.CategoryEnum 类型
	// 0 医生 1 集团
	private Integer category;

	/**
	 * 0 文本+图片方式； 1 富文本h5方式； StyleEnum
	 */
	private Integer styleType;

	private String groupId;

	private Integer userId;

	private String name;

	private String headImage;

	// 标题
	private String title;

	// 内容
	private String content;

	// 内容
	private String contentShow;

	// 内容图片
	private String contentUrl;

	@Embedded
	// 内容图片
	private String[] imageList;

	private Long createTime;

	// private Long updateTime;

	// html url
	private String url;

	public String getContentShow() {

		if (StringUtils.isNotEmpty(content)) {
			contentShow = HtmlRegexpUtil.filterHtml(content);
		}
		return contentShow;
	}

	public void setContentShow(String contentShow) {
		this.contentShow = contentShow;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public Integer getStyleType() {
		return styleType;
	}

	public void setStyleType(Integer styleType) {
		this.styleType = styleType;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String[] getImageList() {
		return imageList;
	}

	public void setImageList(String[] imageList) {
		this.imageList = imageList;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	// public Long getUpdateTime() {
	// return updateTime;
	// }
	//
	// public void setUpdateTime(Long updateTime) {
	// this.updateTime = updateTime;
	// }

}
