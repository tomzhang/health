package com.dachen.health.pack.incomeNew.entity.po;

public class Incomelog {
    private Integer id;

    private Integer doctorId;

    private String groupId;

    private Integer childDoctorId;

    private Integer orderId;

    private Integer refundOrderId;

    private Integer expendId;
    
    private Integer cashId; 

    /**
     * 1=订单收入，2=医生提成收入，3=集团提成收入；11=订单退款，12=医生提成退款，13=集团提成退款，14=提现，15=平台提成，16=提现手续费
     */
    private Integer logType;

    private Double money;

    private Long createDate;

    private Integer year;

    /**
     * 2016年01月
     */
    private String month;

    private String extend1;

    private String extend2;

    /**
     * 1:医生;2集团
     */
    private Integer type;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return doctor_id
     */
    public Integer getDoctorId() {
        return doctorId;
    }

    /**
     * @param doctorId
     */
    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * @return group_id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId == null ? null : groupId.trim();
    }

    /**
     * @return child_doctor_id
     */
    public Integer getChildDoctorId() {
        return childDoctorId;
    }

    /**
     * @param childDoctorId
     */
    public void setChildDoctorId(Integer childDoctorId) {
        this.childDoctorId = childDoctorId;
    }

    /**
     * @return order_id
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * @param orderId
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * @return refund_order_id
     */
    public Integer getRefundOrderId() {
        return refundOrderId;
    }

    /**
     * @param refundOrderId
     */
    public void setRefundOrderId(Integer refundOrderId) {
        this.refundOrderId = refundOrderId;
    }

    /**
     * @return expend_id
     */
    public Integer getExpendId() {
        return expendId;
    }

    /**
     * @param expendId
     */
    public void setExpendId(Integer expendId) {
        this.expendId = expendId;
    }

    public Integer getCashId() {
		return cashId;
	}

	public void setCashId(Integer cashId) {
		this.cashId = cashId;
	}

	/**
     * 获取1=订单收入，2=医生提成收入，3=集团提成收入；11=订单退款，12=医生提成退款，13=集团提成退款，14=提现，15=平台提成，16=提现手续费
     *
     * @return log_type - 1=订单收入，2=医生提成收入，3=集团提成收入；11=订单退款，12=医生提成退款，13=集团提成退款，14=提现，15=平台提成，16=提现手续费
     */
    public Integer getLogType() {
        return logType;
    }

    /**
     * 设置1=订单收入，2=医生提成收入，3=集团提成收入；11=订单退款，12=医生提成退款，13=集团提成退款，14=提现，15=平台提成，16=提现手续费
     *
     * @param logType 1=订单收入，2=医生提成收入，3=集团提成收入；11=订单退款，12=医生提成退款，13=集团提成退款，14=提现，15=平台提成，16=提现手续费
     */
    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    /**
     * @return money
     */
    public Double getMoney() {
        return money;
    }

    /**
     * @param money
     */
    public void setMoney(Double money) {
        this.money = money;
    }

    /**
     * @return create_date
     */
    public Long getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate
     */
    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    /**
     * @return year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * @param year
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * 获取2016年01月
     *
     * @return month - 2016年01月
     */
    public String getMonth() {
        return month;
    }

    /**
     * 设置2016年01月
     *
     * @param month 2016年01月
     */
    public void setMonth(String month) {
        this.month = month == null ? null : month.trim();
    }

    /**
     * @return extend_1
     */
    public String getExtend1() {
        return extend1;
    }

    /**
     * @param extend1
     */
    public void setExtend1(String extend1) {
        this.extend1 = extend1 == null ? null : extend1.trim();
    }

    /**
     * @return extend_2
     */
    public String getExtend2() {
        return extend2;
    }

    /**
     * @param extend2
     */
    public void setExtend2(String extend2) {
        this.extend2 = extend2 == null ? null : extend2.trim();
    }

    /**
     * 获取1:医生;2集团
     *
     * @return type - 1:医生;2集团
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置1:医生;2集团
     *
     * @param type 1:医生;2集团
     */
    public void setType(Integer type) {
        this.type = type;
    }
}