package com.dachen.health.pack.income.entity.vo;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IncomeVO<br>
 * Description： 收入表实体<br>
 * 
 * @author fanp
 * @createTime 2015年8月18日
 * @version 1.0.0
 */
public class IncomeVO implements java.io.Serializable{

    private static final long serialVersionUID = 4746648653422264985L;

    private Integer doctorId;

    /* 总收入 */
    private Long totalIncome;

    /* 未结算收入 */
    private Long outIncome;

    
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