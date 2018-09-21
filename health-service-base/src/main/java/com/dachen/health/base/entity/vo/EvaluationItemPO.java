package com.dachen.health.base.entity.vo;

import java.util.List;

import com.dachen.health.base.entity.po.EvaluationItem;

public class EvaluationItemPO {

	private List<EvaluationItem> goodItem;
	
	private List<EvaluationItem> generalItem;

	public List<EvaluationItem> getGoodItem() {
		return goodItem;
	}

	public void setGoodItem(List<EvaluationItem> goodItem) {
		this.goodItem = goodItem;
	}

	public List<EvaluationItem> getGeneralItem() {
		return generalItem;
	}

	public void setGeneralItem(List<EvaluationItem> generalItem) {
		this.generalItem = generalItem;
	}
}
