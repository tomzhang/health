package com.dachen.health.api.client.order.proxy;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.sdk.exception.HttpApiException;

/**
 * Created by qinyuan.chen
 * Date:2016/12/30
 * Time:16:38
 */
@Component
public class PendingOrderStatusApiClientProxy extends HealthApiClientProxy {

    public void createQuestionByCareItem(String careItemId, Long sentTime,String carePlanId, Integer orderId) throws HttpApiException {
    	if (StringUtils.isBlank(carePlanId)) {
    		throw new HttpApiException("参数有误");
    	}
    	
        Map<String, String> params = new HashMap<String, String>(4);
        putIfNotBlank(params, "careItemId", careItemId);
        putIfNotBlank(params, "sentTime", sentTime);
        putIfNotBlank(params, "carePlanId", carePlanId);
        putIfNotBlank(params, "orderId", orderId);
        try {
            String url = "pendingOrderStatus/createQuestionByCareItem";
            this.postRequest(url, params, String.class);
        } catch (HttpApiException e) {
            throw new HttpApiException(e.getMessage(), e);
        }
    }
    
    public void submitAnswerSheetByCareItem(String careItemId, Long submitTime,String carePlanId, Integer orderId) throws HttpApiException {
    	if (StringUtils.isBlank(carePlanId)) {
    		throw new HttpApiException("参数有误");
    	}
        Map<String, String> params = new HashMap<String, String>(4);
        putIfNotBlank(params, "careItemId", careItemId);
        putIfNotBlank(params, "submitTime", submitTime);
        putIfNotBlank(params, "carePlanId", carePlanId);
        putIfNotBlank(params, "orderId", orderId);
        try {
            String url = "pendingOrderStatus/submitAnswerSheetByCareItem";
            this.postRequest(url, params, String.class);
        } catch (HttpApiException e) {
            throw new HttpApiException(e.getMessage(), e);
        }
    }

}