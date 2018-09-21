package com.dachen.health.pack.order.entity.vo;

import com.dachen.drug.api.entity.CRecipeDetailView;

import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */
public class OrderSimpleInfoVO {

    private Integer id;
    
    private Integer orderType;

    //订单状态
    private Integer orderStatus;

    //会话状态
    private Integer sessionStatus;

    //所患疾病
    private String disease;
    
    //病症时长
    private String diseaseDuration;
    
    //病情描述
    private String diseaseDesc;
    
    //用药情况
    private String drugInfo;
    
    //希望获得医生什么帮助
    private String hopeHelp;
    
    //病情描述的图片
	private List<String> pics;
	
	//检查建议
	private List<String> checkSuggestNames;
	
	//用药建议
	private List<CRecipeDetailView> recipeDetailViews;
	
	//患者评价
	private List<String> evaluation;
	
	//所用药品名称
	private List<String> drugNames;
	
	//药品图片
	private List<String> drugPics;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(Integer sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public String getDiseaseDuration() {
		return diseaseDuration;
	}

	public void setDiseaseDuration(String diseaseDuration) {
		this.diseaseDuration = diseaseDuration;
	}

	public String getDiseaseDesc() {
		return diseaseDesc;
	}

	public void setDiseaseDesc(String diseaseDesc) {
		this.diseaseDesc = diseaseDesc;
	}

	public String getDrugInfo() {
		return drugInfo;
	}

	public void setDrugInfo(String drugInfo) {
		this.drugInfo = drugInfo;
	}

	public String getHopeHelp() {
		return hopeHelp;
	}

	public void setHopeHelp(String hopeHelp) {
		this.hopeHelp = hopeHelp;
	}

	public List<String> getPics() {
		return pics;
	}

	public void setPics(List<String> pics) {
		this.pics = pics;
	}

	public List<String> getCheckSuggestNames() {
		return checkSuggestNames;
	}

	public void setCheckSuggestNames(List<String> checkItem) {
		this.checkSuggestNames = checkItem;
	}

	public List<String> getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(List<String> evaluation) {
		this.evaluation = evaluation;
	}

	public List<CRecipeDetailView> getRecipeDetailViews() {
		return recipeDetailViews;
	}

	public void setRecipeDetailViews(List<CRecipeDetailView> recipeDetailViews) {
		this.recipeDetailViews = recipeDetailViews;
	}

	public List<String> getDrugNames() {
		return drugNames;
	}

	public void setDrugNames(List<String> drugNames) {
		this.drugNames = drugNames;
	}

	public List<String> getDrugPics() {
		return drugPics;
	}

	public void setDrugPics(List<String> drugPics) {
		this.drugPics = drugPics;
	}
	
}
