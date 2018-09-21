package com.dachen.health.pack.order.entity.vo;

import java.util.List;

import com.dachen.health.pack.consult.entity.vo.IllCaseInfoPageVo;
import com.dachen.util.JSONUtil;

public class OrderDetailVersion2 {

	private Integer orderId;
	
	private String msgGroupId;

	private Integer orderType;
	
	private Long appointmentStart;
	
	private Long appointmentEnd;
	
	private Long serviceBeginTime;
	
	private Long createTime;
	
	private Long finishTime;
	
	private Long payTime;
	
	private Integer payType;
	
	private Long price;
	
    private Integer orderNo;
    
    private String hospitalName;
    
    private String hospitalId;
	
    private Integer orderStatus;
    
    private Integer packType;
    
    private Integer timeLong;
    
    private Integer patientUserId;
    
    private String patientTelephone;
    
    private String patientUserName;
    
    private String patientUserRelation;
    
    private Integer patientId;
    
    private String patientName;
    
	private String patientHeight;

	private String patientWeight;

	private String patientMarriage;

	private String patientProfessional;
    
    private String ageStr;
    
    private String area;
    
    private Short sex;
    
    private String headPicFileName;

    private Integer packId;
    
	private String idcard;
	/**
	 * 身份证类型  1身份证 2护照  3军官  4 台胞  5香港身份证 
	 */
	private String idtype;

    private List<OrderDoctorDetail> doctors;

	/**
	 * 积分问诊套餐名称
	 */
	private String pointName;

	/**
	 * 积分问诊点数
	 */
	private Integer point;
    
    /**
     * 备注
     */
    private String remarks;
    
    private String cancelReason;
    
    private String cancelFrom;
    
    private String illCaseInfoId ;

	/**
	 * 新版电子病历id
	 */
	private String illHistoryInfoId;
    
    private IllCaseInfoPageVo illCaseInfoPageVo;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getTimeLong() {
		return timeLong;
	}

	public void setTimeLong(Integer timeLong) {
		this.timeLong = timeLong;
	}

	public Integer getPatientUserId() {
		return patientUserId;
	}

	public void setPatientUserId(Integer patientUserId) {
		this.patientUserId = patientUserId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getAgeStr() {
		return ageStr;
	}

	public void setAgeStr(String ageStr) {
		this.ageStr = ageStr;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Short getSex() {
		return sex;
	}

	public void setSex(Short sex) {
		this.sex = sex;
	}

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}

	public List<OrderDoctorDetail> getDoctors() {
		return doctors;
	}

	public void setDoctors(List<OrderDoctorDetail> doctors) {
		this.doctors = doctors;
	}

	public String getMsgGroupId() {
		return msgGroupId;
	}

	public void setMsgGroupId(String msgGroupId) {
		this.msgGroupId = msgGroupId;
	}

	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getPackType() {
		return packType;
	}

	public void setPackType(Integer packType) {
		this.packType = packType;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getPatientUserName() {
		return patientUserName;
	}

	public void setPatientUserName(String patientUserName) {
		this.patientUserName = patientUserName;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public Long getAppointmentStart() {
		return appointmentStart;
	}

	public void setAppointmentStart(Long appointmentStart) {
		this.appointmentStart = appointmentStart;
	}

	public Long getAppointmentEnd() {
		return appointmentEnd;
	}

	public void setAppointmentEnd(Long appointmentEnd) {
		this.appointmentEnd = appointmentEnd;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getIdtype() {
		return idtype;
	}

	public void setIdtype(String idtype) {
		this.idtype = idtype;
	}

	public String getPatientTelephone() {
		return patientTelephone;
	}

	public void setPatientTelephone(String patientTelephone) {
		this.patientTelephone = patientTelephone;
	}

	public String getPatientUserRelation() {
		return patientUserRelation;
	}

	public void setPatientUserRelation(String patientUserRelation) {
		this.patientUserRelation = patientUserRelation;
	}

	public Long getServiceBeginTime() {
		return serviceBeginTime;
	}

	public void setServiceBeginTime(Long serviceBeginTime) {
		this.serviceBeginTime = serviceBeginTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	

	public String getIllCaseInfoId() {
		return illCaseInfoId;
	}

	public void setIllCaseInfoId(String illCaseInfoId) {
		this.illCaseInfoId = illCaseInfoId;
	}

	
	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Long finishTime) {
		this.finishTime = finishTime;
	}

	public Long getPayTime() {
		return payTime;
	}

	public void setPayTime(Long payTime) {
		this.payTime = payTime;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getCancelFrom() {
		return cancelFrom;
	}

	public void setCancelFrom(String cancelFrom) {
		this.cancelFrom = cancelFrom;
	}

	public IllCaseInfoPageVo getIllCaseInfoPageVo() {
		return illCaseInfoPageVo;
	}

	public void setIllCaseInfoPageVo(IllCaseInfoPageVo illCaseInfoPageVo) {
		this.illCaseInfoPageVo = illCaseInfoPageVo;
	}

	
	
	public String getPatientHeight() {
		return patientHeight;
	}

	public void setPatientHeight(String patientHeight) {
		this.patientHeight = patientHeight;
	}

	public String getPatientWeight() {
		return patientWeight;
	}

	public void setPatientWeight(String patientWeight) {
		this.patientWeight = patientWeight;
	}

	public String getPatientMarriage() {
		return patientMarriage;
	}

	public void setPatientMarriage(String patientMarriage) {
		this.patientMarriage = patientMarriage;
	}

	public String getPatientProfessional() {
		return patientProfessional;
	}

	public void setPatientProfessional(String patientProfessional) {
		this.patientProfessional = patientProfessional;
	}

	
	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public Integer getPackId() {
		return packId;
	}

	public void setPackId(Integer packId) {
		this.packId = packId;
	}

	public String getIllHistoryInfoId() {
		return illHistoryInfoId;
	}

	public void setIllHistoryInfoId(String illHistoryInfoId) {
		this.illHistoryInfoId = illHistoryInfoId;
	}
}
