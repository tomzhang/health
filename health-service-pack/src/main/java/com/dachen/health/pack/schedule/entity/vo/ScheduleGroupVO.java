package com.dachen.health.pack.schedule.entity.vo;

import java.util.List;

public class ScheduleGroupVO {

	private String dateStr;
	
	private List<ScheduleVO> voList;

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public List<ScheduleVO> getVoList() {
		return voList;
	}

	public void setVoList(List<ScheduleVO> vos) {
		this.voList = vos;
	}
}
