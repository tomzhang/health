package com.dachen.health.pack.order.entity.po;

/**
 * @author 李淼淼
 * @version 1.0 2015-10-28
 */
public class SCareScaleAnswer {
    private Integer id;

    private String orderCareItemId;

    private String questionId;

    private String answerId;

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
     * @return order_care_item_id
     */
    public String getOrderCareItemId() {
        return orderCareItemId;
    }

    /**
     * @param orderCareItemId
     */
    public void setOrderCareItemId(String orderCareItemId) {
        this.orderCareItemId = orderCareItemId;
    }

    /**
     * @return question_id
     */
    public String getQuestionId() {
        return questionId;
    }

    /**
     * @param questionId
     */
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    /**
     * @return answer_id
     */
    public String getAnswerId() {
        return answerId;
    }

    /**
     * @param answerId
     */
    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }
}