package com.dachen.health.group.group.entity.po;

import java.io.Serializable;
import java.util.List;

import com.dachen.health.group.group.entity.vo.HospitalInfo;
import com.dachen.util.JSONUtil;

/**
 * 
 * @author pijingwei
 *
 */
public class GroupConfig implements Serializable {

	private static final long serialVersionUID = -5757972681265947553L;

	
	/**
	 * 是否允许医生搜索加入
	 */
	private boolean memberApply;
	
	/**
	 * 是否允许成员邀请医生加入
	 */
	private boolean memberInvite;
	
	/**
	 * 成员邀请是否需要审核
	 */
	private boolean passByAudit;
	
	/**
	 * 集团抽成比例
	 */
	private Integer groupProfit;
	
	public static final Integer GROUP_PROFIT_DEFAULT = 0;
	
	/**
	 * 上级抽成比例
	 */
	private Integer parentProfit;
	
	public static final Integer PARENT_PROFIT_DEFAULT = 0;
	
	/**
	 * 图文咨询 集团抽成比例
	 */
	private Integer textGroupProfit;
	
	public static final Integer TEXT_GROUP_PROFIT_DEFAULT = 0;
	
	/**
	 * 图文咨询 上级抽成比例
	 */
	private Integer textParentProfit;
	
	public static final Integer TEXT_PARENT_PROFIT_DEFAULT = 0;
	
	/**
	 * 电话咨询 集团抽成比例
	 */
	private Integer phoneGroupProfit;
	
	public static final Integer PHONE_GROUP_PROFIT_DEFAULT = 0;
	
	/**
	 * 电话咨询 上级抽成比例
	 */
	private Integer phoneParentProfit;
	
	public static final Integer PHONE_PARENT_PROFIT_DEFAULT = 0;
	
	/**
	 * 关怀计划 集团抽成比例
	 */
	private Integer carePlanGroupProfit;
	
	public static final Integer CARE_PLAN_GROUP_PROFIT_DEFAULT = 0;
	
	/**
	 * 关怀计划 上级抽成比例
	 */
	private Integer carePlanParentProfit;
	
	public static final Integer CARE_PLAN_PARENT_PROFIT_DEFAULT = 0;
	
	/**
	 * 门诊 集团抽成比例
	 */
	private Integer clinicGroupProfit;
	
	public static final Integer CLINIC_GROUP_PROFIT_DEFAULT = 0;
	
	/**
	 * 门诊 上级抽成比例
	 */
	private Integer clinicParentProfit;
	
	public static final Integer CLINIC_PARENT_PROFIT_DEFAULT = 0;
	
	/**
	 * 会诊 集团抽成比例
	 */
	private Integer consultationGroupProfit;
	
	public static final Integer CONSULTATION_GROUP_PROFIT_DEFAULT = 0;
	
	/**
	 * 会诊 上级抽成比例
	 */
	private Integer consultationParentProfit;
	
	public static final Integer CONSULTATION_PARENT_PROFIT_DEFAULT = 0;
	
	/**
	 * 线下预约集团抽成比例
	 */
	private Integer appointmentGroupProfit;
	
	public static final Integer APPOINTMENT_GROUP_PROFIT_DEFAULT = 5;
	
	/**
	 * 线下预约上级抽成比例
	 */
	private Integer appointmentParentProfit;
	
	public static final Integer APPOINTMENT_PARENT_PROFIT_DEFAULT = 5;
	
	/**
	 * 收费项集团抽成比例
	 */
	private Integer chargeItemGroupProfit;
	
	public static final Integer CHARGE_ITEM_GROUP_PROFIT_DEFAULT = 5;
	
	/**
	 * 收费项上级抽成比例
	 */
	private Integer chargeItemParentProfit;
	
	public static final Integer CHARGE_ITEM_PARENT_PROFIT_DEFAULT = 5;
	
	
	/**
	 * 集团的执业医院Id集合
	 */
	private List<String> hospitalIds;
	
	public List<HospitalInfo> getHospitalInfo() {
		return hospitalInfo;
	}

	public void setHospitalInfo(List<HospitalInfo> hospitalInfo) {
		this.hospitalInfo = hospitalInfo;
	}

	private  List<HospitalInfo> hospitalInfo;
	
	/**
	 * 是否开通会诊设置
	 */
	private boolean openConsultation;
	
	/**
	 * 是否开通线下预约服务
	 */
    private boolean openAppointment ;
    
    /**
     * 博德嘉联默认开通
     */
    private boolean openSelfGuide ;
    
	// 
	/**
	 * 值班开始时间
	 * 创建时，再设置默认值
	 * @author wangqiao
	 * @date 2016年3月7日
	 */
	private String dutyStartTime ;

	
	/**
	 * 值班结束时间
	 * 值班时间(24时制) 默认早8点到晚22点
	 * @author wangqiao
	 * @date 2016年3月7日
	 */
	private String dutyEndTime ;
    
	public String getDutyStartTime() {
		return dutyStartTime;
	}

	public void setDutyStartTime(String dutyStartTime) {
		this.dutyStartTime = dutyStartTime;
	}

	public String getDutyEndTime() {
		return dutyEndTime;
	}

	public void setDutyEndTime(String dutyEndTime) {
		this.dutyEndTime = dutyEndTime;
	}

	public boolean isMemberInvite() {
		return memberInvite;
	}

	public void setMemberInvite(boolean memberInvite) {
		this.memberInvite = memberInvite;
	}

	public boolean isPassByAudit() {
		return passByAudit;
	}

	public void setPassByAudit(boolean passByAudit) {
		this.passByAudit = passByAudit;
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

	public boolean isMemberApply() {
		return memberApply;
	}

	public void setMemberApply(boolean memberApply) {
		this.memberApply = memberApply;
	}

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

	public boolean getOpenConsultation() {
		return openConsultation;
	}

	public void setOpenConsultation(boolean openConsultation) {
		this.openConsultation = openConsultation;
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

	public List<String> getHospitalIds() {
		return hospitalIds;
	}

	public void setHospitalIds(List<String> hospitalIds) {
		this.hospitalIds = hospitalIds;
	}

	public boolean isOpenAppointment() {
		return openAppointment;
	}

	public void setOpenAppointment(boolean openAppointment) {
		this.openAppointment = openAppointment;
	}

	public boolean isOpenSelfGuide() {
		return openSelfGuide;
	}

	public void setOpenSelfGuide(boolean openSelfGuide) {
		this.openSelfGuide = openSelfGuide;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
	
}
