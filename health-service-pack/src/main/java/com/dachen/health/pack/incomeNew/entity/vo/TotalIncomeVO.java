package com.dachen.health.pack.incomeNew.entity.vo;

import java.util.ArrayList;
import java.util.List;

public class TotalIncomeVO {
	private int year;
	private List<DoctorMoneyVO> list;
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public List<DoctorMoneyVO> getList() {
		return list;
	}
	public void setList(List<DoctorMoneyVO> list) {
		this.list = list;
	}
	
	public TotalIncomeVO(){
		this.list = new ArrayList<DoctorMoneyVO>();
	}
	

}
