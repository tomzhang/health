package com.dachen.health.pack.order.entity.po;

/**
 * @author 谢佩
 * @version 1.0 2016-01-13
 * @deprecated
 * @see Refund
 */
public class OrderRefund {
    private Integer id;

    /**
     * 退款批次号
     */
    private String refundNo;

    /**
     * 退款类型
     */
    private Integer refundType;

    /**
     * 批量数
     */
    private Integer refundNum;

    /**
     * 退款原订单号
     */
    private String orderRecharegeNo;

    /**
     * 订单号
     */
    private Integer orderId;

    /**
     * 总金额
     */
    private Long orderPrice;

    /**
     * 退款金额
     */
    private Long refundPrice;

    /**
     * 退款理由
     */
    private String refundReason;

    /**
     * 退款状态
     */
    private Integer refundStatus;

    /**
     * 退款时间
     */
    private Long refundCreate;

    /**
     * 退款修改时间
     */
    private Long refundUpdate;

    /**
     * 日志
     */
    private String refundLog;

    /**
     * transaction_id第三方交易ID
     */
    private String transId;

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
     * 获取退款批次号
     *
     * @return refund_no - 退款批次号
     */
    public String getRefundNo() {
        return refundNo;
    }

    /**
     * 设置退款批次号
     *
     * @param refundNo 退款批次号
     */
    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo == null ? null : refundNo.trim();
    }

    /**
     * 获取退款类型
     *
     * @return refund_type - 退款类型
     */
    public Integer getRefundType() {
        return refundType;
    }

    /**
     * 设置退款类型
     *
     * @param refundType 退款类型
     */
    public void setRefundType(Integer refundType) {
        this.refundType = refundType;
    }

    /**
     * 获取批量数
     *
     * @return refund_num - 批量数
     */
    public Integer getRefundNum() {
        return refundNum;
    }

    /**
     * 设置批量数
     *
     * @param refundNum 批量数
     */
    public void setRefundNum(Integer refundNum) {
        this.refundNum = refundNum;
    }

    /**
     * 获取退款原订单号
     *
     * @return order_recharege_no - 退款原订单号
     */
    public String getOrderRecharegeNo() {
        return orderRecharegeNo;
    }

    /**
     * 设置退款原订单号
     *
     * @param orderRecharegeNo 退款原订单号
     */
    public void setOrderRecharegeNo(String orderRecharegeNo) {
        this.orderRecharegeNo = orderRecharegeNo == null ? null : orderRecharegeNo.trim();
    }

    /**
     * 获取订单号
     *
     * @return order_id - 订单号
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单号
     *
     * @param orderId 订单号
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取总金额
     *
     * @return order_price - 总金额
     */
    public Long getOrderPrice() {
        return orderPrice;
    }

    /**
     * 设置总金额
     *
     * @param orderPrice 总金额
     */
    public void setOrderPrice(Long orderPrice) {
        this.orderPrice = orderPrice;
    }

    /**
     * 获取退款金额
     *
     * @return refund_price - 退款金额
     */
    public Long getRefundPrice() {
        return refundPrice;
    }

    /**
     * 设置退款金额
     *
     * @param refundPrice 退款金额
     */
    public void setRefundPrice(Long refundPrice) {
        this.refundPrice = refundPrice;
    }

    /**
     * 获取退款理由
     *
     * @return refund_reason - 退款理由
     */
    public String getRefundReason() {
        return refundReason;
    }

    /**
     * 设置退款理由
     *
     * @param refundReason 退款理由
     */
    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason == null ? null : refundReason.trim();
    }

    /**
     * 获取退款状态
     *
     * @return refund_status - 退款状态
     */
    public Integer getRefundStatus() {
        return refundStatus;
    }

    /**
     * 设置退款状态
     *
     * @param refundStatus 退款状态
     */
    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    /**
     * 获取退款时间
     *
     * @return refund_create - 退款时间
     */
    public Long getRefundCreate() {
        return refundCreate;
    }

    /**
     * 设置退款时间
     *
     * @param refundCreate 退款时间
     */
    public void setRefundCreate(Long refundCreate) {
        this.refundCreate = refundCreate;
    }

    /**
     * 获取退款修改时间
     *
     * @return refund_update - 退款修改时间
     */
    public Long getRefundUpdate() {
        return refundUpdate;
    }

    /**
     * 设置退款修改时间
     *
     * @param refundUpdate 退款修改时间
     */
    public void setRefundUpdate(Long refundUpdate) {
        this.refundUpdate = refundUpdate;
    }

    /**
     * 获取日志
     *
     * @return refund_log - 日志
     */
    public String getRefundLog() {
        return refundLog;
    }

    /**
     * 设置日志
     *
     * @param refundLog 日志
     */
    public void setRefundLog(String refundLog) {
        this.refundLog = refundLog == null ? null : refundLog.trim();
    }

    /**
     * 获取transaction_id第三方交易ID
     *
     * @return trans_id - transaction_id第三方交易ID
     */
    public String getTransId() {
        return transId;
    }

    /**
     * 设置transaction_id第三方交易ID
     *
     * @param transId transaction_id第三方交易ID
     */
    public void setTransId(String transId) {
        this.transId = transId == null ? null : transId.trim();
    }
}