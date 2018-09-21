package com.dachen.health.group.group.entity.param;

import com.dachen.commons.page.PageVO;

/**
 * ProjectName： health-group<br>
 * ClassName： GroupProfitParam<br>
 * Description：抽成关系参数接收类 <br>
 * 
 * @author fanp
 * @createTime 2015年9月2日
 * @version 1.0.0
 */
public class GroupProfitParam extends PageVO{

    /* 集团id */
    private String groupId;

    /* id */
    private String id;
    
    private Integer doctorId;

    /* 父id */
    private Integer parentId;

    /* 抽成比例 */
    private Integer groupProfit;

    private Integer parentProfit;
    
    private Integer updator;

    private Long updatorDate;
    
    /**
     * 图文资讯 上级抽成比例
     */
    private Integer textParentProfit;

    /**
     * 图文资讯 集团抽成比例
     */
    private Integer textGroupProfit;
    
    /**
     * 电话资讯 上级抽成比例
     */
    private Integer phoneParentProfit;

    /**
     * 电话资讯 集团抽成比例
     */
    private Integer phoneGroupProfit;
    
    /**
     *  健康关怀 上级抽成比例
     */
    private Integer carePlanParentProfit;

    /**
     * 健康关怀 集团抽成比例
     */
    private Integer carePlanGroupProfit;
    
    /**
     * 门诊 上级抽成比例
     */
    private Integer clinicParentProfit;

    /**
     * 门诊 集团抽成比例
     */
    private Integer clinicGroupProfit;
    
	/**
	 * 会诊 集团抽成比例
	 */
	private Integer consultationGroupProfit;
	
	/**
	 * 会诊 上级抽成比例
	 */
	private Integer consultationParentProfit;
	
	/**
	 * 收费项集团抽成比例
	 */
	private Integer chargeItemGroupProfit;
	
	/**
	 * 收费项上级抽成比例
	 */
	private Integer chargeItemParentProfit;
	
	/**
	 * 名医面对面集团抽成比例
	 */
	private Integer appointmentGroupProfit;
	
	/**
	 * 名医面对面上级抽成比例
	 */
	private Integer appointmentParentProfit;
    
    /* 搜索关键字 */
    private String keyword;

    
    public Integer getAppointmentGroupProfit() {
		return appointmentGroupProfit;
	}

	public void setAppointmentGroupProfit(Integer appointmentGroupProfit) {
		this.appointmentGroupProfit = appointmentGroupProfit;
	}

	public Integer getAppointmentParentProfit() {
		return appointmentParentProfit;
	}

	public void setAppointmentParentProfit(Integer appointmentParentProfit) {
		this.appointmentParentProfit = appointmentParentProfit;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }



    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getGroupProfit() {
        return groupProfit;
    }

    public void setGroupProfit(Integer groupProfit) {
        this.groupProfit = groupProfit;
    }

    public Integer getParentProfit() {
        return parentProfit;
    }

    public void setParentProfit(Integer parentProfit) {
        this.parentProfit = parentProfit;
    }

	public Integer getTextParentProfit() {
		return textParentProfit;
	}

	public void setTextParentProfit(Integer textParentProfit) {
		this.textParentProfit = textParentProfit;
	}

	public Integer getTextGroupProfit() {
		return textGroupProfit;
	}

	public void setTextGroupProfit(Integer textGroupProfit) {
		this.textGroupProfit = textGroupProfit;
	}

	public Integer getPhoneParentProfit() {
		return phoneParentProfit;
	}

	public void setPhoneParentProfit(Integer phoneParentProfit) {
		this.phoneParentProfit = phoneParentProfit;
	}

	public Integer getPhoneGroupProfit() {
		return phoneGroupProfit;
	}

	public void setPhoneGroupProfit(Integer phoneGroupProfit) {
		this.phoneGroupProfit = phoneGroupProfit;
	}

	public Integer getCarePlanParentProfit() {
		return carePlanParentProfit;
	}

	public void setCarePlanParentProfit(Integer carePlanParentProfit) {
		this.carePlanParentProfit = carePlanParentProfit;
	}

	public Integer getCarePlanGroupProfit() {
		return carePlanGroupProfit;
	}

	public void setCarePlanGroupProfit(Integer carePlanGroupProfit) {
		this.carePlanGroupProfit = carePlanGroupProfit;
	}

	public Integer getClinicParentProfit() {
		return clinicParentProfit;
	}

	public void setClinicParentProfit(Integer clinicParentProfit) {
		this.clinicParentProfit = clinicParentProfit;
	}

	public Integer getClinicGroupProfit() {
		return clinicGroupProfit;
	}

	public void setClinicGroupProfit(Integer clinicGroupProfit) {
		this.clinicGroupProfit = clinicGroupProfit;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
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

	public Integer getConsultationGroupProfit() {
		return consultationGroupProfit;
	}

	public void setConsultationGroupProfit(Integer consultationGroupProfit) {
		this.consultationGroupProfit = consultationGroupProfit;
	}

	public Integer getConsultationParentProfit() {
		return consultationParentProfit;
	}

	public void setConsultationParentProfit(Integer consultationParentProfit) {
		this.consultationParentProfit = consultationParentProfit;
	}

	public Integer getChargeItemGroupProfit() {
		return chargeItemGroupProfit;
	}

	public void setChargeItemGroupProfit(Integer chargeItemGroupProfit) {
		this.chargeItemGroupProfit = chargeItemGroupProfit;
	}

	public Integer getChargeItemParentProfit() {
		return chargeItemParentProfit;
	}

	public void setChargeItemParentProfit(Integer chargeItemParentProfit) {
		this.chargeItemParentProfit = chargeItemParentProfit;
	}

	
	
}
