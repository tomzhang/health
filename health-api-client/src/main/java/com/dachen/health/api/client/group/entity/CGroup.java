package com.dachen.health.api.client.group.entity;

import java.io.Serializable;
import java.util.List;

public class CGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

    /**
     * 公司Id--所属公司
     */
    private String companyId;
    
    /**
     * 加V审核时间
     */
    private Long processVTime;

    /**
     * 集团名称
     */
    private String name;

    /**
     * 集团介绍
     */
    private String introduction;

    /**
     * 创建人
     */
    private Integer creator;
    
    /**
     * 创建人名字
     */
    private String creatorName;

	/**
     * 创建时间
     */
    private Long creatorDate;

    /**
     * 更新人
     */
    private Integer updator;

    /**
     * 更新时间
     */
    private Long updatorDate;

    /* 就诊人数 */
    private Integer cureNum;

    /* 权重 */
    private Integer weight;

    private List<String> diseaselist;
    /*设置预约专家ID*/
    private List<Integer> expertIds;
    
    //值班价格
  	private Integer outpatientPrice ;
    
    /**
     * 公司认证状态，对应web后台-设置-公司认证
     * @see GroupEnum.GroupCertStatus
     */
    private String certStatus;
    
    /**
     * 公司是否屏蔽
     */
    private String skip;
    
    /**
     * active=已激活，inactive=未激活
     */
    private String active;
    
    /**
     * 集团申请状态
     * A=待审核，P=审核通过，NP=审核不通过
     * @author wangqiao
     * @date 2016年3月4日
     */
    private String applyStatus;
    
    /**
     * 集团logo
     */
    private String logoUrl;

    private Integer ifMain;

    public Integer getIfMain() {
        return ifMain;
    }

    public void setIfMain(Integer ifMain) {
        this.ifMain = ifMain;
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public Long getProcessVTime() {
		return processVTime;
	}

	public void setProcessVTime(Long processVTime) {
		this.processVTime = processVTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Long getCreatorDate() {
		return creatorDate;
	}

	public void setCreatorDate(Long creatorDate) {
		this.creatorDate = creatorDate;
	}

	public Integer getUpdator() {
		return updator;
	}

	public void setUpdator(Integer updator) {
		this.updator = updator;
	}

	public Long getUpdatorDate() {
		return updatorDate;
	}

	public void setUpdatorDate(Long updatorDate) {
		this.updatorDate = updatorDate;
	}

	public Integer getCureNum() {
		return cureNum;
	}

	public void setCureNum(Integer cureNum) {
		this.cureNum = cureNum;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public List<String> getDiseaselist() {
		return diseaselist;
	}

	public void setDiseaselist(List<String> diseaselist) {
		this.diseaselist = diseaselist;
	}

	public List<Integer> getExpertIds() {
		return expertIds;
	}

	public void setExpertIds(List<Integer> expertIds) {
		this.expertIds = expertIds;
	}

	public Integer getOutpatientPrice() {
		return outpatientPrice;
	}

	public void setOutpatientPrice(Integer outpatientPrice) {
		this.outpatientPrice = outpatientPrice;
	}

	public String getCertStatus() {
		return certStatus;
	}

	public void setCertStatus(String certStatus) {
		this.certStatus = certStatus;
	}

	public String getSkip() {
		return skip;
	}

	public void setSkip(String skip) {
		this.skip = skip;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getApplyStatus() {
		return applyStatus;
	}

	public void setApplyStatus(String applyStatus) {
		this.applyStatus = applyStatus;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
    
}
