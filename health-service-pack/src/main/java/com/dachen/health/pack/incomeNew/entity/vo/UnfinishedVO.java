package com.dachen.health.pack.incomeNew.entity.vo;

import java.util.ArrayList;
import java.util.List;

public class UnfinishedVO {
	private String keyYM;
	private List<BaseDetailVO> list;
	
	public String getKeyYM() {
		return keyYM;
	}
	public void setKeyYM(String keyYM) {
		this.keyYM = keyYM;
	}
	public List<BaseDetailVO> getList() {
		return list;
	}
	public void setList(List<BaseDetailVO> list) {
		this.list = list;
	}
	public UnfinishedVO() {
		this.list = new ArrayList<BaseDetailVO>();
	}
	
	
	

}
