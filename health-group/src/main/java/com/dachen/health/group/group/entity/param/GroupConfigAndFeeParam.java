package com.dachen.health.group.group.entity.param;

import java.io.Serializable;

/**
 * 
 * @author tan.yf
 *
 */
public class GroupConfigAndFeeParam implements Serializable {
	private static final long serialVersionUID = 1L;
	  /* 集团id */
    private String groupId;

    /* 图文咨询最低价 */
    private Integer textMin;

    /* 图文咨询最高价 */
    private Integer textMax;

    /* 电话咨询最低价 */
    private Integer phoneMin;

    /* 电话咨询最高价 */
    private Integer phoneMax;

    /* 门诊最低价 */
    private Integer clinicMin;

    /* 门诊最高价 */
    private Integer clinicMax;
    
    /* 计划关怀最低价*/
    private Integer carePlanMin;
    /* 计划关怀最高价*/
    private Integer carePlanMax;
    /*最低价*/
    private Integer appointmentMin;
    /*最高价*/
    private Integer appointmentMax;
    /*默认价*/
    private Integer appointmentDefault;
    
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
	
	/**
	 * 是否开启名医面对面
	 */
	private Boolean openAppointment;
	
	/**
	 * 更新|插入 类型 1、 名医面对面 2 、 图文咨询 3 、电话咨询 4、健康关怀 5、收费项
	 */
	private Integer type;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getTextMin() {
		return textMin;
	}

	public void setTextMin(Integer textMin) {
		this.textMin = textMin;
	}

	public Integer getTextMax() {
		return textMax;
	}

	public void setTextMax(Integer textMax) {
		this.textMax = textMax;
	}

	public Integer getPhoneMin() {
		return phoneMin;
	}

	public void setPhoneMin(Integer phoneMin) {
		this.phoneMin = phoneMin;
	}

	public Integer getPhoneMax() {
		return phoneMax;
	}

	public void setPhoneMax(Integer phoneMax) {
		this.phoneMax = phoneMax;
	}

	public Integer getClinicMin() {
		return clinicMin;
	}

	public void setClinicMin(Integer clinicMin) {
		this.clinicMin = clinicMin;
	}

	public Integer getClinicMax() {
		return clinicMax;
	}

	public void setClinicMax(Integer clinicMax) {
		this.clinicMax = clinicMax;
	}

	public Integer getCarePlanMin() {
		return carePlanMin;
	}

	public void setCarePlanMin(Integer carePlanMin) {
		this.carePlanMin = carePlanMin;
	}

	public Integer getCarePlanMax() {
		return carePlanMax;
	}

	public void setCarePlanMax(Integer carePlanMax) {
		this.carePlanMax = carePlanMax;
	}

	public Integer getAppointmentMin() {
		return appointmentMin;
	}

	public void setAppointmentMin(Integer appointmentMin) {
		this.appointmentMin = appointmentMin;
	}

	public Integer getAppointmentMax() {
		return appointmentMax;
	}

	public void setAppointmentMax(Integer appointmentMax) {
		this.appointmentMax = appointmentMax;
	}

	public Integer getAppointmentDefault() {
		return appointmentDefault;
	}

	public void setAppointmentDefault(Integer appointmentDefault) {
		this.appointmentDefault = appointmentDefault;
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

	public Boolean getOpenAppointment() {
		return openAppointment;
	}

	public void setOpenAppointment(Boolean openAppointment) {
		this.openAppointment = openAppointment;
	}
}
