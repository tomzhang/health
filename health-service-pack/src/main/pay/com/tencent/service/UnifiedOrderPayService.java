package com.tencent.service;

import com.tencent.common.Configure;
import com.tencent.protocol.pay_protocol.UnifiedOrderPayReqData;

/**
 * User: peiX
 * Date: 2014/10/29
 * Time: 16:03
 */
public class UnifiedOrderPayService extends BaseService{

    public UnifiedOrderPayService() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        super(Configure.UNIFIED_ORDER);
    }

    /**
     * 请求统一下单服务
     * @param UnifiedOrderPayReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的数据
     * @throws Exception
     */
    public String request(UnifiedOrderPayReqData unifiedOrderPayReqData) throws Exception {

        //--------------------------------------------------------------------
        //发送HTTPS的Post请求到API地址
        //--------------------------------------------------------------------
        String responseString = sendPost(unifiedOrderPayReqData);

        return responseString;
    }
}
