package com.tencent.business;


import static java.lang.Thread.sleep;

import org.slf4j.LoggerFactory;

import com.dachen.commons.exception.ServiceException;
import com.tencent.common.Log;
import com.tencent.common.Signature;
import com.tencent.common.Util;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryResData;
import com.tencent.service.ScanPayQueryService;

/**
 * 微信单笔交易状态查询
 * Date: 2014/12/1
 * Time: 17:05
 */
public class ScanPayQueryBusiness {

    public ScanPayQueryBusiness() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        scanPayQueryService = new ScanPayQueryService();
    }

    public interface ResultListener {
    	
    	void onFailByQuerySignInvalid(ScanPayQueryResData scanPayQueryResData) throws ServiceException;
    	
        //未支付
        Boolean onFail();

        //支付成功
        Boolean onSuccess(String transactionID);

    }

    //打log用
    private static Log log = new Log(LoggerFactory.getLogger(ScanPayQueryBusiness.class));

    //循环调用订单查询API的次数
    private int payQueryLoopInvokedCount = 3;

    private int waitingTimeBeforePayQueryServiceInvoked =3000;
    
    private ScanPayQueryService scanPayQueryService;
    
    /**
     * 多次查询
     *
     * @param scanPayReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @throws Exception
     */
    public Boolean run(String payNo,String transactionId, ResultListener resultListener) throws Exception {
    	
        if (doPayQueryLoop(payQueryLoopInvokedCount,payNo, transactionId,resultListener)) {
            log.i("【支付扣款未知失败、查询到支付成功】");
            //TODO 如果查询成功处理订单状态
            return resultListener.onSuccess(payNo);
        } else {
            return resultListener.onFail();
        }
    }

    /**
     * 进行一次支付订单查询操作
     *
     * @param outTradeNo    商户系统内部的订单号,32个字符内可包含字母, [确保在商户系统唯一]
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @return 该订单是否支付成功
     * @throws Exception
     */
    private boolean doOnePayQuery(String payNo,String outTradeNo,ResultListener resultListener) throws Exception {

        sleep(waitingTimeBeforePayQueryServiceInvoked);//等待一定时间再进行查询，避免状态还没来得及被更新

        String payQueryServiceResponseString;

        ScanPayQueryReqData scanPayQueryReqData = new ScanPayQueryReqData(payNo,outTradeNo);
        payQueryServiceResponseString = scanPayQueryService.request(scanPayQueryReqData);

        log.i("支付订单查询API返回的数据如下：");
        log.i(payQueryServiceResponseString);

        //将从API返回的XML数据映射到Java对象
        ScanPayQueryResData scanPayQueryResData = (ScanPayQueryResData) Util.getObjectFromXML(payQueryServiceResponseString, ScanPayQueryResData.class);
        if (scanPayQueryResData == null || scanPayQueryResData.getReturn_code() == null) {
            log.i("支付订单查询请求逻辑错误，请仔细检测传过去的每一个参数是否合法");
            return false;
        }

        if (scanPayQueryResData.getReturn_code().equals("FAIL")) {
            //注意：一般这里返回FAIL是出现系统级参数错误，请检测Post给API的数据是否规范合法
            log.i("支付订单查询API系统返回失败，失败信息为：" + scanPayQueryResData.getReturn_msg());
            return false;
        } else {

            if (!Signature.checkIsSignValidFromResponseString(payQueryServiceResponseString)) {
                log.e("【支付失败】支付请求API返回的数据签名验证失败，有可能数据被篡改了");
                resultListener.onFailByQuerySignInvalid(scanPayQueryResData);
                return false;
            }

            if (scanPayQueryResData.getResult_code().equals("SUCCESS")) {//业务层成功
                //String transID = scanPayQueryResData.getTransaction_id();
                if (scanPayQueryResData.getTrade_state().equals("SUCCESS")) {
                    //表示查单结果为“支付成功”
                    log.i("查询到订单支付成功");
                    return true;
                } else {
                    //支付不成功
                    log.i("查询到订单支付不成功");
                    return false;
                }
            } else {
                log.i("查询出错，错误码：" + scanPayQueryResData.getErr_code() + "     错误信息：" + scanPayQueryResData.getErr_code_des());
                return false;
            }

        }
    }

    /**
     * 由于有的时候是因为服务延时，所以需要商户每隔一段时间（建议5秒）后再进行查询操作，多试几次（建议3次）
     *
     * @param loopCount     循环次数，至少一次
     * @param outTradeNo    商户系统内部的订单号,32个字符内可包含字母, [确保在商户系统唯一]
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @return 该订单是否支付成功
     * @throws InterruptedException
     */
    private boolean doPayQueryLoop(int loopCount, String payNo,String outTradeNo,ResultListener resultListener) throws Exception {
        //至少查询一次
        if (loopCount == 0) {
            loopCount = 1;
        }
        //进行循环查询
        for (int i = 0; i < loopCount; i++) {
        	if(i==0) {
        		waitingTimeBeforePayQueryServiceInvoked =500;
        	}else {
        		waitingTimeBeforePayQueryServiceInvoked =5000;
        	}
            if (doOnePayQuery(payNo,outTradeNo,resultListener)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置循环多次调用订单查询API的时间间隔
     *
     * @param duration 时间间隔，默认为10秒
     */
    public void setWaitingTimeBeforePayQueryServiceInvoked(int duration) {
        waitingTimeBeforePayQueryServiceInvoked = duration;
    }

    /**
     * 设置循环多次调用订单查询API的次数
     *
     * @param count 调用次数，默认为三次
     */
    public void setPayQueryLoopInvokedCount(int count) {
        payQueryLoopInvokedCount = count;
    }

    public void setScanPayQueryService(ScanPayQueryService service) {
        scanPayQueryService = service;
    }

   

}
