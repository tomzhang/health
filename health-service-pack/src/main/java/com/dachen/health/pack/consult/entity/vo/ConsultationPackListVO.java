package com.dachen.health.pack.consult.entity.vo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ConsultationPackListVO {
	private String id;

	private String groupId;
	private String groupName;
	/*会诊包标题*/
	private String consultationPackTitle;
	/*会诊包描述*/
	private String consultationPackDesc;
	/*会诊包价格*/
	private Integer consultationPrice;
	
	private Integer consultationTimes;
	/* 主会诊医生 不可修改*/
	private Integer consultationDoctor;
	
	private Integer doctorId;
	
	private String doctorPic;
	private String doctorName;
	private String doctorTitle;
	private String doctorHostpital;
	private String doctorDept;
	
	private Set<Integer> doctorIds = new HashSet<Integer>();
	
	private List<Map<String,String>> doctorList ;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getConsultationPackTitle() {
		return consultationPackTitle;
	}

	public void setConsultationPackTitle(String consultationPackTitle) {
		this.consultationPackTitle = consultationPackTitle;
	}

	public String getConsultationPackDesc() {
		return consultationPackDesc;
	}

	public void setConsultationPackDesc(String consultationPackDesc) {
		this.consultationPackDesc = consultationPackDesc;
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

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorPic() {
		return doctorPic;
	}

	public void setDoctorPic(String doctorPic) {
		this.doctorPic = doctorPic;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getDoctorTitle() {
		return doctorTitle;
	}

	public void setDoctorTitle(String doctorTitle) {
		this.doctorTitle = doctorTitle;
	}

	public String getDoctorHostpital() {
		return doctorHostpital;
	}

	public void setDoctorHostpital(String doctorHostpital) {
		this.doctorHostpital = doctorHostpital;
	}

	public String getDoctorDept() {
		return doctorDept;
	}

	public void setDoctorDept(String doctorDept) {
		this.doctorDept = doctorDept;
	}

	public Set<Integer> getDoctorIds() {
		return doctorIds;
	}

	public void setDoctorIds(Set<Integer> doctorIds) {
		this.doctorIds = doctorIds;
	}

	public List<Map<String, String>> getDoctorList() {
		return doctorList;
	}

	public void setDoctorList(List<Map<String, String>> doctorList) {
		this.doctorList = doctorList;
	}

	public Integer getConsultationTimes() {
		return consultationTimes;
	}

	public void setConsultationTimes(Integer consultationTimes) {
		this.consultationTimes = consultationTimes;
	}
	
	
	
}
