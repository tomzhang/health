package com.dachen.health.pack.order.entity.po;

/**
 * @author 谢佩
 * @version 1.0 2015-12-10
 */
public class OrderRecipe {
    /**
     * id
     */
    private String id;

    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 处方ID
     */
    private String recipeId;

    /**
     * 创建时间
     */
    private Long createTime;

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
     * 获取处方ID
     *
     * @return recipe_id - 处方ID
     */
    public String getRecipeId() {
        return recipeId;
    }

    /**
     * 设置处方ID
     *
     * @param recipeId 处方ID
     */
    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId == null ? null : recipeId.trim();
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
}