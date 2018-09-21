package com.alipay.entity;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayCore;
import com.alipay.util.AlipaySubmit;

public class SinglePayReqData {
	
	private String service="";
	private String partner="";
	private String _input_charset="";
	private String trade_no="";//支付宝订单ID
	private String out_trade_no="";//payNo
	private String sign="";
	private String sign_type="";
	
	public SinglePayReqData(String payNo,String out_trade_no) throws UnsupportedEncodingException {
		setService(AlipayConfig.ali_query_service);
		setPartner(AlipayConfig.partner);
		set_input_charset(AlipayConfig.input_charset);
		setTrade_no(out_trade_no);
		setOut_trade_no(payNo);
		Map<String, String> sPara = AlipayCore.paraFilter(toMap());
		String signy = AlipaySubmit.buildRequestMySign(sPara);
		setSign(signy);
		setSign_type(AlipayConfig.sign_type);
	}
	
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSign_type() {
		return sign_type;
	}
	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public String get_input_charset() {
		return _input_charset;
	}
	public void set_input_charset(String _input_charset) {
		this._input_charset = _input_charset;
	}
	public String getTrade_no() {
		return trade_no;
	}
	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	
	 public Map<String,String> toMap(){
	        Map<String,String> map = new HashMap<String, String>();
	        Field[] fields = this.getClass().getDeclaredFields();
	        for (Field field : fields) {
	            Object obj;
	            try {
	                obj = field.get(this);
	                if(obj!=null){
	                    map.put(field.getName(), String.valueOf(obj));
	                }
	            } catch (IllegalArgumentException e) {
	                e.printStackTrace();
	            } catch (IllegalAccessException e) {
	                e.printStackTrace();
	            }
	        }
	        return map;
	    }
	

}
