package com.dachen.health.pack.income.entity.po;

public class Settle {
    private Integer id;

    private String userId;

    private Integer userType;//用户类型（1：集团，2：医生）

    private double settleMoney;

    private Integer status;

    private double taxMoney;

    private double actualMoney;

    private Integer userBankId;

    private Long createTime;

    private Long settleTime;

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
     * @return user_id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * @return user_type
     */
    public Integer getUserType() {
        return userType;
    }

    /**
     * @param userType
     */
    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    /**
     * @return settle_money
     */
    public double getSettleMoney() {
        return settleMoney;
    }

    /**
     * @param settleMoney
     */
    public void setSettleMoney(double settleMoney) {
        this.settleMoney = settleMoney;
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
     * @return tax_money
     */
    public double getTaxMoney() {
        return taxMoney;
    }

    /**
     * @param taxMoney
     */
    public void setTaxMoney(double taxMoney) {
        this.taxMoney = taxMoney;
    }

    /**
     * @return actual_money
     */
    public double getActualMoney() {
        return actualMoney;
    }

    /**
     * @param actualMoney
     */
    public void setActualMoney(double actualMoney) {
        this.actualMoney = actualMoney;
    }

    /**
     * @return user_bank_id
     */
    public Integer getUserBankId() {
        return userBankId;
    }

    /**
     * @param userBankId
     */
    public void setUserBankId(Integer userBankId) {
        this.userBankId = userBankId;
    }

    /**
     * @return create_time
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * @return settle_time
     */
    public Long getSettleTime() {
        return settleTime;
    }

    /**
     * @param settleTime
     */
    public void setSettleTime(Long settleTime) {
        this.settleTime = settleTime;
    }
}