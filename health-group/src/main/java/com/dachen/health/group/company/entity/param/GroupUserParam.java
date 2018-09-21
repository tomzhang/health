package com.dachen.health.group.company.entity.param;

import com.dachen.commons.page.PageVO;

/**
 * 
 * @author pijingwei
 * @date 2015/8/12
 */
public class GroupUserParam extends PageVO {
	
	/* 手机号码 */
	private String telephone;
	
	/* 再次邀请 */
	private Integer againInvite;
	
	/**
	 * Id
	 */
	private String id;
	
	/**
	 * 用户Id
	 */
	private Integer doctorId;
	
	/**
	 * 集团Id或公司Id
	 */
	private String objectId;
	
	/**
	 * 账户类型    1：公司用户   2：集团用户
	 */
	private Integer type;
	
	/**
	 * 状态	I：邀请待通过，C：正常使用， S：已离职
	 */
	private String status;
	
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
	
	/**
	 * 开始时间
	 */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Integer getAgainInvite() {
		return againInvite;
	}

	public void setAgainInvite(Integer againInvite) {
		this.againInvite = againInvite;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

}
