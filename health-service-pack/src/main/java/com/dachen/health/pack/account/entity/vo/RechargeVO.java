package com.dachen.health.pack.account.entity.vo;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： RechargeParam<br>
 * Description：充值记录放回实体 <br>
 * 
 * @author fanp
 * @createTime 2015年8月7日
 * @version 1.0.0
 */
public class RechargeVO implements java.io.Serializable {

    private static final long serialVersionUID = 3693936761565127913L;
    
    private Integer id;
    
    private Integer userId;
    
    /* 充值金额 */
    private Long rechargeMoney;

    /* 支付类型 {@link AccountEnum.PayType} */
    private Integer payType;

    /* 充值状态 {@link AccountEnum.RechargeStatus} */
    private Integer rechargeStatus;
    
    /*来源类型*/
    private Integer sourceType;
    
    /*来源ID*/
    private Integer sourceId;
    
    private String payNo;
    
    private String alipayNo;
    
    private String partner;
    
    public String getAlipayNo() {
		return alipayNo;
	}

	public void setAlipayNo(String alipayNo) {
		this.alipayNo = alipayNo;
	}

	public String getPayNo() {
		return payNo;
	}

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

    public Integer getRechargeStatus() {
        return rechargeStatus;
    }

    public void setRechargeStatus(Integer rechargeStatus) {
        this.rechargeStatus = rechargeStatus;
    }

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	@Override
	public String toString() {
		return "RechargeVO{" +
				"id=" + id +
				", userId=" + userId +
				", rechargeMoney=" + rechargeMoney +
				", payType=" + payType +
				", rechargeStatus=" + rechargeStatus +
				", sourceType=" + sourceType +
				", sourceId=" + sourceId +
				", payNo='" + payNo + '\'' +
				", alipayNo='" + alipayNo + '\'' +
				", partner='" + partner + '\'' +
				'}';
	}
}
