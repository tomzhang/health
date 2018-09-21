package com.dachen.health.pack.account.entity.param;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： RechargeParam<br>
 * Description：充值参数实体 <br>
 * 
 * @author fanp
 * @createTime 2015年8月7日
 * @version 1.0.0
 */
public class RechargeParam {
	
	private Integer id;

    private Integer userId;

    private Long money;

    /* 支付类型 {@link AccountEnum.PayType} */
    private Integer payType;

    /* 第三方支付订单号 */
    private String payNo;

    private Integer SourceType;

    private Integer SourceId;
    
    private Integer orderId;
    
    private String partner;
    
    
    public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
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

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
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

    public Integer getSourceType() {
        return SourceType;
    }

    public void setSourceType(Integer sourceType) {
        SourceType = sourceType;
    }

    public Integer getSourceId() {
        return SourceId;
    }

    public void setSourceId(Integer sourceId) {
        SourceId = sourceId;
    }

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

}
