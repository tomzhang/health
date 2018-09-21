package com.dachen.health.pack.income.entity.po;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： Income<br>
 * Description： 收入表实体<br>
 * 
 * @author fanp
 * @createTime 2015年8月18日
 * @version 1.0.0
 */
public class Income {
    private Integer id;

    private Integer doctorId;

    /* 总收入 */
    private Long totalIncome;

    /* 未结算收入 */
    private Long outIncome;

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

    public Long getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Long totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Long getOutIncome() {
        return outIncome;
    }

    public void setOutIncome(Long outIncome) {
        this.outIncome = outIncome;
    }
}