package com.dachen.health.pack.pack.entity.vo;

import java.util.List;

import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.order.entity.po.OrderDoctor;
import com.dachen.health.user.entity.po.Doctor;

public class PackDoctorVO {
	
	private Integer doctorId;
	private String name;
	private String title;
	private String departments;
	private String hospital;
	private String headPicFileName;
	
	private List<PackVO> packList;

	public PackDoctorVO() {
	}
	
	public PackDoctorVO(User doctorUser, List<PackVO> packList) {
		
		Doctor doctor = doctorUser.getDoctor();
		
		this.doctorId = doctorUser.getUserId();
		this.name = doctorUser.getName();
		this.title = doctor.getTitle();
		this.departments = doctor.getDepartments();
		this.hospital = doctor.getHospital();
		this.headPicFileName = doctorUser.getHeadPicFileName();
		
		this.packList = packList;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public List<PackVO> getPackList() {
		return packList;
	}

	public void setPackList(List<PackVO> packList) {
		this.packList = packList;
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

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}
	
}
