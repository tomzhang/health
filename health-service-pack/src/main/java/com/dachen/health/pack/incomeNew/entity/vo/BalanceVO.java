package com.dachen.health.pack.incomeNew.entity.vo;

import java.util.ArrayList;
import java.util.List;

public class BalanceVO extends BaseDetailVO{
	
	private String YM;
	private List<BaseDetailVO>  list;
	public String getYM() {
		return YM;
	}
	public void setYM(String yM) {
		YM = yM;
	}
	public List<BaseDetailVO> getList() {
		return list;
	}
	public void setList(List<BaseDetailVO> list) {
		this.list = list;
	}
	
	public BalanceVO(){
		this.list = new ArrayList<BaseDetailVO>();;
	}
}
