package com.dachen.health.pack.incomeNew.entity.vo;

import java.util.ArrayList;
import java.util.List;

public class TotalIncomeDVO {
	private int bussnessType;
	private String bussnessName;
	private List<BaseDetailVO> list ;
	public int getBussnessType() {
		return bussnessType;
	}
	public void setBussnessType(int bussnessType) {
		this.bussnessType = bussnessType;
	}
	public String getBussnessName() {
		return bussnessName;
	}
	public void setBussnessName(String bussnessName) {
		this.bussnessName = bussnessName;
	}
	public List<BaseDetailVO> getList() {
		return list;
	}
	public void setList(List<BaseDetailVO> list) {
		this.list = list;
	}
	
	public TotalIncomeDVO(){
		this.list = new ArrayList<BaseDetailVO>();
	}

}
