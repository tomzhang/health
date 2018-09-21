package com.dachen.health.group.group.entity.vo;

import java.util.List;

import com.dachen.health.group.department.entity.vo.DepartmentMobileVO;
import com.dachen.health.group.group.entity.po.GroupCertification;

/**
 * 
 * @author pijingwei
 * @date 2015/8/7
 * 医院集团
 */
public class GroupVO {
	
	public static final String TYPE_HOSPITAL = "hospital";
	public static final String TYPE_GROUP = "group";
	public static final String TYPE_DEPT = "dept";
	
	/**
	 * 管理员名称 
	 */
	private String adminName;
	/**
	 * 集团下所有科室 
	 */
	private List<DepartmentMobileVO> departmentList;
	/**
	 * 集团Id
	 */
	private String id;
	
	/**
	 * 公司Id--所属公司
	 */
	private String companyId;
	
	/**
	 * 集团+v认证时间
	 */
	private Long processVTime;
	
	
	/**
	 * 集团名称
	 */
	private String  name;
	
	/**
	 * 集团介绍
	 */
	private String introduction;
	
	/**
	 * 创建人
	 */
	private Integer creator;
	
	/**
	 * 创建人名字
	 */
	private String creatorName;
	
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
	 * 集团头像
	 */
	private String groupIconPath;
	
	/**
	 * 集团擅长病种
	 */
	private String diseaseName;
	
	/**
	 * 集团认证状态
	 */
	private String certStatus;
	
	/**
	 * 集团认证
	 */
	private GroupCertification groupCert; 
	
	/**
	 * 挂在同一家公司的其他医生集团数量
	 */
	private Long otherGroupCount;
	
	/**
	 * 是否主集团
	 */
	private Boolean isMain;
	
	/**
	 * 是否管理员
	 */
	private Boolean isAdmin;
	
	private String dutyStartTime;
	
	public String dutyEndTime;
	
	/**
	 * 申请加入该集团的医生数量
	 */
	private Long applyCount;
	
	/**
     * 医院id
     */
    private String hospitalId;
    /**
     * 数据类型type（hospital，group）
     * @return
     */
    private String type;
    
    /**
     * 集团简介文档ID
     */
    private String documentId;
	
    /**
     * 集团logo
     */
    private String logoUrl;
    
    /**
     * 集团的屏蔽状态（N表示正常， S表示该集团被屏蔽）
     */
    private String skip;
	
	private boolean openConsultation;

	public String getSkip() {
		return skip;
	}

	public void setSkip(String skip) {
		this.skip = skip;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public List<DepartmentMobileVO> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<DepartmentMobileVO> departmentList) {
		this.departmentList = departmentList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
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


	public String getGroupIconPath() {
		return groupIconPath;
	}

	public void setGroupIconPath(String groupIconPath) {
		this.groupIconPath = groupIconPath;
	}

	public String getDiseaseName() {
		return diseaseName;
	}

	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}
	
	public String getCertStatus() {
		return certStatus;
	}

	public void setCertStatus(String certStatus) {
		this.certStatus = certStatus;
	}

	public GroupCertification getGroupCert() {
		return groupCert;
	}

	public void setGroupCert(GroupCertification groupCert) {
		this.groupCert = groupCert;
	}

	public Long getOtherGroupCount() {
		return otherGroupCount;
	}

	public void setOtherGroupCount(Long otherGroupCount) {
		this.otherGroupCount = otherGroupCount;
	}

	public String getDutyStartTime() {
		return dutyStartTime;
	}

	public void setDutyStartTime(String dutyStartTime) {
		this.dutyStartTime = dutyStartTime;
	}

	public String getDutyEndTime() {
		return dutyEndTime;
	}

	public void setDutyEndTime(String dutyEndTime) {
		this.dutyEndTime = dutyEndTime;
	}

	public Boolean getIsMain() {
		return isMain;
	}

	public void setIsMain(Boolean isMain) {
		this.isMain = isMain;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Long getApplyCount() {
		return applyCount;
	}

	public void setApplyCount(Long applyCount) {
		this.applyCount = applyCount;
	}

	public boolean isOpenConsultation() {
		return openConsultation;
	}

	public void setOpenConsultation(boolean openConsultation) {
		this.openConsultation = openConsultation;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public Long getProcessVTime() {
		return processVTime;
	}

	public void setProcessVTime(Long processVTime) {
		this.processVTime = processVTime;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	
	
}
