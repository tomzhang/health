package com.dachen.health.pack.income.entity.po;

/**
 * @author 李淼淼
 * @version 1.0 2016-01-18
 */
public class SettleSys {
    private Integer id;

    /**
     * Ŀǰ
     */
    private String name;

    private double settlemoney;

    private double unsettlemoney;

    private Integer usertype;

    private Long settleTime;

    private Long createTime;

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
     * 获取Ŀǰ
     *
     * @return name - Ŀǰ
     */
    public String getName() {
        return name;
    }

    /**
     * 设置Ŀǰ
     *
     * @param name Ŀǰ
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * @return settleMoney
     */
    public double getSettlemoney() {
        return settlemoney;
    }

    /**
     * @param settlemoney
     */
    public void setSettlemoney(double settlemoney) {
        this.settlemoney = settlemoney;
    }

    /**
     * @return unSettleMoney
     */
    public double getUnsettlemoney() {
        return unsettlemoney;
    }

    /**
     * @param unsettlemoney
     */
    public void setUnsettlemoney(double unsettlemoney) {
        this.unsettlemoney = unsettlemoney;
    }

    /**
     * @return userType
     */
    public Integer getUsertype() {
        return usertype;
    }

    /**
     * @param usertype
     */
    public void setUsertype(Integer usertype) {
        this.usertype = usertype;
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
}