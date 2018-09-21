package com.tencent.business;

import static java.lang.Thread.sleep;

import org.slf4j.LoggerFactory;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.logger.LoggerUtils;
import com.tencent.common.Log;
import com.tencent.common.Signature;
import com.tencent.common.Util;
import com.tencent.protocol.pay_protocol.NoitfyPayResData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryResData;
import com.tencent.protocol.reverse_protocol.ReverseReqData;
import com.tencent.protocol.reverse_protocol.ReverseResData;
import com.tencent.service.ReverseService;
import com.tencent.service.ScanPayQueryService;

/**
 * User: rizenguo
 * Date: 2014/12/1
 * Time: 17:05
 */
public class AsyNotifyPayBusiness {

    public AsyNotifyPayBusiness() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        scanPayQueryService = new ScanPayQueryService();
    }

    public interface ResultListener {

       /* //API返回ReturnCode不合法，支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问
        void onFailByReturnCodeError(ScanPayResData scanPayResData);

        //API返回ReturnCode为FAIL，支付API系统返回失败，请检测Post给API的数据是否规范合法
        void onFailByReturnCodeFail(ScanPayResData scanPayResData);

        //支付请求API返回的数据签名验证失败，有可能数据被篡改了
        void onFailBySignInvalid(ScanPayResData scanPayResData);

        //查询请求API返回的数据签名验证失败，有可能数据被篡改了
        void onFailByQuerySignInvalid(ScanPayQueryResData scanPayQueryResData);
*/
        //支付失败
        Boolean onFail();

        //支付成功
        Boolean onSuccess(NoitfyPayResData noitfyPayResDataParam,String transactionID);

    }

    //打log用
    private static Log log = new Log(LoggerFactory.getLogger(AsyNotifyPayBusiness.class));

    //每次调用订单查询API时的等待时间，因为当出现支付失败的时候，如果马上发起查询不一定就能查到结果，所以这里建议先等待一定时间再发起查询

    private int waitingTimeBeforePayQueryServiceInvoked = 5000;

    //循环调用订单查询API的次数
    private int payQueryLoopInvokedCount = 3;

    //每次调用撤销API的等待时间
    private int waitingTimeBeforeReverseServiceInvoked = 5000;


    private ScanPayQueryService scanPayQueryService;

    private ReverseService reverseService;

    private String transactionID = "";

    /**
     * 直接执行被扫支付业务逻辑（包含最佳实践流程）
     *
     * @param scanPayReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @throws Exception
     */
    public Boolean run(NoitfyPayResData noitfyPayResDataParam,String payNotifyResponseString, ResultListener resultListener) throws Exception {
        //TODO 处理微信异步通知
    	LoggerUtils.printCommonLog("微信通知获取xml文件日志:"+payNotifyResponseString);
    	
    	if (noitfyPayResDataParam == null || noitfyPayResDataParam.getReturn_code() == null) {
            log.e("【支付失败】支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问");
            //resultListener.onFailByReturnCodeError(scanPayResData);
            throw new ServiceException("请求数据包为空");
        }
    	
    	String outTradeNo = noitfyPayResDataParam.getOut_trade_no();
    	
    	if(noitfyPayResDataParam.getReturn_code().equals("FAIL")) {
    		 throw new ServiceException("微信支付失败!");
    	} else {
    		
    		log.i("支付API系统成功返回数据");
    		if (!Signature.checkIsSignValidFromResponseString(payNotifyResponseString)) {
                log.e("【支付失败】支付请求API返回的数据签名验证失败，有可能数据被篡改了");
                throw new ServiceException("签名失败!");
            }
    		
			if (noitfyPayResDataParam.getResult_code().equals("SUCCESS")) {
	            log.i("【确认支付成功】");
	            String transID = noitfyPayResDataParam.getTransaction_id();
	            LoggerUtils.printCommonLog("订单号:"+outTradeNo+"已向微信支付确认该用户已经支付!");
	            LoggerUtils.printCommonLog("微信transaction_id:"+transID+"请求");
	            return resultListener.onSuccess(noitfyPayResDataParam,transactionID);
	        }else{
	        	if (doPayQueryLoop(payQueryLoopInvokedCount, noitfyPayResDataParam.getTransaction_id(),resultListener)) {
	        		LoggerUtils.printCommonLog("订单号:"+outTradeNo+"已向微信服务器确认该订单已支付!");
	        		return resultListener.onSuccess(noitfyPayResDataParam, outTradeNo);
	        	} else {
	             	LoggerUtils.printCommonLog("订单号:"+outTradeNo+"未能确认微信服务器用户已经支付!");
	             	throw new ServiceException("微信服务器支付未确认！");
	        	}
	       }
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
    private boolean doOnePayQuery(String outTradeNo,ResultListener resultListener) throws Exception {

        sleep(waitingTimeBeforePayQueryServiceInvoked);//等待一定时间再进行查询，避免状态还没来得及被更新

        String payQueryServiceResponseString;

        ScanPayQueryReqData scanPayQueryReqData = new ScanPayQueryReqData("",outTradeNo);
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
                //resultListener.onFailByQuerySignInvalid(scanPayQueryResData);
                return false;
            }

            if (scanPayQueryResData.getResult_code().equals("SUCCESS")) {//业务层成功
                String transID = scanPayQueryResData.getTransaction_id();
                if(transID != null){
                    transactionID = transID;
                }
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
    private boolean doPayQueryLoop(int loopCount, String outTradeNo,ResultListener resultListener) throws Exception {
        //至少查询一次
        if (loopCount == 0) {
            loopCount = 1;
        }
        //进行循环查询
        for (int i = 0; i < loopCount; i++) {
            if (doOnePayQuery(outTradeNo,resultListener)) {
                return true;
            }
        }
        return false;
    }

    //是否需要再调一次撤销，这个值由撤销API回包的recall字段决定
    private boolean needRecallReverse = false;

    /**
     * 进行一次撤销操作
     *
     * @param outTradeNo    商户系统内部的订单号,32个字符内可包含字母, [确保在商户系统唯一]
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @return 该订单是否支付成功
     * @throws Exception
     */
    private boolean doOneReverse(String outTradeNo,ResultListener resultListener) throws Exception {

        sleep(waitingTimeBeforeReverseServiceInvoked);//等待一定时间再进行查询，避免状态还没来得及被更新

        String reverseResponseString;

        ReverseReqData reverseReqData = new ReverseReqData("",outTradeNo);
        reverseResponseString = reverseService.request(reverseReqData);

        log.i("撤销API返回的数据如下：");
        log.i(reverseResponseString);
        //将从API返回的XML数据映射到Java对象
        ReverseResData reverseResData = (ReverseResData) Util.getObjectFromXML(reverseResponseString, ReverseResData.class);
        if (reverseResData == null) {
            log.i("支付订单撤销请求逻辑错误，请仔细检测传过去的每一个参数是否合法");
            return false;
        }
        if (reverseResData.getReturn_code().equals("FAIL")) {
            //注意：一般这里返回FAIL是出现系统级参数错误，请检测Post给API的数据是否规范合法
            log.i("支付订单撤销API系统返回失败，失败信息为：" + reverseResData.getReturn_msg());
            return false;
        } else {

            if (!Signature.checkIsSignValidFromResponseString(reverseResponseString)) {
                log.e("【支付失败】支付请求API返回的数据签名验证失败，有可能数据被篡改了");
                //resultListener.onFailByReverseSignInvalid(reverseResData);
                needRecallReverse = false;//数据被窜改了，不需要再重试
                return false;
            }


            if (reverseResData.getResult_code().equals("FAIL")) {
                log.i("撤销出错，错误码：" + reverseResData.getErr_code() + "     错误信息：" + reverseResData.getErr_code_des());
                if (reverseResData.getRecall().equals("Y")) {
                    //表示需要重试
                    needRecallReverse = true;
                    return false;
                } else {
                    //表示不需要重试，也可以当作是撤销成功
                    needRecallReverse = false;
                    return true;
                }
            } else {
                //查询成功，打印交易状态
                log.i("支付订单撤销成功");
                return true;
            }
        }
    }


    /**
     * 由于有的时候是因为服务延时，所以需要商户每隔一段时间（建议5秒）后再进行查询操作，是否需要继续循环调用撤销API由撤销API回包里面的recall字段决定。
     *
     * @param outTradeNo    商户系统内部的订单号,32个字符内可包含字母, [确保在商户系统唯一]
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @throws InterruptedException
     */
    private void doReverseLoop(String outTradeNo,ResultListener resultListener) throws Exception {
        //初始化这个标记
        needRecallReverse = true;
        //进行循环撤销，直到撤销成功，或是API返回recall字段为"Y"
        while (needRecallReverse) {
            if (doOneReverse(outTradeNo,resultListener)) {
                return;
            }
        }
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

    /**
     * 设置循环多次调用撤销API的时间间隔
     *
     * @param duration 时间间隔，默认为5秒
     */
    public void setWaitingTimeBeforeReverseServiceInvoked(int duration) {
        waitingTimeBeforeReverseServiceInvoked = duration;
    }
   
    public void setScanPayQueryService(ScanPayQueryService service) {
        scanPayQueryService = service;
    }
}
