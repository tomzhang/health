package com.dachen.health.group.department.entity.vo;

import java.util.List;

import com.dachen.health.group.group.entity.vo.GroupDoctorVO;

/**
 * 
 * @author pijingwei
 * @date 2015/8/10
 * 门诊表实体类
 */
public class DepartmentMobileVO {
	
	private List<DepartmentMobileVO> subList;
	
	private List<GroupDoctorVO> doctorList;
	
	/**
	 * Id
	 */
	private String id;
	
	/**
	 * 医生集团Id
	 */
	private String groupId;
	
	/**
	 * 父节点Id
	 */
	private String parentId;
	
	/**
	 * 门诊（部门）名称
	 */
	private String name;
	
	/**
	 * 门诊（部门）描述
	 */
	private String description;
	
	/**
	 * 创建人
	 */
	private Integer creator;
	
	/**
	 * 创建时间
	 */
	private Long creatorDate;
	
	/**
	 * 更新人
	 */
	private Integer updator;
	
	/**
	 * 更新时间
	 */
	private Long updatorDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
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

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Long getCreatorDate() {
		return creatorDate;
	}

	public void setCreatorDate(Long creatorDate) {
		this.creatorDate = creatorDate;
	}

	public Integer getUpdator() {
		return updator;
	}

	public void setUpdator(Integer updator) {
		this.updator = updator;
	}

	public Long getUpdatorDate() {
		return updatorDate;
	}

	public void setUpdatorDate(Long updatorDate) {
		this.updatorDate = updatorDate;
	}

	public List<DepartmentMobileVO> getSubList() {
		return subList;
	}

	public void setSubList(List<DepartmentMobileVO> subList) {
		this.subList = subList;
	}

	public List<GroupDoctorVO> getDoctorList() {
		return doctorList;
	}

	public void setDoctorList(List<GroupDoctorVO> doctorList) {
		this.doctorList = doctorList;
	}
	
}
