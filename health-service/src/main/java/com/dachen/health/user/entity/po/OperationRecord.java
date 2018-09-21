package com.dachen.health.user.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * 操作记录实体类
 * @author liangcs
 * @date 2016/08/19
 */
@Entity(value="t_user_record", noClassnameStored = true)
public class OperationRecord {
	/**
	 * id
	 */
	@Id
	private String id;//记录id
	
	private Long createTime;//创建时间
	
	private Integer creator;//记录创建者

	
	private String objectType;//操作类型

	private String objectId;//记录修改的对象id
	
	private String content;//操作内容
	
	private Change change;//变更内容

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Change getChange() {
		return change;
	}

	public void setChange(Change change) {
		this.change = change;
	} 
	
}
