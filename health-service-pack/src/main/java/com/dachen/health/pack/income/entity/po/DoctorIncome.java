package com.dachen.health.pack.income.entity.po;


/**
 * 医生收入
 *@author wangqiao
 *@date 2016年1月23日
 *
 */
public class DoctorIncome {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 医生id
     */
    private Integer doctorId;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 订单收入
     */
    private Double orderIncome;

    /**
     * 订单分成收入
     */
    private Double shareIncome;

    /**
     * 实际收入
     */
    private Double actualIncome;

    /**
     * 上级医生分成比例
     */
    private Integer divisionDoctorProp;

    /**
     * 集团分成比例
     */
    private Integer divisionGroupProp;

    /**
     * 平台分成比例
     */
    private Integer divisionSysProp;

    /**
     * 上级分成医生id
     */
    private Integer divisionDoctorId;

    /**
     * 集团id
     */
    private String divisionGroupId;

    /**
     * 结算状态，2=未结算，1=已结算
     */
    private String settleStatus;

    /**
     * 订单状态，3=已付款，4=已完成
     */
    private String orderStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 完成时间
     */
    private Long completeTime;

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
     * 获取医生id
     *
     * @return doctor_id - 医生id
     */
    public Integer getDoctorId() {
        return doctorId;
    }

    /**
     * 设置医生id
     *
     * @param doctorId 医生id
     */
    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
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
     * 获取订单收入
     *
     * @return order_income - 订单收入
     */
    public Double getOrderIncome() {
        return orderIncome;
    }

    /**
     * 设置订单收入
     *
     * @param orderIncome 订单收入
     */
    public void setOrderIncome(Double orderIncome) {
        this.orderIncome = orderIncome;
    }

    /**
     * 获取订单分成收入
     *
     * @return share_income - 订单分成收入
     */
    public Double getShareIncome() {
        return shareIncome;
    }

    /**
     * 设置订单分成收入
     *
     * @param shareIncome 订单分成收入
     */
    public void setShareIncome(Double shareIncome) {
        this.shareIncome = shareIncome;
    }

    /**
     * 获取实际收入
     *
     * @return actual_income - 实际收入
     */
    public Double getActualIncome() {
        return actualIncome;
    }

    /**
     * 设置实际收入
     *
     * @param actualIncome 实际收入
     */
    public void setActualIncome(Double actualIncome) {
        this.actualIncome = actualIncome;
    }

    /**
     * 获取上级医生分成比例
     *
     * @return division_doctor_prop - 上级医生分成比例
     */
    public Integer getDivisionDoctorProp() {
        return divisionDoctorProp;
    }

    /**
     * 设置上级医生分成比例
     *
     * @param divisionDoctorProp 上级医生分成比例
     */
    public void setDivisionDoctorProp(Integer divisionDoctorProp) {
        this.divisionDoctorProp = divisionDoctorProp;
    }

    /**
     * 获取集团分成比例
     *
     * @return division_group_prop - 集团分成比例
     */
    public Integer getDivisionGroupProp() {
        return divisionGroupProp;
    }

    /**
     * 设置集团分成比例
     *
     * @param divisionGroupProp 集团分成比例
     */
    public void setDivisionGroupProp(Integer divisionGroupProp) {
        this.divisionGroupProp = divisionGroupProp;
    }

    /**
     * 获取平台分成比例
     *
     * @return division_sys_prop - 平台分成比例
     */
    public Integer getDivisionSysProp() {
        return divisionSysProp;
    }

    /**
     * 设置平台分成比例
     *
     * @param divisionSysProp 平台分成比例
     */
    public void setDivisionSysProp(Integer divisionSysProp) {
        this.divisionSysProp = divisionSysProp;
    }

    /**
     * 获取上级分成医生id
     *
     * @return division_doctor_id - 上级分成医生id
     */
    public Integer getDivisionDoctorId() {
        return divisionDoctorId;
    }

    /**
     * 设置上级分成医生id
     *
     * @param divisionDoctorId 上级分成医生id
     */
    public void setDivisionDoctorId(Integer divisionDoctorId) {
        this.divisionDoctorId = divisionDoctorId;
    }

    /**
     * 获取集团id
     *
     * @return division_group_id - 集团id
     */
    public String getDivisionGroupId() {
        return divisionGroupId;
    }

    /**
     * 设置集团id
     *
     * @param divisionGroupId 集团id
     */
    public void setDivisionGroupId(String divisionGroupId) {
        this.divisionGroupId = divisionGroupId == null ? null : divisionGroupId.trim();
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
     * 获取订单状态，3=已付款，4=已完成
     *
     * @return order_status - 订单状态，3=已付款，4=已完成
     */
    public String getOrderStatus() {
        return orderStatus;
    }

    /**
     * 设置订单状态，3=已付款，4=已完成
     *
     * @param orderStatus 订单状态，3=已付款，4=已完成
     */
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus == null ? null : orderStatus.trim();
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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
     * 获取完成时间
     *
     * @return complete_time - 完成时间
     */
    public Long getCompleteTime() {
        return completeTime;
    }

    /**
     * 设置完成时间
     *
     * @param completeTime 完成时间
     */
    public void setCompleteTime(Long completeTime) {
        this.completeTime = completeTime;
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