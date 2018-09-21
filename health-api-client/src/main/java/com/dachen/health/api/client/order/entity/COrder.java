package com.dachen.health.api.client.order.entity;

import java.io.Serializable;

public class COrder implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * 购买用户id
     */
    private Integer userId;

    /**
     * 医生id
     */
    private Integer doctorId;
    
    /**
     * 医助id
     */
    private Integer assistantId ;
    
    /**
     * 医生集团ID（订单所属集团）
     */
    private String groupId;

    /**
     * 订单类型（1：套餐订单；2：报到；3：门诊订单）
     */
    private Integer orderType;

    /**
     * 套餐类型
     */
    private Integer packType;

    /**
     * 套餐id
     */
    private Integer packId;

    /**
     * 价格
     */
    private Long price;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 完成时间
     */
    private Long finishTime;

    /**
     * 病人id
     */
    private Integer patientId;

    /**
     * 病情id
     * 注：订单中的病情资料以 illCaseInfo 数据为主 订单数据表中有存储 illCaseInfoId
     *    在新api中添加获取病情资料时请勿使用 diseaseId 字段以及 disease中的数据
     */
    @Deprecated
    private Integer diseaseId;
    
    
    private String diseaseID;//所患疾病id 从初始化数据表中获取
    
    /**
     * 主诉 、 病情描述 不存储数据库 只在订单生成时候做返回值使用
     */
    private String mainCase;

    /**
     * 订单状态--1：待预约；2：待支付；3：已支付；4：已完成；5：已取消
     * @see OrderEnum.OrderStatus
     */
    private Integer orderStatus;

    /**
     * 退款状态--1：未申请退款；2：申请退款；3：退款成功；4：退款失败
     * @see OrderEnum.OrderRefundStatus
     */
    private Integer refundStatus;

    /**
     * 套餐时间
     */
    private Integer timeLong;

    /**
     * 上一个状态（订单状态）
     * 1:待预约，2：待支付，3：已支付，4：已完成，5：已取消 
     * @see OrderEnum.OrderStatus
     */
    private Integer preOrderStatus;

    /**
     * 取消人（0 sys）
     */
    private Integer cancelFrom;

    /**
     * 订单号
     */
    private Integer orderNo;

    /**
     * 关怀计划id
     */
    private String careTemplateId;

    /**
     * （关怀计划且price=0）即为随访，此字段用来存储订单结束后医生给患者推荐下一个关怀套餐的ID
     */
    private String followTemplateId;
    
    private Long payTime;
    
    private Integer payType;

    /**
     * 0：未激活订单 1：已激活订单
     */
    private Integer acStatus=1; 
    
    /**
     * 电子病历id
     * 注：订单中的病情资料以 illCaseInfo 数据为主 订单数据表中有存储 illCaseInfoId
     *    在新api中添加获取病情资料时请勿使用diseaseId字段以及 disease中的数据
     *   
     */
    private String illCaseInfoId;
    
    private String consultationPackId;
    
    private Integer helpTimes;
    //集团名称
    private String groupName;
    
    private String msgId;
    
    private String imTitleName;
    
    private String hospitalId;
    
    private String cancelReason;//取消原因
    
    //期望预约时间id
    private String expectAppointmentIds;
    
    /**关怀计划的名称**/
    private String careName;
    
    /**健康关怀里面下图文/电话订单时需要存储健康关怀的订单**/
    private Integer careOrderId;

    //积分（积分问诊使用）
    private Integer point;
	
	
	private COrderSession orderSession;

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getCareTemplateId() {
		return careTemplateId;
	}

	public void setCareTemplateId(String careTemplateId) {
		this.careTemplateId = careTemplateId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public boolean isFinished() {
		if (this.getOrderStatus() == COrderEnum.OrderStatus.已完成.getIndex() || this.getOrderStatus() == COrderEnum.OrderStatus.已取消.getIndex()) {
			return true;
		}
		return false;
	}

	public Integer getAssistantId() {
		return assistantId;
	}

	public void setAssistantId(Integer assistantId) {
		this.assistantId = assistantId;
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

	public Integer getPackId() {
		return packId;
	}

	public void setPackId(Integer packId) {
		this.packId = packId;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
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

	public Integer getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(Integer diseaseId) {
		this.diseaseId = diseaseId;
	}

	public String getDiseaseID() {
		return diseaseID;
	}

	public void setDiseaseID(String diseaseID) {
		this.diseaseID = diseaseID;
	}

	public String getMainCase() {
		return mainCase;
	}

	public void setMainCase(String mainCase) {
		this.mainCase = mainCase;
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

	public Integer getTimeLong() {
		return timeLong;
	}

	public void setTimeLong(Integer timeLong) {
		this.timeLong = timeLong;
	}

	public Integer getPreOrderStatus() {
		return preOrderStatus;
	}

	public void setPreOrderStatus(Integer preOrderStatus) {
		this.preOrderStatus = preOrderStatus;
	}

	public Integer getCancelFrom() {
		return cancelFrom;
	}

	public void setCancelFrom(Integer cancelFrom) {
		this.cancelFrom = cancelFrom;
	}

	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public String getFollowTemplateId() {
		return followTemplateId;
	}

	public void setFollowTemplateId(String followTemplateId) {
		this.followTemplateId = followTemplateId;
	}

	public Long getPayTime() {
		return payTime;
	}

	public void setPayTime(Long payTime) {
		this.payTime = payTime;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getAcStatus() {
		return acStatus;
	}

	public void setAcStatus(Integer acStatus) {
		this.acStatus = acStatus;
	}

	public String getIllCaseInfoId() {
		return illCaseInfoId;
	}

	public void setIllCaseInfoId(String illCaseInfoId) {
		this.illCaseInfoId = illCaseInfoId;
	}

	public String getConsultationPackId() {
		return consultationPackId;
	}

	public void setConsultationPackId(String consultationPackId) {
		this.consultationPackId = consultationPackId;
	}

	public Integer getHelpTimes() {
		return helpTimes;
	}

	public void setHelpTimes(Integer helpTimes) {
		this.helpTimes = helpTimes;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getImTitleName() {
		return imTitleName;
	}

	public void setImTitleName(String imTitleName) {
		this.imTitleName = imTitleName;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getExpectAppointmentIds() {
		return expectAppointmentIds;
	}

	public void setExpectAppointmentIds(String expectAppointmentIds) {
		this.expectAppointmentIds = expectAppointmentIds;
	}

	public String getCareName() {
		return careName;
	}

	public void setCareName(String careName) {
		this.careName = careName;
	}

	public Integer getCareOrderId() {
		return careOrderId;
	}

	public void setCareOrderId(Integer careOrderId) {
		this.careOrderId = careOrderId;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public COrderSession getOrderSession() {
		return orderSession;
	}

	public void setOrderSession(COrderSession orderSession) {
		this.orderSession = orderSession;
	}
	
}
