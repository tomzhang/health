package com.dachen.health.pack.illhistory.entity.vo;

/**
 * 用药建议详情
 * Created by fuyongde on 2016/12/21.
 */
public class RecipeDetail {
    /**每次服药数量**/
    private String doseQuantity;
    /**服药方法**/
    private String doseMothor;
    /**服药持续天数**/
    private Integer doseDays;
    /**服药方法**/
    private String doseMothod;
    /**每次服药单位**/
    private String doseUnit;
    /**每次服药单位名称**/
    private String doseUnitName;
    /**药品id**/
    private String goodsId;
    private String goodsTitle;
    /**药品名称**/
    private String goodsTradeName;
    /**通用名称**/
    private String goodsGeneralName;
    /**药品数量（应购数量）**/
    private Integer goodsNumber;
    /**id**/
    private String id;
    /**用药周期长度**/
    private Integer periodNum;
    /**用药周期服药次数**/
    private Integer periodTimes;
    /**用药周期单位（天、周、月）**/
    private String periodUnit;
    /**recipeId**/
    private String recipeId;
    /**适用人群**/
    private String patients;

    public String getPatients() {
        return patients;
    }

    public void setPatients(String patients) {
        this.patients = patients;
    }

    public String getDoseMothor() {
        return doseMothor;
    }

    public void setDoseMothor(String doseMothor) {
        this.doseMothor = doseMothor;
    }

    public String getDoseUnitName() {
        return doseUnitName;
    }

    public void setDoseUnitName(String doseUnitName) {
        this.doseUnitName = doseUnitName;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public String getGoodsTradeName() {
        return goodsTradeName;
    }

    public void setGoodsTradeName(String goodsTradeName) {
        this.goodsTradeName = goodsTradeName;
    }

    public String getGoodsGeneralName() {
        return goodsGeneralName;
    }

    public void setGoodsGeneralName(String goodsGeneralName) {
        this.goodsGeneralName = goodsGeneralName;
    }

    public Integer getDoseDays() {
        return doseDays;
    }

    public void setDoseDays(Integer doseDays) {
        this.doseDays = doseDays;
    }

    public String getDoseMothod() {
        return doseMothod;
    }

    public void setDoseMothod(String doseMothod) {
        this.doseMothod = doseMothod;
    }

    public String getDoseUnit() {
        return doseUnit;
    }

    public void setDoseUnit(String doseUnit) {
        this.doseUnit = doseUnit;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(Integer goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPeriodNum() {
        return periodNum;
    }

    public void setPeriodNum(Integer periodNum) {
        this.periodNum = periodNum;
    }

    public Integer getPeriodTimes() {
        return periodTimes;
    }

    public void setPeriodTimes(Integer periodTimes) {
        this.periodTimes = periodTimes;
    }

    public String getPeriodUnit() {
        return periodUnit;
    }

    public void setPeriodUnit(String periodUnit) {
        this.periodUnit = periodUnit;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getDoseQuantity() {
        return doseQuantity;
    }

    public void setDoseQuantity(String doseQuantity) {
        this.doseQuantity = doseQuantity;
    }
}
