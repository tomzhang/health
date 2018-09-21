package com.dachen.commons.asyn.queue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dachen.commons.asyn.queue.impl.RedisQueue;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.util.PropertiesUtil;

@Component
public class QueueFactory {
    private static final Logger logger = LoggerFactory.getLogger(QueueFactory.class);
    private static QueueFactory instance = null;
    private IQueue queue = null;

    @Resource(name = "jedisTemplate")
   	protected JedisTemplate xedisClient;
    
    private QueueFactory() {
        instance = this;
    }

    public static QueueFactory getInstance() {
        return instance;
    }

    @PostConstruct
    private void init() throws Exception {
    	String eventEnabled = PropertiesUtil.getContextProperty("asyn.event.disabled");
		if (StringUtils.isNotBlank(eventEnabled) && "true".equals(eventEnabled)) {
			logger.warn("QueueFactory is disabled.");
			return;
		}
		
        queue = new RedisQueue();
        queue.init(xedisClient);
    }

    @PreDestroy
    private void stop() {
        if (queue != null) queue.stop();
    }
    
    protected void checkEnabled() {
    	String eventEnabled = PropertiesUtil.getContextProperty("asyn.event.disabled");
		if (StringUtils.isNotBlank(eventEnabled) && "true".equals(eventEnabled)) {
			throw new ServiceException("asyn envet is disabled!");
		}
    }

    public void sendMessage(String topic, String json) {
    	checkEnabled();
		
//        long begin = System.currentTimeMillis();
        try {
            if (queue != null) queue.sendMessage(topic, json);
        } catch (Throwable e) {
            logger.error(topic + "/" + json + "\n" + e.getMessage(), e);
        } finally {
        	
        }
    }

    public void subscribe(String topic, QueueMessageListener listener) {
    	checkEnabled();
		
        if (queue != null) queue.subscribe(topic, listener);
    }

    public long size(String topic) {
        if (queue != null) return queue.size(topic);
        else return 0;
    }

    public long qps(String topic) {
        if (queue != null) return queue.qps(topic);
        else return 0;
    }
}
