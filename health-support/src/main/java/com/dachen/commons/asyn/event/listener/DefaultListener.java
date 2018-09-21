package com.dachen.commons.asyn.event.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventType;
import com.dachen.commons.asyn.event.annotation.EcEventListener;
import com.dachen.commons.asyn.event.annotation.EcEventMapping;
import com.dachen.mq.MQConstant;
import com.dachen.mq.producer.BasicProducer;

@Component("EcLiteDefaultListener")
@EcEventListener
public class DefaultListener {
    private static final Logger logger = LoggerFactory.getLogger(DefaultListener.class);
    
    @EcEventMapping(type = {EventType.UserBaseInfoUpdated})
    public void fireEvent(EcEvent event) throws Exception {
    	String userList = event.param("userList");
    	logger.debug("fanoutMessage to {},{}",MQConstant.EXCHANGE_USER_CHANGE,userList);
		BasicProducer.fanoutMessage(MQConstant.EXCHANGE_USER_CHANGE, userList);
    }
    
}
