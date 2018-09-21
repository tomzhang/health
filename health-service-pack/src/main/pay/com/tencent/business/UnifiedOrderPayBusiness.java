package com.tencent.business;


import org.slf4j.LoggerFactory;

import com.tencent.common.Log;
import com.tencent.common.Signature;
import com.tencent.common.Util;
import com.tencent.protocol.pay_protocol.UnifiedOrderPayReqData;
import com.tencent.protocol.pay_protocol.UnifiedOrderPayResData;
import com.tencent.service.UnifiedOrderPayService;

/**
 * User: peiX
 * Date: 2015/8/19
 * Time: 10:05
 */
public class UnifiedOrderPayBusiness {

    public UnifiedOrderPayBusiness() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        unifiedOrderPayService = new UnifiedOrderPayService();
    }

    public interface ResultListener {

        //API返回ReturnCode不合法，支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问
        void onFailByReturnCodeError(UnifiedOrderPayResData unifiedOrderPayResData);

        //API返回ReturnCode为FAIL，支付API系统返回失败，请检测Post给API的数据是否规范合法
        void onFailByReturnCodeFail(UnifiedOrderPayResData unifiedOrderPayResData);

        //支付请求API返回的数据签名验证失败，有可能数据被篡改了
        void onFailBySignInvalid(UnifiedOrderPayResData unifiedOrderPayResData);

        //查询请求API返回的数据签名验证失败，有可能数据被篡改了
        void onFailByQuerySignInvalid(UnifiedOrderPayResData unifiedOrderPayResData);

        //撤销请求API返回的数据签名验证失败，有可能数据被篡改了
        void onFailByReverseSignInvalid(UnifiedOrderPayResData unifiedOrderPayResData);

        //用户余额不足，换其他卡支付或是用现金支付
        void onFailByMoneyNotEnough(UnifiedOrderPayResData unifiedOrderPayResData);

        //支付失败
        void onFail(UnifiedOrderPayResData unifiedOrderPayResData);

        //支付成功
        void onSuccess(UnifiedOrderPayResData unifiedOrderPayResData,String prepayId);

    }

    //打log用
    private static Log log = new Log(LoggerFactory.getLogger(UnifiedOrderPayBusiness.class));

  

    UnifiedOrderPayService unifiedOrderPayService;

    private String prepayId = "";

    /**
     * 直接执行被扫支付业务逻辑（包含最佳实践流程）
     *
     * @param scanPayReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @throws Exception
     */
    public void run(UnifiedOrderPayReqData unifiedOrderPayReqData, ResultListener resultListener) throws Exception {

        //--------------------------------------------------------------------
        //构造请求“被扫支付API”所需要提交的数据
        //--------------------------------------------------------------------
        //String outTradeNo = unifiedOrderPayReqData.getOut_trade_no();

        //接受API返回
        String payServiceResponseString;

        long costTimeStart = System.currentTimeMillis();


        log.i("支付API返回的数据如下：");
        payServiceResponseString = unifiedOrderPayService.request(unifiedOrderPayReqData);

        long costTimeEnd = System.currentTimeMillis();
        long totalTimeCost = costTimeEnd - costTimeStart;
        log.i("api请求总耗时：" + totalTimeCost + "ms");

        //打印回包数据
        log.i(payServiceResponseString);

        //将从API返回的XML数据映射到Java对象
        UnifiedOrderPayResData unifiedOrderPayResData = (UnifiedOrderPayResData) Util.getObjectFromXML(payServiceResponseString, UnifiedOrderPayResData.class);

        if (unifiedOrderPayResData == null || unifiedOrderPayResData.getReturn_code() == null) {
            log.e("【支付失败】支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问");
            resultListener.onFailByReturnCodeError(unifiedOrderPayResData);
            return;
        }

        if (unifiedOrderPayResData.getReturn_code().equals("FAIL")) {
            //注意：一般这里返回FAIL是出现系统级参数错误，请检测Post给API的数据是否规范合法
            log.e("【支付失败】支付API系统返回失败，请检测Post给API的数据是否规范合法");
            resultListener.onFailByReturnCodeFail(unifiedOrderPayResData);
            return;
        } else {
            log.i("支付API系统成功返回数据");
            //--------------------------------------------------------------------
            //收到API的返回数据的时候得先验证一下数据有没有被第三方篡改，确保安全
            //--------------------------------------------------------------------
            if (!Signature.checkIsSignValidFromResponseString(payServiceResponseString)) {
                log.e("【支付失败】支付请求API返回的数据签名验证失败，有可能数据被篡改了");
                resultListener.onFailBySignInvalid(unifiedOrderPayResData);
                return;
            }

            //获取错误码
            String errorCode = unifiedOrderPayResData.getErr_code();
            //获取错误描述
            String errorCodeDes = unifiedOrderPayResData.getErr_code_des();

            if (unifiedOrderPayResData.getResult_code().equals("SUCCESS")) {

                //--------------------------------------------------------------------
                //1)直接扣款成功
                //--------------------------------------------------------------------

                log.i("【统一预定单下单成功】");

                String prepay_id = unifiedOrderPayResData.getPrepay_id();
                if(prepay_id != null){
                	prepayId = prepay_id;
                }

                resultListener.onSuccess(unifiedOrderPayResData,prepayId);
            }else{

                //出现业务错误
                log.i("业务返回失败");
                log.i("err_code:" + errorCode);
                log.i("err_code_des:" + errorCodeDes);

                //业务错误时错误码有好几种，商户重点提示以下几种
                if (errorCode.equals("NOAUTH") || errorCode.equals("ORDERPAID") || errorCode.equals("NOTENOUGH") || errorCode.equals("ORDERCLOSED") || errorCode.equals("OUT_TRADE_NO_USED") ||errorCode.equals("SIGNERROR") || errorCode.equals("ORDERCLOSED")) {

                    //--------------------------------------------------------------------
                    //2)统一下单失败
                    //--------------------------------------------------------------------
                	
                	//以下几种情况建议明确提示用户，指导接下来的工作
                    if (errorCode.equals("NOAUTH")) {
                        //请商户前往申请此接口权限
                        log.w("【请商户前往申请此接口权限】原因是：" + errorCodeDes);
                    } else if (errorCode.equals("ORDERPAID")) {
                        //商户订单已支付
                        log.w("【商户订单已支付】原因是：" + errorCodeDes);
                    } else if (errorCode.equals("NOTENOUGH")) {
                        //提示用户余额不足，换其他卡支付或是用现金支付
                        log.w("【支付扣款明确失败】原因是：" + errorCodeDes);
                        resultListener.onFailByMoneyNotEnough(unifiedOrderPayResData);
                    }
                } else {
                	//TODO 设置 异常接口
                    
                }
            }
        }
    }

	public UnifiedOrderPayService getUnifiedOrderPayService() {
		return unifiedOrderPayService;
	}

	public void setUnifiedOrderPayService(
			UnifiedOrderPayService unifiedOrderPayService) {
		this.unifiedOrderPayService = unifiedOrderPayService;
	}
 
}
