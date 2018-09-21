package com.dachen.health.pack.incomeNew.entity.po;

public class SettleNew {
    private Integer id;

    private Integer doctorId;

    private String groupId;

    /**
     * 1=医生，2=医生集团
     */
    private Integer objectType;

    /**
     * 1=不允许结算，2=未结算，3=已结算，4=已过期
     */
    private Integer status;

    private Double monthMoney;

    private Double totalMoney;

    private Integer bankcardId;

    private Long createDate;

    private Long settleDate;

    private Integer settleUserId;

    private Integer year;

    private String month;

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
     * @return doctor_id
     */
    public Integer getDoctorId() {
        return doctorId;
    }

    /**
     * @param doctorId
     */
    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * @return group_id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId == null ? null : groupId.trim();
    }

    /**
     * 获取1=医生，2=医生集团
     *
     * @return object_type - 1=医生，2=医生集团
     */
    public Integer getObjectType() {
        return objectType;
    }

    /**
     * 设置1=医生，2=医生集团
     *
     * @param objectType 1=医生，2=医生集团
     */
    public void setObjectType(Integer objectType) {
        this.objectType = objectType;
    }

    /**
     * 获取1=不允许结算，2=未结算，3=已结算，4=已过期
     *
     * @return status - 1=不允许结算，2=未结算，3=已结算，4=已过期
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置1=不允许结算，2=未结算，3=已结算，4=已过期
     *
     * @param status 1=不允许结算，2=未结算，3=已结算，4=已过期
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return month_money
     */
    public Double getMonthMoney() {
        return monthMoney;
    }

    /**
     * @param monthMoney
     */
    public void setMonthMoney(Double monthMoney) {
        this.monthMoney = monthMoney;
    }

    /**
     * @return total_money
     */
    public Double getTotalMoney() {
        return totalMoney;
    }

    /**
     * @param totalMoney
     */
    public void setTotalMoney(Double totalMoney) {
        this.totalMoney = totalMoney;
    }

    /**
     * @return bankcard_id
     */
    public Integer getBankcardId() {
        return bankcardId;
    }

    /**
     * @param bankcardId
     */
    public void setBankcardId(Integer bankcardId) {
        this.bankcardId = bankcardId;
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
     * @return settle_date
     */
    public Long getSettleDate() {
        return settleDate;
    }

    /**
     * @param settleDate
     */
    public void setSettleDate(Long settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * @return settle_user_id
     */
    public Integer getSettleUserId() {
        return settleUserId;
    }

    /**
     * @param settleUserId
     */
    public void setSettleUserId(Integer settleUserId) {
        this.settleUserId = settleUserId;
    }

    /**
     * @return year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * @param year
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * @return month
     */
    public String getMonth() {
        return month;
    }

    /**
     * @param month
     */
    public void setMonth(String month) {
        this.month = month == null ? null : month.trim();
    }
}