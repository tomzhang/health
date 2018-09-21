package com.dachen.health.group.group.entity.po;

import java.io.Serializable;

/**
 * 集团中某个医生的抽成比例 配置信息，对应c_group_profit中 config属性
 *@author wangqiao
 *@date 2015年12月26日
 *
 */
public class GroupProfitConfig implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 图文咨询 集团抽成比例
	 */
	private Integer textGroupProfit;
	
	/**
	 * 图文咨询 上级抽成比例
	 */
	private Integer textParentProfit;
	
	/**
	 * 电话咨询 集团抽成比例
	 */
	private Integer phoneGroupProfit;
	
	/**
	 * 电话咨询 上级抽成比例
	 */
	private Integer phoneParentProfit;
	
	/**
	 * 关怀计划 集团抽成比例
	 */
	private Integer carePlanGroupProfit;
	
	/**
	 * 关怀计划 上级抽成比例
	 */
	private Integer carePlanParentProfit;
	
	/**
	 * 门诊 集团抽成比例
	 */
	private Integer clinicGroupProfit;
	
	/**
	 * 门诊 上级抽成比例
	 */
	private Integer clinicParentProfit;
	
	/**
	 * 会诊 集团抽成比例
	 */
	private Integer consultationGroupProfit;
	
	/**
	 * 会诊 上级抽成比例
	 */
	private Integer consultationParentProfit;
	
	/**
	 * 线下预约集团抽成比例
	 */
	private Integer appointmentGroupProfit;
	
	/**
	 * 线下预约上级抽成比例
	 */
	private Integer appointmentParentProfit;
	
	/**
	 * 收费项集团抽成比例
	 */
	private Integer chargeItemGroupProfit;
	
	/**
	 * 收费项上级抽成比例
	 */
	private Integer chargeItemParentProfit;

	public Integer getTextGroupProfit() {
		return textGroupProfit;
	}

	public void setTextGroupProfit(Integer textGroupProfit) {
		this.textGroupProfit = textGroupProfit;
	}

	public Integer getTextParentProfit() {
		return textParentProfit;
	}

	public void setTextParentProfit(Integer textParentProfit) {
		this.textParentProfit = textParentProfit;
	}

	public Integer getPhoneGroupProfit() {
		return phoneGroupProfit;
	}

	public void setPhoneGroupProfit(Integer phoneGroupProfit) {
		this.phoneGroupProfit = phoneGroupProfit;
	}

	public Integer getPhoneParentProfit() {
		return phoneParentProfit;
	}

	public void setPhoneParentProfit(Integer phoneParentProfit) {
		this.phoneParentProfit = phoneParentProfit;
	}

	public Integer getCarePlanGroupProfit() {
		return carePlanGroupProfit;
	}

	public void setCarePlanGroupProfit(Integer carePlanGroupProfit) {
		this.carePlanGroupProfit = carePlanGroupProfit;
	}

	public Integer getCarePlanParentProfit() {
		return carePlanParentProfit;
	}

	public void setCarePlanParentProfit(Integer carePlanParentProfit) {
		this.carePlanParentProfit = carePlanParentProfit;
	}

	public Integer getClinicGroupProfit() {
		return clinicGroupProfit;
	}

	public void setClinicGroupProfit(Integer clinicGroupProfit) {
		this.clinicGroupProfit = clinicGroupProfit;
	}

	public Integer getClinicParentProfit() {
		return clinicParentProfit;
	}

	public void setClinicParentProfit(Integer clinicParentProfit) {
		this.clinicParentProfit = clinicParentProfit;
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

