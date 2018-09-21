package com.dachen.health.pack.income.entity.vo;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IncomeDetailVO<br>
 * Description： 收入明细实体<br>
 * 
 * @author fanp
 * @createTime 2015年8月18日
 * @version 1.0.0
 */
public class IncomeDetailVO implements java.io.Serializable {

    private static final long serialVersionUID = 7398406169042528878L;

    private Integer doctorId;

    /* 订单金额 */
    private Long money;

    /* 备注 */
    private String remark;

    /* 收入时间(订单完成付款时间) */
    private Long incomeTime;

    /* 结算时间 */
    private Long finishTime;

    /* 状态 {@link IncomeEnum.IncomeStatus} */
    private Integer status;

    private Integer userId;

    /* 用户名 */
    private String userName;

    /* 头像 */
    private String headPicFileName;

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadPicFileName() {
        return headPicFileName;
    }

    public void setHeadPicFileName(String headPicFileName) {
        this.headPicFileName = headPicFileName;
    }
}