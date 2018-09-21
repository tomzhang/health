package com.dachen.health.operationLog.mq;

import com.alibaba.fastjson.JSONObject;
import com.dachen.health.operationLog.constant.OperationLogConstant;
import com.dachen.health.operationLog.entity.po.OperationLog;
import com.dachen.health.operationLog.service.IOperationLogService;
import com.dachen.mq.ExchangeType;
import com.dachen.mq.consume.annotation.MqConsumeMapping;
import com.dachen.mq.consume.listener.AbstractMqConsumerListener;
import com.dachen.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 钟良
 * @desc 处理health行为日志
 * @date:2017/9/29 11:05 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Component
@MqConsumeMapping(exchangeType = ExchangeType.DIRECT, queueName = OperationLogConstant.HEALTH_OPERATION_LOG_QUEUE)
public class OperationLogMqListener extends AbstractMqConsumerListener {
    private static final Logger logger = LoggerFactory.getLogger(OperationLogMqListener.class);

    @Autowired
    private IOperationLogService operationLogService;

    @Override
    public void handleMessage(String jsonMessage) throws Exception {
        logger.debug("OperationLogMqListener-handleMessage jsonMessage：{}", jsonMessage);
        if (StringUtil.isNotBlank(jsonMessage)) {
            OperationLog operationLog = JSONObject.parseObject(jsonMessage, OperationLog.class);

            operationLogService.save(operationLog);
        }
    }
}
