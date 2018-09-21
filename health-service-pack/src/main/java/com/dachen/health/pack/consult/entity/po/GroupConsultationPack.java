package com.dachen.health.pack.consult.entity.po;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.util.JSONUtil;

@Entity(value="t_group_consultation_pack",noClassnameStored=true)
public class GroupConsultationPack {
	
	@Id
	private String id;

	private String groupId;
	/*会诊包标题*/
	private String consultationPackTitle;
	/*会诊包描述*/
	private String consultationPackDesc;
	@Deprecated
	private Integer maxFee;
	@Deprecated
	private Integer minFee;
	@Deprecated
	private Integer groupPercent;
	/*会诊包价格*/
	private Integer consultationPrice;
	/* 主会诊医生 不可修改*/
	private Integer consultationDoctor;
	/* 主会诊医生分成比例*/
	private Integer consultationDoctorPercent;
	
	@Deprecated
	private Integer unionDoctorPercent;
	/*参与医生*/
	private Set<Integer> doctorIds = new HashSet<Integer>();
	/*参与医生比例  key 医生ID，value 医生分成比例*/
	private Map<String,Integer> doctorPercents = new HashMap<String,Integer>();
	/*创建时间*/
	private Long createTime;
	
	private Integer isDelete = 0;

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
	@Deprecated
	public Integer getUnionDoctorPercent() {
		return unionDoctorPercent;
	}
	public void setUnionDoctorPercent(Integer unionDoctorPercent) {
		this.unionDoctorPercent = unionDoctorPercent;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Set<Integer> getDoctorIds() {
		return doctorIds;
	}

	public void setDoctorIds(Set<Integer> doctorIds) {
		this.doctorIds = doctorIds;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public String getConsultationPackTitle() {
		return consultationPackTitle;
	}

	public void setConsultationPackTitle(String title) {
		this.consultationPackTitle = title;
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
		return JSONUtil.toJSONString(this);
	}
	
}
