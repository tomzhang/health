package com.dachen.health.pack.income.entity.po;


/**
 * 集团提成
 *@author wangqiao
 *@date 2016年1月23日
 *
 */
public class GroupDivision {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 集团id
     */
    private String groupId;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 收入id
     */
    private Integer incomeId;

    /**
     * 收入医生id
     */
    private Integer incomeDoctorId;

    /**
     * 集团提成收入
     */
    private Double divisionIncome;

    /**
     * 结算状态，2=未结算，1=已结算
     */
    private String settleStatus;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 结算时间
     */
    private Long settleTime;

    /**
     * 备用字段1
     */
    private String extend1;

    /**
     * 备用字段2
     */
    private String extend2;

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
     * 获取集团id
     *
     * @return group_id - 集团id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * 设置集团id
     *
     * @param groupId 集团id
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId == null ? null : groupId.trim();
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
     * 获取收入id
     *
     * @return income_id - 收入id
     */
    public Integer getIncomeId() {
        return incomeId;
    }

    /**
     * 设置收入id
     *
     * @param incomeId 收入id
     */
    public void setIncomeId(Integer incomeId) {
        this.incomeId = incomeId;
    }

    /**
     * 获取收入医生id
     *
     * @return income_doctor_id - 收入医生id
     */
    public Integer getIncomeDoctorId() {
        return incomeDoctorId;
    }

    /**
     * 设置收入医生id
     *
     * @param incomeDoctorId 收入医生id
     */
    public void setIncomeDoctorId(Integer incomeDoctorId) {
        this.incomeDoctorId = incomeDoctorId;
    }

    /**
     * 获取集团提成收入
     *
     * @return division_income - 集团提成收入
     */
    public Double getDivisionIncome() {
        return divisionIncome;
    }

    /**
     * 设置集团提成收入
     *
     * @param divisionIncome 集团提成收入
     */
    public void setDivisionIncome(Double divisionIncome) {
        this.divisionIncome = divisionIncome;
    }

    /**
     * 获取结算状态，0=未结算，1=已结算
     *
     * @return settle_status - 结算状态，0=未结算，1=已结算
     */
    public String getSettleStatus() {
        return settleStatus;
    }

    /**
     * 设置结算状态，0=未结算，1=已结算
     *
     * @param settleStatus 结算状态，0=未结算，1=已结算
     */
    public void setSettleStatus(String settleStatus) {
        this.settleStatus = settleStatus == null ? null : settleStatus.trim();
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

    /**
     * 获取结算时间
     *
     * @return settle_time - 结算时间
     */
    public Long getSettleTime() {
        return settleTime;
    }

    /**
     * 设置结算时间
     *
     * @param settleTime 结算时间
     */
    public void setSettleTime(Long settleTime) {
        this.settleTime = settleTime;
    }

    /**
     * 获取备用字段1
     *
     * @return extend_1 - 备用字段1
     */
    public String getExtend1() {
        return extend1;
    }

    /**
     * 设置备用字段1
     *
     * @param extend1 备用字段1
     */
    public void setExtend1(String extend1) {
        this.extend1 = extend1 == null ? null : extend1.trim();
    }

    /**
     * 获取备用字段2
     *
     * @return extend_2 - 备用字段2
     */
    public String getExtend2() {
        return extend2;
    }

    /**
     * 设置备用字段2
     *
     * @param extend2 备用字段2
     */
    public void setExtend2(String extend2) {
        this.extend2 = extend2 == null ? null : extend2.trim();
    }
}