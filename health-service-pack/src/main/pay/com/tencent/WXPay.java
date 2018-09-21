package com.tencent;

import com.tencent.business.AsyNotifyPayBusiness;
import com.tencent.business.DownloadBillBusiness;
import com.tencent.business.RefundBusiness;
import com.tencent.business.RefundQueryBusiness;
import com.tencent.business.ScanPayBusiness;
import com.tencent.business.ScanPayQueryBusiness;
import com.tencent.common.Configure;
import com.tencent.protocol.downloadbill_protocol.DownloadBillReqData;
import com.tencent.protocol.pay_protocol.NoitfyPayResData;
import com.tencent.protocol.pay_protocol.ScanPayReqData;
import com.tencent.protocol.pay_protocol.UnifiedOrderPayReqData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_query_protocol.RefundQueryReqData;
import com.tencent.protocol.reverse_protocol.ReverseReqData;
import com.tencent.service.DownloadBillService;
import com.tencent.service.RefundQueryService;
import com.tencent.service.RefundService;
import com.tencent.service.ReverseService;
import com.tencent.service.ScanPayQueryService;
import com.tencent.service.ScanPayService;
import com.tencent.service.UnifiedOrderPayService;

/**
 * SDK总入口
 */
public class WXPay {

    /**
     * 初始化SDK依赖的几个关键配置
     * @param key 签名算法需要用到的秘钥
     * @param appID 公众账号ID
     * @param mchID 商户ID
     * @param sdbMchID 子商户ID，受理模式必填
     * @param certLocalPath HTTP证书在服务器中的路径，用来加载证书用
     * @param certPassword HTTP证书的密码，默认等于MCHID
     */
    public static void initSDKConfiguration(String key,String appID,String mchID,String sdbMchID,String certLocalPath,String certPassword){
        Configure.setKey(key);
        Configure.setAppID(appID);
        Configure.setMchID(mchID);
        Configure.setSubMchID(sdbMchID);
        Configure.setCertLocalPath(certLocalPath);
        Configure.setCertPassword(certPassword);
    }
    
    public static String requestUnifiedOrderPayService(UnifiedOrderPayReqData unifiedOrderPayReqData)throws Exception {
    	return new UnifiedOrderPayService().request(unifiedOrderPayReqData);
    }
    
    /*public static void main(String rags[]) {
    	
    	//System.out.println(new WXPay().getClass().getClassLoader().getSystemResource(name));
    	
    	//UnifiedOrderPayReqData unifiedOrderPayReqData = new UnifiedOrderPayReqData("测试下单", "测试下单1", "测试下单1", "1005853661886908", 1, Util.getCurrTime(), Util.getafterTimeStamp(), "WX", "df", "APP", "no_credit","");
    	
    	UnifiedOrderPayReqData unifiedOrderPayReqData 
		= new UnifiedOrderPayReqData("测试商品3", 
								   "测试商品3",
								   String.valueOf(10090907), 
								   1, 
								   "http://121.35.210.169:60089/health-im-api/pack/paynotify/wxpaycallback", 
								   "APP"
								   );
    	try {
    		String payServiceResponseString = WXPay.requestUnifiedOrderPayService(unifiedOrderPayReqData);
			UnifiedOrderPayResData unifiedOrderPayResData = (UnifiedOrderPayResData) Util.getObjectFromXML(payServiceResponseString, UnifiedOrderPayResData.class);
			System.out.println(payServiceResponseString);
			AppPayReqData appPayReqData = new AppPayReqData(unifiedOrderPayResData.getPrepay_id(), Util.getTimeStamp());     
			System.out.println(appPayReqData.toMap().toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }*/
    
    /**
     * 请求支付服务
     * @param scanPayReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的数据
     * @throws Exception
     */
    public static String requestScanPayService(ScanPayReqData scanPayReqData) throws Exception{
        return new ScanPayService().request(scanPayReqData);
    }

    /**
     * 请求支付查询服务
     * @param scanPayQueryReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
	public static String requestScanPayQueryService(ScanPayQueryReqData scanPayQueryReqData) throws Exception{
		return new ScanPayQueryService().request(scanPayQueryReqData);
	}

    /**
     * 请求退款服务
     * @param refundReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
    public static String requestRefundService(RefundReqData refundReqData) throws Exception{
        return new RefundService().request(refundReqData);
    }

    /**
     * 请求退款查询服务
     * @param refundQueryReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
	public static String requestRefundQueryService(RefundQueryReqData refundQueryReqData) throws Exception{
		return new RefundQueryService().request(refundQueryReqData);
	}

    /**
     * 请求撤销服务
     * @param reverseReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
	public static String requestReverseService(ReverseReqData reverseReqData) throws Exception{
		return new ReverseService().request(reverseReqData);
	}

    /**
     * 请求对账单下载服务
     * @param downloadBillReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
    public static String requestDownloadBillService(DownloadBillReqData downloadBillReqData) throws Exception{
        return new DownloadBillService().request(downloadBillReqData);
    }

    /**
     * 直接执行被扫支付业务逻辑（包含最佳实践流程）
     * @param scanPayReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @throws Exception
     */
    public static void doScanPayBusiness(ScanPayReqData scanPayReqData, ScanPayBusiness.ResultListener resultListener) throws Exception {
        new ScanPayBusiness().run(scanPayReqData, resultListener);
    }
    
    
    

    /**
     * 调用退款业务逻辑
     * @param refundReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 业务逻辑可能走到的结果分支，需要商户处理
     * @throws Exception
     */
    public static String doRefundBusiness(RefundReqData refundReqData, RefundBusiness.ResultListener resultListener) throws Exception {
    	RefundBusiness refundBiz = new RefundBusiness();
    	refundBiz.run(refundReqData,resultListener);
    	return refundBiz.getResult();
    }

    /**
     * 运行退款查询的业务逻辑
     * @param refundQueryReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @throws Exception
     */
    public static void doRefundQueryBusiness(RefundQueryReqData refundQueryReqData,RefundQueryBusiness.ResultListener resultListener) throws Exception {
        new RefundQueryBusiness().run(refundQueryReqData,resultListener);
    }
    
    
    /**
     * 校验支付回调的通知
     * @param noitfyPayResData 这个数据对象里面包含了通知回调的请求数据
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @throws Exception
     */
    public static Boolean doNoitfyPayBusiness(NoitfyPayResData noitfyPayResData,String resultmxl, AsyNotifyPayBusiness.ResultListener resultListener)throws Exception {
    	return new AsyNotifyPayBusiness().run(noitfyPayResData, resultmxl,resultListener);
    }
    
    /**
     *  运行订单查询业务逻辑
     * @param payNo
     * @param resultListener
     * @return
     * @throws Exception
     */
    /*public static Boolean doAsyNotifyPayBusiness(NoitfyPayResData noitfyPayResData,String resultXml,AsyNotifyPayBusiness.ResultListener resultListener) throws Exception {
    	return new AsyNotifyPayBusiness().run(noitfyPayResData,resultXml, resultListener);
    }*/
    
    /**
     *  运行订单查询业务逻辑
     * @param payNo
     * @param resultListener
     * @return
     * @throws Exception
     */
    public static Boolean doScanPayQueryBusiness(String payNo,String transactionId,ScanPayQueryBusiness.ResultListener resultListener) throws Exception {
    	return new ScanPayQueryBusiness().run(payNo,transactionId, resultListener);
    }
    /**
     * 请求对账单下载服务
     * @param downloadBillReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @return API返回的XML数据
     * @throws Exception
     */
    public static void doDownloadBillBusiness(DownloadBillReqData downloadBillReqData,DownloadBillBusiness.ResultListener resultListener) throws Exception {
        new DownloadBillBusiness().run(downloadBillReqData,resultListener);
    }


}
