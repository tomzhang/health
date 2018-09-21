package com.dachen.health.pack.evaluate.entity.vo;

import java.util.List;

public class TopSixEvaluationVO {

	private Integer userNum;
	
	private String goodRate;

	private List<EvaluationStatVO> evaluateStatList;


	public Integer getUserNum() {
		return userNum;
	}

	public void setUserNum(Integer userNum) {
		this.userNum = userNum;
	}
	
	public String getGoodRate() {
		return goodRate;
	}
	
	public void setGoodRate(String goodRate) {
		this.goodRate = goodRate;
	}

	public List<EvaluationStatVO> getEvaluateStatList() {
		return evaluateStatList;
	}

	public void setEvaluateStatList(List<EvaluationStatVO> evaluateStatList) {
		this.evaluateStatList = evaluateStatList;
	}


}
