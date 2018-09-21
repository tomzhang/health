package com.dachen.health.pack.account.entity.vo;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： Bank<br>
 * Description： 银行<br>
 * 
 * @author fanp
 * @createTime 2015年8月18日
 * @version 1.0.0
 */
public class BankVO implements java.io.Serializable {

    private static final long serialVersionUID = 7865137266707252991L;

    private Integer id;

    private String bankName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}