package com.dachen.health.group.group.entity.vo;

import com.dachen.health.base.helper.UserHelper;

/**
 * 申请加入医生集团VO
 *@author wangqiao
 *@date 2015年12月23日
 *
 */
public class GroupDoctorApplyVO {

	/**
	 * 申请id
	 */
	private String id;
	
	/**
	 * 集团id
	 */
	private String groupId;
	
	/**
	 * 医生id（申请人）
	 */
	private Integer doctorId;
	
	/**
	 * 申请留言
	 */
	private String applyMsg;
	
	/**
	 * 申请时间
	 */
	private long applyDate;
	
	//////////////////////////////
	
    /**
     * 头像名称(返回头像全路径)
     */
    private String headPicFileName;
    
    /**
     * 医生姓名
     */
    private String name;
    
    /**
     * 医生职称
     */
    private String title;
    
    /**
     * 医生科室
     */
    private String departments;
    
    /**
     * 医院名称
     */
    private String hospital;
    
    /**
     * 医生账号状态
     */
    private Integer status;
    
    /**
     * 申请状态  ： J=正在申请 ，C=审核通过，M=申请拒绝，Z=申请过期
     */
    private String applyStatus; 
    
    /**
     * 审核备注
     */
    private String checkRemark;

	public String getCheckRemark() {
		return checkRemark;
	}

	public void setCheckRemark(String checkRemark) {
		this.checkRemark = checkRemark;
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

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getApplyMsg() {
		return applyMsg;
	}

	public void setApplyMsg(String applyMsg) {
		this.applyMsg = applyMsg;
	}

	public long getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(long applyDate) {
		this.applyDate = applyDate;
	}

	public String getHeadPicFileName() {
//		return headPicFileName;  更新成头像全路径
		return UserHelper.buildHeaderPicPath(headPicFileName,doctorId);
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getApplyStatus() {
		return applyStatus;
	}

	public void setApplyStatus(String applyStatus) {
		this.applyStatus = applyStatus;
	}
    
    
    
	
}

