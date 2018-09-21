package com.dachen.health.pack.consult.entity.vo;

import java.util.List;

import com.dachen.util.JSONUtil;

public class CategoryFriendsPageVo {

	private String deptId;
	
	private String deptName;
	
	private String firstLetter;
	
	private List<ConsultationFriendsVo> doctors;

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getFirstLetter() {
		return firstLetter;
	}

	public void setFirstLetter(String firstLetter) {
		this.firstLetter = firstLetter;
	}

	public List<ConsultationFriendsVo> getDoctors() {
		return doctors;
	}

	public void setDoctors(List<ConsultationFriendsVo> doctors) {
		this.doctors = doctors;
	}
	
	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
}
