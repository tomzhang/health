package com.dachen.health.group.department.entity.param;

import com.dachen.commons.page.PageVO;

/**
 * @author pijingwei
 * @date 2015/8/10
 */
public class DepartmentDoctorParam extends PageVO {

	private String keyword;
	
	/**
	 * id
	 */
	private String id;
	
	/**
	 * 集团Id
	 */
	private String groupId;
	
	/**
	 * 门诊Id
	 */
	private String departmentId;
	
	/**
	 * 医生Id
	 */
	private Integer doctorId;
	
	/**
	 * 创建人（申请人）
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
	 * 更新时间（通过日期）
	 */
	private Long updatorDate;
	
	/**
	 * 会诊包id
	 */
	private String consultationPackId;
	
	/**
	 * 集团医生状态
	 */
	private String doctorStatus;
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

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

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
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

	public String getConsultationPackId() {
		return consultationPackId;
	}

	public void setConsultationPackId(String consultationPackId) {
		this.consultationPackId = consultationPackId;
	}
	
	public String getDoctorStatus() {
		return doctorStatus;
	}

	public void setDoctorStatus(String doctorStatus) {
		this.doctorStatus = doctorStatus;
	}

	@Override
	public String toString() {
		return "DepartmentDoctorParam [keyword=" + keyword + ", id=" + id + ", groupId=" + groupId + ", departmentId="
				+ departmentId + ", doctorId=" + doctorId + ", creator=" + creator + ", creatorDate=" + creatorDate
				+ ", updator=" + updator + ", updatorDate=" + updatorDate + ", consultationPackId=" + consultationPackId
				+ "]";
	}
}
