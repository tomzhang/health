package com.dachen.health.pack.consult.entity.vo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.page.PageVO;

public class ConsultationPackParams extends PageVO{
	
	private Integer doctorId;
	
	private String id; 
	/*会诊包标题*/
	private String consultationPackTitle;
	/*会诊包描述*/
	private String consultationPackDesc;
	/*会诊包价格*/
	private Integer consultationPrice;
	/* 主会诊医生 不可修改*/
	private Integer consultationDoctor;
	/* 主会诊医生分成比例*/
	private Integer consultationDoctorPercent;
	
	/**
	 * JSON
	 * String str = "{\"doctor1\":\"percent1\",\"doctor2\":\"percent2\"}";
	 * 
	 * 
	 */
	private String doctorPercents;
	

	private String groupId;

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConsultationPackTitle() {
		return consultationPackTitle;
	}

	public void setConsultationPackTitle(String title) {
		this.consultationPackTitle = title;
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

	public Integer getConsultationDoctorPercent() {
		return consultationDoctorPercent;
	}

	public void setConsultationDoctorPercent(Integer consultationDoctorPercent) {
		this.consultationDoctorPercent = consultationDoctorPercent;
	}

	public Map<String,Integer> getDoctorPercents() {
		
		return (Map<String,Integer>)JSON.parse(doctorPercents);
	}

	public void setDoctorPercents(String doctorPercents) {
		this.doctorPercents = doctorPercents;
	}
}
