package com.dachen.health.group.department.entity.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dachen.health.group.group.entity.vo.GroupDoctorInfoVO;

public class OnlineDocByGorupVO {
	
	private String deptName;
	
	private List<Integer> docIds;
	
	private Map<Integer,String> paramMap = new HashMap<Integer, String>();
	
	private List<GroupDoctorInfoVO> groupDoctorInfoLists = new ArrayList<GroupDoctorInfoVO>();
	
	public Map<Integer, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<Integer, String> paramMap) {
		this.paramMap = paramMap;
	}

	public List<Integer> getDocIds() {
		return docIds;
	}

	public void setDocIds(List<Integer> docIds) {
		this.docIds = docIds;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public List<GroupDoctorInfoVO> getGroupDoctorInfoLists() {
		return groupDoctorInfoLists;
	}

	public void setGroupDoctorInfoLists(List<GroupDoctorInfoVO> groupDoctorInfoLists) {
		this.groupDoctorInfoLists = groupDoctorInfoLists;
	}

}
