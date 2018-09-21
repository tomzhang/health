package com.dachen.health.pack.order.entity.po;

/**
 * @author 谢佩
 * @version 1.0 2015-12-10
 */
public class OrderDrug {
    /**
     * id
     */
    private String id;

    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 医药ID
     */
    private String drugId;

    /**
     * 获取id
     *
     * @return id - id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取订单ID
     *
     * @return order_id - 订单ID
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单ID
     *
     * @param orderId 订单ID
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取医药ID
     *
     * @return drug_id - 医药ID
     */
    public String getDrugId() {
        return drugId;
    }

    /**
     * 设置医药ID
     *
     * @param drugId 医药ID
     */
    public void setDrugId(String drugId) {
        this.drugId = drugId == null ? null : drugId.trim();
    }
}