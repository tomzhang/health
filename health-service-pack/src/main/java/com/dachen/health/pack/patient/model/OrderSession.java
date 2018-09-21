package com.dachen.health.pack.patient.model;

import java.util.List;

/**
 * 订单会话关系表
 * @author 李淼淼
 * @version 1.0 2015-09-14
 */
public class OrderSession {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 订单
     */
    private Integer orderId;

    /**
     * 会话组id(原会话 仅代表医患)
     */
    private String msgGroupId;

    /**
     * 助患会话id
     */
    @Deprecated
    private String assistantPatientGroupId ;
    
    /**
     * 医助会话id
     */
    @Deprecated
    private String assistantDoctorGroupId ;
    
    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 订单对应的会话完成时间
     */
    private Long finishTime;

    /**
     * 最后修改时间
     */
    private Long lastModifyTime;

    /**
     * 预约时间
     */
    private Long appointTime;

    /**
     * 服务开始时间
     */
    private Long serviceBeginTime;

    /**
     * 服务结束时间
     */
    private Long serviceEndTime;
    
    /**
     * 叫号开始时间
     */
    private Long treatBeginTime;

    /**
     * 患者可发
     */
    private Boolean patientCanSend;
    
    
    private Boolean isSendOverTime;
    
    private Integer timeLong;
    
    private String toUserIds;
    
    /**消息总数**/
    @Deprecated //2016-12-07 11:00:01
    private Integer totalReplyCount;
    
    /**已回复消息数量**/
    @Deprecated //2016-12-07 11:00:01
    private Integer replidCount; 

    private String firstMessageId;
    
    public String getToUserIds() {
		return toUserIds;
	}

	public void setToUserIds(String toUserIds) {
		this.toUserIds = toUserIds;
	}
	

	public static String appendStringUserId(List<String> docIds) {
    	String resString = String.join("|", docIds);
    	return resString;
    }
    
	public Boolean getIsSendOverTime() {
		return isSendOverTime;
	}

	public void setIsSendOverTime(Boolean isSendOverTime) {
		this.isSendOverTime = isSendOverTime;
	}

	public Integer getTimeLong() {
		return timeLong;
	}

	public void setTimeLong(Integer timeLong) {
		this.timeLong = timeLong;
	}

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
     * 获取订单
     *
     * @return order_id - 订单
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单
     *
     * @param orderId 订单
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取会话组id
     *
     * @return msg_group_id - 会话组id
     */
    public String getMsgGroupId() {
        return msgGroupId;
    }

    /**
     * 设置会话组id
     *
     * @param msgGroupId 会话组id
     */
    public void setMsgGroupId(String msgGroupId) {
        this.msgGroupId = msgGroupId == null ? null : msgGroupId.trim();
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
     * 获取最后修改时间
     *
     * @return last_modify_time - 最后修改时间
     */
    public Long getLastModifyTime() {
        return lastModifyTime;
    }

    /**
     * 设置最后修改时间
     *
     * @param lastModifyTime 最后修改时间
     */
    public void setLastModifyTime(Long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    /**
     * 获取预约时间
     *
     * @return appoint_time - 预约时间
     */
    public Long getAppointTime() {
        return appointTime;
    }

    /**
     * 设置预约时间
     *
     * @param appointTime 预约时间
     */
    public void setAppointTime(Long appointTime) {
        this.appointTime = appointTime;
    }

    /**
     * 获取服务开始时间
     *
     * @return service_begin_time - 服务开始时间
     */
    public Long getServiceBeginTime() {
        return serviceBeginTime;
    }

    /**
     * 设置服务开始时间
     *
     * @param serviceBeginTime 服务开始时间
     */
    public void setServiceBeginTime(Long serviceBeginTime) {
        this.serviceBeginTime = serviceBeginTime;
    }

    /**
     * 获取服务结束时间
     *
     * @return service_end_time - 服务结束时间
     */
    public Long getServiceEndTime() {
        return serviceEndTime;
    }

    /**
     * 设置服务结束时间
     *
     * @param serviceEndTime 服务结束时间
     */
    public void setServiceEndTime(Long serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
    }

    public Long getTreatBeginTime() {
		return treatBeginTime;
	}

	public void setTreatBeginTime(Long treatBeginTime) {
		this.treatBeginTime = treatBeginTime;
	}
    /**
     * 获取患者可发
     *
     * @return patient_can_send - 患者可发
     */
    public Boolean getPatientCanSend() {
        return patientCanSend;
    }

    /**
     * 设置患者可发
     *
     * @param patientCanSend 患者可发
     */
    public void setPatientCanSend(Boolean patientCanSend) {
        this.patientCanSend = patientCanSend;
    }
    
    public String getAssistantPatientGroupId() {
		return assistantPatientGroupId;
	}

	public void setAssistantPatientGroupId(String assistantPatientGroupId) {
		this.assistantPatientGroupId = assistantPatientGroupId;
	}

	public String getAssistantDoctorGroupId() {
		return assistantDoctorGroupId;
	}

	public void setAssistantDoctorGroupId(String assistantDoctorGroupId) {
		this.assistantDoctorGroupId = assistantDoctorGroupId;
	}

	
	public Integer getTotalReplyCount() {
		return totalReplyCount;
	}

	public void setTotalReplyCount(Integer totalReplyCount) {
		this.totalReplyCount = totalReplyCount;
	}

	public Integer getReplidCount() {
		return replidCount;
	}

	public void setReplidCount(Integer replidCount) {
		this.replidCount = replidCount;
	}

    public Long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
    }

    public String getFirstMessageId() {
        return firstMessageId;
    }

    public void setFirstMessageId(String firstMessageId) {
        this.firstMessageId = firstMessageId;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        OrderSession other = (OrderSession) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
            && (this.getMsgGroupId() == null ? other.getMsgGroupId() == null : this.getMsgGroupId().equals(other.getMsgGroupId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getLastModifyTime() == null ? other.getLastModifyTime() == null : this.getLastModifyTime().equals(other.getLastModifyTime()))
            && (this.getAppointTime() == null ? other.getAppointTime() == null : this.getAppointTime().equals(other.getAppointTime()))
            && (this.getServiceBeginTime() == null ? other.getServiceBeginTime() == null : this.getServiceBeginTime().equals(other.getServiceBeginTime()))
            && (this.getServiceEndTime() == null ? other.getServiceEndTime() == null : this.getServiceEndTime().equals(other.getServiceEndTime()))
            && (this.getPatientCanSend() == null ? other.getPatientCanSend() == null : this.getPatientCanSend().equals(other.getPatientCanSend()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getMsgGroupId() == null) ? 0 : getMsgGroupId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getLastModifyTime() == null) ? 0 : getLastModifyTime().hashCode());
        result = prime * result + ((getAppointTime() == null) ? 0 : getAppointTime().hashCode());
        result = prime * result + ((getServiceBeginTime() == null) ? 0 : getServiceBeginTime().hashCode());
        result = prime * result + ((getServiceEndTime() == null) ? 0 : getServiceEndTime().hashCode());
        result = prime * result + ((getPatientCanSend() == null) ? 0 : getPatientCanSend().hashCode());
        return result;
    }
}