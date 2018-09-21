package com.dachen.commons.asyn.event.listener;

import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.queue.QueueFactory;
import com.dachen.commons.asyn.queue.QueueMessageListener;

public class BaseEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(BaseEventConsumer.class);

    public void subscribe(final QueueFactory queueFactory, final String topic, final Executor executor, final ListenerFactory listenerFactory) {
        queueFactory.subscribe(topic, new QueueMessageListener() {

            public void recieveMessages(String topic, String jsonStr) {
                logger.debug("{} {}", topic, jsonStr);
                try {
                    EcEvent ecEvent = EcEvent.parserJSONString(jsonStr);
                    execute(listenerFactory, ecEvent);
                } catch (Exception ignore) {
                }
            }

            public Executor getExecutor() {
                return executor;
            }
        });
    }

    public void execute(ListenerFactory listenerFactory, EcEvent event) {
        Long createTime = event.param("createTime");
        Long delayTime = null;
        if (createTime != null) {
            delayTime = System.currentTimeMillis() - createTime;
        }
        long begin = System.currentTimeMillis();
        List<ListenerItem> listener = listenerFactory.getListener(event.getType());
        for (ListenerItem l : listener) {
            if (l != null && l.listener != null && l.method != null) {
                try {
                    l.method.invoke(l.listener, event);
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
        logger.debug("delay: {}  execute cost:{}", delayTime, System.currentTimeMillis() - begin);
    }
}
