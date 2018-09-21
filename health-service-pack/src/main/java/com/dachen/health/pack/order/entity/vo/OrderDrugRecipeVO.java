package com.dachen.health.pack.order.entity.vo;

import java.util.List;

import com.dachen.medice.vo.DrugRecipe;

public class OrderDrugRecipeVO {
	
	private Integer orderId;
	
	private List<DrugRecipe> drugRecipes;

	public List<DrugRecipe> getDrugRecipes() {
		return drugRecipes;
	}

	public void setDrugRecipes(List<DrugRecipe> drugRecipes) {
		this.drugRecipes = drugRecipes;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

}
