package com.alipay.entity;

/**
 * 支付宝通知请求返回参数
 * @author Administrator
 *
 */
public class PayNotifyReqData {
	
	private String appid;//应用APPID
	
	private String out_trade_no;//系统订单号
	
	private String trade_no;//支付宝订单号
	
	private String trade_status;//交易状态
	
	private String total_fee;//交易金额
	

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

	public String getTrade_status() {
		return trade_status;
	}

	public void setTrade_status(String trade_status) {
		this.trade_status = trade_status;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	@Override
	public String toString() {
		return "PayNotifyReqData{" +
				"appid='" + appid + '\'' +
				", out_trade_no='" + out_trade_no + '\'' +
				", trade_no='" + trade_no + '\'' +
				", trade_status='" + trade_status + '\'' +
				", total_fee='" + total_fee + '\'' +
				'}';
	}
}
