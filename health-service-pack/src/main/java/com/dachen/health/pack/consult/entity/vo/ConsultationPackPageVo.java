package com.dachen.health.pack.consult.entity.vo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dachen.health.user.entity.vo.UserInfoVO;

public class ConsultationPackPageVo {

	private String id;

	private String groupId;
	
	
	private String consultationPackDesc;
	@Deprecated
	private Integer maxFee;
	@Deprecated
	private Integer minFee;
	@Deprecated
	private Integer groupPercent;
	
	private Integer consultationDoctorPercent;
	@Deprecated
	private Integer unionDoctorPercent;
	
	private Set<Integer> doctorIds; 
	
	private List<UserInfoVO> doctorInfoList;
	
	/*会诊包标题*/
	private String consultationPackTitle;
	/*会诊包价格*/
	private Integer consultationPrice;
	/* 主会诊医生 不可修改*/
	private Integer consultationDoctor;
	private String consultationDoctorName;
	
	private Map<String,Integer> doctorPercents;

	
	public String getConsultationDoctorName() {
		return consultationDoctorName;
	}

	public void setConsultationDoctorName(String consultationDoctorName) {
		this.consultationDoctorName = consultationDoctorName;
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

	public String getConsultationPackDesc() {
		return consultationPackDesc;
	}

	public void setConsultationPackDesc(String consultationPackDesc) {
		this.consultationPackDesc = consultationPackDesc;
	}

	public Integer getMaxFee() {
		return maxFee;
	}

	public void setMaxFee(Integer maxFee) {
		this.maxFee = maxFee;
	}

	public Integer getMinFee() {
		return minFee;
	}

	public void setMinFee(Integer minFee) {
		this.minFee = minFee;
	}

	public Integer getGroupPercent() {
		return groupPercent;
	}

	public void setGroupPercent(Integer groupPercent) {
		this.groupPercent = groupPercent;
	}

	public Integer getConsultationDoctorPercent() {
		return consultationDoctorPercent;
	}

	public void setConsultationDoctorPercent(Integer consultationDoctorPercent) {
		this.consultationDoctorPercent = consultationDoctorPercent;
	}

	public Integer getUnionDoctorPercent() {
		return unionDoctorPercent;
	}

	public void setUnionDoctorPercent(Integer unionDoctorPercent) {
		this.unionDoctorPercent = unionDoctorPercent;
	}

	public List<UserInfoVO> getDoctorInfoList() {
		return doctorInfoList;
	}

	public void setDoctorInfoList(List<UserInfoVO> doctorInfoList) {
		this.doctorInfoList = doctorInfoList;
	}

	public Set<Integer> getDoctorIds() {
		return doctorIds;
	}

	public void setDoctorIds(Set<Integer> doctorIds) {
		this.doctorIds = doctorIds;
	}
	
	public String getConsultationPackTitle() {
		return consultationPackTitle;
	}

	public void setConsultationPackTitle(String consultationPackTitle) {
		this.consultationPackTitle = consultationPackTitle;
	}

	public Integer getConsultationPrice() {
		return consultationPrice;
	}

	public void setConsultationPrice(Integer consultationPrice) {
		this.consultationPrice = consultationPrice;
	}

	public Integer getConsultationDoctor() {
		return consultationDoctor;
	}

	public void setConsultationDoctor(Integer consultationDoctor) {
		this.consultationDoctor = consultationDoctor;
	}


	public Map<String, Integer> getDoctorPercents() {
		return doctorPercents;
	}

	public void setDoctorPercents(Map<String, Integer> doctorPercents) {
		this.doctorPercents = doctorPercents;
	}

	@Override
	public String toString() {
		return "ConsultationPackPageVo [id=" + id + ", groupId=" + groupId + ", consultationPackDesc="
				+ consultationPackDesc +", consultationPackTitle=" + consultationPackTitle+ ", consultationPrice=" + consultationPrice + ", consultationDoctor=" + consultationDoctor + 
				 ", consultationDoctorPercent=" + consultationDoctorPercent + ", doctorPercents="
				+ doctorPercents + ", doctorInfoList=" + doctorInfoList + "]";
	}
	
}
