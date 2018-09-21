package com.dachen.health.group.schedule.entity.vo;

import java.util.List;
import java.util.Map;

import com.dachen.health.group.schedule.entity.po.Offline;

public class DoctorOfflineVO implements java.io.Serializable{


	private static final long serialVersionUID = 9089207104569546139L;
	
	private Integer index;//第几周

	private Integer[]  days;//一周每天日期集合
	 
	private List<Offline> offlineList;
	
	private Map<String,List<Offline>> offlineMap;

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer[] getDays() {
		return days;
	}

	public void setDays(Integer[] days) {
		this.days = days;
	}

	public List<Offline> getOfflineList() {
		return offlineList;
	}

	public void setOfflineList(List<Offline> offlineList) {
		this.offlineList = offlineList;
	}

	public Map<String, List<Offline>> getOfflineMap() {
		return offlineMap;
	}

	public void setOfflineMap(Map<String, List<Offline>> offlineMap) {
		this.offlineMap = offlineMap;
	}
	 
	
	
	 
}
