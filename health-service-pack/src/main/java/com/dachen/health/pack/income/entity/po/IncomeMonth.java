package com.dachen.health.pack.income.entity.po;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IncomeMonth<br>
 * Description：月收入统计表实体 <br>
 * 
 * @author fanp
 * @createTime 2015年8月18日
 * @version 1.0.0
 */
public class IncomeMonth {
    private Integer id;

    /* 医生id */
    private Integer doctorId;

    /* 月份，格式201508 */
    private Integer month;

    /* 金额 */
    private Long money;

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

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }
}