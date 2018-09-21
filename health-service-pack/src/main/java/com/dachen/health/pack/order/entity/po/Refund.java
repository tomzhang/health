package com.dachen.health.pack.order.entity.po;

import com.dachen.health.commons.constants.OrderEnum;

/**
 * @author 李淼淼
 * @version 1.0 2016-03-24
 */
public class Refund {
    private Integer id;

    private Integer orderId;

    private Integer refundOrderId;

    private Long money;

    private Integer payType;

    private String payNo;

    /**
     * @see OrderEnum.OrderRefundStatus
     */
    private Integer status;

    private Long completeDate;

    private String account;

    private Long createDate;

    private Integer createUserId;

    private String remark;

    private String refundNo;

    private String refundReason;

    private String transId;

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
     * @return money
     */
    public Long getMoney() {
        return money;
    }

    /**
     * @param money
     */
    public void setMoney(Long money) {
        this.money = money;
    }

    /**
     * @return pay_type
     */
    public Integer getPayType() {
        return payType;
    }

    /**
     * @param payType
     */
    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    /**
     * @return pay_no
     */
    public String getPayNo() {
        return payNo;
    }

    /**
     * @param payNo
     */
    public void setPayNo(String payNo) {
        this.payNo = payNo == null ? null : payNo.trim();
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
     * @return complete_date
     */
    public Long getCompleteDate() {
        return completeDate;
    }

    /**
     * @param completeDate
     */
    public void setCompleteDate(Long completeDate) {
        this.completeDate = completeDate;
    }

    /**
     * @return account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account
     */
    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
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
     * @return remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * @return refund_no
     */
    public String getRefundNo() {
        return refundNo;
    }

    /**
     * @param refundNo
     */
    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo == null ? null : refundNo.trim();
    }

    /**
     * @return refund_reason
     */
    public String getRefundReason() {
        return refundReason;
    }

    /**
     * @param refundReason
     */
    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason == null ? null : refundReason.trim();
    }

    /**
     * @return trans_id
     */
    public String getTransId() {
        return transId;
    }

    /**
     * @param transId
     */
    public void setTransId(String transId) {
        this.transId = transId == null ? null : transId.trim();
    }
}