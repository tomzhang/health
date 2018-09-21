package com.dachen.health.pack.patient.model;

/**
 * 订单状态修改记录
 * @author 李淼淼
 * @version 1.0 2015-09-17
 */
public class OrderStatusLog {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 原值
     */
    private Integer sourceVal;

    /**
     * 修改后的值
     */
    private Integer val;

    /**
     * 创建时间
     */
    private Long createTime;

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
     * 获取订单id
     *
     * @return order_id - 订单id
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单id
     *
     * @param orderId 订单id
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取原值
     *
     * @return source_val - 原值
     */
    public Integer getSourceVal() {
        return sourceVal;
    }

    /**
     * 设置原值
     *
     * @param sourceVal 原值
     */
    public void setSourceVal(Integer sourceVal) {
        this.sourceVal = sourceVal;
    }

    /**
     * 获取修改后的值
     *
     * @return val - 修改后的值
     */
    public Integer getVal() {
        return val;
    }

    /**
     * 设置修改后的值
     *
     * @param val 修改后的值
     */
    public void setVal(Integer val) {
        this.val = val;
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
        OrderStatusLog other = (OrderStatusLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
            && (this.getSourceVal() == null ? other.getSourceVal() == null : this.getSourceVal().equals(other.getSourceVal()))
            && (this.getVal() == null ? other.getVal() == null : this.getVal().equals(other.getVal()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getSourceVal() == null) ? 0 : getSourceVal().hashCode());
        result = prime * result + ((getVal() == null) ? 0 : getVal().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }
}