package com.dachen.health.pack.evaluate.entity.vo;

import java.util.List;

public class EvaluationDetailVO {

	private List<EvaluationStatVO> evaluateStatList;
	
	private List<EvaluationVO> evaluateVOList;

	public List<EvaluationStatVO> getEvaluateStatList() {
		return evaluateStatList;
	}

	public void setEvaluateStatList(List<EvaluationStatVO> evaluateStatList) {
		this.evaluateStatList = evaluateStatList;
	}

	public List<EvaluationVO> getEvaluateVOList() {
		return evaluateVOList;
	}

	public void setEvaluateVOList(List<EvaluationVO> evaluateVOList) {
		this.evaluateVOList = evaluateVOList;
	}
	
}
