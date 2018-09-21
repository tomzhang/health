package com.dachen.health.operationLog.mq;

import com.alibaba.fastjson.JSONObject;
import com.dachen.health.operationLog.constant.OperationLogConstant;
import com.dachen.mq.producer.BasicProducer;
import java.util.HashMap;
import java.util.Map;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author 钟良
 * @desc
 * @date:2017/9/29 15:08 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Component
public class OperationLogMqProducer {

    /**
     * 发送MQ消息
     *
     * @param userId 操作用户Id
     * @param operationType 操作类型
     * @param content 操作内容
     */
    @Async
    public void sendMsgToMq(Integer userId, String operationType, String content) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userId", userId);
        dataMap.put("operationType", operationType);
        dataMap.put("content", content);
        dataMap.put("date", System.currentTimeMillis());
        BasicProducer.sendMessage(OperationLogConstant.HEALTH_OPERATION_LOG_QUEUE, JSONObject.toJSONString(dataMap));
    }
}
