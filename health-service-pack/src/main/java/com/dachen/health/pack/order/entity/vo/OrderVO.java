package com.dachen.health.pack.order.entity.vo;

import java.util.List;

import com.dachen.health.base.helper.UserHelper;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.user.entity.vo.NurseVO;
import com.dachen.util.DateUtil;
import com.dachen.util.JSONUtil;

public class OrderVO {
	/* 订单信息 */
	private Integer orderId;

	private Integer orderType;
	private String orderTypeStr;

	private Integer packType;

	private Integer timeLong;

	private Integer price;

	private Integer packId;

	private String packName;

	private Long money = 0l;

	private Integer payNo;

	private String groupId;

	private String groupName;

	private String formateTime;// 格式化后的时间

	/* 订单状态 1:待预约，2：待支付，3：已支付，4：已完成，5：已取消 {@link OrderEnum.OrderStatus} */
	private Integer orderStatus;
	private String orderStatusStr;

	private Integer refundStatus;

	private Long createTime;

	private Long appointTime;

	private Long serviceBeginTime;

	private Long serviceEndTime;

	/* 患者信息 */
	private Integer patientId;

	private String patientName;

	private Short sex;

	private Integer age;

	private String patientAge;

	private String telephone;
	/**
	 * 原始的电话号码
	 */
	private String ysTelphone;

	private Long birthday;

	private String topPath;

	/* 病情信息 */
	private Integer diseaseId;

	// 医生信息
	private Integer doctorId;

	private String doctorName;

	private String userName;

	private Integer userId;

	private String area;

	private String relation;

	private Integer orderNo;

	// 医生信息
	private DoctorVO doctorVo;

	// 会诊订单会返回两个医生信息
	private List<DoctorVO> doctorVos;

	private NurseVO nurseVo;

	private UserVO userVo;

	private CheckInVO checkInVo;

	private OrderSession orderSession;

	private Integer preOrderStatus;

	private String appointStrTime;

	private Long payTime;

	private Integer payType;
	private String payTypeName;
	private Integer recordStatus;
	// 会议结束时间
	private String conferenceStopTime;

	private String remarks;

	private String hospitalId;

	private String hospitalName;

	/**
	 * 电子病历id
	 */
	private String illCaseInfoId;

	/**
	 * 转诊医生名称
	 */
	private String transferDoctorName;

	/**
	 * 接诊医生名称
	 */
	private String receiveDoctorName;

	private String cancelReason;// 取消原因

	private String offlineItemId;

	/**
	 * 医助id
	 */
	private Integer assistantId;

	/**
	 * 积分问诊套餐名称
	 */
	private String pointName;

	/**
	 * 积分问诊点数
	 */
	private Integer point;

	//订单待处理状态：1.待处理，0.其他
	private Integer pendingOrderStatus;

	//订单待处理类型：1.图文订单-医生未回复，2.电话订单-医生未开始，3.电话订单-医生未结束，4.健康关怀-患者未答题
	private Integer pendingOrderWaitType;

	//等待时间
	private Long waitTime;

	/**
     * 新版电子病历id
     */
	private String illHistoryInfoId;
	
	/**
	 * 助手备注
	 */
	private String assistantComment;
	
	/**
	 * 电话类型患者期望预约时间
	 */
	private String expectAppointment;
	
	
	public Integer getPendingOrderStatus() {
		return pendingOrderStatus;
	}

	public void setPendingOrderStatus(Integer pendingOrderStatus) {
		this.pendingOrderStatus = pendingOrderStatus;
	}

	public Integer getPendingOrderWaitType() {
		return pendingOrderWaitType;
	}

	public void setPendingOrderWaitType(Integer pendingOrderWaitType) {
		this.pendingOrderWaitType = pendingOrderWaitType;
	}

	public Long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(Long waitTime) {
		this.waitTime = waitTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public String getConferenceStopTime() {
		return conferenceStopTime;
	}

	public void setConferenceStopTime(String conferenceStopTime) {
		this.conferenceStopTime = conferenceStopTime;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPatientAge() {
		if (birthday == null) {
			return null;
		}
		age = DateUtil.calcAge(birthday);
		if (age == 0) {
			return DateUtil.calcMonth(birthday) == 0 ? "1个月" : DateUtil.calcMonth(birthday) + "个月";
		}
		return age + "岁";
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Long getPayTime() {
		return payTime;
	}

	public void setPayTime(Long payTime) {
		this.payTime = payTime;
	}

	public String getAppointStrTime() {
		return appointStrTime;
	}

	public void setAppointStrTime(String appointStrTime) {
		this.appointStrTime = appointStrTime;
	}

	/*
	 * 会话状态 1:待预约，2：待支付，3：已支付，4：已完成，5：已取消,17已支付服务中 {@link OrderEnum.OrderStatus}
	 */
	private Integer orderSessionStatus;

	private String msgGroupId;

	public Integer getPreOrderStatus() {
		return preOrderStatus;
	}

	public void setPreOrderStatus(Integer preOrderStatus) {
		this.preOrderStatus = preOrderStatus;
	}

	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public String getMsgGroupId() {
		return msgGroupId;
	}

	public void setMsgGroupId(String msgGroupId) {
		this.msgGroupId = msgGroupId;
	}

	public Long getAppointTime() {
		return appointTime;
	}

	public void setAppointTime(Long appointTime) {
		this.appointTime = appointTime;
	}

	public Long getServiceEndTime() {
		return serviceEndTime;
	}

	public void setServiceEndTime(Long serviceEndTime) {
		this.serviceEndTime = serviceEndTime;
	}

	public CheckInVO getCheckInVo() {
		return checkInVo;
	}

	public void setCheckInVo(CheckInVO checkInVo) {
		this.checkInVo = checkInVo;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public UserVO getUserVo() {
		return userVo;
	}

	public void setUserVo(UserVO userVo) {
		this.userVo = userVo;
	}

	public DoctorVO getDoctorVo() {
		return doctorVo;
	}

	public void setDoctorVo(DoctorVO doctorVo) {
		this.doctorVo = doctorVo;
	}

	public void setTimeLong(Integer timeLong) {
		this.timeLong = timeLong;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getTopPath() {
		if (topPath == null)
			return UserHelper.buildHeaderPicPath("", null);
		else
			return topPath;
	}

	public void setTopPath(String topPath) {
		this.topPath = topPath;
	}

	public Integer getPayNo() {
		return payNo;
	}

	public void setPayNo(Integer payNo) {
		this.payNo = payNo;
	}

	public Long getMoney() {
		return money;
	}

	public void setMoney(Long money) {
		this.money = money;
	}

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getPackType() {
		return packType;
	}

	public void setPackType(Integer packType) {
		this.packType = packType;
	}

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public Integer getTimeLong() {
		return timeLong;
	}

	/*
	 * public void setTimeLong(Integer timeLong) { this.timeLong = timeLong; }
	 */

	public Integer getPrice() {
		return (money.intValue());
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getPackId() {
		return packId;
	}

	public void setPackId(Integer packId) {
		this.packId = packId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public Short getSex() {
		return sex;
	}

	public void setSex(Short sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		age = DateUtil.calcAge(birthday);
		return age;
	}

	public Integer getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(Integer diseaseId) {
		this.diseaseId = diseaseId;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(Integer refundStatus) {
		this.refundStatus = refundStatus;
	}

	public OrderSession getOrderSession() {
		return orderSession;
	}

	public void setOrderSession(OrderSession orderSession) {
		this.orderSession = orderSession;
	}

	public Integer getOrderSessionStatus() {
		return orderSessionStatus;
	}

	public void setOrderSessionStatus(Integer orderSessionStatus) {
		this.orderSessionStatus = orderSessionStatus;
	}

	public Integer getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(Integer recordStatus) {
		this.recordStatus = recordStatus;
	}

	public NurseVO getNurseVo() {
		return nurseVo;
	}

	public void setNurseVo(NurseVO nurseVo) {
		this.nurseVo = nurseVo;
	}

	public List<DoctorVO> getDoctorVos() {
		return doctorVos;
	}

	public void setDoctorVos(List<DoctorVO> doctorVos) {
		this.doctorVos = doctorVos;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getFormateTime() {
		return formateTime;
	}

	public void setFormateTime(String formateTime) {
		this.formateTime = formateTime;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getOrderTypeStr() {
		return orderTypeStr;
	}

	public void setOrderTypeStr(String orderTypeStr) {
		this.orderTypeStr = orderTypeStr;
	}

	public String getOrderStatusStr() {
		return orderStatusStr;
	}

	public void setOrderStatusStr(String orderStatusStr) {
		this.orderStatusStr = orderStatusStr;
	}

	public String getIllCaseInfoId() {
		return illCaseInfoId;
	}

	public void setIllCaseInfoId(String illCaseInfoId) {
		this.illCaseInfoId = illCaseInfoId;
	}

	public String getTransferDoctorName() {
		return transferDoctorName;
	}

	public void setTransferDoctorName(String transferDoctorName) {
		this.transferDoctorName = transferDoctorName;
	}

	public String getReceiveDoctorName() {
		return receiveDoctorName;
	}

	public void setReceiveDoctorName(String receiveDoctorName) {
		this.receiveDoctorName = receiveDoctorName;
	}

	public Long getServiceBeginTime() {
		return serviceBeginTime;
	}

	public void setServiceBeginTime(Long serviceBeginTime) {
		this.serviceBeginTime = serviceBeginTime;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getOfflineItemId() {
		return offlineItemId;
	}

	public void setOfflineItemId(String offlineItemId) {
		this.offlineItemId = offlineItemId;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

	public String getYsTelphone() {
		return ysTelphone;
	}

	public Integer getAssistantId() {
		return assistantId;
	}

	public void setAssistantId(Integer assistantId) {
		this.assistantId = assistantId;
	}

	public void setYsTelphone(String ysTelphone) {
		this.ysTelphone = ysTelphone;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public String getPayTypeName() {
		return payTypeName;
	}

	public void setPayTypeName(String payTypeName) {
		this.payTypeName = payTypeName;
	}

	public void setPatientAge(String patientAge) {
		this.patientAge = patientAge;
	}

	public String getIllHistoryInfoId() {
		return illHistoryInfoId;
	}

	public void setIllHistoryInfoId(String illHistoryInfoId) {
		this.illHistoryInfoId = illHistoryInfoId;
	}

	public String getAssistantComment() {
		return assistantComment;
	}

	public void setAssistantComment(String assistantComment) {
		this.assistantComment = assistantComment;
	}

	public String getExpectAppointment() {
		return expectAppointment;
	}

	public void setExpectAppointment(String expectAppointment) {
		this.expectAppointment = expectAppointment;
	}
	
}
