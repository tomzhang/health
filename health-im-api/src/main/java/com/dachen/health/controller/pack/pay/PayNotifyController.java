package com.dachen.health.controller.pack.pay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.entity.PayNotifyReqData;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.pack.pay.service.IAlipayPayService;
import com.dachen.health.pack.pay.service.IWechatPayService;
import com.tencent.common.Util;
import com.tencent.protocol.pay_protocol.NoitfyPayResData;


/**
 * ProjectName： Pay Notify<br>
 * ClassName： PayNotifyController<br>
 * Description： 支付通知 controller<br>
 *
 * @author xiepei
 * @version 1.0.0
 * @createTime 2015年8月17日
 */
@RestController
@RequestMapping("/pack/paynotify")
public class PayNotifyController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IAlipayPayService aliPayService;

    @Autowired
    private IWechatPayService wechatPayService;

    /**
     * 支付宝收款回调通知
     */
    @RequestMapping(value = "/alipaycallback")
    public void alipayNotityCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tag = "alipaycallback";
        logger.info("{}. User-Agent={}", tag, request.getHeader("User-Agent"));
        PrintWriter out = response.getWriter();
        try {
            /*获取请求参数*/
            Map<String, String> params = new HashMap<String, String>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //logger.info("签名字段验证--------"+name+"-------"+valueStr);
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                params.put(name, valueStr);
            }
            logger.info("{}. 支付宝通知请求参数：{}", tag, params.toString());
            if (!aliPayService.isValidate(params)) {
                logger.error("{}. 支付宝通知验证失败!", tag);
                out.print("fail");
                return;
            }

            //封装回调参数
            PayNotifyReqData payNotifyReqData = aliPayService.alipayRequsetHandle(params);

            try {
                aliPayService.handleCallBackOrderHandelFunction(payNotifyReqData);
                out.print("success");
            } catch (ServiceException e) {
                logger.error("handleCallBackOrderHandelFunction报ServiceException：" + e.getResultCode(), e);
                logger.error(e.getMessage(), e);
                if (e.getResultCode() == 200) {
                    out.print("success");
                } else {
                    out.print("fail");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            out.print("fail");
        } finally {
            out.close();
        }
    }

    /**
     * 微信支付通知
     */
    @RequestMapping(value = "/wxpaycallback", method = RequestMethod.POST)
    public void wxpayNotityCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tag = "wxpaycallback";
        logger.info("{}. User-Agent={}", tag, request.getHeader("User-Agent"));

        PrintWriter out = response.getWriter();
        InputStream inStream = request.getInputStream();
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String resultxml = new String(outSteam.toByteArray(), "utf-8");
            logger.info("{}. resultxml={}", tag, resultxml);
            String returnXml = wechatPayService.handleCallBackOrderHandelFunction(resultxml);
            logger.info("{}. returnXml={}", tag, returnXml);
            out.print(new ByteArrayInputStream(returnXml.getBytes(Charset.forName("UTF-8"))));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            out.print(new ByteArrayInputStream(Util.setXML(Util.FAIL, "后台系统异常").getBytes(Charset.forName("UTF-8"))));
        } finally {
            IOUtils.closeQuietly(outSteam);
            out.close();
        }
    }

}
