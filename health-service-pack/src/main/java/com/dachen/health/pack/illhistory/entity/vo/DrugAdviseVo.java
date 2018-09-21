package com.dachen.health.pack.illhistory.entity.vo;

import java.util.List;

/**
 * Created by fuyongde on 2016/12/21.
 */
public class DrugAdviseVo {
    private String id;
    private List<RecipeDetail> recipeDetailList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<RecipeDetail> getRecipeDetailList() {
        return recipeDetailList;
    }

    public void setRecipeDetailList(List<RecipeDetail> recipeDetailList) {
        this.recipeDetailList = recipeDetailList;
    }
}
