package com.dachen.health.pack.pay.entity;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： OrderParam<br>
 * Description：支付宝回调参数接收类 <br>
 * 
 * @author peiX
 * @createTime 2015年8月10日
 * @version 1.0.0
 */
public class PaymentVO {
	
	/*合作者身份ID*/
	private String partner;
	
	/*支付订单编号*/
	private String payNo;
	
	private String payAlftNo;
	
	/*支付总金额 */
	private Integer paymentMoney;
	
	/*支付状态*/
	private String tradeStatus;
	
	/*支付类型*/
	private Integer payType;
	
	/*是否成功*/
	private Boolean isSuccess;

	
	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getPayAlftNo() {
		return payAlftNo;
	}

	public void setPayAlftNo(String payAlftNo) {
		this.payAlftNo = payAlftNo;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getPayNo() {
		return payNo;
	}

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	public Integer getPaymentMoney() {
		return paymentMoney;
	}

	public void setPaymentMoney(Integer paymentMoney) {
		this.paymentMoney = paymentMoney;
	}

	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}


	@Override
	public String toString() {
		return "PaymentVO{" +
				"partner='" + partner + '\'' +
				", payNo='" + payNo + '\'' +
				", payAlftNo='" + payAlftNo + '\'' +
				", paymentMoney=" + paymentMoney +
				", tradeStatus='" + tradeStatus + '\'' +
				", payType=" + payType +
				", isSuccess=" + isSuccess +
				'}';
	}
}
