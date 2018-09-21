package com.dachen.health.pack.dynamic.entity.po;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

@Entity(value = "t_dynamic",noClassnameStored = true)
public class Dynamic {
	@Id
	private String id;
	
	//com.dachen.health.dynamic.entity.CategoryEnum 类型
	// 0 医生 1 集团
	private Integer category;
	
	/**
	 * 0 文本+图片方式； 1  富文本h5方式； StyleEnum
	 */
	private Integer styleType;
	
	
	private String groupId;
	
	/**
	 * 发送的医生
	 */
	private String[]  to;
	
	private Integer userId;
	
	//标题
	private String title;
	
	//内容
	private String content;
	
	//内容图片
	private String contentUrl;
	
	@Embedded
	//内容图片
	private String[] imageList;
	
	//html url
	private String url;
	
	
	//0 未删除  1已删除 DynamicDeleteEnum
	private Integer isDelete;
	
	//创建者
	private String creator;
	
	private Long createTime;
	
	private Long updateTime;
	
	//更新者
	private String updator;

	private List<Integer> userIds;

	public List<Integer> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Integer> userIds) {
		this.userIds = userIds;
	}

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getUpdator() {
		return updator;
	}

	public void setUpdator(String updator) {
		this.updator = updator;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
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

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	
}
