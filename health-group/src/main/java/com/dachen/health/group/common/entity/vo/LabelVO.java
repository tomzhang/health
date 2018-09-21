package com.dachen.health.group.common.entity.vo;

/**
 * 
 * @author pijingwei
 * @date 2015/8/13
 */
public class LabelVO {

	/**
	 * id
	 */
	private String id;
	
	/**
	 * 关联Id（关系Id）
	 */
	private String relationId;
	
	/**
	 * 标签名称
	 */
	private String name;
	
	/**
	 * 标签描述
	 */
	private String description;
	
	/**
	 * 创建者
	 */
	private String creator;
	
	/**
	 * 创建时间
	 */
	private Long creatorDate;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRelationId() {
		return relationId;
	}

	public void setRelationId(String relationId) {
		this.relationId = relationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Long getCreatorDate() {
		return creatorDate;
	}

	public void setCreatorDate(Long creatorDate) {
		this.creatorDate = creatorDate;
	}
	
	
}
