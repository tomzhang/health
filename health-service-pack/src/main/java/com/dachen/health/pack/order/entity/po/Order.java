package com.dachen.health.pack.order.entity.po;

import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderStatus;
import com.dachen.util.JSONUtil;

/**
 * 套餐订单表
 * @author 李淼淼
 * @version 1.0 2015-11-24
 */
public class Order {
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
    /**
     * 新版电子病历id
     */
    private String illHistoryInfoId;
    /**
     * 是否复诊(true-初诊 false-复诊)
     */
    private Boolean oldSession;

    public Boolean getOldSession() {
        return oldSession;
    }

    public void setOldSession(Boolean oldSession) {
        this.oldSession = oldSession;
    }

    public String getIllHistoryInfoId() {
        return illHistoryInfoId;
    }

    public void setIllHistoryInfoId(String illHistoryInfoId) {
        this.illHistoryInfoId = illHistoryInfoId;
    }

    public String getCareName() {
		return careName;
	}

	public void setCareName(String careName) {
		this.careName = careName;
	}

	/**
     * 订单激活状态 ，提供分享 0：未激活订单，1：已激活订单
     * @return
     */
	public Integer getAcStatus() {
		return acStatus;
	}

	public void setAcStatus(Integer acStatus) {
		this.acStatus = acStatus;
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


    /**
     * 备注
     */
    private String remarks;

    /**
     * 记录状态
     * @see OrderEnum.OrderRecordStatus
     */
    private Integer recordStatus;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取购买用户id
     *
     * @return user_id - 购买用户id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置购买用户id
     *
     * @param userId 购买用户id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取医生id
     *
     * @return doctor_id - 医生id
     */
    public Integer getDoctorId() {
        return doctorId;
    }

    /**
     * 设置医生id
     *
     * @param doctorId 医生id
     */
    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * 获取医生集团ID
     *
     * @return group_id - 医生集团ID
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * 设置医生集团ID
     *
     * @param groupId 医生集团ID
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId == null ? null : groupId.trim();
    }

    /**
     * 获取订单类型（1：套餐订单；2：报到；3：门诊订单）
     *
     * @return order_type - 订单类型（1：套餐订单；2：报到；3：门诊订单）
     */
    public Integer getOrderType() {
        return orderType;
    }

    /**
     * 设置订单类型（1：套餐订单；2：报到；3：门诊订单）
     *
     * @param orderType 订单类型（1：套餐订单；2：报到；3：门诊订单）
     */
    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    /**
     * 获取套餐类型
     *
     * @return pack_type - 套餐类型
     */
    public Integer getPackType() {
        return packType;
    }

    /**
     * 设置套餐类型
     *
     * @param packType 套餐类型
     */
    public void setPackType(Integer packType) {
        this.packType = packType;
    }

    /**
     * 获取套餐id
     *
     * @return pack_id - 套餐id
     */
    public Integer getPackId() {
        return packId;
    }

    /**
     * 设置套餐id
     *
     * @param packId 套餐id
     */
    public void setPackId(Integer packId) {
        this.packId = packId;
    }

    /**
     * 获取价格
     *
     * @return price - 价格
     */
    public Long getPrice() {
        return price;
    }

    /**
     * 设置价格
     *
     * @param price 价格
     */
    public void setPrice(Long price) {
        this.price = price;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取完成时间
     *
     * @return finish_time - 完成时间
     */
    public Long getFinishTime() {
        return finishTime;
    }

    /**
     * 设置完成时间
     *
     * @param finishTime 完成时间
     */
    public void setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
    }

    /**
     * 获取病人id
     *
     * @return patient_id - 病人id
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置病人id
     *
     * @param patientId 病人id
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取病情id
     * 注：订单中的病情资料以 illCaseInfo 数据为主 订单数据表中有存储 illCaseInfoId
     *    在新api中添加获取病情资料时请勿使用 diseaseId 字段以及 disease中的数据
     * @return disease_id - 病情id
     */
    @Deprecated
    public Integer getDiseaseId() {
        return diseaseId;
    }

    /**
     * 设置病情id
     *
     * @param diseaseId 病情id
     */
    @Deprecated
    public void setDiseaseId(Integer diseaseId) {
        this.diseaseId = diseaseId;
    }

    /**
     * 获取订单状态--1：待预约；2：待支付；3：已支付；4：已完成；5：已取消
     *
     * @return order_status - 订单状态--1：待预约；2：待支付；3：已支付；4：已完成；5：已取消
     */
    public Integer getOrderStatus() {
        return orderStatus;
    }

    /**
     * 设置订单状态--1：待预约；2：待支付；3：已支付；4：已完成；5：已取消
     *
     * @param orderStatus 订单状态--1：待预约；2：待支付；3：已支付；4：已完成；5：已取消
     */
    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * 获取退款状态--1：未申请退款；2：申请退款；3：退款成功；4：退款失败
     *
     * @return refund_status - 退款状态--1：未申请退款；2：申请退款；3：退款成功；4：退款失败
     */
    public Integer getRefundStatus() {
        return refundStatus;
    }

    /**
     * 设置退款状态--1：未申请退款；2：申请退款；3：退款成功；4：退款失败
     *
     * @param refundStatus 退款状态--1：未申请退款；2：申请退款；3：退款成功；4：退款失败
     */
    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    /**
     * 获取套餐时间
     *
     * @return time_long - 套餐时间
     */
    public Integer getTimeLong() {
        return timeLong;
    }

    /**
     * 设置套餐时间
     *
     * @param timeLong 套餐时间
     */
    public void setTimeLong(Integer timeLong) {
        this.timeLong = timeLong;
    }

    /**
     * 获取上一个状态（订单状态）
     *
     * @return pre_order_status - 上一个状态（订单状态）
     */
    public Integer getPreOrderStatus() {
        return preOrderStatus;
    }

    /**
     * 设置上一个状态（订单状态）
     *
     * @param preOrderStatus 上一个状态（订单状态）
     */
    public void setPreOrderStatus(Integer preOrderStatus) {
        this.preOrderStatus = preOrderStatus;
    }

    /**
     * 获取取消人（0 sys）
     *
     * @return cancel_from - 取消人（0 sys）
     */
    public Integer getCancelFrom() {
        return cancelFrom;
    }

    /**
     * 设置取消人（0 sys）
     *
     * @param cancelFrom 取消人（0 sys）
     */
    public void setCancelFrom(Integer cancelFrom) {
        this.cancelFrom = cancelFrom;
    }

    /**
     * 获取订单号
     *
     * @return order_no - 订单号
     */
    public Integer getOrderNo() {
        return orderNo;
    }

    /**
     * 设置订单号
     *
     * @param orderNo 订单号
     */
    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取关怀计划id
     *
     * @return care_template_id - 关怀计划id
     */
    public String getCareTemplateId() {
        return careTemplateId;
    }

    /**
     * 设置关怀计划id
     *
     * @param careTemplateId 关怀计划id
     */
    public void setCareTemplateId(String careTemplateId) {
        this.careTemplateId = careTemplateId == null ? null : careTemplateId.trim();
    }

    /**
     * 获取随访id
     *
     * @return follow_template_id - 随访id
     */
    public String getFollowTemplateId() {
        return followTemplateId;
    }

    /**
     * 设置随访id
     *
     * @param followTemplateId 随访id
     */
    public void setFollowTemplateId(String followTemplateId) {
        this.followTemplateId = followTemplateId == null ? null : followTemplateId.trim();
    }

    /**
     * 获取备注
     *
     * @return remarks - 备注
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置备注
     *
     * @param remarks 备注
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

	public Integer getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(Integer recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getIllCaseInfoId() {
		return illCaseInfoId;
	}

	public void setIllCaseInfoId(String illCaseInfoId) {
		this.illCaseInfoId = illCaseInfoId;
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

	public String getConsultationPackId() {
		return consultationPackId;
	}

	public void setConsultationPackId(String consultationPackId) {
		this.consultationPackId = consultationPackId;
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

	public Integer getAssistantId() {
		return assistantId;
	}

	public void setAssistantId(Integer assistantId) {
		this.assistantId = assistantId;
	}
	
	public Integer getCareOrderId() {
		return careOrderId;
	}

	public void setCareOrderId(Integer careOrderId) {
		this.careOrderId = careOrderId;
	}

	public boolean ifFinished() {
		if (this.getOrderStatus() == OrderStatus.已完成.getIndex() || this.getOrderStatus() == OrderStatus.已取消.getIndex()) {
			return true;
		}
		return false;
	}

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    @Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
	
}