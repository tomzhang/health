package com.alipay.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.alipay.config.AlipayConfig;
import com.alipay.entity.SinglePayReqData;
import com.alipay.entity.SinglePayResData;
import com.alipay.sign.MD5;
import com.alipay.sign.RSA;
import com.alipay.util.httpClient.HttpProtocolHandler;
import com.alipay.util.httpClient.HttpRequest;
import com.alipay.util.httpClient.HttpResponse;
import com.alipay.util.httpClient.HttpResultType;
import com.dachen.commons.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* *
 *类名：AlipaySubmit
 *功能：支付宝各接口请求提交类
 *详细：构造支付宝各接口表单HTML文本，获取远程HTTP数据
 *版本：3.3
 *日期：2012-08-13
 *说明：
 *商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipaySubmit {

    protected static Logger logger = LoggerFactory.getLogger(AlipaySubmit.class);
    
    /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
    public static final String ALIPAY_RESULT_T = "T";
    public static final String ALIPAY_RESULT_F = "F";
    
    public static Boolean doaliPayQueryLoop(int loopCount, String outTradeNo,String alipayNo) throws ServiceException {
        //至少查询一次
        if (loopCount == 0) {
            loopCount = 1;
        }
        //进行循环查询
        for (int i = 0; i < loopCount; i++) {
            if (doOnePayQuery(outTradeNo,alipayNo)) {
                return true;
            }
        }
        return false;
    }
    
    
    public static Boolean doOnePayQuery(String payNo,String alipayNo) {
    	
    	if(null ==payNo&&null == alipayNo) {
			throw new ServiceException(400,"交易查询订单号不能为空");
		}
		
		try {
			SinglePayReqData singlePayReqData = new SinglePayReqData(payNo, alipayNo);
			logger.info("支付宝单笔交易请求参数:{}", singlePayReqData.toMap());
			String reuntPayResultXml = AlipaySubmit.buildRequest("", "", singlePayReqData.toMap());
			logger.info("支付宝单笔交易查询返回XML参数:{}", reuntPayResultXml);
			if(null == reuntPayResultXml) {
				return false;
			}
			
			SinglePayResData resData = (SinglePayResData) UtilData.getObjectFromXML(reuntPayResultXml, SinglePayResData.class,"alipay");
			//根据返回的参数进行反钓鱼验证
			if(AlipaySubmit.ALIPAY_RESULT_T.equals(resData.getIs_success())) {
				return true;
			}else {
				return false;
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
    }
    
    public static String buildRequestSign(Map<String, String> sPara,String md5) {
    	String prestr = AlipayCore.createLinkString(sPara,1); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = MD5.sign(prestr, AlipayConfig.key, AlipayConfig.input_charset);
        return mysign;
    }
    
    /**
     * 生成签名结果
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
	public static String buildRequestSign(Map<String, String> sPara) {
    	String prestr = AlipayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = "";
        if(AlipayConfig.sign_type.equals("MD5") ) {
        	mysign = MD5.sign(prestr, AlipayConfig.private_key, AlipayConfig.input_charset);
        }else if(AlipayConfig.sign_type.equals("RSA")){
        	mysign = RSA.sign(prestr, AlipayConfig.ali_prvate_key_pkcs8, AlipayConfig.input_charset);
        }
        return mysign;
    }
	 
	/**
     * 生成签名结果
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
	public static String buildRequestMySign(Map<String, String> sPara) {
    	String prestr = AlipayCore.createLinkString(sPara,0); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = "";
        if(AlipayConfig.sign_type.equals("MD5") ) {
        	mysign = MD5.sign(prestr, AlipayConfig.private_key, AlipayConfig.input_charset);
        }else if(AlipayConfig.sign_type.equals("RSA")){
        	mysign = RSA.sign(prestr, AlipayConfig.ali_prvate_key_pkcs8, AlipayConfig.input_charset);
        }
        return mysign;
    }

	
   

    
    
   
    
    /**
     * 建立请求，以模拟远程HTTP的POST请求方式构造并获取支付宝的处理结果
     * 如果接口中没有上传文件参数，那么strParaFileName与strFilePath设置为空值
     * 如：buildRequest("", "",sParaTemp)
     * @param strParaFileName 文件类型的参数名
     * @param strFilePath 文件路径
     * @param sParaTemp 请求参数数组
     * @return 支付宝处理结果
     * @throws Exception
     */
    public static String buildRequest(String strParaFileName, String strFilePath,Map<String, String> sParaTemp) throws Exception {
       
        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();

        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        //设置编码集
        request.setCharset(AlipayConfig.input_charset);
        request.setParameters(generatNameValuePair(sParaTemp));
        request.setUrl(ALIPAY_GATEWAY_NEW+"_input_charset="+AlipayConfig.input_charset);

        HttpResponse response = httpProtocolHandler.execute(request,strParaFileName,strFilePath);
        if (response == null) {
            return null;
        }
        
        String strResult = response.getStringResult();

        return strResult;
    }
    
    public static String buildRequestGet(Map<String, String> sParaTemp) throws Exception {
        
        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();

        HttpRequest request = new HttpRequest(HttpResultType.STRING);
        //设置编码集
        request.setMethod("GET");
        request.setCharset(AlipayConfig.input_charset);
        request.setParameters(generatNameValuePair(sParaTemp));
        request.setUrl(ALIPAY_GATEWAY_NEW+"_input_charset="+AlipayConfig.input_charset);

        HttpResponse response = httpProtocolHandler.execute(request,"","");
        if (response == null) {
            return null;
        }
        
        String strResult = response.getStringResult();

        return strResult;
    }
    
    
	/**
	 * 建立请求，以表单HTML形式构造（默认）
	 * 
	 * @param sParaTemp
	 *            请求参数数组
	 * @param strMethod
	 *            提交方式。两个值可选：post、get
	 * @param strButtonName
	 *            确认按钮显示文字
	 * @return 提交表单HTML文本
	 */
	public static String buildRequest(Map<String, String> sParaTemp, String strMethod, String strButtonName) {
		// 待请求参数数组
		List<String> keys = new ArrayList<String>(sParaTemp.keySet());
		StringBuffer sbHtml = new StringBuffer();

		sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + ALIPAY_GATEWAY_NEW
				+ "_input_charset=" + AlipayConfig.input_charset + "\" method=\"" + strMethod + "\"    >");

		for (int i = 0; i < keys.size(); i++) {
			String name = (String) keys.get(i);
			String value = (String) sParaTemp.get(name);

			sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
		}

		// submit按钮控件请不要含有name属性
		sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
		sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");

		return sbHtml.toString();
	}
    
	
    public static String buildRequest(Map<String, String> sParaTemp) throws Exception{
    	return buildRequest("", "",sParaTemp);
    }
    
    
    /**
     * MAP类型数组转换成NameValuePair类型
     * @param properties  MAP类型数组
     * @return NameValuePair类型数组
     */
    private static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
        }

        return nameValuePair;
    }
    
    /**
     * 用于防钓鱼，调用接口query_timestamp来获取时间戳的处理函数
     * 注意：远程解析XML出错，与服务器是否支持SSL等配置有关
     * @return 时间戳字符串
     * @throws IOException
     * @throws DocumentException
     * @throws MalformedURLException
     */
	public static String query_timestamp() throws MalformedURLException,
                                                        DocumentException, IOException {

        //构造访问query_timestamp接口的URL串
        String strUrl = ALIPAY_GATEWAY_NEW + "service=query_timestamp&partner=" + AlipayConfig.partner + "&_input_charset" +AlipayConfig.input_charset;
        StringBuffer result = new StringBuffer();

        SAXReader reader = new SAXReader();
        Document doc = reader.read(new URL(strUrl).openStream());

        List<Node> nodeList = doc.selectNodes("//alipay/*");

        for (Node node : nodeList) {
            // 截取部分不需要解析的信息
            if (node.getName().equals("is_success") && node.getText().equals("T")) {
                // 判断是否有成功标示
                List<Node> nodeList1 = doc.selectNodes("//response/timestamp/*");
                for (Node node1 : nodeList1) {
                    result.append(node1.getText());
                }
            }
        }

        return result.toString();
    }
	
	
}
