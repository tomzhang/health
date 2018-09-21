package com.dachen.health.group.department.entity.vo;

import java.util.ArrayList;
import java.util.List;

import com.dachen.health.group.group.entity.po.GroupDoctor;

public class DeptByDoctorVO {
	
	private String deptName;

	private List<GroupDoctor> groupDoctorList = new ArrayList<GroupDoctor>();

	public List<GroupDoctor> getGroupDoctorList() {
		return groupDoctorList;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

}
