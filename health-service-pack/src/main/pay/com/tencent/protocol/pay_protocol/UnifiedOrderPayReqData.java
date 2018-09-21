package com.tencent.protocol.pay_protocol;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.tencent.common.Configure;
import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;

public class UnifiedOrderPayReqData {
	
	private String appid = "";
    private String mch_id = "";
    private String nonce_str = "";
    private String sign = "";
    private String body = "";
    private String detail = "";
    private String out_trade_no = "";
    private int total_fee = 0;
    private String spbill_create_ip = "";
    private String notify_url = "";
    private String trade_type="";
    
    /**
     * 
     * @param body 商品或支付单简要描述
     * @param detail 商品描述
     * @param attach 附加数据
     * @param out_trade_no 商户订单号
     * @param total_fee 总金额
     * @param time_start 交易起始时间
     * @param time_expire 交易结束时间
     * @param goods_tag 商品标记
     * @param notify_url 通知地址
     * @param trade_type 交易类型
     * @param limit_pay 指定支付方式
     */
    public UnifiedOrderPayReqData(String body,String detail,String out_trade_no,int total_fee,String notify_url,String trade_type) {
    	/*//微信分配的公众号ID（开通公众号之后可以获取到）
        setAppid(Configure.getAppid());
        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setMch_id(Configure.getMchid());
        //要支付的商品的描述信息，用户会在支付成功页面里看到这个信息
        setBody(body);
        //商品详情
        setDetail(detail);
        //订单号
        setOut_trade_no(out_trade_no);
        //设置订单金额 单位分
        setTotal_fee(total_fee);
        setSpbill_create_ip(Configure.getIP());
        setNotify_url(notify_url);
        setTrade_type("APP");
        //随机字符串，不长于32 位
        setNonce_str(RandomStringGenerator.getRandomStringByLength(32));
        
        String sign = Signature.getSign(toMap());
        setSign(sign);*///把签名数据设置到Sign这个属性中
    	this(body, detail, out_trade_no, total_fee, notify_url, trade_type, false);
    }
    
    public UnifiedOrderPayReqData(String body,String detail,String out_trade_no,int total_fee,String notify_url,String trade_type, boolean isBDJL) {
    	//注册标记（区分博德嘉联）
    	Configure.setBDJL(isBDJL);
    	//微信分配的公众号ID（开通公众号之后可以获取到）
        setAppid(Configure.getAppid());
        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setMch_id(Configure.getMchid());
        //要支付的商品的描述信息，用户会在支付成功页面里看到这个信息
        setBody(body);
        //商品详情
        setDetail(detail);
        //订单号
        setOut_trade_no(out_trade_no);
        //设置订单金额 单位分
        setTotal_fee(total_fee);
        setSpbill_create_ip(Configure.getIP());
        setNotify_url(notify_url);
        setTrade_type("APP");
        //随机字符串，不长于32 位
        setNonce_str(RandomStringGenerator.getRandomStringByLength(32));
        
        String sign = Signature.getSign(toMap());
        setSign(sign);//把签名数据设置到Sign这个属性中
    }

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}


	public String getMch_id() {
		return mch_id;
	}


	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}


	public String getNonce_str() {
		return nonce_str;
	}


	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}


	public String getSign() {
		return sign;
	}


	public void setSign(String sign) {
		this.sign = sign;
	}


	public String getBody() {
		return body;
	}


	public void setBody(String body) {
		this.body = body;
	}


	public String getDetail() {
		return detail;
	}


	public void setDetail(String detail) {
		this.detail = detail;
	}


	public String getOut_trade_no() {
		return out_trade_no;
	}


	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}


	public int getTotal_fee() {
		return total_fee;
	}


	public void setTotal_fee(int total_fee) {
		this.total_fee = total_fee;
	}


	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}


	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}


	public String getNotify_url() {
		return notify_url;
	}


	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	
	public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<String, Object>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object obj;
            try {
                obj = field.get(this);
                if(obj!=null){
                    map.put(field.getName(), obj);
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
