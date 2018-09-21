package com.dachen.health.mq;

import com.alibaba.fastjson.JSONObject;
import com.dachen.mq.producer.BasicProducer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author cuizhiquan
 * @Description
 * @date 2017/12/11 15:45
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Component
public class RabbitMqProducer {

    /**
     * 平台banner exchangeName
     */
    public static final String PLATFORM_BANNER_EXCHANGE = "platformBannerChange";

    /**
     * 科室banner exchangeName
     */
    public static final String DEPT_BANNER_EXCHANGE = "deptBannerChange";


    public void sendFanoutMessage(String exchangeName,String message){
        BasicProducer.fanoutMessage(exchangeName, message);
    }
}
