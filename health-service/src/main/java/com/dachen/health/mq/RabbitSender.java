package com.dachen.health.mq;

import com.dachen.mq.producer.BasicProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cuizhiquan
 * @Description
 * @date 2017/12/8 11:38
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class RabbitSender extends BasicProducer{

    private static final Logger logger = LoggerFactory.getLogger(RabbitSender.class);

    /**
     * 发送热力值的exchangeName
     */
    public static final String USERCHECKHEATVALUE = "USERCHECK-HEATVALUE";

    /**
     * 发送热力值
     * @param circleId
     * @param userId
     */
	public static void sendUserCheckHeatValue(Integer userId) {
		if (userId != null) {
			fanoutMessage(USERCHECKHEATVALUE, userId+"");
			logger.info("sendUserCheckHeatValue msg:{}", userId);
		}
	}
}
