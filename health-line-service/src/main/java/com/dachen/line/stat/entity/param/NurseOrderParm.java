package com.dachen.line.stat.entity.param;

import java.util.List;

public class NurseOrderParm {

	private List<String> hospitalList;

	private List<String> departList;

	private List<String> timeList;

	private List<String> serviceList;

	private Integer pageNo;

	private Integer pageSize;

	private int start;

	public int getStart() {

		if (pageSize <= 0) {
			pageSize=10;
		}
		else
		{	
			if (pageNo <= 1) {
				pageNo = 1;
			}
			start = (pageNo - 1) * pageSize;
		}
		
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public List<String> getHospitalList() {
		return hospitalList;
	}

	public void setHospitalList(List<String> hospitalList) {
		this.hospitalList = hospitalList;
	}

	public List<String> getDepartList() {
		return departList;
	}

	public void setDepartList(List<String> departList) {
		this.departList = departList;
	}

	public List<String> getTimeList() {
		return timeList;
	}

	public void setTimeList(List<String> timeList) {
		this.timeList = timeList;
	}

	public List<String> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<String> serviceList) {
		this.serviceList = serviceList;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

}
