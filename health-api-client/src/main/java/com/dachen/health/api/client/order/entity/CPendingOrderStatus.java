package com.dachen.health.api.client.order.entity;

import java.io.Serializable;

/**
 * Created by qinyuan.chen
 * Date:2016/12/30
 * Time:16:47
 */
public class CPendingOrderStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private Integer orderId;//订单ID

    private Integer orderStatus;//订单待处理状态：1.待处理，0.其他

    private Integer orderWaitType;//订单待处理类型：1.图文订单-医生未回复，2.电话订单-医生未开始，3.电话订单-医生未结束，4.健康关怀-患者未答题

    private Long flagTime;//等待开始时间，当前时间 - 等待开始时间 = 患者等待时间

    private Long createTime;//创建时间

    private Long updateTime;//更新时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getOrderWaitType() {
        return orderWaitType;
    }

    public void setOrderWaitType(Integer orderWaitType) {
        this.orderWaitType = orderWaitType;
    }

    public Long getFlagTime() {
        return flagTime;
    }

    public void setFlagTime(Long flagTime) {
        this.flagTime = flagTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
