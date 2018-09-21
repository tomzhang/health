package com.dachen.health.pack.schedule.entity.vo;

import java.util.List;

import com.dachen.health.pack.conference.entity.vo.ConfListVO;
import com.dachen.health.pack.order.entity.vo.OrderDetailVO;

public class ScheduleVO {
	
	private String scheduleTime;
	
	private Integer scheduleType;

	private String title;
	
	private String price;
	//患者名
	private String patientName;
	
	private Integer patientId;
	
	private String patientHeadIcon;
	
	private String patientTele;
	//医生名
	private String doctorName;
	
	private Integer doctorId;
	
	private String doctorHeadIcon;
	
	private String doctorTele;
	
	private String troubleFree;
	
	private String hospital;
	
	private String clinicType;
	
	private String offlineName;
	
	private String relationId;
	
	private Long appointTime;
	
	private Integer orderId;
	
	private String carePlanName;
	
	private String careItemId;
	
	/**
	 * 订单详情
	 */
	private OrderDetailVO orderDetail;
	
	/**
	 * 通话记录
	 */
	private List<ConfListVO> callRecordList;
	
	/**
	 * 导医日程卡片状态（0未拨打、1拨打成功、2拨打失败）
	 */
	private Integer flag;
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOfflineName() {
		return offlineName;
	}

	public void setOfflineName(String offlineName) {
		this.offlineName = offlineName;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getPatientHeadIcon() {
		return patientHeadIcon;
	}

	public void setPatientHeadIcon(String patientHeadIcon) {
		this.patientHeadIcon = patientHeadIcon;
	}

	public String getPatientTele() {
		return patientTele;
	}

	public void setPatientTele(String patientTele) {
		this.patientTele = patientTele;
	}

	public String getDoctorHeadIcon() {
		return doctorHeadIcon;
	}

	public void setDoctorHeadIcon(String doctorHeadIcon) {
		this.doctorHeadIcon = doctorHeadIcon;
	}

	public String getDoctorTele() {
		return doctorTele;
	}

	public void setDoctorTele(String doctorTele) {
		this.doctorTele = doctorTele;
	}

	public String getTroubleFree() {
		return troubleFree;
	}

	public void setTroubleFree(String troubleFree) {
		this.troubleFree = troubleFree;
	}

	public String getClinicType() {
		return clinicType;
	}

	public void setClinicType(String clinicType) {
		this.clinicType = clinicType;
	}

	public String getRelationId() {
		return relationId;
	}

	public void setRelationId(String relationId) {
		this.relationId = relationId;
	}

	public Long getAppointTime() {
		return appointTime;
	}

	public void setAppointTime(Long appointTime) {
		this.appointTime = appointTime;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getCarePlanName() {
		return carePlanName;
	}

	public void setCarePlanName(String carePlanName) {
		this.carePlanName = carePlanName;
	}

	public String getCareItemId() {
		return careItemId;
	}

	public void setCareItemId(String careItemId) {
		this.careItemId = careItemId;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public Integer getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(Integer scheduleType) {
		this.scheduleType = scheduleType;
	}

	public OrderDetailVO getOrderDetail() {
		return orderDetail;
	}

	public void setOrderDetail(OrderDetailVO orderDetail) {
		this.orderDetail = orderDetail;
	}

	public List<ConfListVO> getCallRecordList() {
		return callRecordList;
	}

	public void setCallRecordList(List<ConfListVO> callRecordList) {
		this.callRecordList = callRecordList;
	}


}
