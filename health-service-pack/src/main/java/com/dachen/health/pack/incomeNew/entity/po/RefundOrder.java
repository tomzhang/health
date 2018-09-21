package com.dachen.health.pack.incomeNew.entity.po;

public class RefundOrder {
    private Integer id;

    private Integer userId;

    private Integer orderId;
    
    /**
     *
     */
    private String thrid_refund_id;

    /**
     * 1=自动退款，2=申请退款，3=投诉退款
     */
    private Integer type;

    private Integer status;

    private Double money;

    private Integer operator;

    private Integer createUserId;

    private Long createrDate;

    private Integer lastUpdateUserId;

    private Long lastUpdateDate;

    /**
     * @return ID
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
     * @return user_id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getThrid_refund_id() {
		return thrid_refund_id;
	}

	public void setThrid_refund_id(String thrid_refund_id) {
		this.thrid_refund_id = thrid_refund_id;
	}

	/**
     * 获取1=自动退款，2=申请退款，3=投诉退款
     *
     * @return type - 1=自动退款，2=申请退款，3=投诉退款
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置1=自动退款，2=申请退款，3=投诉退款
     *
     * @param type 1=自动退款，2=申请退款，3=投诉退款
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(Integer status) {
        this.status = status;
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
     * @return operator
     */
    public Integer getOperator() {
        return operator;
    }

    /**
     * @param operator
     */
    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    /**
     * @return create_user_id
     */
    public Integer getCreateUserId() {
        return createUserId;
    }

    /**
     * @param createUserId
     */
    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * @return creater_date
     */
    public Long getCreaterDate() {
        return createrDate;
    }

    /**
     * @param createrDate
     */
    public void setCreaterDate(Long createrDate) {
        this.createrDate = createrDate;
    }

    /**
     * @return last_update_user_id
     */
    public Integer getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    /**
     * @param lastUpdateUserId
     */
    public void setLastUpdateUserId(Integer lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    /**
     * @return last_update_date
     */
    public Long getLastUpdateDate() {
        return lastUpdateDate;
    }

    /**
     * @param lastUpdateDate
     */
    public void setLastUpdateDate(Long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}