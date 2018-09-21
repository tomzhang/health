package com.dachen.health.pack.order.entity.param;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： OrderParam<br>
 * Description：购买套餐订单参数接收类 <br>
 * 
 * @author fanp
 * @createTime 2015年8月10日
 * @version 1.0.0
 */
public class CheckOrderParam {

    /* 购买用户id */
    private Integer userId;

    /* 支付类型 */
    private Integer payType;
    
    /* 病情id */
    private String payNo;
    
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

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

}
