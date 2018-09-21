package com.dachen.health.pack.income.entity.po;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IncomeDetail<br>
 * Description： 收入明细实体<br>
 * 
 * @author fanp
 * @createTime 2015年8月18日
 * @version 1.0.0
 */
public class IncomeDetail {
    private Integer id;

    /* 医生id */
    private Integer doctorId;

    /* 用户id */
    private Integer userId;

    /* 订单id */
    private Integer orderId;

    /* 套餐id */
    private Integer packId;

    /* 收益金额 */
    private Long money;

    /* 分成金额 */
    private Long shareMoney;

    /* 收益类型(1:订单收益，2:分成收益) */
    private Integer incomeType;

    /* 备注 */
    private String remark;

    /* 收入时间(订单完成付款时间) */
    private Long incomeTime;

    /* 创建时间 */
    private Long createTime;

    /* 结算时间 */
    private Long finishTime;

    /* 状态 {@link IncomeEnum.IncomeStatus} */
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getPackId() {
        return packId;
    }

    public void setPackId(Integer packId) {
        this.packId = packId;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public Long getShareMoney() {
        return shareMoney;
    }

    public void setShareMoney(Long shareMoney) {
        this.shareMoney = shareMoney;
    }

    public Integer getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(Integer incomeType) {
        this.incomeType = incomeType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getIncomeTime() {
        return incomeTime;
    }

    public void setIncomeTime(Long incomeTime) {
        this.incomeTime = incomeTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}