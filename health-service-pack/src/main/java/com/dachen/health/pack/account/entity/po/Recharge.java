package com.dachen.health.pack.account.entity.po;

import com.dachen.health.commons.constants.AccountEnum;

/**
 * ProjectName： health-service<br>
 * ClassName： Recharge<br>
 * Description： 充值记录表<br>
 * 
 * @author fanp
 * @createTime 2015年8月5日
 * @version 1.0.0
 */
public class Recharge {

    /* 主键 */
    private Integer id;

    /* 会员id */
    private Integer userId;

    /* 充值金额 */
    private Long rechargeMoney;

    /** 支付类型 {@link AccountEnum.PayType} */
    private Integer payType;

    /* 发送给第三方支付接口唯一订单号 */
    private String payNo;

    /* 创建时间 */
    private Long createTime;

    /** 充值状态 {@link AccountEnum.RechargeStatus} */
    private Integer rechargeStatus;

    /** 来源类型 {@link AccountEnum.SourceType} */
    private Integer sourceType;

    /* 来源id */
    private Integer sourceId;
    
    private String param;
    
    private String partner;
    
    public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getRechargeMoney() {
        return rechargeMoney;
    }

    public void setRechargeMoney(Long rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getRechargeStatus() {
        return rechargeStatus;
    }

    public void setRechargeStatus(Integer rechargeStatus) {
        this.rechargeStatus = rechargeStatus;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

}
