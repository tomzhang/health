package com.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
	
 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {
	
	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String partner = "2088021101653625"; 
	
	// 支付宝的公钥，无需修改该值                                             	
  //public static String ali_public_key  ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	public static String ali_public_key  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	//单笔交易查询接口 服务代码
	public static String ali_query_service="single_trade_query";
	//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	//单笔原路有密退款接口
	public static String ali_refund_fastpay_by_platform_pwd="refund_fastpay_by_platform_pwd";
	
	//pauck8签名使用
	public static String ali_prvate_key_pkcs8="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKOix7S2QXDzID7VfSuXw6W+HrJ34DGQtlJ7I9HFU5S1PpIfHnviRZxC+yrTzxMEBARS3f51FctX6Y0fXQeANlWOu3KH/aCByls9CENPkFA9oeVB8/ikHeUSos7SdgKWDHYB+zLbRYUAATwp1v2h1DOr56sLLkzo6DfVH1jG5j23AgMBAAECgYBprdYV8mK/JHVdvxjQj3nuT6OZlOu4dwIYsKhIcIzzDD51th1E2rQkH+oAFby1RjGl7TKrhEhoA8W0u12kW3w6qoxUEN+iYmXLwt8IbC9YNYRewSDTLGD0m4Ly9v5jI1Q+GxUiPzbCKJjggAz0vycbEK+TaDYDU890XbV6+4JQoQJBANXxXhg4n3JIMKLdBhxoIfvYwxntvNcManQUMk5ZZYwmQoP/LEXgiLw0mdSMKENdiu/xoMTprYF/1PpyBztN9ucCQQDDzbkrqPGjCnFXNscXOgiFiWfNEg50Z0tUcsLyur4vkQrnuMVr81hcuYhWYmMmUqU6NlWddbJIPEYa4dCUpDixAkBFQJIyyPFj7tY/gcgend6SRUIyPHBqNAypcnguQGXMjf3t+EV+gQYB1g0NbikCVV9J+C8QTovpZVWtlAefPMd1AkEAj9LuABbxnywNgt12siXrmoMKnwRzX6d8GsTNU9Q93NyyYPFE+n1d24ZgketlHwI34aGFk8qfMRYs/JQATR6vkQJATWdB/ZsquxSduL8+rnVArnO7wMGgIgZ9H36+kiwJTROJ7Fjw4A0+DB4joFkf2f7LeY2AN2YH1HE+YALV+8EvKQ==";
	
	public static String private_key="MIICXAIBAAKBgQCjose0tkFw8yA+1X0rl8Olvh6yd+AxkLZSeyPRxVOUtT6SHx574kWcQvsq088TBAQEUt3+dRXLV+mNH10HgDZVjrtyh/2ggcpbPQhDT5BQPaHlQfP4pB3lEqLO0nYClgx2Afsy20WFAAE8Kdb9odQzq+erCy5M6Og31R9YxuY9twIDAQABAoGAaa3WFfJivyR1Xb8Y0I957k+jmZTruHcCGLCoSHCM8ww+dbYdRNq0JB/qABW8tUYxpe0yq4RIaAPFtLtdpFt8OqqMVBDfomJly8LfCGwvWDWEXsEg0yxg9JuC8vb+YyNUPhsVIj82wiiY4IAM9L8nGxCvk2g2A1PPdF21evuCUKECQQDV8V4YOJ9ySDCi3QYcaCH72MMZ7bzXDGp0FDJOWWWMJkKD/yxF4Ii8NJnUjChDXYrv8aDE6a2Bf9T6cgc7TfbnAkEAw825K6jxowpxVzbHFzoIhYlnzRIOdGdLVHLC8rq+L5EK57jFa/NYXLmIVmJjJlKlOjZVnXWySDxGGuHQlKQ4sQJARUCSMsjxY+7WP4HIHp3ekkVCMjxwajQMqXJ4LkBlzI397fhFfoEGAdYNDW4pAlVfSfgvEE6L6WVVrZQHnzzHdQJBAI/S7gAW8Z8sDYLddrIl65qDCp8Ec1+nfBrEzVPUPdzcsmDxRPp9XduGYJHrZR8CN+GhhZPKnzEWLPyUAE0er5ECQE1nQf2bKrsUnbi/Pq51QK5zu8DBoCIGfR9+vpIsCU0TiexY8OANPgweI6BZH9n+y3mNgDdmB9RxPmAC1fvBLyk=";
	//public static String private_key="txyv5apqyya08soj2pyjjscmnu6dbj1y";
	
	//商户号
	public static String store_no ="jandk@cms.net.cn";
		                        
	public static String key = "nusqzsc00njsccsnmg7x5gi0zketfie4";
	
	public static String mobile_pay_type="mobile.securitypay.pay";
	
	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "/logs/test/";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";
	
	public static String alipay_type="1";
	
	// 签名方式 不需修改
	public static String sign_type = "RSA";
	public static String sign_type_md5 = "MD5";

}
