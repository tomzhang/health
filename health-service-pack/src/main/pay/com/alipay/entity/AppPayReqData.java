package com.alipay.entity;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayCore;
import com.alipay.util.AlipaySubmit;
import com.dachen.health.commons.constants.PackConstants;
import com.dachen.util.PropertiesUtil;


public class AppPayReqData {
	
	private String _input_charset = "";
	private String notify_url = "";
	private String total_fee ="";
	private String out_trade_no = "";
	private String service = "";
	private String body="";
	private String subject = "";
	private String seller_id = "";
	private String partner = "";
	private String payment_type = "";
	private String sign_type ="";
	private String sign = "";
	
	/**
	 * 
	 * @param out_trade_no 订单号
	 * @param total_fee 支付金额
	 * @param body 商品标题
	 * @param subject 商品信息
	 * @param payTimespam 去系统毫秒数
	 * @throws UnsupportedEncodingException 
	 */
	public AppPayReqData(String out_trade_no,Integer total_fee,String body,String subject,Long payTimespam) throws UnsupportedEncodingException {
		set_input_charset(AlipayConfig.input_charset);
		setNotify_url(PropertiesUtil.getContextProperty(PackConstants.ALIPAY_CALL_BACK));
		setSeller_id(AlipayConfig.store_no);
		setPartner(AlipayConfig.partner);
		setService(AlipayConfig.mobile_pay_type);
		setPayment_type(AlipayConfig.alipay_type);
		setOut_trade_no(out_trade_no);
		setTotal_fee(((float)total_fee)/100+"");
		setBody(body);
		setSubject(subject);
		//setGmt_create(UtilDate.getDateToString(new Date(payTimespam)));
		Map<String, String> sPara = AlipayCore.paraFilter(toMap());
		String sign = AlipaySubmit.buildRequestSign(sPara);
		setSign(URLEncoder.encode(sign, "utf-8"));
		setSign_type(AlipayConfig.sign_type);
		
	}
	
	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	public String get_input_charset() {
		return _input_charset;
	}
	public void set_input_charset(String _input_charset) {
		this._input_charset = _input_charset;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getSeller_id() {
		return seller_id;
	}
	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	
	public String toPayToLinkString() {
		return AlipayCore.createLinkString(toMap());
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
