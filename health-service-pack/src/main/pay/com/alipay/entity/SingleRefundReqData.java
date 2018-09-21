package com.alipay.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayCore;
import com.alipay.util.AlipaySubmit;
import com.dachen.health.commons.constants.PackConstants;
import com.dachen.util.DateUtil;
import com.dachen.util.PropertiesUtil;

/**
 *  支付宝退款请求实体类
 *  
 *  笔数据集格式为：第一笔交易退款数据集#第二笔交易退款数据集#第三笔交易退款数据集…#第N笔交易退款数据集；
 *	交易退款数据集的格式为：原付款支付宝交易号^退款总金额^退款理由；
 *	不支持退分润功能。
 *  detail_data中的退款笔数总和要等于参数batch_num的值；
 *  “退款理由”长度不能大于256字节，“退款理由”中不能有“^”、“|”、“$”、“#”等影响detail_data格式的特殊字符；
 *  detail_data中退款总金额不能大于交易总金额；
 *  一笔交易可以多次退款，退款次数最多不能超过99次，需要遵守多次退款的总金额不超过该笔交易付款金额的原则。
 *  
 * @author 谢佩
 *
 */
public class SingleRefundReqData {
	
	
	private String service;//服务接口名称
	private String partner;//合作身份ID
	private String _input_charset;//编码格式
	private String sign_type;//签名格式类型，MD5,RSA
	private String sign;//数据签名
	private String notify_url;//回调通知链接
	private String seller_email;//卖家用户邮箱
	private String seller_user_id;//卖家用户ID,
	private String refund_date;//退款请求时间,格式：yyyy-MM-dd hh:mm:ss
	private String batch_no;//退款批次号
	private String batch_num;//总笔数 默认为1
	private String detail_data;//单笔数据集
	
	public SingleRefundReqData(String batch_refund_no,String refundNum,String detail_data)  {
		/*setService("refund_fastpay_by_platform_pwd");
		setPartner("2088021101653625");
		set_input_charset("utf-8");
		setNotify_url("http://192.168.1.1:8080/refund_fastpay_by_platform_pwd-JAVA-UTF-8/notify_url.jsp");
		setSeller_email("jandk@cms.net.cn");
		//setSeller_user_id(AlipayConfig.partner);
		setRefund_date("2016-01-14 10:05:11");
		setBatch_no("201601141101");
		setBatch_num("1");
		setDetail_data("2016011221001004980090482006^0.01^协商退款");
		Map<String, String> sPara = AlipayCore.paraFilter(toMap());
		String sign = AlipaySubmit.buildRequestSign(sPara, "md5");
		setSign(sign);
		setSign_type("MD5");*/
		
		setService(AlipayConfig.ali_refund_fastpay_by_platform_pwd);
		setPartner(AlipayConfig.partner);
		setSeller_user_id(AlipayConfig.partner);
		set_input_charset(AlipayConfig.input_charset);
		setNotify_url(PropertiesUtil.getContextProperty(PackConstants.ALIPAY_REFUND_CALL_BACK));
		setSeller_email(AlipayConfig.store_no);
		setRefund_date(DateUtil.formatDate2Str());
		setBatch_no(batch_refund_no);
		setBatch_num(refundNum);
		setDetail_data(detail_data);
		Map<String, String> sPara = AlipayCore.paraFilter(toMap());
		String sign = AlipaySubmit.buildRequestSign(sPara, "md5");
		setSign(sign);
		setSign_type(AlipayConfig.sign_type_md5);
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
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getSeller_email() {
		return seller_email;
	}
	public void setSeller_email(String seller_email) {
		this.seller_email = seller_email;
	}
	public String getSeller_user_id() {
		return seller_user_id;
	}
	public void setSeller_user_id(String seller_user_id) {
		this.seller_user_id = seller_user_id;
	}
	public String getRefund_date() {
		return refund_date;
	}
	public void setRefund_date(String refund_date) {
		this.refund_date = refund_date;
	}
	public String getBatch_no() {
		return batch_no;
	}
	public void setBatch_no(String batch_no) {
		this.batch_no = batch_no;
	}
	public String getBatch_num() {
		return batch_num;
	}
	public void setBatch_num(String batch_num) {
		this.batch_num = batch_num;
	}
	public String getDetail_data() {
		return detail_data;
	}
	public void setDetail_data(String detail_data) {
		this.detail_data = detail_data;
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
