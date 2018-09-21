package com.dachen.health.controller.pack.pay;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.entity.PayNotifyReqData;
import com.alipay.entity.SingleRefundResData;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.logger.LoggerUtils;
import com.dachen.health.pack.pay.service.IAlipayPayService;


/**
 * ProjectName：Refund Notify<br>
 * ClassName： RefundNotifyController<br>
 * Description： 退款通知 controller<br>
 * 
 * @author xiepei
 * @createTime 2016年1月8日
 * @version 1.0.0
 */
@RestController
@RequestMapping("/pack/refundnotify")
public class RefundNotifyController {
	@Autowired
	private IAlipayPayService aliPayService;
	
	/**
	 * 支付宝原路退款回调接口
	 */
	@RequestMapping(value = "/alipaycallback")
	public void alipayNotityCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();
		try {
			/*获取请求参数*/
			Map<String,String> params = new HashMap<String,String>();
			Map<String,String[]> requestParams = request.getParameterMap();
			for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
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
			LoggerUtils.printCommonLog("支付宝通知请求参数："+params.toString());
			if(!aliPayService.isValidate(params)) {
				LoggerUtils.printCommonLog("支付宝通知验证失败!");
				out.print("fail");
				return;
			}
			
			//封装回调参数
			SingleRefundResData singleRefundResData = aliPayService.refundAlipayRequsetHandle(params);
			
			try {
				aliPayService.handleCallBackRefundOrderHandelFunction(singleRefundResData);
				out.print("success");
			}catch(ServiceException e) {
				if(e.getResultCode()==200) {
					out.print("success");
				}else {
					LoggerUtils.printExceptionLog(e.getMessage(), e);
					out.print("fail");
				}
			}
		}catch(Exception e) {
			LoggerUtils.printExceptionLog(e.getMessage(), e);
			out.print("fail");
		}
		out.close();
	}
	
	
	
}
