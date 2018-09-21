package com.dachen.commons.asyn.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dachen.commons.asyn.queue.QueueFactory;

public class EventProducer {
    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);

    public static void fireEvent(EcEvent event) {
        String jsonStr=null;
        try {
            String queueName = event.getType().getQueueName();
            QueueFactory.getInstance().sendMessage(queueName, event.toJSONString());
        }catch(Exception e){
        	logger.error(e.getMessage());
        } finally {
            logger.debug("EventProducer {}", jsonStr);
        }
    }
}
