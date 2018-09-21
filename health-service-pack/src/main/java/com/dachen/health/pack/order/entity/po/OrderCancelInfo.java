package com.dachen.health.pack.order.entity.po;

import org.mongodb.morphia.annotations.Entity;

/**
 * Created by fuyongde on 2017/2/15.
 */
@Entity(value = "t_order_cancel_info", noClassnameStored = true)
public class OrderCancelInfo {
    /**用户id**/
    private Integer userId;
    /**订单取消的时间**/
    private Long cancelTime;
    /**订单id**/
    private Integer orderId;
    /**订单取消之前的状态
     * @see com.dachen.health.commons.constants.OrderEnum.OrderStatus
     * **/
    private Integer historyStatus;
    /**记录的创建时间**/
    private Long createTime;

    /**取消的类型**/
    private Integer cancelType;

    public Integer getCancelType() {
        return cancelType;
    }

    public void setCancelType(Integer cancelType) {
        this.cancelType = cancelType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Long cancelTime) {
        this.cancelTime = cancelTime;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getHistoryStatus() {
        return historyStatus;
    }

    public void setHistoryStatus(Integer historyStatus) {
        this.historyStatus = historyStatus;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
