package com.dachen.health.group.group.entity.param;

import java.util.List;

import com.dachen.commons.page.PageVO;

/**
 * @author pijingwei
 * @date 2015/8/10
 */
public class GroupDoctorParam extends PageVO {

	/**
	 * id
	 */
	private String id;
	
	/**
	 * id
	 */
	private String groupDoctorId;
	
	/**
	 * 集团Id
	 */
	private String groupId;
	
	/**
	 * 医生Id
	 */
	private Integer doctorId;
	
	/**
	 * 帐号状态  C：正在使用，I：已邀请待确认， S：已停用（已离职），O：已踢出   N：已拒绝
	 */
	private String status;
	
	/**
	 * 推荐人Id
	 */
	private Integer referenceId;
	
	/**
	 * 邀请信息记录
	 */
	private String recordMsg;
	
	/**
	 * 联系方式
	 */
	private String contactWay;
	
	/**
	 * 备注
	 */
	private String remarks;
	
	/**
	 * 创建人
	 */
	private Integer creator;
	
	/**
	 * 创建时间（加入日期）
	 */
	private Long creatorDate;
	
	/**
	 * 更新人
	 */
	private Integer updator;
	
	/**
	 * 更新日期
	 */
	private Long updatorDate;
	
	/**
	 * 开始时间
	 */
	private Long endTime;
	
	/**
	 * 结束时间
	 */
	private Long startTime;
	
	
	//在线状态1，在线，2离线
	private String onLineState;
	
	/**
	 * 会诊包id
	 */
	private String consultationPackId;
	
	/**科室id**/
	private String deptId;
	
	/**区域id数组**/
	private String[] areaId;
 	
	/**省份id**/
	private String provinceId;
	
	/**城市id**/
	private String cityId;
	
	/**地区id**/
	private String countryId;
	
	/**服务套餐的id**/
	private String packId;
	
	/**拥有套餐的医生的id**/
	private List<Integer> hasTypeIds;
	
	/**关键字模糊搜索**/
	private String keyword;
	
	public List<Integer> getHasTypeIds() {
		return hasTypeIds;
	}

	public void setHasTypeIds(List<Integer> hasTypeIds) {
		this.hasTypeIds = hasTypeIds;
	}

	public String getPackId() {
		return packId;
	}

	public void setPackId(String packId) {
		this.packId = packId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String[] getAreaId() {
		return areaId;
	}

	public void setAreaId(String[] areaId) {
		this.areaId = areaId;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupDoctorId() {
		return groupDoctorId;
	}

	public void setGroupDoctorId(String groupDoctorId) {
		this.groupDoctorId = groupDoctorId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}

	public String getRecordMsg() {
		return recordMsg;
	}

	public void setRecordMsg(String recordMsg) {
		this.recordMsg = recordMsg;
	}

	public String getContactWay() {
		return contactWay;
	}

	public void setContactWay(String contactWay) {
		this.contactWay = contactWay;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public String getOnLineState() {
		return onLineState;
	}

	public void setOnLineState(String onLineState) {
		this.onLineState = onLineState;
	}

	public String getConsultationPackId() {
		return consultationPackId;
	}

	public void setConsultationPackId(String consultationPackId) {
		this.consultationPackId = consultationPackId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public String toString() {
		return "GroupDoctorParam [id=" + id + ", groupDoctorId=" + groupDoctorId + ", groupId=" + groupId
				+ ", doctorId=" + doctorId + ", status=" + status + ", referenceId=" + referenceId + ", recordMsg="
				+ recordMsg + ", contactWay=" + contactWay + ", remarks=" + remarks + ", creator=" + creator
				+ ", creatorDate=" + creatorDate + ", updator=" + updator + ", updatorDate=" + updatorDate
				+ ", endTime=" + endTime + ", startTime=" + startTime + ", onLineState=" + onLineState
				+ ", consultationPackId=" + consultationPackId + "]";
	}
	
}
