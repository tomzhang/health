package com.tencent.protocol.pay_protocol;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.tencent.common.Configure;
import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;

public class AppPayReqData {
	
	private String appid = "";
    private String partnerid = "";
    private String prepayid = "";
    private String packageValue = "";
    private String timestamp = "";
    private String noncestr = "";
    private String sign = "";
    
    public AppPayReqData(String prepayid, String timestamp) {
    	
    	//微信分配的公众号ID（开通公众号之后可以获取到）
        setAppid(Configure.getAppid());
        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setPartnerid(Configure.getMchid());
        setTimestamp(timestamp);
        setPackageValue("Sign=WXPay");
        setPrepayid(prepayid);
        //随机字符串，不长于32 位
        setNoncestr(RandomStringGenerator.getRandomStringByLength(32));
        String sign = Signature.getSign(toMap());
        setSign(sign);//把签名数据设置到Sign这个属性中
    }

    
   
	
	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getPartnerid() {
		return partnerid;
	}


	public void setPartnerid(String partnerid) {
		this.partnerid = partnerid;
	}




	public String getPrepayid() {
		return prepayid;
	}




	public void setPrepayid(String prepayid) {
		this.prepayid = prepayid;
	}




	public String getPackageValue() {
		return packageValue;
	}




	public void setPackageValue(String packageValue) {
		this.packageValue = packageValue;
	}




	public String getTimestamp() {
		return timestamp;
	}




	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}




	public String getNoncestr() {
		return noncestr;
	}




	public void setNoncestr(String noncestr) {
		this.noncestr = noncestr;
	}




	public String getSign() {
		return sign;
	}




	public void setSign(String sign) {
		this.sign = sign;
	}
	

	public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<String, Object>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object obj;
            try {
                obj = field.get(this);
                if(obj!=null){
                	if("packageValue".equals(field.getName())) {
                		 map.put("package", obj);
                	}else {
                		 map.put(field.getName(), obj);
                	}
                	//map.put(field.getName(), obj);
                   
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
